package de.mwvb.blockpuzzle.view

import android.graphics.Point
import android.view.View
import de.mwvb.blockpuzzle.logic.Game

class MyDragShadowBuilder(view: TeilView, private val f: Float) : View.DragShadowBuilder(view) {

    override fun onProvideShadowMetrics(outShadowSize: Point?, outShadowTouchPoint: Point?) {
        val br = PlayingFieldView.w / Game.blocks
        val brh = br / 2
        val tv = this.view as TeilView
        if (tv.gamePiece == null) return // Programmschutz

        outShadowSize?.set(tv.width * 2, tv.height * 2) // normale Größe

        val ax = f * (tv.gamePiece.minX * br + brh)
        val ay = f * (tv.gamePiece.maxY * br + brh + br + br)
        outShadowTouchPoint?.set(ax.toInt(), ay.toInt())
    }
}