package de.mwvb.blockpuzzle.view

import android.graphics.Point
import android.view.View
import de.mwvb.blockpuzzle.logic.Game

class MyDragShadowBuilder(view: TeilView, private val f: Float) : View.DragShadowBuilder(view) {

    override fun onProvideShadowMetrics(outShadowSize: Point?, outShadowTouchPoint: Point?) {
        val br = SpielfeldView.w / Game.blocks
        val brh = br / 2
        val tv = this.view as TeilView
        if (tv.teil == null) return // Programmschutz

        outShadowSize?.set((tv.width * 2).toInt(), (tv.height * 2).toInt()) // normale Größe

        val ax = (tv.teil.minX * br + brh) * f
        val ay = (tv.teil.maxY * br + brh + br + br) * f
        outShadowTouchPoint?.set(ax.toInt(), ay.toInt())
    }
}