package de.mwvb.blockpuzzle.data

import android.content.ClipData
import android.content.ClipDescription.MIMETYPE_TEXT_PLAIN
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import de.mwvb.blockpuzzle.R
import de.mwvb.blockpuzzle.game.GameEngineFactory
import de.mwvb.blockpuzzle.game.stonewars.deathstar.SpaceNebulaRoute
import de.mwvb.blockpuzzle.gamestate.TrophiesDAO
import de.mwvb.blockpuzzle.global.AbstractDAO
import de.mwvb.blockpuzzle.global.GlobalData
import de.mwvb.blockpuzzle.global.messages.MessageFactory
import kotlinx.android.synthetic.main.activity_data_market.*

/**
 * Datenmarktplatz auf Planeten
 */
class DataMarketActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_market)

        if (Build.VERSION.SDK_INT >= 21) {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.navigationBackground)
        }
        AbstractDAO.init(this)

        pasteBtn.setOnClickListener { onPaste() }
        copyBtn.setOnClickListener { onCopy() }
        enterPlayername.setOnClickListener { startActivity(Intent(this, PlayerNameActivity::class.java)) }
    }

    override fun onResume() {
        super.onResume()
        try {
            // Data exchange not possible in Death Star mode
            val enabled = SpaceNebulaRoute.isNoDeathStarMode
            pasteBtn.isEnabled = enabled
            copyBtn.isEnabled = enabled && GlobalData.get().isPlayernameEntered

            dataview.text = if (enabled) DataService().get() else ""
            trophies.text = getTrophiesText()
        } catch (e: Exception) {
            Toast.makeText(this, e.javaClass.toString() + ": " + e.message + "\n" + e.stackTrace[0].toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun onCopy() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val o = ClipData.newPlainText("BlockPuzzleDataPacket", DataService().get())
        clipboard.setPrimaryClip(o)
        Toast.makeText(this, resources.getString(R.string.copied), Toast.LENGTH_SHORT).show()
    }

    private fun onPaste() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val messages = MessageFactory(this)
        if (clipboard.hasPrimaryClip()) {
            if (clipboard.primaryClipDescription!!.hasMimeType(MIMETYPE_TEXT_PLAIN)) {
                val item = clipboard.primaryClip!!.getItemAt(0)
                val pasteData = item.text
                if (pasteData != null) {
                    DataService().put(pasteData.toString(), messages).show()
                    return // success
                }
            }
        }
        messages.nothingToInsert.show()
    }

    private fun getTrophiesText(): String {
        val planet = GameEngineFactory().getPlanet()
        val trophies = TrophiesDAO().load(planet.clusterNumber)
        val platinum = GlobalData.get().platinumTrophies
        return resources.getString(R.string.trophies, planet.clusterNumber, trophies.bronze, trophies.silver, trophies.golden, platinum)
    }
}