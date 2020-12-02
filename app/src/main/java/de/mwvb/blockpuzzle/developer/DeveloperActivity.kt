package de.mwvb.blockpuzzle.developer

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import de.mwvb.blockpuzzle.Features
import de.mwvb.blockpuzzle.R
import de.mwvb.blockpuzzle.persistence.IPersistence
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
        backBtn.setOnClickListener { finish() }
        resetAllBtn.setOnClickListener { onResetAll() }
        openMap.setOnClickListener { onOpenMap() }
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()

        val per = per()
        val pa = PlanetAccess(per)
        val planet = pa.planet
        saveScore.isEnabled = (planet != null)
        saveOtherScore.isEnabled = (planet != null)
        score.setText("")
        ownername.text = " "
        otherScore.setText("")
        otherMoves.setText("")
        if (planet != null) {
            per.setGameID(planet)

            score.setText("" + per.loadScore())
            ownername.text = per.loadOwnerName()
            otherScore.setText("" + per.loadOwnerScore())
            otherMoves.setText("" + per.loadOwnerMoves())
            nextRound.setText("" + per.loadNextRound())
        }
    }

    private fun onSave() {
        val score = Integer.parseInt(score.text.toString())
        val per = per()
        per.saveScore(score)
        if (score <= 0) {
            per.saveMoves(0)
        }
        finish()
    }

    private fun onLiberated() {
        val per = per()
        val planet = PlanetAccess(per).planet
        planet!!.isOwner = true
        per.savePlanet(planet)
        finish()
    }

    private fun onConquered() {
        val per = per()
        val planet = PlanetAccess(per).planet
        planet!!.isOwner = false
        per.savePlanet(planet)
        per.saveScore(-9999)
        per.saveMoves(0)
        finish()
    }

    private fun onSaveOther() {
        val score = Integer.parseInt(otherScore.text.toString())
        val moves = Integer.parseInt(otherMoves.text.toString())
        val per = per()
        per.saveOwner(score, moves, "Detlef")
        finish()
    }

    private fun onSaveNextRound() {
        val index = Integer.parseInt(nextRound.text.toString())
        val per = per()
        per.saveNextRound(index)
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
        per().resetAll()
        System.exit(0)
    }

    private fun onOpenMap() {
        val pa = PlanetAccess(per())
        pa.planets.forEach { p -> p.isVisibleOnMap = true }
        pa.savePlanets()
        finish()
    }

    private fun per(): IPersistence {
        return Persistence(this)
    }
}