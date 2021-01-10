package de.mwvb.blockpuzzle

import android.app.AlertDialog
import android.content.ClipDescription
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import de.mwvb.blockpuzzle.game.DoesNotWorkException
import de.mwvb.blockpuzzle.game.Game
import de.mwvb.blockpuzzle.game.GameEngineFactory
import de.mwvb.blockpuzzle.game.IGameView
import de.mwvb.blockpuzzle.gamepiece.GamePiece
import de.mwvb.blockpuzzle.gamepiece.GamePieceTouchListener
import de.mwvb.blockpuzzle.gamepiece.GamePieceView
import de.mwvb.blockpuzzle.gravitation.ShakeService
import de.mwvb.blockpuzzle.persistence.IPersistence
import de.mwvb.blockpuzzle.persistence.Persistence
import de.mwvb.blockpuzzle.playingfield.Action
import de.mwvb.blockpuzzle.playingfield.IPlayingFieldView
import de.mwvb.blockpuzzle.playingfield.PlayingFieldView
import de.mwvb.blockpuzzle.playingfield.QPosition
import kotlinx.android.synthetic.main.activity_main.*
import java.text.DecimalFormat

/**
 * GameActivity
 *
 * It has still the name MainActivity because I'm not sure how to change the name
 * without destroying everything.
 */
class MainActivity : AppCompatActivity(), IGameView {
    private lateinit var game: Game
    private lateinit var shakeService : ShakeService

    override fun onCreate(savedInstanceState: Bundle?) {
        // INIT PHASE
        if (Build.VERSION.SDK_INT >= 21) {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.navigationBackground);
        }
        game = GameEngineFactory().create(this, per())
        shakeService = ShakeService(game)

        // SUPER PHASE
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // REST OF METHOD PHASE
        (placeholder1 as ViewGroup).addView(GamePieceView(baseContext, 1, false))
        (placeholder2 as ViewGroup).addView(GamePieceView(baseContext, 2, false))
        (placeholder3 as ViewGroup).addView(GamePieceView(baseContext, 3, false))
        (parking      as ViewGroup).addView(GamePieceView(baseContext, -1, true))

        initTouchListener(1)
        initTouchListener(2)
        initTouchListener(3)
        initTouchListener(-1)
        playingField.setOnDragListener(createDragListener(false)) // Drop Event für Spielfeld
        parking.setOnDragListener(createDragListener(true)) // Drop Event fürs Parking
        newGame.visibility = when (game.isNewGameButtonVisible) {
            true -> View.VISIBLE
            false -> View.INVISIBLE
        }
        newGame.setOnClickListener(onNewGame())
        shakeService.initShakeDetection(this)
    }

    // Activity reactivated
    override fun onResume() {
        super.onResume()
        try {
            shakeService.setActive(true)
            game.initGame()
        } catch (e: Exception) {
            Toast.makeText(this, e.javaClass.toString() + ": " + e.message + "\n" + e.stackTrace[0].toString(), Toast.LENGTH_LONG).show()
        }
    }

    // Activity goes sleeping
    override fun onPause() {
        game.save();

        shakeService.setActive(false)

        super.onPause()
    }

    /** Drag or rotate game piece */
    private fun initTouchListener(index: Int) {
        getGamePieceView(index).setOnTouchListener(object : GamePieceTouchListener(index, resources) {
            override fun up(view: View?, event: MotionEvent?) {
                getGamePieceView(index).endDragMode()
                game.rotate(index)
            }

            override fun isDragAllowed(): Boolean {
                return !game.isGameOver && game.isDragAllowed
            }
        })
    }

    /** mainly for dropping the game piece */
    private fun createDragListener(targetIsParking: Boolean): View.OnDragListener {
        return View.OnDragListener { _, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                }
                DragEvent.ACTION_DROP -> dropped(event, targetIsParking)
                DragEvent.ACTION_DRAG_ENTERED -> {
                    if (targetIsParking) {
                        getGamePieceView(-1).onDragEnter();
                    }
                    true
                }
                DragEvent.ACTION_DRAG_LOCATION -> true
                DragEvent.ACTION_DRAG_EXITED -> {
                    if (targetIsParking) {
                        getGamePieceView(-1).onDragLeave();
                    }
                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    try {
                        if (targetIsParking) {
                            getGamePieceView(-1).onDragLeave();
                        }
                        // Da ich nicht weiß, welcher ausgeblendet ist, blende ich einfach alle ein.
                        getGamePieceView(1).endDragMode()
                        getGamePieceView(2).endDragMode()
                        getGamePieceView(3).endDragMode()
                        getGamePieceView(-1).endDragMode()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(this, "F/dragEnd: " + e.message, Toast.LENGTH_LONG).show()
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun dropped(event: DragEvent, targetIsParking: Boolean): Boolean {
        try {
            val item = event.clipData.getItemAt(0)
            val index: Int = item.text.toString().toInt()
            val gamePiece = getGamePieceView(index).gamePiece
            var xy : QPosition? = null

            if (!targetIsParking) {
                // geg.: px, ges.: SpielfeldView Koordinaten (0 - 9)
                xy = calculatePlayingFieldCoordinates(event, gamePiece)
            }
            game.dispatch(targetIsParking, index, gamePiece, xy)
        } catch (e: DoesNotWorkException) {
            playingField.soundService.doesNotWork()
            Toast.makeText(this, R.string.gehtNicht, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "MA:" + e.message, Toast.LENGTH_LONG).show()
        }
        return true
    }

    private fun calculatePlayingFieldCoordinates(event: DragEvent, gamePiece: GamePiece): QPosition {
        val f = resources.displayMetrics.density
        var x = event.x / f // px -> dp
        var y = event.y / f
        // jetzt in Spielfeld Koordinaten umrechnen
        val br = PlayingFieldView.w / Game.blocks
        x /= br
        y = y / br - 2 - (gamePiece.maxY - gamePiece.minY)
        return QPosition(x.toInt(), y.toInt())
    }

    override fun getSpecialAction(specialState: Int): Action {
        if (specialState == 2) { // Death Star destroyed
            return Action() {
                val intent = Intent(this, InfoActivity::class.java)
                val args = Bundle()
                args.putInt("mode", 2)
                intent.putExtras(args)
                startActivity(intent)
            }
        }
        return Action() {}
    }

    /** Start new game */
    private fun onNewGame(): (View) -> Unit {
        return {
            if (game.isGameOver || game.lessScore()) {
                game.newGame()
            } else {
                val dialog: AlertDialog.Builder = AlertDialog.Builder(this)
                dialog.setTitle(resources.getString(R.string.startNewGame))
                dialog.setPositiveButton(resources.getString(android.R.string.ok)) { _, _ -> game.newGame() }
                dialog.setNegativeButton(resources.getString(android.R.string.cancel), null)
                dialog.show()
            }
        }
    }

    override fun showScore(score: Int, delta: Int, gameOver: Boolean) {
        var text = getScoreText(score, gameOver)
        if (gameOver) {
            if (game.gameCanBeWon() && game.isWon) {
                playingField.soundService.youWon()
            } else {
                playingField.soundService.gameOver()
            }
        } else if (delta > 0) {
            text += " (" + DecimalFormat("+#,##0").format(delta) + ")";
        }
        info.text = text
    }

    private fun getScoreText(score: Int, gameOver: Boolean): String {
        val ret: String
        if (gameOver) {
            if (game.gameCanBeWon() && game.isWon) {
                if (score == 1) {
                    ret = resources.getString(R.string.winScore1)
                } else {
                    ret = resources.getString(R.string.winScore2)
                }
            } else {
                if (score == 1) {
                    ret = resources.getString(R.string.gameOverScore1)
                } else {
                    ret = resources.getString(R.string.gameOverScore2)
                }
            }
        } else {
            if (score == 1) {
                ret = resources.getString(R.string.score1)
            } else {
                ret = resources.getString(R.string.score2)
            }
        }
        return ret.replace("XX", DecimalFormat("#,##0").format(score))
    }

    override fun getPlayingFieldView(): IPlayingFieldView {
        return playingField
    }

    override fun getGamePieceView(index: Int): GamePieceView {
        return when (index) {
            1 -> (placeholder1 as ViewGroup).getChildAt(0) as GamePieceView
            2 -> (placeholder2 as ViewGroup).getChildAt(0) as GamePieceView
            3 -> (placeholder3 as ViewGroup).getChildAt(0) as GamePieceView
            -1 -> (parking     as ViewGroup).getChildAt(0) as GamePieceView
            else -> throw RuntimeException()
        }
    }

    override fun showMoves(moves: Int) {
        val text: String
        if (moves == 0) {
            text = ""
        } else if (moves == 1) {
            text = DecimalFormat("#,##0").format(moves) + " " + resources.getString(R.string.move)
        } else {
            text = DecimalFormat("#,##0").format(moves) + " " + resources.getString(R.string.moves)
        }
        infoDisplay.setText(text)
    }

    override fun onBackPressed() {
        if (per().loadDeathStarMode() == 1) {
            if (game.isDragAllowed) { // during wait time back pressing is not allowed, game state could get unstable
                startActivity(Intent(this, BridgeActivity::class.java))
            }
        } else {
            super.onBackPressed()
        }
    }

    override fun showTerritoryName(resId: Int) {
        val text = resources.getText(resId).trim()
        territoryName.text = text
        territoryName.visibility = if (text.isEmpty()) View.GONE else View.VISIBLE // hide label to save space
    }

    override fun showToast(msg: String) {
        var msg2 = msg
        if (msg.startsWith("+")) {
            msg2 = msg.substring(1)
            playingField.soundService.youWon()
        } else if (msg.startsWith("-")) {
            msg2 = msg.substring(1)
            playingField.soundService.gameOver()
        }
        Toast.makeText(this, msg2, Toast.LENGTH_LONG).show()
    }

    override fun shake() {
        playingField.soundService.shake()
    }

    override fun playSound(number: Int) {
        playingField.soundService.playSound(number)
    }

    private fun per(): IPersistence {
        return Persistence(this)
    }
}
