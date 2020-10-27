package de.mwvb.blockpuzzle

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import de.mwvb.blockpuzzle.gamepiece.sets.AllGamePieceSets
import kotlinx.android.synthetic.main.activity_start.*

/**
 * App start activity
 */
class StartActivity : AppCompatActivity() {
    private var MAX_SETS = AllGamePieceSets.sets.size
    private var currentGamePieceSetNumber = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        classicGame.setOnClickListener { showGameActivity(Features.GAME_MODE_CLASSIC) }
        cleanerGame.setOnClickListener { showGameActivity(Features.GAME_MODE_CLEANER) }
        saveGPSN.setOnClickListener { saveGamePieceSetNumber() }
    }

    private fun showGameActivity(gameMode: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("gameMode", gameMode)
        intent.putExtra("gamePieceSetNumber", currentGamePieceSetNumber)
        startActivity(intent)
    }

    private fun saveGamePieceSetNumber() {
        var msg = "Zufallsspiel eingestellt."
        try {
            val input: String = gamePieceSetNumber.text.toString()
            var number = 0
            if (!input.isEmpty()) {
                number = input.toInt()
            }
            if (number < 0 || number > MAX_SETS) {
                msg = "Bitte Zahl im Bereich 0 bis " + MAX_SETS + " eingeben!"
            } else {
                currentGamePieceSetNumber = number
                if (number > 0) {
                    msg = "Spiel " + number + " eingestellt."
                }
            }
        } catch (e: Exception) {
            msg = "Bitte Zahl im Bereich 0 bis " + MAX_SETS + " eingeben!"
        }
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
}
