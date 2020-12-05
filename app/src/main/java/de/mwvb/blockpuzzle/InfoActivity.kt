package de.mwvb.blockpuzzle

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import de.mwvb.blockpuzzle.persistence.IPersistence
import de.mwvb.blockpuzzle.persistence.Persistence
import de.mwvb.blockpuzzle.sound.SoundService
import kotlinx.android.synthetic.main.activity_info.*

class InfoActivity : AppCompatActivity() {
    private val soundService = SoundService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        soundService.init(this)

        contBtn.setOnClickListener {
            soundService.alarm(false)
            startActivity(Intent(this, BridgeActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            soundService.alarm(true)
        } catch (e: Exception) {
            Toast.makeText(this, e.javaClass.toString() + ": " + e.message + "\n" + e.stackTrace[0].toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun per(): IPersistence {
        return Persistence(this)
    }
}
