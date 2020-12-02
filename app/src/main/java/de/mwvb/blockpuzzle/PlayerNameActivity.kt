package de.mwvb.blockpuzzle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import de.mwvb.blockpuzzle.persistence.IPersistence
import de.mwvb.blockpuzzle.persistence.Persistence
import kotlinx.android.synthetic.main.activity_player_name.*

// TODO Enter -> click Save Btn
class PlayerNameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_name)

        saveBtn.setOnClickListener { onSaveBtn() }
    }

    override fun onResume() {
        super.onResume()
        try {
            playername.setText(per().loadPlayerName())
        } catch (e: Exception) {
            Toast.makeText(this, e.javaClass.toString() + ": " + e.message + "\n" + e.stackTrace[0].toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun onSaveBtn() {
        val per = per()
        val pn = playername.text.toString()
        if (pn.trim().isEmpty()) return;
        per.savePlayerName(pn)
        per.savePlayernameEntered(true)
        finish()
    }

    private fun per(): IPersistence {
        return Persistence(this)
    }
}