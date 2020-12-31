package de.mwvb.blockpuzzle.developer

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import de.mwvb.blockpuzzle.Features
import de.mwvb.blockpuzzle.R
import de.mwvb.blockpuzzle.persistence.Persistence
import de.mwvb.blockpuzzle.persistence.PlanetAccess
import kotlinx.android.synthetic.main.activity_developer.*

class DeveloperActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_developer)

        if (!Features.developerMode) throw RuntimeException("Not allowed")

        saveScore.setOnClickListener { onSave() }
        liberated.setOnClickListener { onLiberated() }
        conquered.setOnClickListener { onConquered() }
        saveOtherScore.setOnClickListener { onSaveOther() }
        saveNextRound.setOnClickListener { onSaveNextRound() }
        saveTodayDate.setOnClickListener { onSaveTodayDate() }
        deleteTrophies.setOnClickListener { onDeleteTrophies() }

        resetAllBtn.setOnClickListener { onResetAll() }
        openMap.setOnClickListener { onOpenMap() }
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()

        val pa = pa()
        val planet = pa.planet

        saveScore.isEnabled = (planet != null)
        liberated.isEnabled = (planet != null)
        conquered.isEnabled = (planet != null)
        saveOtherScore.isEnabled = (planet != null)
        saveNextRound.isEnabled = (planet != null)

        score.setText("")
        ownername.text = " "
        otherScore.setText("")
        otherMoves.setText("")
        if (planet != null) {
            score.setText("" + pa.persistence.loadScore())
            ownername.text = pa.persistence.loadOwnerName()
            otherScore.setText("" + pa.persistence.loadOwnerScore())
            otherMoves.setText("" + pa.persistence.loadOwnerMoves())
            nextRound.setText("" + pa.persistence.loadNextRound())
        }
        todayDate.setText(Persistence(this).loadTodayDate())
    }

    private fun onSave() {
        val pa = pa()
        val score = Integer.parseInt(score.text.toString())
        pa.persistence.saveScore(score)
        if (score <= 0) {
            pa.persistence.saveMoves(0)
        }
        finish()
    }

    private fun onLiberated() {
        val pa = pa()
        pa.planet.isOwner = true
        pa.persistence.savePlanet(pa.planet)
        finish()
    }

    private fun onConquered() {
        val pa = pa()
        pa.planet.isOwner = false
        pa.persistence.savePlanet(pa.planet)
        pa.persistence.saveScore(-1)
        pa.persistence.saveMoves(0)
        finish()
    }

    private fun onSaveOther() {
        val pa = pa()
        val score = Integer.parseInt(otherScore.text.toString())
        val moves = Integer.parseInt(otherMoves.text.toString())
        pa.persistence.saveOwner(score, moves, "Detlef")
        finish()
    }

    private fun onSaveNextRound() {
        val pa = pa()
        val index = Integer.parseInt(nextRound.text.toString())
        pa.persistence.saveNextRound(index)
        finish()
    }

    private fun onSaveTodayDate() {
        Persistence(this).saveTodayDate(todayDate.text.toString())
        finish()
    }

    private fun onDeleteTrophies() {
        val dialog: AlertDialog.Builder = AlertDialog.Builder(this)
        dialog.setTitle("Wirklich alle Trophäen auf 0 setzen?")
        dialog.setPositiveButton(resources.getString(android.R.string.ok)) { _, _ -> deleteAllTrophies() }
        dialog.setNegativeButton(resources.getString(android.R.string.cancel), null)
        dialog.show()
    }

    private fun deleteAllTrophies() {
        val pa = pa()
        (pa.persistence as Persistence).clearAllTrophies(pa.planet)
        finish()
    }

    private fun onResetAll() {
        val dialog: AlertDialog.Builder = AlertDialog.Builder(this)
        dialog.setTitle("ACHTUNG: Wirklich alle Daten löschen?")
        dialog.setPositiveButton(resources.getString(android.R.string.ok)) { _, _ -> onReallyResetAll() }
        dialog.setNegativeButton(resources.getString(android.R.string.cancel), null)
        dialog.show()
    }

    private fun onReallyResetAll() {
        pa().persistence.resetAll()
        System.exit(0)
    }

    private fun onOpenMap() {
        val pa = pa()
        pa.spaceObjects.forEach { p -> p.isVisibleOnMap = true }
        pa.savePlanets()
        finish()
    }

    private fun pa(): PlanetAccess {
        val per = Persistence(this)
        val pa = PlanetAccess(per)
        if (pa.planet != null) {
            per.setGameID(pa.planet)
        }
        return pa
    }
}