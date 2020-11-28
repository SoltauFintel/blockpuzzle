package de.mwvb.blockpuzzle

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import de.mwvb.blockpuzzle.persistence.Persistence
import kotlinx.android.synthetic.main.activity_start_screen.*

/**
 * Start Screen activity
 */
class StartScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_screen)

        GameState.persistence = Persistence(this)

        stoneWars.setOnClickListener { onStoneWars() }
        oldGame.setOnClickListener { onOldGame() }
    }

    override fun onResume() {
        super.onResume()
        try {
            GameState.load()
            if (GameState.isStoneWars()) {
                GameState.activateStoneWars()
                startActivity(Intent(this, BridgeActivity::class.java))
            }
        } catch (e: Exception) {
            Toast.makeText(this, e.javaClass.toString() + ": " + e.message + "\n" + e.stackTrace[0].toString(), Toast.LENGTH_LONG).show()
        }
    }

    override fun onPause() {
        GameState.save()
        super.onPause()
    }

    private fun onStoneWars() {
        GameState.activateStoneWars()
        startActivity(Intent(this, InfoActivity::class.java))
    }

    private fun onOldGame() {
        GameState.activateOldGame()
        startActivity(Intent(this, MainActivity::class.java))
    }
}
