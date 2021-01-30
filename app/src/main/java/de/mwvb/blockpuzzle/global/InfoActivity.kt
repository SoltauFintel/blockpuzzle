package de.mwvb.blockpuzzle.global

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import de.mwvb.blockpuzzle.R
import de.mwvb.blockpuzzle.game.MainActivity
import de.mwvb.blockpuzzle.sound.SoundService
import kotlinx.android.synthetic.main.activity_info.*

class InfoActivity : AppCompatActivity() {
    private val soundService = SoundService()

    companion object {
        const val MODE = "mode"
        const val MILKY_WAY_ALERT = 1
        const val BACK_FROM_DEATH_STAR = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        if (Build.VERSION.SDK_INT >= 21) {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.navigationBackground)
        }
        AbstractDAO.init(this)

        soundService.init(this)

        val mode = intent.extras?.getInt(MODE)
        val activity: Class<*>
        activity = when (mode) {
            MILKY_WAY_ALERT -> {
                infotext.setText(R.string.milkyWayAlertInfotext)
                MainActivity::class.java
            }
            BACK_FROM_DEATH_STAR -> {
                infotext.setText(R.string.backFromDeathStar)
                BridgeActivity::class.java
            }
            else -> {
                BridgeActivity::class.java
            }
        }

        contBtn.setOnClickListener { startActivity(Intent(this, activity)) }
        muteBtn.setOnClickListener { soundService.alarm(false) }
    }

    override fun onResume() {
        super.onResume()
        try {
            soundService.alarm(true)
        } catch (e: Exception) {
            Toast.makeText(this, e.javaClass.toString() + ": " + e.message + "\n" + e.stackTrace[0].toString(), Toast.LENGTH_LONG).show()
        }
    }

    override fun onPause() {
        super.onPause()
        soundService.alarm(false) // make sure that alarm is off
    }

    override fun onBackPressed() {
        // Wenn ich das hier nicht verhindern würde, würde die App zur Bridge gehen und der Alarm geht nie mehr aus.
    }
}
