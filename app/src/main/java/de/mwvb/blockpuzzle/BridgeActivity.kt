package de.mwvb.blockpuzzle

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import de.mwvb.blockpuzzle.gamedefinition.GameDefinition
import de.mwvb.blockpuzzle.planet.GiantPlanet
import de.mwvb.blockpuzzle.planet.Moon
import kotlinx.android.synthetic.main.activity_bridge.*
import java.text.DecimalFormat
import java.util.*
import java.util.stream.Collectors

class BridgeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bridge)

        navigation.setOnClickListener { startActivity(Intent(this, StartActivity::class.java)) }
        travel.setOnClickListener { onTravel() }
        landOnPlanet.setOnClickListener { onLandOnPlanet() }
        communications.setOnClickListener {}
        leaveBridge.setOnClickListener { startActivity(Intent(this, ShipActivity::class.java)) }
    }

    override fun onResume() {
        super.onResume()
        showPosition();
    }

    // TODO Wenn man die App während des Reisens beendet, bleibt man im Raum stehen.
    private fun onTravel() {
        val target = GameState.getTarget()
        if (target == null) {
            Toast.makeText(this, resources.getString(R.string.selectTargetInNavigation), Toast.LENGTH_LONG).show()
            return
        }
        if (GameState.getPlanet() != null && target.number == GameState.getPlanet()!!.number) {
            Toast.makeText(this, resources.getString(R.string.hereWeAre), Toast.LENGTH_SHORT).show()
            return
        }
        GameState.travel(target)
        showPosition()
        Toast.makeText(this, resources.getString(R.string.inOrbitOfPlanetP1, target.number), Toast.LENGTH_LONG).show()
    }

    @SuppressLint("SetTextI18n")
    private fun showPosition() {
        positionView.text = getPositionInfo() + getTargetInfo()
        if (GameState.flightMode == 0) {
            landOnPlanet.text = resources.getString(R.string.startFromPlanet)
        } else {
            landOnPlanet.text = resources.getString(R.string.landOnPlanet)
        }
    }

    // Zeile 1
    private fun getPositionInfo(): String {
        var info = resources.getString(R.string.position) + ":   G=" + GameState.galaxy + "  C=" + GameState.cluster.number +
                "  Q=" + GameState.cluster.getQuadrant(GameState.x, GameState.y) +
                "  X=" + GameState.x + "  Y=" + GameState.y
        if (GameState.getPlanet() != null) {
            info += "\n" + getPlanetInfo()
        }
        return info
    }

    // Zeile 2
    private fun getPlanetInfo(): String {
        val f = if (GameState.flightMode == 1) resources.getString(R.string.inOrbitOf) else resources.getString(R.string.landedOn)
        val pa: String
        pa = when {
            GameState.getPlanet() is GiantPlanet -> resources.getString(R.string.giantPlanet)
            GameState.getPlanet() is Moon -> resources.getString(R.string.moon)
            else -> resources.getString(R.string.planet)
        }
        var info = f + " " + pa + " #" + GameState.getPlanet()!!.number + ", " + resources.getString(R.string.gravitation) + " " + GameState.getPlanet()!!.gravitation
        info += "\n" + if (GameState.getPlanet()!!.isOwner) {
            resources.getString(R.string.liberatedPlanetByYou)
        } else {
            val owner = getOwner()
            if (owner.isEmpty()) {
                resources.getString(R.string.planetOccByP1, resources.getString(R.string.orangeUnion))
            } else {
                resources.getString(R.string.planetOccByP1, owner)
            }
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

    // Ziel (Zeile 3)
    private fun getTargetInfo(): String {
        var info = ""
        val target = GameState.getTarget()
        if (target != null && (GameState.x != target.x || GameState.y != target.y)) {
            info = "\n" + resources.getString(R.string.targetPlanetP1, target.number)
        }
        return info
    }

    private fun onLandOnPlanet() {
        if (GameState.flightMode == 0) {
            GameState.flightMode = 1
        } else {
            GameState.flightMode = 0
        }
        GameState.saveFlightMode()
        showPosition()
    }
}
