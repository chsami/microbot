package net.runelite.client.plugins.griffinplugins.griffintrainer

import net.runelite.client.plugins.microbot.Microbot
import kotlin.random.Random

class TrainerInterruptor {
    companion object {
        var isInterrupted = false

        fun sleepUntil(awaitedCondition: () -> Boolean) {
            var done: Boolean
            val startTime = System.currentTimeMillis()
            do {
                if (isInterrupted) return
                done = awaitedCondition()
            } while (!done && System.currentTimeMillis() - startTime < 5000 || isInterrupted)
        }

        fun sleepUntil(awaitedCondition: () -> Boolean, time: Int) {
            var done: Boolean
            val startTime = System.currentTimeMillis()
            do {
                if (isInterrupted) return
                done = awaitedCondition()
            } while (!done && System.currentTimeMillis() - startTime < time || isInterrupted)
        }

        fun sleepUntilTrue(awaitedCondition: () -> Boolean, time: Int, timeout: Int): Boolean {
            val startTime = System.currentTimeMillis()
            do {
                if (isInterrupted) return false
                if (awaitedCondition()) {
                    return true
                }
                sleep(time)
            } while (System.currentTimeMillis() - startTime < timeout || isInterrupted)
            return false
        }

        fun sleep(start: Int) {
            if (!Microbot.getClientForKotlin().isClientThread) {
                val startTime = System.currentTimeMillis()
                do {
                    if (isInterrupted) return
                } while (System.currentTimeMillis() - startTime < start || isInterrupted)
            }
        }


        fun sleep(start: Int, end: Int) {
            if (!Microbot.getClientForKotlin().isClientThread) {
                val startTime = System.currentTimeMillis()
                do {
                    if (isInterrupted) return
                } while (System.currentTimeMillis() - startTime < Random.nextInt(start, end) || isInterrupted)
            }
        }


    }
}