package de.mwvb.blockpuzzle.developer

import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import de.mwvb.blockpuzzle.Features
import de.mwvb.blockpuzzle.GameState
import de.mwvb.blockpuzzle.GameState.cluster
import de.mwvb.blockpuzzle.R
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

    override fun onResume() {
        super.onResume()

        val planet = GameState.getPlanet()
        saveScore.isEnabled = (planet != null)
        saveOtherScore.isEnabled = (planet != null)
        score.setText("")
        ownername.setText(" ")
        otherScore.setText("")
        otherMoves.setText("")
        if (planet != null) {
            val per = GameState.persistence!!
            per.setGameID(planet)

            score.setText("" + per.loadScore())
            ownername.setText(per.loadOwnerName())
            otherScore.setText("" + per.loadOwnerScore())
            otherMoves.setText("" + per.loadOwnerMoves())
            nextRound.setText("" + per.loadNextRound())
        }
    }

    private fun onSave() {
        val score = Integer.parseInt(score.text.toString())
        val per = GameState.persistence!!
        per.saveScore(score)
        if (score <= 0) {
            per.saveMoves(0)
        }
        finish()
    }

    private fun onLiberated() {
        val per = GameState.persistence!!
        val planet = GameState.getPlanet()
        planet!!.isOwner = true
        per.savePlanet(planet)
        finish()
    }

    private fun onConquered() {
        val per = GameState.persistence!!
        val planet = GameState.getPlanet()
        planet!!.isOwner = false
        per.savePlanet(planet)
        per.saveScore(-9999)
        per.saveMoves(0)
        finish()
    }

    private fun onSaveOther() {
        val score = Integer.parseInt(otherScore.text.toString())
        val moves = Integer.parseInt(otherMoves.text.toString())
        val per = GameState.persistence!!
        per.saveOwner(score, moves, "Detlef")
        finish()
    }

    private fun onSaveNextRound() {
        val index = Integer.parseInt(nextRound.text.toString())
        val per = GameState.persistence!!
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
        GameState.persistence!!.resetAll()
        System.exit(0)
    }

    private fun onOpenMap() {
        GameState.cluster.planets.forEach { p -> p.isVisibleOnMap = true }
        finish()
    }
}