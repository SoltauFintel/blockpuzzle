package de.mwvb.blockpuzzle

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
        soundService.alarm(true)
    }
}
