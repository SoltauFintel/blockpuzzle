package de.mwvb.blockpuzzle

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.view.DragEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import de.mwvb.blockpuzzle.logic.*
import de.mwvb.blockpuzzle.logic.spielstein.GamePiece
import de.mwvb.blockpuzzle.view.GamePieceView
import de.mwvb.blockpuzzle.view.IGameView
import de.mwvb.blockpuzzle.view.MyDragShadowBuilder
import de.mwvb.blockpuzzle.view.PlayingFieldView
import kotlinx.android.synthetic.main.activity_main.*
import java.text.DecimalFormat

/**
 * GameActivity
 *
 * It has still the name MainActivity because I'm not sure how to change the name
 * without destroying everything.
 */
class MainActivity : AppCompatActivity(), IGameView {
    private val game = Game(this)
    private var persistence: Persistence? = null

    /* put this into your activity class */
    private var mSensorManager: SensorManager? = null
    private var mAccel = 0f // acceleration apart from gravity
    private var mAccelCurrent = 0f// current acceleration including gravity
    private var mAccelLast = 0f // last acceleration including gravity
    /** Feature toggle. false: auto-gravity, true: player has to shake his phone to start gravity */
    val withGravity = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        println("GAME MODE: " + intent.getStringExtra("gameMode"))

        persistence = Persistence(this)
        game.setPersistence(persistence)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)

        playingField.setGame(game)

        (placeholder1 as ViewGroup).addView(GamePieceView(baseContext, 1, false, persistence))
        (placeholder2 as ViewGroup).addView(GamePieceView(baseContext, 2,false, persistence))
        (placeholder3 as ViewGroup).addView(GamePieceView(baseContext, 3,false, persistence))
        (parking      as ViewGroup).addView(GamePieceView(baseContext, -1,true, persistence))

        initTouchListeners() // Zum Auslösen des Drag&Drop Events
        playingField.setOnDragListener(createDragListener(false)) // Drop Event für Spielfeld
        parking.setOnDragListener(createDragListener(true)) // Drop Event fürs Parking
        newGame.setOnClickListener(onNewGame())
        rotatingMode.setOnClickListener(onRotatingMode())
        initShakeDetection()

        game.setSoundService(playingField.soundService)
        game.initGame()
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
            val gamePiece = getGamePiece(index)!!

            // geg.: px, ges.: SpielfeldView Koordinaten (0 - 9)
            val xy = calculatePlayingFieldCoordinates(event, gamePiece)

            game.dispatch(targetIsParking, index, gamePiece, xy)
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
                    rotatingMode.setBackgroundColor(resources.getColor(R.color.colorDrehmodus))
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
                val tv = it as GamePieceView
                if (!game.isGameOver) {
                    tv.rotate()
                    game.moveImpossible(index)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Fc: " + e.message, Toast.LENGTH_LONG).show()
            }
        }
        getGamePieceView(index).setDrehmodus(true)
    }

    private fun initShakeDetection() {
        if (withGravity) {
            mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager?;
            mSensorManager!!.registerListener(
                mSensorListener,
                mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL
            );
            mAccel = 0.00f;
            mAccelCurrent = SensorManager.GRAVITY_EARTH;
            mAccelLast = SensorManager.GRAVITY_EARTH;
        }
    }

    private val mSensorListener: SensorEventListener = object : SensorEventListener { // https://stackoverflow.com/a/2318356/3478021
        override fun onSensorChanged(se: SensorEvent) {
            val x = se.values[0]
            val y = se.values[1]
            val z = se.values[2]
            mAccelLast = mAccelCurrent
            mAccelCurrent = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta = mAccelCurrent - mAccelLast
            mAccel = mAccel * 0.9f + delta // perform low-cut filter
            if (mAccel > 14) game.shaked() // value is how hard you have to shake
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) { //
        }
    }

    // Activity goes sleeping
    override fun onPause() {
        if (withGravity) {
            mSensorManager!!.unregisterListener(mSensorListener)
        }
        super.onPause()
    }

    // Activity reactivated
    override fun onResume() {
        super.onResume()
        if (withGravity) {
            mSensorManager!!.registerListener(
                mSensorListener,
                mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    override fun rotatingModeOff() {
        rotatingMode.text = resources.getText(R.string.drehenAus)
        rotatingMode.setBackgroundColor(resources.getColor(R.color.colorNormal))
        initTouchListeners()
    }

    private fun initTouchListeners() {
        initTouchListener(1)
        initTouchListener(2)
        initTouchListener(3)
        initTouchListener(-1)
    }

    // TODO delta auch persistieren und nach Programmneustart korrekt anzeigen
    override fun updateScore(delta: Int) {
        var text = getScoreText(game.score, game.isGameOver)
        if (game.isGameOver) {
            playingField.soundService.gameOver()
        } else {
            // TODO Tausenderpunkt für delta
            if (delta > 0) {
                text += " (+$delta)";
            } else if (delta < 0) {
                text += " ($delta)";
            }
        }
        info.text = text
    }

    private fun getScoreText(score: Int, gameOver: Boolean): String {
        val ret: String
        if (gameOver) {
            if (score == 1) {
                ret = resources.getString(R.string.gameOverScore1)
            } else {
                ret = resources.getString(R.string.gameOverScore2)
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

    override fun drawPlayingField() {
        playingField.draw()
    }

    override fun clearRows(filledRows: FilledRows, action: Action?) {
        playingField.clearRows(filledRows, action)
    }

    override fun setGamePiece(index: Int, teil: GamePiece?, write: Boolean) {
        val tv = getGamePieceView(index)
        tv.endDragMode()
        tv.isGrey = false
        tv.gamePiece = teil // macht draw()
        if (write) {
            tv.write()
        }
    }

    override fun getGamePiece(index: Int): GamePiece? {
        return getGamePieceView(index).gamePiece
    }

    override fun grey(index: Int, grey: Boolean) {
        val tv = getGamePieceView(index)
        tv.isGrey = grey
        tv.draw()
    }

    private fun getGamePieceView(index: Int): GamePieceView {
        return when (index) {
             1 -> (placeholder1 as ViewGroup).getChildAt(0) as GamePieceView
             2 -> (placeholder2 as ViewGroup).getChildAt(0) as GamePieceView
             3 -> (placeholder3 as ViewGroup).getChildAt(0) as GamePieceView
            -1 -> (parking      as ViewGroup).getChildAt(0) as GamePieceView
            else -> throw RuntimeException()
        }
    }

    override fun doesNotWork() {
        Toast.makeText(this, R.string.gehtNicht, Toast.LENGTH_SHORT).show()
        playingField.soundService.doesNotWork()
    }

/*
    override fun onBackPressed() {
        // do nothing
        // Vorher war es so, dass dann der Spielstand verloren geht. Das will ich so erstmal verhindern.

        // Wir spielen ein Sound ab, damit der User wenigstens merkt, dass die Taste nicht kaputt ist.
        // Man könnte aber auch die Anwendung minimieren.
        playingField.soundService.backPressed(game.isGameOver)
    }
*/

    override fun restoreGamePieceViews() {
        // restore GamePieceViews 1-3 und Parking area
        getGamePieceView(1).read();
        getGamePieceView(2).read();
        getGamePieceView(3).read();
        getGamePieceView(-1).read();
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

    override fun getWithGravityOption(): Boolean {
        return withGravity
    }
}
