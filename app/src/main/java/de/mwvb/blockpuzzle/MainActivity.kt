package de.mwvb.blockpuzzle

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipDescription
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import de.mwvb.blockpuzzle.logic.FilledRows
import de.mwvb.blockpuzzle.logic.Game
import de.mwvb.blockpuzzle.logic.spielstein.Spielstein
import de.mwvb.blockpuzzle.view.MyDragShadowBuilder
import de.mwvb.blockpuzzle.view.SpielfeldView
import de.mwvb.blockpuzzle.view.TeilView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val game = Game(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)

        spielfeld.setGame(game)

        (placeholder1 as ViewGroup).addView(TeilView(baseContext, false))
        (placeholder2 as ViewGroup).addView(TeilView(baseContext, false))
        (placeholder3 as ViewGroup).addView(TeilView(baseContext, false))
        (parking as ViewGroup).addView(TeilView(baseContext, true))

        initDragAndDrop()
        neuesSpiel.setOnClickListener {
            // TODO Sicherheitsabfrage wenn Punkte > 0
            game.newGame()
        }
        drehmodus.setOnClickListener {
            if (!game.isGameOver) {
                if (game.toggleDrehmodus()) {
                    drehmodus.text = resources.getText(R.string.drehenAn)
                    initClickListener(1)
                    initClickListener(2)
                    initClickListener(3)
                    initClickListener(-1)
                } else {
                    drehmodus.text = resources.getText(R.string.drehenAus)
                    initTouchListener(1)
                    initTouchListener(2)
                    initTouchListener(3)
                    initTouchListener(-1)
                }
            }
        }

        game.newGame() // start first game
    }

    private fun initDragAndDrop() {
        // Zum Auslösen des Drag&Drop Events:
        initTouchListener(1)
        initTouchListener(2)
        initTouchListener(3)
        initTouchListener(-1)

        // Drop Events:
        spielfeld.setOnDragListener(createDragListener(false))
        parking.setOnDragListener(createDragListener(true))
    }

    @SuppressLint("ClickableViewAccessibility") // click geht nicht, wir brauchen onTouch
    private fun initTouchListener(index: Int) {
        getTeilView(index).setOnClickListener(null)
        getTeilView(index).setOnTouchListener { it, _ ->
            val data = ClipData.newPlainText("index", index.toString())
            val tv = it as TeilView
            if (tv.spielstein != null && !game.isGameOver) {
                tv.startDragMode()
                val dragShadowBuilder = MyDragShadowBuilder(tv, resources.displayMetrics.density)
                it.startDragAndDrop(data, dragShadowBuilder, it, 0)
            }
            true
        }
        getTeilView(index).setDrehmodus(false)
    }

    private fun initClickListener(index: Int) {
        getTeilView(index).setOnTouchListener(null)
        getTeilView(index).setOnClickListener {
            val tv = it as TeilView
            if (!game.isGameOver) {
                tv.rotate()
                game.moveImpossible(index)
            }
        }
        getTeilView(index).setDrehmodus(true)
    }

    private fun createDragListener(targetIsParking: Boolean): View.OnDragListener {
        return View.OnDragListener { _, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                }
                DragEvent.ACTION_DROP -> {
                    val item = event.clipData.getItemAt(0)
                    val index: Int = item.text.toString().toInt()
                    val spielstein = getTeil(index)

                    // geg.: px, ges.: SpielfeldView Koordinaten (0 - 9)
                    val f = resources.displayMetrics.density
                    var x = event.x / f // px -> dp
                    var y = event.y / f
                    // jetzt in Spielfeld Koordinaten umrechnen
                    val br = SpielfeldView.w / Game.blocks
                    x /= br
                    y = y / br - 2 - (spielstein!!.maxY - spielstein.minY)

                    game.dispatch(targetIsParking, index, spielstein, x, y)
                    true
                }
                DragEvent.ACTION_DRAG_ENTERED -> true
                DragEvent.ACTION_DRAG_LOCATION -> true
                DragEvent.ACTION_DRAG_EXITED -> true
                DragEvent.ACTION_DRAG_ENDED -> {
                    // Da ich nicht weiß, welcher ausgeblent ist, blende ich einfach alle ein.
                    getTeilView(1).endDragMode()
                    getTeilView(2).endDragMode()
                    getTeilView(3).endDragMode()
                    getTeilView(-1).endDragMode()
                    true
                }
                else -> false
            }
        }
    }

    fun updatePunkte() {
        if (game.isGameOver) {
            info.text = resources.getQuantityString(R.plurals.punkteGameOver, game.punkte, game.punkte)
            spielfeld.playGameOverSound()
        } else {
            // Man muss bei Plurals die Anzahl 2x übergeben.
            info.text = resources.getQuantityString(R.plurals.punkte, game.punkte, game.punkte)
        }
    }

    fun drawSpielfeld() {
        spielfeld.draw()
    }

    fun clearRows(filledRows: FilledRows) {
        spielfeld.clearRows(filledRows)
    }

    fun setTeil(index: Int, teil: Spielstein?) {
        val tv = getTeilView(index)
        tv.endDragMode()
        tv.isGrey = false
        tv.spielstein = teil // macht draw()
    }

    fun getTeil(index: Int): Spielstein? {
        return getTeilView(index).spielstein
    }

    fun grey(index: Int, grey: Boolean) {
        val tv = getTeilView(index)
        tv.isGrey = grey
        tv.draw()
    }

    private fun getTeilView(index: Int): TeilView {
        return when (index) {
             1 -> (placeholder1 as ViewGroup).getChildAt(0) as TeilView
             2 -> (placeholder2 as ViewGroup).getChildAt(0) as TeilView
             3 -> (placeholder3 as ViewGroup).getChildAt(0) as TeilView
            -1 -> (parking      as ViewGroup).getChildAt(0) as TeilView
            else -> throw RuntimeException()
        }
    }

    fun gehtNicht() {
        Toast.makeText(this, R.string.gehtNicht, Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        // do nothing
        // Vorher war es so, dass dann der Spielstand verloren geht. Das will ich so erstmal verhindern.

        // Wir spielen ein Sound ab, damit der User wenigstens merkt, dass die Taste nicht kaputt ist.
        // Man könnte aber auch die Anwendung minimieren.
        spielfeld.playCrunchSound()
    }
}
