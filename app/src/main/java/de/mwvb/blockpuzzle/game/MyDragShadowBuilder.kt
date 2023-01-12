package de.mwvb.blockpuzzle.game

import android.graphics.Point
import android.util.Log
import android.view.View
import de.mwvb.blockpuzzle.gamepiece.GamePieceView
import de.mwvb.blockpuzzle.playingfield.PlayingFieldView

class MyDragShadowBuilder(view: GamePieceView, private val f: Float) : View.DragShadowBuilder(view) {

    override fun onProvideShadowMetrics(outShadowSize: Point?, outShadowTouchPoint: Point?) {
        if (view == null) return

        val br = PlayingFieldView.w / GameEngineBuilder.blocks
        val brh = br / 2
        val tv = this.view as GamePieceView
        if (tv.gamePiece == null) return // Programmschutz

        outShadowSize?.set(tv.width * 2, tv.height * 2) // normale Größe

        val ax = f * (tv.gamePiece.minX * br + brh)
        val ay = f * (tv.gamePiece.maxY * br + brh + br + br)
        Log.e("MSDB_jux", "b 9 *******")
        outShadowTouchPoint?.set(ax.toInt(), ay.toInt())
    }
}