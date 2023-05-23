package com.rodaClone.driver.ut.lineartimer

import android.view.animation.Animation
import android.view.animation.Transformation

/**
 * Created by kartikeykushwaha on 18/12/16.
 */
class ArcProgressAnimation(
    linearTimerView: LinearTimerView, endingAngle: Int,
    timerListener: TimerListener
) : Animation() {
    private val linearTimerView: LinearTimerView
    private val startingAngle: Float
    private val endingAngle: Float
    private val timerListener: TimerListener
    override fun applyTransformation(interpolatedTime: Float, transformation: Transformation) {
        val finalAngle = startingAngle + (endingAngle - startingAngle) * interpolatedTime
        linearTimerView.preFillAngle = finalAngle
        linearTimerView.requestLayout()

        // If interpolatedTime = 0.0 -> Animation has started.
        // If interpolatedTime = 1.0 -> Animation has completed.
        if (interpolatedTime.toDouble() == 1.0) timerListener.animationComplete()
    }

    /**
     * Interface to inform the implementing class about events wrt timer.
     */
    interface TimerListener {
        /**
         * Animation complete.
         */
        fun animationComplete()
    }

    /**
     * Instantiates a new Arc progress animation.
     *
     * @param linearTimerView the linear timer view
     * @param endingAngle     the ending angle
     * @param timerListener   the timer listener
     */
    init {
        startingAngle = linearTimerView.preFillAngle
        this.endingAngle = endingAngle.toFloat()
        this.linearTimerView = linearTimerView
        this.timerListener = timerListener
    }
}