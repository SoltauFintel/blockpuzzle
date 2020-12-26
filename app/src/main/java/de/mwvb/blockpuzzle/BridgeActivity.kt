package de.mwvb.blockpuzzle

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import de.mwvb.blockpuzzle.developer.DeveloperActivity
import de.mwvb.blockpuzzle.game.GameInfoService
import de.mwvb.blockpuzzle.game.NewGameService
import de.mwvb.blockpuzzle.persistence.GlobalData
import de.mwvb.blockpuzzle.persistence.IPersistence
import de.mwvb.blockpuzzle.persistence.Persistence
import de.mwvb.blockpuzzle.persistence.PlanetAccess
import de.mwvb.blockpuzzle.planet.IPlanet
import kotlinx.android.synthetic.main.activity_bridge.*
import java.util.*

class BridgeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bridge)

        if (Build.VERSION.SDK_INT >= 21) {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.navigationBackground);
        }

        navigation.setOnClickListener { startActivity(Intent(this, NavigationActivity::class.java)) }
        play.setOnClickListener { onPlay() }
        newGame.setOnClickListener { onNewGame() }
        dataexchange.setOnClickListener { startActivity(Intent(this, DataMarketActivity::class.java)) }
        developer.visibility = if (Features.developerMode) View.VISIBLE else View.INVISIBLE
        developer.setOnClickListener { onDeveloper() }
        quitGame.setOnClickListener{ onQuitGame() }
    }

    override fun onResume() {
        super.onResume()
        try {
            update()
        } catch (e: Exception) {
            Toast.makeText(this, e.javaClass.toString() + ": " + e.message + "\n" + e.stackTrace[0].toString(), Toast.LENGTH_LONG).show()
        }
    }

    override fun onBackPressed() { // do nothing
    }

    private fun update() {
        val pa = pa()
        positionView.text = GameInfoService().getPositionInfo(pa, resources)
        play.isEnabled = isGameBtnEnabled(pa)
    }

    private fun getOwner(): String {
        val per = per()
        val planet = PlanetAccess(per).planet

        val owners = TreeSet<String>()
        for (gi in 0 until planet.gameDefinitions.size) {
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
        val games = getPlanet().gameDefinitions.size
        when (games) {
            0 -> return
            1 -> startActivity(Intent(this, MainActivity::class.java))
            else -> selectTerritory(0)
        }
    }

    private fun selectTerritory(mode: Int) {
        GlobalData.selectTerritoryMode = mode
        startActivity(Intent(this, SelectTerritoryActivity::class.java))
    }

    private fun onNewGame() {
        if (getPlanet().gameDefinitions.size == 1) {
            val dialog: AlertDialog.Builder = AlertDialog.Builder(this)
            dialog.setTitle(R.string.newLiberationAttemptQuestion)
            dialog.setPositiveButton(resources.getString(android.R.string.ok)) { _, _ -> onResetGame() }
            dialog.setNegativeButton(resources.getString(android.R.string.cancel), null)
            dialog.show()
        } else {
            selectTerritory(1)
        }
    }

    private fun onResetGame() {
        NewGameService().newGame(per())
        update()
    }

    private fun isGameBtnEnabled(pa: PlanetAccess): Boolean {
        return pa.planet.hasGames()
    }

    private fun onQuitGame() {
        val dialog: AlertDialog.Builder = AlertDialog.Builder(this)
        dialog.setTitle(R.string.leaveShipInSpace)
        dialog.setPositiveButton(resources.getString(android.R.string.ok)) { _, _ -> onQuitGame2() }
        dialog.setNegativeButton(resources.getString(android.R.string.cancel), null)
        dialog.show()
    }

    private fun onQuitGame2() {
        per().saveOldGame(0)
        finishAffinity() // App beenden
    }

    private fun onDeveloper() {
        if (getPlanet().gameDefinitions.size == 1) {
            startActivity(Intent(this, DeveloperActivity::class.java))
        } else {
            selectTerritory(2)
        }
    }

    private fun getPlanet(): IPlanet {
        return pa().planet
    }

    private fun pa(): PlanetAccess {
        return PlanetAccess(per())
    }

    private fun per(): IPersistence {
        return Persistence(this)
    }
}
