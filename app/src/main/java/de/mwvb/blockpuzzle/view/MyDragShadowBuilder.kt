package de.mwvb.blockpuzzle.view

import android.graphics.Point
import android.view.View

class MyDragShadowBuilder(view: TeilView) : View.DragShadowBuilder(view) {

    override fun onProvideShadowMetrics(outShadowSize: Point?, outShadowTouchPoint: Point?) {
        val br = 60
        val tv = this.view as TeilView
        if (tv.teil == null) return // Programmschutz
        outShadowSize?.set((tv.width * 2).toInt(), (tv.height * 2).toInt()) // normale Größe
        val ax = tv.teil.minX * br + br / 2
        val ay = tv.teil.maxY * br + br / 2 + 120
        outShadowTouchPoint?.set(ax, ay)
    }
}