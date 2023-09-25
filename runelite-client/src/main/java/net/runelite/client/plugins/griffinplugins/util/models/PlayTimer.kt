package net.runelite.client.plugins.griffinplugins.griffintrainer.models

import java.time.Duration

class PlayTimer {
    private var startTime: Long = 0

    var timeout = 0
    fun start() {
        startTime = System.currentTimeMillis()
    }

    fun reset() {
        startTime = System.currentTimeMillis()
        timeout = 0
    }

    fun setRandomTimeout(min: Int, max: Int) {
        timeout = min + (Math.random() * (max - min)).toInt()
    }

    val isTimerComplete: Boolean
        get() = if (timeout == 0) {
            false
        } else elapsedMinutes >= timeout
    val timeRemaining: Int
        get() = if (timeout == 0) {
            0
        } else timeout - elapsedMinutes
    val elapsedMinutes: Int
        get() {
            val currentTime = System.currentTimeMillis()
            return ((currentTime - startTime) / 1000 / 60).toInt()
        }
    val elapsedTimeString: String
        get() {
            val duration = Duration.ofMillis(System.currentTimeMillis() - startTime)
            return String.format("%02d:%02d:%02d", duration.toHours(), duration.toMinutesPart(), duration.toSecondsPart())
        }
}
