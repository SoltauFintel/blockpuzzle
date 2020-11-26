package de.mwvb.blockpuzzle

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import de.mwvb.blockpuzzle.developer.DeveloperActivity
import de.mwvb.blockpuzzle.game.GameInfoService
import de.mwvb.blockpuzzle.planet.GiantPlanet
import de.mwvb.blockpuzzle.planet.IPlanet
import de.mwvb.blockpuzzle.planet.Moon
import kotlinx.android.synthetic.main.activity_planet.*
import java.text.DecimalFormat

class PlanetActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_planet)

        if (!Features.developerMode) developer.visibility = View.INVISIBLE

        if (GameState.getPlanet() == null || GameState.getPlanet()!!.selectedGame == null) {
            finish()
            return
        }

        play.setOnClickListener { onPlay() }
        newGame.setOnClickListener { onNewGame() } // Spieler möchte Spiel neustarten
        dataexchange.setOnClickListener { startActivity(Intent(this, DataMarketActivity::class.java)) }
        bank.setOnClickListener {} // Dailys: tägliche LibGuard-Gebühren eintreiben
        searchForArtefacts.setOnClickListener {} // Außenteam entsenden
        buy.setOnClickListener {} // Sternenkarte des Quadranten, besseren Antrieb, Deuterium, Schiffsreparatur, bessere Drehfeder, Committer, ...
        backToShip.setOnClickListener { finish() } // auf gleichen Weg zurück wie man gekommen ist
        developer.setOnClickListener { startActivity(Intent(this, DeveloperActivity::class.java)) }

        if (GameState.getPlanet()!!.gameDefinitions.size > 1) {
            play.text = resources.getString(R.string.liberateTerritory_BlockPuzzle)
        } else {
            play.text = resources.getString(R.string.liberatePlanet_BlockPuzzle)
        }
    }

    override fun onResume() {
        super.onResume()
        showPlanetInfo(GameState.getPlanet()!!)
        gameInfoView.text = GameInfoService().getGameInfo(resources)
        play.isEnabled = isGameBtnEnabled()
    }

    private fun showPlanetInfo(planet: IPlanet) {
        var info = when (planet) {
            is Moon -> resources.getString(R.string.moon)
            is GiantPlanet -> resources.getString(R.string.giantPlanet)
            else -> resources.getString(R.string.planet)
        }
        info += " #" + planet.number
        if (planet.gameDefinitions.size > 1) {
            info += " / " + resources.getString(planet.selectedGame.territoryName)
        }
        info += " / " + GameState.transportation
        planetInfo.text = info
    }

    private fun onPlay() {
        if (GameState.getPlanet()!!.gameDefinitions.isEmpty()) {
            return
        }
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun onNewGame() {
        val dialog: AlertDialog.Builder = AlertDialog.Builder(this)
        dialog.setTitle(R.string.newLiberationAttemptQuestion)
        dialog.setPositiveButton(resources.getString(android.R.string.ok)) { _, _ -> newGame() }
        dialog.setNegativeButton(resources.getString(android.R.string.cancel), null)
        dialog.show()
    }

    private fun newGame() {
        val per = GameState.persistence!!
        val planet = GameState.getPlanet()!!
        per.setGameID(planet)
        per.saveScore(-1)
        per.saveMoves(0)
        planet.isOwner = false
        per.savePlanet(planet)

        gameInfoView.text = GameInfoService().getGameInfo(resources)
        play.isEnabled = isGameBtnEnabled()
    }

    private fun isGameBtnEnabled(): Boolean {
        return GameState.getPlanet()!!.hasGames()
    }
}