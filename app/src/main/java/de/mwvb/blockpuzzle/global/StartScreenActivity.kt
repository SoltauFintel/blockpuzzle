package de.mwvb.blockpuzzle.global

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import de.mwvb.blockpuzzle.R
import de.mwvb.blockpuzzle.game.MainActivity
import kotlinx.android.synthetic.main.activity_start_screen.*

/**
 * Start Screen activity
 */
class StartScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_screen)

        if (Build.VERSION.SDK_INT >= 21) {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.colorHeadlineBackground)
        }
        AbstractDAO.init(this)

        val migration = Migration5to6()
        if (migration.isNecessary) {
            println("Data migration to version 6 is necessary.")
            migration.migrate(this)
            // IF the migration crashes it's better also to crash the app, because we don't want to loose the old game state.
            println("Migration finished.")
        }

        stoneWars.setOnClickListener { onStoneWars() }
        oldGame.setOnClickListener { onOldGame() }
    }

    override fun onResume() {
        super.onResume()
        try {
//            if (Features.developerMode) { // This is for Death Star testing.
//                val gd = GlobalData.get()
//                if (gd.todesstern == 1) {
//                    gd.todesstern = 0
//                    gd.currentPlanet = 35
//                    gd.save()
//                } else {
//                    gd.currentPlanet = 35
//                    gd.save()
//                }
//            }

            if (GlobalData.get().gameType == GameType.STONE_WARS) {
                startActivity(Intent(this, BridgeActivity::class.java))
            }
        } catch (e: Exception) {
            Toast.makeText(this, e.javaClass.toString() + ": " + e.message + "\n" + e.stackTrace[0].toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun onStoneWars() {
        val gd = GlobalData.get()
        gd.gameType = GameType.STONE_WARS
        val intent = Intent(this, InfoActivity::class.java)
        if (gd.todesstern == 1) {
            val args = Bundle()
            args.putInt(InfoActivity.MODE, InfoActivity.MILKY_WAY_ALERT)
            intent.putExtras(args)
        }
        gd.save()
        startActivity(intent)
    }

    private fun onOldGame() {
        val gd = GlobalData.get()
        gd.gameType = GameType.OLD_GAME
        gd.todesstern = 0
        gd.save()
        startActivity(Intent(this, MainActivity::class.java))
    }
}
