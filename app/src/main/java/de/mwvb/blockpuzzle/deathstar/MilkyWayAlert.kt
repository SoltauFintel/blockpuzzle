package de.mwvb.blockpuzzle.deathstar

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import de.mwvb.blockpuzzle.InfoActivity
import de.mwvb.blockpuzzle.playingfield.Action

/**
 * Action to show Milky Way alert
 */
class MilkyWayAlert(base: Context?) : ContextWrapper(base), Action {

    override fun execute() {
        val intent = Intent(this, InfoActivity::class.java)
        val args = Bundle()
        args.putInt("mode", 1)
        intent.putExtras(args)
        startActivity(intent)
    }
}