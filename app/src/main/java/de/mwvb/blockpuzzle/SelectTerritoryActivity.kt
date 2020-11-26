package de.mwvb.blockpuzzle

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import de.mwvb.blockpuzzle.game.GameInfoService
import de.mwvb.blockpuzzle.planet.IPlanet
import kotlinx.android.synthetic.main.activity_select_territory.*
import java.text.DecimalFormat

class SelectTerritoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_territory)

        if (GameState.getPlanet() == null) {
            finish()
            return
        }

        territory1.setOnClickListener { selectTerritory(0) }
        territory2.setOnClickListener { selectTerritory(1) }
        territory3.setOnClickListener { selectTerritory(2) }

        val planet = GameState.getPlanet()!!
        val n = planet.gameDefinitions.size
        if (n == 2) {
            set12(planet)
            territory3.visibility = View.INVISIBLE
            gameInfoView3.visibility = View.INVISIBLE
        } else if (n == 3) {
            set12(planet)
            territory3.text = resources.getString(planet.gameDefinitions[2].territoryName)
            gameInfoView3.text = getGameInfoText(2)
        } else { // wrong value
            finish()
            return
        }
    }

    private fun set12(planet: IPlanet) {
        territory1.text = resources.getString(planet.gameDefinitions[0].territoryName)
        territory2.text = resources.getString(planet.gameDefinitions[1].territoryName)
        gameInfoView1.text = getGameInfoText(0)
        gameInfoView2.text = getGameInfoText(1)
    }

    private fun getGameInfoText(gi: Int): String {
        return GameInfoService().getSelectedGameInfo(resources, GameState.getPlanet()!!.gameDefinitions[gi])
    }

    private fun thousand(n: Int): String {
        return DecimalFormat("#,##0").format(n)
    }

    private fun selectTerritory(territoryNumber: Int) {
        val planet = GameState.getPlanet()!!
        planet.selectedGame = planet.gameDefinitions[territoryNumber]
        finish()
        startActivity(Intent(this, PlanetActivity::class.java))
    }
}