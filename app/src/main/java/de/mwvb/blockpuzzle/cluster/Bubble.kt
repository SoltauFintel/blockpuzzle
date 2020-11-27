package de.mwvb.blockpuzzle.cluster

import android.graphics.*
import android.widget.Button
import de.mwvb.blockpuzzle.GameState
import de.mwvb.blockpuzzle.planet.IPlanet

/**
 * Planet speech bubble for cluster view
 */
class Bubble(val background: Int, val backgroundForTarget: Int, val f: Float) {
    val rectanglePaint = Paint()
    val trianglePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    val textPaint = Paint()
    /** speech bubble visibility  */
    var isVisible = false
    private var planet: IPlanet? = null

    init {
        rectanglePaint.color = background
        trianglePaint.color = background
        trianglePaint.style = Paint.Style.FILL_AND_STROKE
        trianglePaint.isAntiAlias = true
        textPaint.color = Color.BLACK
        textPaint.textSize = 20f * f
    }

    fun setPlanet(planet: IPlanet?) {
        if (planet == null || planet === this.planet) {
            // von sichtbar auf nicht sichtbar schalten, da gleiche Position
            isVisible = false
            this.planet = null
        } else {
            // sichtbar!
            isVisible = true
            this.planet = planet
        }
    }
    fun getPlanet(): IPlanet? {
        return this.planet
    }

    fun draw(canvas: Canvas, selectTargetButton: Button) {
        if (planet == null || !isVisible) {
            selectTargetButton.isEnabled = false
            return
        };
        val p = planet!!
        val myX: Float = p.x * ClusterView.w * f
        val myY: Float = p.y * ClusterView.w * f - p.radius * f
        val bubbleWidth = 240f * f
        val bubbleBoxHeight = 86f * f
        val rx = myX - bubbleWidth / 2
        val ry = myY - bubbleBoxHeight - 30
        val r = RectF(rx, ry, rx + bubbleWidth, ry + bubbleBoxHeight)
        val isTarget = (GameState.getPlanet()?.number == p.number)
        if (isTarget) {
            rectanglePaint.color = backgroundForTarget
            trianglePaint.color = backgroundForTarget
        }
        canvas.drawRoundRect(r, 5 * f, 5 * f, rectanglePaint)
        canvas.drawPath(getPath(myX, myY), trianglePaint)
        if (isTarget) {
            rectanglePaint.color = background
            trianglePaint.color = background
        }

        for (i in 1..3) {
            val xx = rx + 8.5f * f
            val yy = ry + 3f * f + i * 23f * f
            canvas.drawText(p.getInfoText(i), xx, yy, textPaint)
        }
        selectTargetButton.isEnabled = true
    }

    private fun getPath(myX: Float, myY: Float): Path {
        val path = Path()
        path.fillType = Path.FillType.EVEN_ODD
        path.moveTo(myX, myY)
        path.lineTo(myX + 30, myY - 31)
        path.lineTo(myX - 30, myY - 31)
        path.close()
        return path
    }

    fun hide() {
        isVisible = false
        // Important: Keep x and y value! (-> keep this.planet)
    }
}