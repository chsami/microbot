package net.runelite.client.plugins.griffinplugins.griffintrainer

import net.runelite.api.GameState
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.plugins.PluginDescriptor.Griffin
import net.runelite.client.plugins.griffinplugins.griffinantibotdetector.BotDetectorAPI
import net.runelite.client.plugins.microbot.Microbot
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.random.Random


@PluginDescriptor(name = Griffin + GriffinAntiBotDetectorPlugin.CONFIG_GROUP, enabledByDefault = false)
class GriffinAntiBotDetectorPlugin : Plugin() {
    companion object {
        const val CONFIG_GROUP = "Anti Bot Detector"
    }

    protected var scheduledExecutorService = Executors.newScheduledThreadPool(100)

    override fun startUp() {
        val botDetectorAPI = BotDetectorAPI()

        scheduledExecutorService.scheduleWithFixedDelay({
            if (Microbot.getClientForKotlin().gameState != GameState.LOGGED_IN) {
                return@scheduleWithFixedDelay
            }

            val localPlayer = Microbot.getClientForKotlin().localPlayer
            localPlayer ?: return@scheduleWithFixedDelay

            val playerName = localPlayer.name
            playerName ?: return@scheduleWithFixedDelay

            randomSleep()

            val predictionResults = botDetectorAPI.predict(playerName)
            predictionResults ?: return@scheduleWithFixedDelay

            val reportingPlayerName = getReportingPlayerName()
            botDetectorAPI.report(reportingPlayerName, predictionResults)

        }, 5, 25, TimeUnit.MINUTES)
    }

    override fun shutDown() {
        scheduledExecutorService.shutdownNow()
    }

    private fun getReportingPlayerName(): String {
        val reportingPlayerName = "AnonymousUser_${UUID.randomUUID()}"

        if (Random.nextInt(1, 101) < 95) {
            return reportingPlayerName
        }

        val randomPlayer = Microbot.getClientForKotlin().players.randomOrNull()
        randomPlayer ?: return reportingPlayerName

        val playerName = randomPlayer.name
        playerName ?: return reportingPlayerName

        return playerName
    }

    private fun randomSleep() {
        val fiveMinutes = 1000 * 60 * 5
        val tenMinutes = 1000 * 60 * 10
        Thread.sleep(Random.nextInt(fiveMinutes, tenMinutes).toLong())
    }
}
