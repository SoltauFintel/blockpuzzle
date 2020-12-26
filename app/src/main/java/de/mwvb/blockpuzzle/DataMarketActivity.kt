package de.mwvb.blockpuzzle

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
import de.mwvb.blockpuzzle.data.DataService
import de.mwvb.blockpuzzle.game.ResourceService
import de.mwvb.blockpuzzle.gamedefinition.ResourceAccess
import de.mwvb.blockpuzzle.persistence.IPersistence
import de.mwvb.blockpuzzle.persistence.Persistence
import kotlinx.android.synthetic.main.activity_data_market.*

/**
 * Datenmarktplatz auf Planeten
 */
class DataMarketActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_market)

        if (Build.VERSION.SDK_INT >= 21) {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.navigationBackground);
        }

        pasteBtn.setOnClickListener { onPaste() }
        copyBtn.setOnClickListener { onCopy() }
        enterPlayername.setOnClickListener { startActivity(Intent(this, PlayerNameActivity::class.java)) }
    }

    override fun onResume() {
        super.onResume()
        try {
            dataview.setText(DataService().get(per()))
            copyBtn.isEnabled = per().loadPlayernameEntered()
        } catch (e: Exception) {
            Toast.makeText(this, e.javaClass.toString() + ": " + e.message + "\n" + e.stackTrace[0].toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun onCopy() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val o = ClipData.newPlainText("BlockPuzzleDataPacket", DataService().get(per()))
        clipboard.setPrimaryClip(o)
        Toast.makeText(this, resources.getString(R.string.copied), Toast.LENGTH_SHORT).show()
    }

    private fun onPaste() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (clipboard.hasPrimaryClip()) {
            if (clipboard.primaryClipDescription!!.hasMimeType(MIMETYPE_TEXT_PLAIN)) {
                val item = clipboard.primaryClip!!.getItemAt(0)
                val pasteData = item.text
                if (pasteData != null) {
                    val msg = DataService().put(pasteData.toString(), per(), ResourceService().getResourceAccess(this, null));
                    if (msg != null && !msg.isEmpty()) {
                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                    }
                    return // success
                }
            }
        }
        Toast.makeText(this, resources.getString(R.string.nothingToInsert), Toast.LENGTH_SHORT).show()
    }

    private fun per(): IPersistence {
        return Persistence(this)
    }
}