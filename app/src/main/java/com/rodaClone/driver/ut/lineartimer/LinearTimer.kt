package com.rodaClone.driver.ut.lineartimer

import kotlin.Throws
import android.animation.ObjectAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import java.lang.Exception
import java.lang.IllegalStateException

/**
 * Created by kartikeykushwaha on 18/12/16.
 * @see https://github.com/krtkush/LinearTimer
 */
class LinearTimer private constructor(builder: Builder) : ArcProgressAnimation.TimerListener {
    private val linearTimerView: LinearTimerView?
    private var arcProgressAnimation: ArcProgressAnimation? = null
    private val timerListener: TimerListener?
    private val endingAngle: Int
    private val totalDuration: Long
    private val timeElapsed: Long
    private var preFillAngle: Float
    private val countType: Int
    private var animationDuration: Long = 0
    private val updateInterval: Long
    private var countDownTimer: LinearTimerCountDownTimer? = null
    private var countUpTimer: LinearTimerCountUpTimer? = null

    /**
     * If the user has defined timeElapsed value, this method calculates the length of pre-fill.
     */
    private fun determinePreFillAngle() {
        val timeElapsedPercentage = timeElapsed.toFloat() / totalDuration.toFloat() * 100
        preFillAngle = timeElapsedPercentage / 100 * 360
    }

    /**
     * Method to start the timer.
     */
    fun startTimer() {
        if (basicParametersCheck()) {
            if (intStatusCode == LinearTimerStates.INITIALIZED.staus) {
                // Store the current status code in intStatusCode integer
                intStatusCode = LinearTimerStates.ACTIVE.staus
                arcProgressAnimation = ArcProgressAnimation(linearTimerView!!, endingAngle, this)
                arcProgressAnimation!!.duration = animationDuration
                linearTimerView.startAnimation(arcProgressAnimation)
                checkForCountUpdateType()
            } else throw IllegalStateException("LinearTimer is not in INITIALIZED state right now.")
        }
    }

    /**
     * A method to pause the running timer.
     * @throws IllegalStateException IllegalStateException is thrown if the user tries to pause
     * a timer that is not in the ACTIVE state.
     */
    @Throws(IllegalStateException::class)
    fun pauseTimer() {
        if (basicParametersCheck()) {
            // Timer may be paused only in active state.
            if (intStatusCode == LinearTimerStates.ACTIVE.staus) {

                // Store the current status code in intStatusCode integer.
                intStatusCode = LinearTimerStates.PAUSED.staus

                // Clear animations off of linearTimerView, set prefillAngle to current
                // state and refresh view.
                linearTimerView!!.clearAnimation()
                linearTimerView.preFillAngle = linearTimerView.preFillAngle
                linearTimerView.invalidate()

                // Cancel the CountDown/CountUp timer so it stops counting up/down.
                if (countType == COUNT_DOWN_TIMER) countDownTimer!!.cancel() else if (countType == COUNT_UP_TIMER) countUpTimer!!.pause()
            } else throw IllegalStateException("LinearTimer is not in active right now.")
        }
    }

    /**
     * Method to resume a paused timer.
     * @throws IllegalStateException thrown if the user attempts to resume a timer that has not been
     * paused.
     */
    @Throws(IllegalStateException::class)
    fun resumeTimer() {
        if (basicParametersCheck()) {
            if (intStatusCode == LinearTimerStates.PAUSED.staus) {

                // Reinitialize the animations as it may not be simply continued.
                // The animation is reinitialized with the linearTimerView,
                // the ending angle and duration is set to pending time left from the timer.
                arcProgressAnimation = ArcProgressAnimation(linearTimerView!!, endingAngle, this)
                if (countType == COUNT_DOWN_TIMER) arcProgressAnimation!!.duration =
                    countDownTimer!!.timeLeft else if (countType == COUNT_UP_TIMER) arcProgressAnimation!!.duration =
                    countUpTimer!!.timeLeft

                // re-initialize the countdown timer with the pending millis from previous instance
                // and start.
                if (countType == COUNT_DOWN_TIMER) {
                    countDownTimer = LinearTimerCountDownTimer(
                        countDownTimer!!.timeLeft,
                        updateInterval,
                        timerListener!!
                    )
                    countDownTimer!!.start()
                } else if (countType == COUNT_UP_TIMER) countUpTimer!!.resume()

                // Start animation again.
                linearTimerView.startAnimation(arcProgressAnimation)

                // Store the current status code in intStatusCode integer
                intStatusCode = LinearTimerStates.ACTIVE.staus
            } else throw IllegalStateException("LinearTimer is not in paused state right now.")
        }
    }

    /**
     * Reset the timer to start angle and then start the progress again.
     */
    fun restartTimer() {
        if (basicParametersCheck()) {
            if (arcProgressAnimation != null) {
                // Store the current status code in intStatusCode integer
                intStatusCode = LinearTimerStates.ACTIVE.staus

                // Reset the pre filling angle as passed by user during initialization
                linearTimerView!!.preFillAngle = preFillAngle
                arcProgressAnimation = ArcProgressAnimation(linearTimerView, endingAngle, this)
                arcProgressAnimation!!.duration = animationDuration
                // Cancel the circle animation
                arcProgressAnimation!!.cancel()
                // Start arc animation on the timerView
                linearTimerView.startAnimation(arcProgressAnimation)
                checkForCountUpdateType()
            }
        }
    }

    /**
     * Method to reset the LinearTimer to start angle only
     */
    fun resetTimer() {
        if (basicParametersCheck()) {
            if (intStatusCode == LinearTimerStates.PAUSED.staus
                || intStatusCode == LinearTimerStates.FINISHED.staus
            ) {
                //Store the current status code in intStatusCode integer
                intStatusCode = LinearTimerStates.INITIALIZED.staus

                //Cancel the circle animation
                arcProgressAnimation!!.cancel()
                //Reset the pre filling angle as passed by user during initialization
                linearTimerView!!.preFillAngle = preFillAngle
                linearTimerView.invalidate()

                //Cancel the countdown timer so it stops counting up/down
                if (countType == COUNT_DOWN_TIMER) countDownTimer!!.cancel() else if (countType == COUNT_UP_TIMER) countUpTimer!!.stop()

                //Inform listeners the timer was reset
                timerListener!!.onTimerReset()
            } else throw IllegalStateException("Cannot reset when LinearTimer is in ACTIVE or INITIALIZED state.")
        }
    }

    /**
     * A method that determines the current state of LinearTimer. One of the following is returned
     * 1) Initialized
     * 2) Active
     * 3) Paused
     * 4) Finished
     * @return Returns an enum that defines the current state of the LinearTimer.
     */
    val state: LinearTimerStates
        get() = when (intStatusCode) {
            0 -> LinearTimerStates.INITIALIZED
            1 -> LinearTimerStates.ACTIVE
            2 -> LinearTimerStates.PAUSED
            3 -> LinearTimerStates.FINISHED
            else -> LinearTimerStates.INITIALIZED
        }

    override fun animationComplete() {
        try {
            if (listenerCheck()) {
                // Store the current status code in intStatusCode integer
                intStatusCode = LinearTimerStates.FINISHED.staus
                timerListener!!.animationComplete()
                timerListener.timerFinish()
            }
        } catch (ex: LinearTimerListenerMissingException) {
            ex.printStackTrace()
        }
    }

    /**
     * Interface to inform the implementing class about events wrt timer.
     */
    interface TimerListener {
        /**
         * Animation complete.
         */
        fun animationComplete()

        /**
         * Timer tick.
         *
         * @param tickUpdateInMillis the tick update in millis
         */
        fun timerTick(tickUpdateInMillis: Long)
        fun timerFinish()
        fun onTimerReset()
    }

    /**
     * Method to check whether the following basic params, needed to setup the timer,
     * have been passed by the user or not -
     * 1. LinearTimerView
     * 2. Duration
     */
    private fun basicParametersCheck(): Boolean {
        try {
            if (timerViewCheck() && durationCheck()) return true
        } catch (ex: LinearTimerViewMissingException) {
            ex.printStackTrace()
        } catch (ex: LinearTimerDurationMissingException) {
            ex.printStackTrace()
        }
        return false
    }

    /**
     * This method checks whether the LinearTimerView's reference has been passed or not.
     */
    @Throws(LinearTimerViewMissingException::class)
    private fun timerViewCheck(): Boolean {
        return if (linearTimerView == null) throw LinearTimerViewMissingException("Reference to LinearTimer View missing.") else true
    }

    /**
     * This method checks whether totalDuration has been provided or not.
     */
    @Throws(LinearTimerDurationMissingException::class)
    private fun durationCheck(): Boolean {
        return if (totalDuration == -1L) throw LinearTimerDurationMissingException("Timer totalDuration missing.") else true
    }

    /**
     * This method checks whether the user has provided reference to the class
     * implementing the listener interface.
     */
    @Throws(LinearTimerListenerMissingException::class)
    private fun listenerCheck(): Boolean {
        return if (timerListener == null) throw LinearTimerListenerMissingException("Timer listener is missing.") else true
    }

    /**
     * Method to check if the user wants tick updates of the timer
     * and of what type - countdown or up.
     */
    private fun checkForCountUpdateType() {
        when (countType) {
            COUNT_DOWN_TIMER -> {
                countDownTimer = LinearTimerCountDownTimer(
                    animationDuration,
                    updateInterval, timerListener!!
                )
                countDownTimer!!.start()
            }
            COUNT_UP_TIMER -> {
                countUpTimer = LinearTimerCountUpTimer(
                    animationDuration,
                    updateInterval, timerListener
                )
                countUpTimer!!.start()
            }
        }
    }

    /**
     * Builder class to initialize LinearTimer.
     */
    class Builder {
        var progressDirection = CLOCK_WISE_PROGRESSION
        var linearTimerView: LinearTimerView? = null
        var timerListener: TimerListener? = null
        var preFillAngle = 0f
        var endingAngle = 360
        var totalDuration: Long = -1
        var timeElapsed: Long = 0
        var countType = COUNT_UP_TIMER // Even if the user does not need the updates,

        // we need to keep track of time elapsed, left etc.
        // Specially for the case of resuming the timer.
        // Hence, we default to COUNT_UP_TIMER.
        var updateInterval: Long = 1000

        /**
         * Not a mandatory field. Default is clock wise progression.
         *
         * @param progressDirection Clock wise or anti-clock wise direction of the progress.
         * LinearTimer.CLOCK_WISE_PROGRESSION
         * or LinearTimer.COUNTER_CLOCK_WISE_PROGRESSION
         * @return the builder
         */
        fun progressDirection(progressDirection: Int): Builder {
            this.progressDirection = progressDirection
            return this
        }

        /**
         * A mandatory field.
         *
         * @param linearTimerView The reference to the view.
         * @return the builder
         */
        fun linearTimerView(linearTimerView: LinearTimerView?): Builder {
            this.linearTimerView = linearTimerView
            return this
        }

        /**
         * Not a mandatory field. Use only if updates regarding animation and timer
         * ticks are needed.
         *
         * @param timerListener Reference of the class implementing the TimerListener interface.
         * @return the builder
         */
        fun timerListener(timerListener: TimerListener?): Builder {
            this.timerListener = timerListener
            return this
        }

        /**
         * Not a mandatory field.
         *
         * @param preFillAngle Angle up-till which the circle should be pre-filled.
         * @return the builder
         */
        fun preFillAngle(preFillAngle: Float): Builder {
            this.preFillAngle = preFillAngle
            return this
        }

        /**
         * A mandatory field. Duration for which the user wants
         * to run the timer for.
         *
         * @param totalDuration in milliseconds,
         * @return builder
         */
        fun duration(totalDuration: Int): Builder {
            this.totalDuration = totalDuration.toLong()
            return this
        }

        /**
         * Overloaded method.
         * When the user wants to continue the timer animation from a certain point and that point
         * is in respect to the time elapsed. USe of this method will override the `preFillAngle`
         * value.
         *
         * @param totalDuration in milliseconds.
         * @param timeElapsed   in milliseconds.
         * @return the builder
         */
        fun duration(totalDuration: Long, timeElapsed: Long): Builder {
            this.totalDuration = totalDuration
            this.timeElapsed = timeElapsed
            return this
        }

        /**
         * Not a mandatory field.
         *
         * @param endingAngle The angle at which the user wants the animation to end.
         * @return the builder
         */
        fun endingAngle(endingAngle: Int): Builder {
            this.endingAngle = endingAngle
            return this
        }

        /**
         * Not a mandatory field.
         * This enables the LinearTimer library to return time elapsed or time left depending on the
         * type of timer applied.
         *
         * @param countType      The type of timer the user wants to show;
         * LinearTimer.COUNT_UP_TIMER or LinearTimer.COUNT_DOWN_TIMER.
         * @param updateInterval Duration (in millis) after which user wants the updates.
         * Should be > 0.
         * @return the count update
         */
        fun getCountUpdate(countType: Int, updateInterval: Long): Builder {
            this.countType = countType
            this.updateInterval = updateInterval
            return this
        }

        /**
         * Build linear timer.
         *
         * @return the linear timer
         */
        fun build(): LinearTimer {
            return LinearTimer(this)
        }
    }

    /**
     * Exception thrown when user fails to provide reference to the LinearTimerView.
     */
    private inner class LinearTimerViewMissingException
    /**
     * Instantiates a new Linear timer view missing exception.
     *
     * @param message the message
     */
    internal constructor(message: String?) : Exception(message)

    /**
     * Exception thrown when user fails to provide the totalDuration of the timer.
     */
    private inner class LinearTimerDurationMissingException
    /**
     * Instantiates a new Linear timer duration missing exception.
     *
     * @param message the message
     */
    internal constructor(message: String?) : Exception(message)

    /**
     * Exception thrown when user fails to reference to the class
     * implementing the listener interface.
     */
    private inner class LinearTimerListenerMissingException
    /**
     * Instantiates a new Linear timer listener missing exception.
     *
     * @param message the message
     */
    internal constructor(message: String?) : Exception(message)

    companion object {
        /**
         * Constant for builder method `progressDirection()`.
         */
        const val CLOCK_WISE_PROGRESSION = 0

        /**
         * Constant for builder method `progressDirection()`.
         */
        const val COUNTER_CLOCK_WISE_PROGRESSION = 1

        /**
         * Argument (arg1) for builder method `getCountUpdate()`.
         */
        const val COUNT_UP_TIMER = 2

        /**
         * Argument (arg1) for builder method `getCountUpdate()`.
         */
        const val COUNT_DOWN_TIMER = 3

        /**
         * A boolean to track which state LinerTimer is in currently. The boolean is updated
         * constantly as and when the state changes
         * Package level access given
         */
        var intStatusCode = 0
    }

    init {
        linearTimerView = builder.linearTimerView
        timerListener = builder.timerListener
        endingAngle = builder.endingAngle
        totalDuration = builder.totalDuration
        preFillAngle = builder.preFillAngle
        timeElapsed = builder.timeElapsed
        countType = builder.countType
        updateInterval = builder.updateInterval
        if (basicParametersCheck()) {

            // Calculate the animation duration.
            animationDuration = totalDuration - timeElapsed

            // timeElapsed = 0 does not need pre-fill angle to be determined.
            if (timeElapsed > 0) determinePreFillAngle()

            // Set the pre-fill angle.
            linearTimerView!!.preFillAngle = preFillAngle

            // Store the current status code in intStatusCode integer
            intStatusCode = LinearTimerStates.INITIALIZED.staus

            // If the user wants to show the progress in counter clock wise manner,
            // we flip the view on its Y-Axis and let it function as is.
            if (builder.progressDirection == COUNTER_CLOCK_WISE_PROGRESSION) {
                val objectAnimator = ObjectAnimator
                    .ofFloat(linearTimerView, "rotationY", 0.0f, 180f)
                objectAnimator.interpolator = AccelerateDecelerateInterpolator()
                objectAnimator.start()
            }
        }
    }
}