package de.mwvb.blockpuzzle.developer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import de.mwvb.blockpuzzle.Features
import de.mwvb.blockpuzzle.GameState
import de.mwvb.blockpuzzle.R
import kotlinx.android.synthetic.main.activity_developer.*
import kotlinx.android.synthetic.main.activity_player_name.*
import java.lang.RuntimeException

class DeveloperActivity : AppCompatActivity() {
    // TODO GamePieceSet Index hochsetzen (um das Spielende zu simulieren)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_developer)

        if (!Features.developerMode) throw RuntimeException("Not allowed")

        saveScore.setOnClickListener { onSave() }
        liberated.setOnClickListener { onLiberated() }
        conquered.setOnClickListener { onConquered() }
        saveOtherScore.setOnClickListener { onSaveOther() }
        backBtn.setOnClickListener { finish() }
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
}