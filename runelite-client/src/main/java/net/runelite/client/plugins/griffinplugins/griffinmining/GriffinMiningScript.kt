package net.runelite.client.plugins.griffinplugins.griffinmining

import net.runelite.client.plugins.griffinplugins.griffintrainer.GriffinTrainerConfig
import net.runelite.client.plugins.griffinplugins.griffintrainer.trainers.mining.MiningTrainer
import net.runelite.client.plugins.microbot.Script
import java.util.concurrent.TimeUnit

class GriffinMiningScript : Script() {

    fun run(config: GriffinTrainerConfig): Boolean {
        val miningTrainer = MiningTrainer(config)

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay({
            if (!super.run()) return@scheduleWithFixedDelay

            try {
                miningTrainer.run()

            } catch (ex: Exception) {
                println(ex)
            }
        }, 0, 200, TimeUnit.MILLISECONDS)
        return true
    }
}