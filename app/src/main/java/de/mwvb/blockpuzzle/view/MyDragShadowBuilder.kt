package de.mwvb.blockpuzzle.view

import android.graphics.Point
import android.view.View
import de.mwvb.blockpuzzle.logic.Game

class MyDragShadowBuilder : View.DragShadowBuilder {
    //(view: TeilView, f: Float) : View.DragShadowBuilder(view)
    val f: Float

    constructor(view: TeilView, f: Float) : super(view) {
        this.f = f
    }

    override fun onProvideShadowMetrics(outShadowSize: Point?, outShadowTouchPoint: Point?) {
        val br = SpielfeldView.w / Game.blocks
        val tv = this.view as TeilView
        if (tv.teil == null) return // Programmschutz

        outShadowSize?.set((tv.width * 2).toInt(), (tv.height * 2).toInt()) // normale Größe

        val ax = (tv.teil.minX * br + br / 2) * f
        val ay = (tv.teil.maxY * br + br / 2 + br + br) * f
        outShadowTouchPoint?.set(ax.toInt(), ay.toInt())
    }
}