package com.rodaClone.driver.ut.lineartimer

import android.os.CountDownTimer

class LinearTimerCountDownTimer
/**
 * @param millisLeftUntilFinished The number of millis in the future from the call
 * to [.start] until the countdown is done and [.onFinish]
 * is called.
 * @param countDownInterval       The interval along the way to receive
 * [.onTick] callbacks.
 */(
    millisLeftUntilFinished: Long,
    countDownInterval: Long, private val timerListener: LinearTimer.TimerListener
) : CountDownTimer(millisLeftUntilFinished, countDownInterval) {
    /**
     * Returns the milliseconds remaining before the counter finishes itself
     *
     * @return millisLeftUntilFinished
     */
    var timeLeft: Long = 0
        private set

    override fun onTick(millisUntilFinished: Long) {
        timeLeft = millisUntilFinished
        timerListener.timerTick(millisUntilFinished)
    }

    override fun onFinish() {
        if (LinearTimer.intStatusCode != LinearTimerStates.PAUSED.staus) {
            timerListener.timerTick(0)
            //            timerListener.timerFinish();
        }
    }
}