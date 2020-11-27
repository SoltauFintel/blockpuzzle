package de.mwvb.blockpuzzle

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import de.mwvb.blockpuzzle.developer.DeveloperActivity
import de.mwvb.blockpuzzle.game.GameInfoService
import de.mwvb.blockpuzzle.game.NewGameService
import de.mwvb.blockpuzzle.planet.GiantPlanet
import de.mwvb.blockpuzzle.planet.Moon
import kotlinx.android.synthetic.main.activity_bridge.*
import java.util.*

class BridgeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bridge)

        navigation.setOnClickListener { startActivity(Intent(this, StartActivity::class.java)) }
        play.setOnClickListener { onPlay() }
        newGame.setOnClickListener { onNewGame() }
        dataexchange.setOnClickListener { startActivity(Intent(this, DataMarketActivity::class.java)) }
        developer.visibility = if (Features.developerMode) View.VISIBLE else View.INVISIBLE
        developer.setOnClickListener { onDeveloper() }
        quitGame.setOnClickListener{ onQuitGame() }
    }

    override fun onResume() {
        super.onResume()
        update()
    }

    override fun onBackPressed() {
    }

    private fun update() {
        positionView.text = getPositionInfo()
        play.isEnabled = isGameBtnEnabled()
    }

    // Zeile 1
    private fun getPositionInfo(): String {
        var info = resources.getString(R.string.position) + ":   G=" + GameState.galaxy + "  C=" + GameState.cluster.number +
                "  Q=" + GameState.cluster.getQuadrant(GameState.getPlanet()!!.x, GameState.getPlanet()!!.y) +
                "  X=" + GameState.getPlanet()!!.x + "  Y=" + GameState.getPlanet()!!.y
        info += "\n" + getPlanetInfo() + "\n" + GameInfoService().getGameInfo(resources)
        return info
    }

    // Zeile 2
    private fun getPlanetInfo(): String {
        val f = resources.getString(R.string.inOrbitOf)
        val pa = when {
            GameState.getPlanet() is GiantPlanet -> resources.getString(R.string.giantPlanet)
            GameState.getPlanet() is Moon -> resources.getString(R.string.moon)
            else -> resources.getString(R.string.planet)
        }
        var info = f + " " + pa + " #" + GameState.getPlanet()!!.number + ", " + resources.getString(R.string.gravitation) + " " + GameState.getPlanet()!!.gravitation
        if (GameState.getPlanet()!!.gameDefinitions.size > 1) {
            info += "\n" + resources.getString(GameState.getPlanet()!!.selectedGame.territoryName)
        }
        return info
    }

    private fun getOwner(): String {
        val per = GameState.persistence!!
        val planet = GameState.getPlanet()!!

        val max = planet.gameDefinitions.size - 1
        val owners = TreeSet<String>()
        for (gi in 0..max) {
            per.setGameID(planet, gi)
            val owner = per.loadOwnerName()
            if (owner != null && owner.isNotEmpty()) {
                owners.add(owner)
            }
        }
        if (owners.isEmpty()) return ""
        var ret = ""
        owners.forEach { o -> ret += "/$o" }
        return ret.substring("/".length)
    }

    private fun onPlay() {
        if (GameState.getPlanet() == null || GameState.getPlanet()!!.gameDefinitions.isEmpty()) {
            return
        }
        if (GameState.getPlanet()!!.gameDefinitions.size == 1) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            GameState.selectTerritoryMode = 0
            startActivity(Intent(this, SelectTerritoryActivity::class.java))
        }
    }

    private fun onNewGame() {
        if (GameState.getPlanet()!!.gameDefinitions.size == 1) {
            val dialog: AlertDialog.Builder = AlertDialog.Builder(this)
            dialog.setTitle(R.string.newLiberationAttemptQuestion)
            dialog.setPositiveButton(resources.getString(android.R.string.ok)) { _, _ -> onResetGame() }
            dialog.setNegativeButton(resources.getString(android.R.string.cancel), null)
            dialog.show()
        } else {
            GameState.selectTerritoryMode = 1
            startActivity(Intent(this, SelectTerritoryActivity::class.java))
        }
    }

    private fun onResetGame() {
        NewGameService().newGame()
        update()
    }

    private fun isGameBtnEnabled(): Boolean {
        return GameState.getPlanet()!!.hasGames()
    }

    private fun onQuitGame() {
        val dialog: AlertDialog.Builder = AlertDialog.Builder(this)
        dialog.setTitle(R.string.leaveShipInSpace)
        dialog.setPositiveButton(resources.getString(android.R.string.ok)) { _, _ -> leaveGame() }
        dialog.setNegativeButton(resources.getString(android.R.string.cancel), null)
        dialog.show()
    }

    private fun leaveGame() {
        GameState.quitGame()
        finishAffinity() // App beenden
    }

    private fun onDeveloper() {
        if (GameState.getPlanet()!!.gameDefinitions.size == 1) {
            startActivity(Intent(this, DeveloperActivity::class.java))
        } else {
            GameState.selectTerritoryMode = 2
            startActivity(Intent(this, SelectTerritoryActivity::class.java))
        }
    }
}
