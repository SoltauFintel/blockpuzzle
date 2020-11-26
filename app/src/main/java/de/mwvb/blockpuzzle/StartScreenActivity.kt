package de.mwvb.blockpuzzle

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
        GameState.load()
        if (GameState.isStoneWars()) {
            onStoneWars()
        }
    }

    override fun onPause() {
        GameState.save()
        super.onPause()
    }

    private fun onStoneWars() {
        GameState.setOldGame(2)
        startActivity(Intent(this, BridgeActivity::class.java))
    }

    private fun onOldGame() {
        GameState.setOldGame(1)
        startActivity(Intent(this, MainActivity::class.java))
    }
}
