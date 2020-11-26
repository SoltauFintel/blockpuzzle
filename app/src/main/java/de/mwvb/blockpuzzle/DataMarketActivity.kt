package de.mwvb.blockpuzzle

import android.content.ClipData
import android.content.ClipDescription.MIMETYPE_TEXT_PLAIN
import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import de.mwvb.blockpuzzle.data.DataService
import kotlinx.android.synthetic.main.activity_data_market.*

/**
 * Datenmarktplatz auf Planeten
 */
class DataMarketActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_market)

        pasteBtn.setOnClickListener { onPaste() }
        copyBtn.setOnClickListener { onCopy() }
    }

    override fun onResume() {
        super.onResume()
        dataview.setText(DataService().get())
    }

    private fun onCopy() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val o = ClipData.newPlainText("BlockPuzzleDataPacket", DataService().get())
        clipboard.setPrimaryClip(o)
        Toast.makeText(this, "Kopiert", Toast.LENGTH_SHORT).show()
    }

    private fun onPaste() {
        var clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (clipboard.hasPrimaryClip()) {
            if (clipboard.primaryClipDescription!!.hasMimeType(MIMETYPE_TEXT_PLAIN)) {
                val item = clipboard.primaryClip!!.getItemAt(0)
                val pasteData = item.text
                if (pasteData != null) {
                    val msg = DataService().put(pasteData.toString());
                    if (msg != null && !msg.isEmpty()) {
                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                    }
                    return // success
                }
            }
        }
        Toast.makeText(this, "Nichts einzufügen", Toast.LENGTH_SHORT).show()
    }
}