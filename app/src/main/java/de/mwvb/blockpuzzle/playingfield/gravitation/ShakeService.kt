package de.mwvb.blockpuzzle.playingfield.gravitation

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import de.mwvb.blockpuzzle.game.GameEngine
import de.mwvb.blockpuzzle.global.Features
import kotlin.math.sqrt

class ShakeService(gameEngine: GameEngine) {
    private val mSensorListener: SensorEventListener
    private var mSensorManager: SensorManager? = null
    private var mAccel = 0f // acceleration apart from gravity
    private var mAccelCurrent = 0f// current acceleration including gravity
    private var mAccelLast = 0f // last acceleration including gravity

    init {
        mSensorListener = object : SensorEventListener { // https://stackoverflow.com/a/2318356/3478021
            override fun onSensorChanged(se: SensorEvent) {
                val x = se.values[0]
                val y = se.values[1]
                val z = se.values[2]
                mAccelLast = mAccelCurrent
                mAccelCurrent = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
                val delta = mAccelCurrent - mAccelLast
                mAccel = mAccel * 0.9f + delta // perform low-cut filter
                if (mAccel > 14) gameEngine.shaked() // value is how hard you have to shake
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) { //
            }
        }
    }

    fun initShakeDetection(activity: Activity) {
        if (Features.shakeForGravitation) {
            mSensorManager = activity.getSystemService(Context.SENSOR_SERVICE) as SensorManager?
            mSensorManager!!.registerListener(
                mSensorListener,
                mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL
            )
            mAccel = 0.00f
            mAccelCurrent = SensorManager.GRAVITY_EARTH
            mAccelLast = SensorManager.GRAVITY_EARTH
        }
    }

    fun setActive(active: Boolean) {
        if (Features.shakeForGravitation) {
            if (active) {
                mSensorManager!!.registerListener(
                    mSensorListener,
                    mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL
                )
            } else {
                mSensorManager!!.unregisterListener(mSensorListener)
            }
        }
    }
}