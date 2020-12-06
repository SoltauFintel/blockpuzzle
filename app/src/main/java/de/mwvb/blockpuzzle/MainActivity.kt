package de.mwvb.blockpuzzle

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipDescription
import android.os.Build
import android.os.Bundle
import android.view.DragEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import de.mwvb.blockpuzzle.game.*
import de.mwvb.blockpuzzle.gamepiece.GamePiece
import de.mwvb.blockpuzzle.gamepiece.GamePieceView
import de.mwvb.blockpuzzle.gravitation.ShakeService
import de.mwvb.blockpuzzle.persistence.IPersistence
import de.mwvb.blockpuzzle.persistence.Persistence
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
        val stoneWars = per().isStoneWars
        if (stoneWars) {
            game = StoneWarsGame(this)
        } else {
            game = Game(this)
        }
        shakeService = ShakeService(game)

        // SUPER PHASE
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // REST OF METHOD PHASE
        (placeholder1 as ViewGroup).addView(GamePieceView(baseContext, 1, false))
        (placeholder2 as ViewGroup).addView(GamePieceView(baseContext, 2, false))
        (placeholder3 as ViewGroup).addView(GamePieceView(baseContext, 3, false))
        (parking      as ViewGroup).addView(GamePieceView(baseContext, -1, true))

        initTouchListeners() // Zum Auslösen des Drag&Drop Events
        playingField.setOnDragListener(createDragListener(false)) // Drop Event für Spielfeld
        parking.setOnDragListener(createDragListener(true)) // Drop Event fürs Parking
        newGame.visibility = when (game.isNewGameButtonVisible) {
            true -> View.VISIBLE
            false -> View.INVISIBLE
        }
        newGame.setOnClickListener(onNewGame())
        rotatingMode.setOnClickListener(onRotatingMode())
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

    /** Spielsteinbewegung starten */
    @SuppressLint("ClickableViewAccessibility") // click geht nicht, wir brauchen onTouch
    private fun initTouchListener(index: Int) {
        getGamePieceView(index).setOnClickListener(null)
        getGamePieceView(index).setOnTouchListener { it, _ ->
            try {
                val data = ClipData.newPlainText("index", index.toString())
                val tv = it as GamePieceView
                if (tv.gamePiece != null && !game.isGameOver) {
                    tv.startDragMode()
                    val dragShadowBuilder = MyDragShadowBuilder(tv, resources.displayMetrics.density)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // 7.0 Nougat API level 24
                        it.startDragAndDrop(data, dragShadowBuilder, it, 0)
                    } else { // for API level 19 (4.4. Kitkat)
                        @Suppress("DEPRECATION")
                        it.startDrag(data, dragShadowBuilder, it, 0)
                    }
                }
            } catch(e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "FT: " + e.message, Toast.LENGTH_LONG).show()
            }
            true
        }
        getGamePieceView(index).setDrehmodus(false)
    }

    /** Spielstein droppen */
    private fun createDragListener(targetIsParking: Boolean): View.OnDragListener {
        return View.OnDragListener { _, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                }
                DragEvent.ACTION_DROP -> drop(event, targetIsParking)
                DragEvent.ACTION_DRAG_ENTERED -> true
                DragEvent.ACTION_DRAG_LOCATION -> true
                DragEvent.ACTION_DRAG_EXITED -> true
                DragEvent.ACTION_DRAG_ENDED -> {
                    try {
                        // Da ich nicht weiß, welcher ausgeblent ist, blende ich einfach alle ein.
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

    private fun drop(event: DragEvent, targetIsParking: Boolean): Boolean {
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

    /** Turn rotating mode on/off */
    private fun onRotatingMode(): (View) -> Unit {
        return {
            if (!game.isGameOver) {
                if (game.toggleRotatingMode()) {
                    // Drehen ist an
                    rotatingMode.text = resources.getText(R.string.drehenAn)
                    rotatingMode.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.colorDrehmodus))
                    initClickListener(1)
                    initClickListener(2)
                    initClickListener(3)
                    initClickListener(-1)
                } else {
                    // Drehen ist aus
                    rotatingModeOff()
                }
            }
        }
    }

    /** Spielstein drehen */
    private fun initClickListener(index: Int) {
        getGamePieceView(index).setOnTouchListener(null)
        getGamePieceView(index).setOnClickListener {
            try {
                game.rotate(index)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Fc: " + e.message, Toast.LENGTH_LONG).show()
            }
        }
        getGamePieceView(index).setDrehmodus(true)
    }

    override fun rotatingModeOff() {
        rotatingMode.text = resources.getText(R.string.drehenAus)
        rotatingMode.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.colorHeadlineBackground))
        initTouchListeners()
    }

    private fun initTouchListeners() {
        initTouchListener(1)
        initTouchListener(2)
        initTouchListener(3)
        initTouchListener(-1)
    }

    override fun showScore(score: Int, delta: Int, gameOver: Boolean) {
        var text = getScoreText(score, gameOver)
        if (gameOver) {
            if (game.gameCanBeWon() && game.isWon) {
                playingField.soundService.youWon()
            } else {
                playingField.soundService.gameOver()
            }
        } else if (delta != 0) {
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
            -1 -> (parking      as ViewGroup).getChildAt(0) as GamePieceView
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

    override fun showToast(msg: String) {
        var msg2 = msg
        if (msg.startsWith("+")) {
            msg2 = msg.substring(1)
            playingField.soundService.youWon()
        }
        Toast.makeText(this, msg2, Toast.LENGTH_LONG).show()
    }

    override fun shake() {
        playingField.soundService.shake()
    }

    private fun per(): IPersistence {
        return Persistence(this)
    }
}
