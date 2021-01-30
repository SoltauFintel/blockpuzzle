package de.mwvb.blockpuzzle.global.developer

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import de.mwvb.blockpuzzle.R
import de.mwvb.blockpuzzle.game.GameEngineFactory
import de.mwvb.blockpuzzle.gamestate.Spielstand
import de.mwvb.blockpuzzle.gamestate.SpielstandDAO
import de.mwvb.blockpuzzle.gamestate.TrophyService
import de.mwvb.blockpuzzle.global.AbstractDAO
import de.mwvb.blockpuzzle.global.Features
import de.mwvb.blockpuzzle.planet.IPlanet
import de.mwvb.blockpuzzle.planet.SpaceObjectStateDAO
import de.mwvb.blockpuzzle.planet.SpaceObjectStateService
import kotlinx.android.synthetic.main.activity_developer.*

class DeveloperActivity : AppCompatActivity() {
    private val spielstandDAO = SpielstandDAO()
    private val sosDAO = SpaceObjectStateDAO()
    private var planet: IPlanet? = null
    private var index: Int = 0
    private var ss: Spielstand? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_developer)

        if (!Features.developerMode) throw RuntimeException("Not allowed")
        AbstractDAO.init(this)

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

        load()

        saveScore.isEnabled = (planet != null)
        liberated.isEnabled = (planet != null)
        conquered.isEnabled = (planet != null)
        saveOtherScore.isEnabled = (planet != null)
        saveNextRound.isEnabled = (planet != null)

        score.setText("")
        ownername.text = " "
        otherScore.setText("")
        otherMoves.setText("")
        if (ss != null) {
            score.setText("" + ss!!.score)
            ownername.text = ss!!.ownerName
            otherScore.setText("" + ss!!.ownerScore)
            otherMoves.setText("" + ss!!.ownerMoves)
            nextRound.setText("" + ss!!.nextRound)
        }
        todayDate.setText(DeveloperService().loadToday())
    }

    private fun load() {
        planet = GameEngineFactory().getPlanet()
        if (planet == null) {
            index = 0
            ss = null
        } else {
            index = planet!!.gameDefinitions.indexOf(planet!!.selectedGame)
            ss = SpielstandDAO().load(planet)
        }
    }

    private fun save() {
        spielstandDAO.save(planet, index, ss)
    }

    private fun onSave() {
        if (ss != null) {
            ss!!.score = Integer.parseInt(score.text.toString())
            if (ss!!.score <= 0) {
                ss!!.moves = 0
            }
            save()
            finish()
        }
    }

    private fun onLiberated() {
        if (planet != null) {
            val sos = sosDAO.load(planet)
            sos.isOwner = true
            sosDAO.save(planet, sos)
            finish()
        }
    }

    private fun onConquered() {
        if (ss != null) {
            val sos = sosDAO.load(planet)
            sos.isOwner = false
            sosDAO.save(planet, sos)
            ss!!.unsetScore()
            ss!!.moves = 0
            save()
            finish()
        }
    }

    private fun onSaveOther() {
        if (ss != null) {
            ss!!.ownerScore = Integer.parseInt(otherScore.text.toString())
            ss!!.ownerMoves = Integer.parseInt(otherMoves.text.toString())
            ss!!.ownerName = "Detlef"
            save()
            finish()
        }
    }

    private fun onSaveNextRound() {
        if (ss != null) {
            ss!!.nextRound =  Integer.parseInt(nextRound.text.toString())
            save()
            finish()
        }
    }

    private fun onSaveTodayDate() {
        DeveloperService().saveToday(todayDate.text.toString())
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
        TrophyService().clear()
        finish()
    }

    private fun onOpenMap() {
        SpaceObjectStateService().openMap()
        finish()
    }

    private fun onResetAll() {
        val dialog: AlertDialog.Builder = AlertDialog.Builder(this)
        dialog.setTitle("ACHTUNG: Wirklich ALLE Daten löschen?")
        dialog.setPositiveButton(resources.getString(android.R.string.ok)) { _, _ -> DeveloperService().resetAll() }
        dialog.setNegativeButton(resources.getString(android.R.string.cancel), null)
        dialog.show()
    }
}