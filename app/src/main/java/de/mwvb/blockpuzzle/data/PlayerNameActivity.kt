package de.mwvb.blockpuzzle.data

import android.os.Build
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import de.mwvb.blockpuzzle.R
import de.mwvb.blockpuzzle.global.AbstractDAO
import de.mwvb.blockpuzzle.global.GlobalData
import de.mwvb.blockpuzzle.planet.SpaceObjectStateService
import kotlinx.android.synthetic.main.activity_player_name.*

class PlayerNameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_name)

        if (Build.VERSION.SDK_INT >= 21) {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.navigationBackground)
        }
        AbstractDAO.init(this)

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
            val gd = GlobalData.get()
            playername.setText(gd.playername?:"")
            gameSounds.isChecked = gd.isGameSounds
            sunMode.isChecked = gd.isSunMode
        } catch (e: Exception) {
            Toast.makeText(this, e.javaClass.toString() + ": " + e.message + "\n" + e.stackTrace[0].toString(), Toast.LENGTH_LONG).show()
        }
    }

    override fun onPause() {
        super.onPause()
        val gd = GlobalData.get()
        gd.isGameSounds = gameSounds.isChecked
        gd.isSunMode = sunMode.isChecked
        gd.save()
    }

    private fun onSaveBtn() {
        val pn = playername.text.toString().trim()
        when {
            pn.isEmpty() -> return
            pn == "open_map" -> SpaceObjectStateService().openMap()
            else -> savePlayername(pn)
        }
        finish()
    }

    private fun savePlayername(pn: String) {
        val gd = GlobalData.get()
        gd.playername = pn
        gd.isPlayernameEntered = true
        gd.save()
    }
}