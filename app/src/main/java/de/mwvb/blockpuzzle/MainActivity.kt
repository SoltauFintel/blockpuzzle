package de.mwvb.blockpuzzle

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.DragEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import de.mwvb.blockpuzzle.logic.Action
import de.mwvb.blockpuzzle.logic.FilledRows
import de.mwvb.blockpuzzle.logic.Game
import de.mwvb.blockpuzzle.logic.QPosition
import de.mwvb.blockpuzzle.logic.spielstein.Spielstein
import de.mwvb.blockpuzzle.view.MyDragShadowBuilder
import de.mwvb.blockpuzzle.view.SpielfeldView
import de.mwvb.blockpuzzle.view.TeilView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val game = Game(this)
    private var pref: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pref = getSharedPreferences("spielstand", Context.MODE_PRIVATE)
        game.setStorage(pref)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)

        spielfeld.setGame(game)

        (placeholder1 as ViewGroup).addView(TeilView(baseContext, 1, false, pref))
        (placeholder2 as ViewGroup).addView(TeilView(baseContext, 2,false, pref))
        (placeholder3 as ViewGroup).addView(TeilView(baseContext, 3,false, pref))
        (parking      as ViewGroup).addView(TeilView(baseContext, -1,true, pref))

        initTouchListeners() // Zum Auslösen des Drag&Drop Events
        spielfeld.setOnDragListener(createDragListener(false)) // Drop Event für Spielfeld
        parking.setOnDragListener(createDragListener(true)) // Drop Event fürs Parking

        neuesSpiel.setOnClickListener {
            if (game.isGameOver || game.punkte < 10) {
                game.newGame()
            } else {
                val dialog: AlertDialog.Builder = AlertDialog.Builder(this)
                dialog.setTitle("Neues Spiel starten?")
                dialog.setPositiveButton("OK") { _, _ -> game.newGame() }
                dialog.setNegativeButton("Cancel", null)
                dialog.show()
            }
        }
        drehmodus.setOnClickListener {
            if (!game.isGameOver) {
                if (game.toggleDrehmodus()) {
                    // Drehen ist an
                    drehmodus.text = resources.getText(R.string.drehenAn)
                    drehmodus.setBackgroundColor(resources.getColor(R.color.colorDrehmodus))
                    initClickListener(1)
                    initClickListener(2)
                    initClickListener(3)
                    initClickListener(-1)
                } else {
                    // Drehen ist aus
                    drehmodusAus()
                }
            }
        }

        game.initGame()
    }

    /** Spielsteinbewegung starten */
    @SuppressLint("ClickableViewAccessibility") // click geht nicht, wir brauchen onTouch
    private fun initTouchListener(index: Int) {
        getSpielsteinView(index).setOnClickListener(null)
        getSpielsteinView(index).setOnTouchListener { it, _ ->
            try {
                val data = ClipData.newPlainText("index", index.toString())
                val tv = it as TeilView
                if (tv.spielstein != null && !game.isGameOver) {
                    tv.startDragMode()
                    val dragShadowBuilder = MyDragShadowBuilder(tv, resources.displayMetrics.density)
                    it.startDragAndDrop(data, dragShadowBuilder, it, 0)
                }
            } catch(e: Exception) {
                Toast.makeText(this, "FT: " + e.message, Toast.LENGTH_LONG).show()
            }
            true
        }
        getSpielsteinView(index).setDrehmodus(false)
    }

    /** Spielstein drehen */
    private fun initClickListener(index: Int) {
        getSpielsteinView(index).setOnTouchListener(null)
        getSpielsteinView(index).setOnClickListener {
            try {
                val tv = it as TeilView
                if (!game.isGameOver) {
                    tv.rotate()
                    game.moveImpossible(index)
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Fc: " + e.message, Toast.LENGTH_LONG).show()
            }
        }
        getSpielsteinView(index).setDrehmodus(true)
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
                        getSpielsteinView(1).endDragMode()
                        getSpielsteinView(2).endDragMode()
                        getSpielsteinView(3).endDragMode()
                        getSpielsteinView(-1).endDragMode()
                    } catch (e: Exception) {
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
            val spielstein = getSpielstein(index)!!

            // geg.: px, ges.: SpielfeldView Koordinaten (0 - 9)
            val xy = calculateSpielfeldKoordinate(event, spielstein)

            game.dispatch(targetIsParking, index, spielstein, xy)
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
        return true
    }

    private fun calculateSpielfeldKoordinate(event: DragEvent, spielstein: Spielstein): QPosition {
        val f = resources.displayMetrics.density
        var x = event.x / f // px -> dp
        var y = event.y / f
        // jetzt in Spielfeld Koordinaten umrechnen
        val br = SpielfeldView.w / Game.blocks
        x /= br
        y = y / br - 2 - (spielstein.maxY - spielstein.minY)
        return QPosition(x.toInt(), y.toInt())
    }

    fun drehmodusAus() {
        drehmodus.text = resources.getText(R.string.drehenAus)
        drehmodus.setBackgroundColor(resources.getColor(R.color.colorNormal))
        initTouchListeners()
    }

    private fun initTouchListeners() {
        initTouchListener(1)
        initTouchListener(2)
        initTouchListener(3)
        initTouchListener(-1)
    }

    // TODO delta auch persistieren und nach Programmneustart korrekt anzeigen
    fun updatePunkte(delta: Int) {
        if (game.isGameOver) {
            info.text =
                resources.getQuantityString(R.plurals.punkteGameOver, game.punkte, game.punkte)
            spielfeld.playGameOverSound()
        } else {
            // Man muss bei Plurals die Anzahl 2x übergeben.
            var t = resources.getQuantityString(R.plurals.punkte, game.punkte, game.punkte)
            if (delta > 0) {
                t += " (+$delta)";
            } else if (delta < 0) {
                t += " ($delta)";
            }
            info.text = t
        }
    }

    fun drawSpielfeld() {
        spielfeld.draw()
    }

    fun clearRows(filledRows: FilledRows, action: Action) {
        spielfeld.clearRows(filledRows, action)
    }

    fun setSpielstein(index: Int, teil: Spielstein?, write: Boolean) {
        val tv = getSpielsteinView(index)
        tv.endDragMode()
        tv.isGrey = false
        tv.spielstein = teil // macht draw()
        if (write) {
            tv.write()
        }
    }

    fun getSpielstein(index: Int): Spielstein? {
        return getSpielsteinView(index).spielstein
    }

    fun grey(index: Int, grey: Boolean) {
        val tv = getSpielsteinView(index)
        tv.isGrey = grey
        tv.draw()
    }

    private fun getSpielsteinView(index: Int): TeilView {
        return when (index) {
             1 -> (placeholder1 as ViewGroup).getChildAt(0) as TeilView
             2 -> (placeholder2 as ViewGroup).getChildAt(0) as TeilView
             3 -> (placeholder3 as ViewGroup).getChildAt(0) as TeilView
            -1 -> (parking      as ViewGroup).getChildAt(0) as TeilView
            else -> throw RuntimeException()
        }
    }

    /*fun setInfoanzeigeText(text: String) {
        infoanzeige.text = text
    }*/

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

    fun restoreSpielsteinViews() {
        // SpielsteinViews 1-3 und Parking restoren
        getSpielsteinView(1).read();
        getSpielsteinView(2).read();
        getSpielsteinView(3).read();
        getSpielsteinView(-1).read();
    }
}
