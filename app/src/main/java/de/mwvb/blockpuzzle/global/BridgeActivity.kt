package de.mwvb.blockpuzzle.global

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import de.mwvb.blockpuzzle.R
import de.mwvb.blockpuzzle.cluster.NavigationActivity
import de.mwvb.blockpuzzle.data.DataMarketActivity
import de.mwvb.blockpuzzle.game.GameEngineFactory
import de.mwvb.blockpuzzle.game.MainActivity
import de.mwvb.blockpuzzle.game.stonewars.deathstar.SpaceNebulaRoute
import de.mwvb.blockpuzzle.global.developer.DeveloperActivity
import de.mwvb.blockpuzzle.planet.IPlanet
import de.mwvb.blockpuzzle.planet.SelectTerritoryActivity
import kotlinx.android.synthetic.main.activity_bridge.*

class BridgeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bridge)

        if (Build.VERSION.SDK_INT >= 21) {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.navigationBackground)
        }
        AbstractDAO.init(this)

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
            e.printStackTrace()
            Toast.makeText(this, e.javaClass.toString() + ": " + e.message + "\n" + e.stackTrace[0].toString(), Toast.LENGTH_LONG).show()
        }
    }

    override fun onBackPressed() { // do nothing
    }

    private fun update() {
        val planet = getPlanet()
        navigation.isEnabled = SpaceNebulaRoute.isNoDeathStarMode
        positionView.text = planet.getInfo(resources) // all lines under Navigation button
        play.isEnabled = isGameBtnEnabled(planet)
        newGame.setText(planet.newLiberationAttemptButtonTextResId)
    }

    private fun onPlay() {
        if (getPlanet().userMustSelectTerritory()) {
            selectTerritory(SelectTerritoryActivity.CONTINUE_WITH_PLAY_GAME)
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun selectTerritory(mode: Int) {
        val intent = Intent(this, SelectTerritoryActivity::class.java)
        val args = Bundle()
        args.putInt(SelectTerritoryActivity.MODE, mode)
        intent.putExtras(args)
        startActivity(intent)
    }

    private fun onNewGame() {
        val planet = getPlanet()
        if (planet.userMustSelectTerritory()) {
            selectTerritory(SelectTerritoryActivity.CONTINUE_WITH_RESET_GAME)
        } else {
            val dialog: AlertDialog.Builder = AlertDialog.Builder(this)
            dialog.setTitle(planet.newLiberationAttemptQuestionResId)
            dialog.setPositiveButton(android.R.string.ok) { _, _ -> onResetGame() }
            dialog.setNegativeButton(android.R.string.cancel, null)
            dialog.show()
        }
    }

    private fun onResetGame() {
        GameEngineFactory().getPlanet().resetGame()
        update()
    }

    private fun isGameBtnEnabled(planet: IPlanet) = planet.hasGames()

    private fun onQuitGame() {
        val dialog: AlertDialog.Builder = AlertDialog.Builder(this)
        dialog.setTitle(R.string.leaveShipInSpace)
        dialog.setPositiveButton(resources.getString(android.R.string.ok)) { _, _ -> onQuitGame2() }
        dialog.setNegativeButton(resources.getString(android.R.string.cancel), null)
        dialog.show()
    }

    private fun onQuitGame2() {
        val gd = GlobalData.get()
        gd.gameType = GameType.NOT_SELECTED
        gd.save()
        finishAffinity() // App beenden
    }

    private fun onDeveloper() {
        if (getPlanet().userMustSelectTerritory()) {
            selectTerritory(SelectTerritoryActivity.CONTINUE_WITH_DEVELOPER_ACTIVITY)
        } else {
            startActivity(Intent(this, DeveloperActivity::class.java))
        }
    }

    private fun getPlanet() = GameEngineFactory().getPlanet()
}
