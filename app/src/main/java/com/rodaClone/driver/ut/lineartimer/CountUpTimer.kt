package com.rodaClone.driver.ut.lineartimer

import android.os.Handler
import android.os.Message
import android.os.SystemClock

/**
 * Created by kartikeykushwaha on 02/02/17.
 *
 * CountUpTimer is similar to the Android CountDownTimer in respect to implementation and
 * behaviour.
 */
abstract class CountUpTimer(
    /**
     * Duration for which the timer should run.
     */
    private val duration: Long,
    /**
     * Duration after which the time update should be sent.
     */
    private val interval: Long
) {
    private var base: Long = 0
    fun start() {
        base = SystemClock.elapsedRealtime()
        handler.sendMessage(handler.obtainMessage(MSG))
    }

    fun stop() {
        handler.removeMessages(MSG)
        onFinish()
    }

    /**
     * Pauses the count up timer by removing the callbacks to the handler and stores
     * the time stamp when the pause button was tapped
     */
    fun pause() {
        handler.removeMessages(MSG)
        pauseStart = SystemClock.elapsedRealtime()
    }

    /**
     * Resumes the count up timer by re initiating the callbacks to the handler and stores
     * when the resume button was tapped on.
     * Since we have the pause and resume times, we can calculate the elapsed time in paused state.
     * Once we retrieve the total time spent in paused state, we add it to the base variable. (simple maths)
     */
    fun resume() {
        // Store resume time
        pauseEnd = SystemClock.elapsedRealtime()
        // Calculate elapsed paused time
        elapsedPausedTime = pauseEnd - pauseStart
        // Add paused time to base time stamp
        base += elapsedPausedTime
        handler.sendMessage(handler.obtainMessage(MSG))
    }

    abstract fun onTick(elapsedTime: Long)
    abstract fun onFinish()
    private val handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            synchronized(this@CountUpTimer) {
                val elapsedTime: Long
                elapsedTime = SystemClock.elapsedRealtime() - base

                // If elapsed paused time is not zero, reset them to zero so as to begin tracking
                // any further instances of pause button being tapped on
                if (elapsedPausedTime != 0L) {
                    elapsedPausedTime = 0L
                    pauseStart = 0L
                    pauseEnd = 0L
                }

                // If condition set up to hinder onTick callBacks being sent if
                // elapsedtime somehow is more than the duration.
                // Stop the timer if it has run for the required duration.
                if (elapsedTime >= duration) {
                    stop()
                } else {
                    onTick(elapsedTime)
                    sendMessageDelayed(obtainMessage(MSG), interval)
                }
            }
        }
    }

    companion object {
        /**
         * Tracks when the counter is paused
         */
        private var pauseStart: Long = 0

        /**
         * Tracks when the counter is resumed
         */
        private var pauseEnd: Long = 0

        /**
         * tracks what was the elapsed pause time
         */
        private var elapsedPausedTime: Long = 0
        private const val MSG = 1
    }
}