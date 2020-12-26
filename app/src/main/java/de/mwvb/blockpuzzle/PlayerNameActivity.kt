package de.mwvb.blockpuzzle

import android.os.Build
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import de.mwvb.blockpuzzle.persistence.IPersistence
import de.mwvb.blockpuzzle.persistence.Persistence
import kotlinx.android.synthetic.main.activity_player_name.*

class PlayerNameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_name)

        if (Build.VERSION.SDK_INT >= 21) {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.navigationBackground);
        }

        playername.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                onSaveBtn()
                true
            } else {
                false
            }
        }
        saveBtn.setOnClickListener { onSaveBtn() }
    }

    override fun onResume() {
        super.onResume()
        try {
            val per = per()
            playername.setText(per.loadPlayerName())
            gameSounds.isChecked = per.isGameSoundOn
        } catch (e: Exception) {
            Toast.makeText(this, e.javaClass.toString() + ": " + e.message + "\n" + e.stackTrace[0].toString(), Toast.LENGTH_LONG).show()
        }
    }

    override fun onPause() {
        super.onPause()
        per().saveGameSound(gameSounds.isChecked)
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