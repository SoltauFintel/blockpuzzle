package de.mwvb.blockpuzzle.game

import android.content.ClipDescription
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import de.mwvb.blockpuzzle.R
import de.mwvb.blockpuzzle.gamepiece.GamePiece
import de.mwvb.blockpuzzle.gamepiece.GamePieceTouchListener
import de.mwvb.blockpuzzle.gamepiece.GamePieceView
import de.mwvb.blockpuzzle.gamestate.GamePlayState
import de.mwvb.blockpuzzle.gamestate.Spielstand
import de.mwvb.blockpuzzle.gamestate.SpielstandDAO
import de.mwvb.blockpuzzle.global.AbstractDAO
import de.mwvb.blockpuzzle.global.BridgeActivity
import de.mwvb.blockpuzzle.global.GlobalData
import de.mwvb.blockpuzzle.global.InfoActivity
import de.mwvb.blockpuzzle.global.messages.MessageFactory
import de.mwvb.blockpuzzle.playingfield.Action
import de.mwvb.blockpuzzle.playingfield.IPlayingFieldView
import de.mwvb.blockpuzzle.playingfield.PlayingFieldView
import de.mwvb.blockpuzzle.playingfield.QPosition
import de.mwvb.blockpuzzle.playingfield.gravitation.ShakeService
import kotlinx.android.synthetic.main.activity_main.*
import java.text.DecimalFormat

/**
 * GameActivity
 *
 * It has still the name MainActivity because I'm not sure how to change the name
 * without destroying everything.
 */
class MainActivity : AppCompatActivity(), IGameView {
    private lateinit var gameEngine: GameEngine
    private lateinit var shakeService : ShakeService
    private var messages: MessageFactory? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // INIT PHASE
        if (Build.VERSION.SDK_INT >= 21) {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.navigationBackground)
        }

        // SUPER PHASE
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AbstractDAO.init(this)

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
        initNewGameButton()
    }

    // Activity reactivated
    override fun onResume() {
        super.onResume()
        try {
            initGameEngine()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, e.javaClass.toString() + ": " + e.message + "\n" + e.stackTrace[0].toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun initGameEngine() {
        gameEngine = GameEngineFactory().create(this)

        shakeService = ShakeService(gameEngine)
        shakeService.setActive(true)
        shakeService.initShakeDetection(this)

        newGame.visibility = when (gameEngine.isNewGameButtonVisible) {
            true  -> View.VISIBLE
            false -> View.INVISIBLE
        }
    }

    // Activity goes sleeping
    override fun onPause() {
        gameEngine.save()

        shakeService.setActive(false)

        super.onPause()
    }

    /** Drag or rotate game piece */
    private fun initTouchListener(index: Int) {
        getGamePieceView(index).setOnTouchListener(object : GamePieceTouchListener(index, resources) {
            override fun up(view: View?, event: MotionEvent?) {
                getGamePieceView(index).endDragMode()
                gameEngine.rotate(index)
            }

            override fun isDragAllowed(): Boolean {
                return !gameEngine.isLostGame && gameEngine.isDragAllowed
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
                        getGamePieceView(-1).onDragEnter()
                    }
                    true
                }
                DragEvent.ACTION_DRAG_LOCATION -> true
                DragEvent.ACTION_DRAG_EXITED -> {
                    if (targetIsParking) {
                        getGamePieceView(-1).onDragLeave()
                    }
                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    try {
                        if (targetIsParking) {
                            getGamePieceView(-1).onDragLeave()
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
            gameEngine.dispatch(targetIsParking, DropActionModel(index, gamePiece, xy))

            if (gameEngine.rebuild) {
                gameEngine.rebuild = false;
                gameEngine.isDragAllowed = false // will be set to true in GameEngine construction
                @Suppress("DEPRECATION")
                Handler().postDelayed({ initGameEngine() }, 1200)
            }
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
        val br = PlayingFieldView.w / gameEngine.blocks
        x /= br
        y = y / br - 2 - (gamePiece.maxY - gamePiece.minY)
        return QPosition(x.toInt(), y.toInt())
    }

    private fun initNewGameButton() {
        newGame.setOnClickListener {
            if (gameEngine.isLostGame || gameEngine.lessScore()) {
                startNewGame()
            } else {
                val dialog: AlertDialog.Builder = AlertDialog.Builder(this)
                dialog.setTitle(resources.getString(R.string.startNewGame))
                dialog.setPositiveButton(resources.getString(android.R.string.ok)) { _, _ -> startNewGame() }
                dialog.setNegativeButton(resources.getString(android.R.string.cancel), null)
                dialog.show()
            }
        }
    }

    private fun startNewGame() {
        SpielstandDAO().deleteOldGame()
        initGameEngine()
    }

    override fun getSpecialAction(specialState: Int): Action {
        if (specialState == 2) { // Death Star destroyed
            return Action {
                val intent = Intent(this, InfoActivity::class.java)
                val args = Bundle()
                args.putInt(InfoActivity.MODE, InfoActivity.BACK_FROM_DEATH_STAR)
                intent.putExtras(args)
                startActivity(intent)
            }
        }
        return Action {}
    }

    // TODO Das ist eher Fachlogik. inkl. getScoreText() | R.string.moves|score2 wird auch woanders genutzt. (Bubble infoText)
    override fun showScoreAndMoves(ss: Spielstand) {
        var text = getScoreText(ss)
        if (ss.delta > 0) {
            text += " (" + DecimalFormat("+#,##0").format(ss.delta) + ")"
        }
        info.text = text

        infoDisplay.text = when (ss.moves) {
            0    -> ""
            1    -> DecimalFormat("#,##0").format(ss.moves) + " " + resources.getString(R.string.move)
            else -> DecimalFormat("#,##0").format(ss.moves) + " " + resources.getString(R.string.moves)
        }
    }

    private fun getScoreText(ss: Spielstand): String {
        val ret = when (ss.state) {
            GamePlayState.LOST_GAME -> if (ss.score == 1) R.string.gameOverScore1 else R.string.gameOverScore2
            GamePlayState.WON_GAME  -> if (ss.score == 1) R.string.winScore1 else R.string.winScore2
            else/*PLAYING*/         -> if (ss.score == 1) R.string.score1 else R.string.score2
        }
        return resources.getString(ret).replace("XX", DecimalFormat("#,##0").format(ss.score))
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

    override fun onBackPressed() {
        if (GlobalData.get().todesstern == 1) {
            if (gameEngine.isDragAllowed) { // during wait time back pressing is not allowed, game state could get unstable
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

    override fun shake() {
        playingField.soundService.shake()
    }

    override fun playSound(number: Int) {
        playingField.soundService.playSound(number)
    }

    override fun getMessages(): MessageFactory {
        if (messages == null) {
            messages = MessageFactory(this)
        }
        return messages!!
    }
}
