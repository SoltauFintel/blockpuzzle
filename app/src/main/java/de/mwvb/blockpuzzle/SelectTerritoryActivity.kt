package de.mwvb.blockpuzzle

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import de.mwvb.blockpuzzle.developer.DeveloperActivity
import de.mwvb.blockpuzzle.game.GameInfoService
import de.mwvb.blockpuzzle.game.NewGameService
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

    private fun selectTerritory(territoryNumber: Int) {
        val planet = GameState.getPlanet()!!
        planet.selectedGame = planet.gameDefinitions[territoryNumber]
        when (GameState.selectTerritoryMode) {
            1 -> {
                onNewLiberationAttemptQuestion()
            }
            2 -> {
                finish()
                startActivity(Intent(this, DeveloperActivity::class.java))
            }
            else -> {
                finish()
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }

    private fun onNewLiberationAttemptQuestion() {
        val dialog: AlertDialog.Builder = AlertDialog.Builder(this)
        dialog.setTitle(R.string.newLiberationAttemptQuestion)
        dialog.setPositiveButton(resources.getString(android.R.string.ok)) { _, _ -> onResetGame() }
        dialog.setNegativeButton(resources.getString(android.R.string.cancel), null)
        dialog.show()
    }

    private fun onResetGame() {
        NewGameService().newGame()
        finish()
    }
}