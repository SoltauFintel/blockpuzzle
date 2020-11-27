package de.mwvb.blockpuzzle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_player_name.*

class PlayerNameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_name)

        saveBtn.setOnClickListener { onSaveBtn() }
    }

    override fun onResume() {
        super.onResume()
        playername.setText(GameState.playername)
    }

    private fun onSaveBtn() {
        GameState.savePlayername(playername.text.toString())
        GameState.persistence!!.savePlayernameEntered(true)
        finish()
    }
}