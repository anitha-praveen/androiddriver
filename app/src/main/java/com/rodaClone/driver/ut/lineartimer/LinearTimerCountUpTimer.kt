package com.rodaClone.driver.ut.lineartimer

class LinearTimerCountUpTimer     // Reduce the time duration by 1000 millis so as to make sure that tha animation and
// counter finish at the same time.
    (
    private val timerDuration: Long,
    countUpInterval: Long, private val timerListener: LinearTimer.TimerListener?
) : CountUpTimer(
    timerDuration, countUpInterval
) {
    /**
     * Returns the milliseconds remaining before the counter finishes itself
     *
     * @return millisLeftUntilFinished
     */
    var timeLeft: Long = 0
        private set

    override fun onTick(elapsedTime: Long) {
        timeLeft = timerDuration - elapsedTime
        timerListener?.timerTick(elapsedTime)
    }

    override fun onFinish() {
        if (LinearTimer.intStatusCode != LinearTimerStates.PAUSED.staus) {
            timerListener?.timerTick(timerDuration)
        }
    }
}