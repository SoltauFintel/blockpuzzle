package de.mwvb.blockpuzzle

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import de.mwvb.blockpuzzle.persistence.IPersistence
import de.mwvb.blockpuzzle.persistence.Persistence
import kotlinx.android.synthetic.main.activity_start_screen.*

/**
 * Start Screen activity
 */
class StartScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_screen)

        if (Build.VERSION.SDK_INT >= 21) {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.colorHeadlineBackground);
        }

        stoneWars.setOnClickListener { onStoneWars() }
        oldGame.setOnClickListener { onOldGame() }
    }

    override fun onResume() {
        super.onResume()
        try {
            if (per().loadOldGame() == 2) {
                startActivity(Intent(this, BridgeActivity::class.java))
            }
        } catch (e: Exception) {
            Toast.makeText(this, e.javaClass.toString() + ": " + e.message + "\n" + e.stackTrace[0].toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun onStoneWars() {
        val per = per()
        per.saveOldGame(2)
        val intent = Intent(this, InfoActivity::class.java)
        if (per.loadDeathStarMode() == 1) {
            val args = Bundle()
            args.putInt("mode", 1)
            intent.putExtras(args)
        }
        startActivity(intent)
    }

    private fun onOldGame() {
        val per = per()
        per.saveOldGame(1)
        per.saveDeathStarMode(0)
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun per(): IPersistence {
        return Persistence(this)
    }
}
