package de.mwvb.blockpuzzle

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_start.*

/**
 * App start activity
 */
class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        classicGame.setOnClickListener { showGameActivity("classic") }
        cleanerGame.setOnClickListener { showGameActivity("cleaner") }
    }

    private fun showGameActivity(gameMode: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("gameMode", gameMode)
        startActivity(intent)
    }
}
