package de.mwvb.blockpuzzle.playingfield

import android.os.Handler

class RowExplosion {

    fun clearRows(filledRows: FilledRows, action: Action?, view: PlayingFieldView) {
        if (filledRows.hits > 0) {
            view.setFilledRows(filledRows)
            @Suppress("DEPRECATION")
            val handler = Handler()
            handler.postDelayed({ view.drawmode(30, true, filledRows.hits >= 3) }, 50)
            handler.postDelayed({ view.drawmode(31) }, 200)
            handler.postDelayed({ view.drawmode(32) }, 350)
            handler.postDelayed({ view.drawmode(0)
                view.setFilledRows(null)
                action?.execute()
            }, 500)
            // See also time delay in DeathStarGame.deathStarDestroyed()
        }
    }
}