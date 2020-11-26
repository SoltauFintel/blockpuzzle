package de.mwvb.blockpuzzle

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import de.mwvb.blockpuzzle.developer.DeveloperActivity
import kotlinx.android.synthetic.main.activity_ship.*

/**
 * Man ist hier quasi im Flur (oder Turbolift) des Raumschiffs und kann in verschiedene Räume des Raumschiffs gelangen.
 */
class ShipActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ship)

        bridge.setOnClickListener { finish() } // zurück
        playername.setOnClickListener { startActivity(Intent(this, PlayerNameActivity::class.java)) } // TODO Das wird mal der Captains Raum mit Logbuch und Namenseingabe. Wobei Namenseingabe könnte auch in den Optionen landen.
        transporter.setOnClickListener { onTransporter() } // einfachste Möglichkeit auf den Planeten zu kommen
        frachtraum.setOnClickListener {} // TODO gesammelte Informationen etc.
        engine.setOnClickListener {} // TODO zeigt Energiereserven an, besseren Warpantrieb einbauen
        shuttle.setOnClickListener { onShuttle() }
        leaveShip.setOnClickListener { onLeaveShip() }
    }

    private fun onTransporter() {
        GameState.transportation = resources.getString(R.string.transportationTransporter)
        toPlanetSurface()
    }

    private fun onShuttle() {
        // alternative Möglichkeit um zum Planeten zu kommen, man kann mehr transportieren, oder wenn Planet nicht "beamfähig" ist
        GameState.transportation = resources.getString(R.string.transportationShuttle)
        toPlanetSurface()
    }

    // Notausstieg (App beenden, wenn man die App wieder startet hat einen die Crew wieder aufgelesen), auf Planeten: zum Marktplatz
    private fun onLeaveShip() {
        if (GameState.flightMode == 0) { // Landed on planet
            GameState.transportation = resources.getString(R.string.transportationShip)
            toPlanetSurface()
        } else { // in space
            val dialog: AlertDialog.Builder = AlertDialog.Builder(this)
            dialog.setTitle(R.string.leaveShipInSpace)
            dialog.setPositiveButton(resources.getString(android.R.string.ok)) { _, _ -> leaveGame() }
            dialog.setNegativeButton(resources.getString(android.R.string.cancel), null)
            dialog.show()
        }
    }

    private fun toPlanetSurface() {
        if (GameState.getPlanet() == null) {
            return
        }
        val n = GameState.getPlanet()!!.gameDefinitions.size
        if (n < 0 || n > 3) {
            return
        }
        if (n == 1) {
            GameState.getPlanet()!!.selectedGame = GameState.getPlanet()!!.gameDefinitions[0]
            startActivity(Intent(this, PlanetActivity::class.java))
            return
        } else {
            startActivity(Intent(this, SelectTerritoryActivity::class.java))
            return
        }
    }

    private fun leaveGame() {
        GameState.setOldGame(0) // Show start screen at next start
        GameState.save()
        finishAffinity() // App beenden
    }
}