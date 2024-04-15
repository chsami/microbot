package net.runelite.client.plugins.griffinplugins.griffintrainer

import com.google.inject.Provides
import net.runelite.client.config.ConfigManager
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.plugins.PluginDescriptor.Griffin
import net.runelite.client.ui.overlay.OverlayManager
import javax.inject.Inject

@PluginDescriptor(name = Griffin + GriffinTrainerPlugin.CONFIG_GROUP + "[<font color=#f22727>DANGER</font>]", enabledByDefault = false)
class GriffinTrainerPlugin : Plugin() {
    companion object {
        const val CONFIG_GROUP = "Auto Pilot"
    }

    @Inject
    private lateinit var overlayManager: OverlayManager

    @Inject
    private lateinit var overlay: GriffinTrainerOverlay

    @Inject
    private lateinit var config: GriffinTrainerConfig

    @Provides
    fun provideConfig(configManager: ConfigManager): GriffinTrainerConfig {
        return configManager.getConfig(GriffinTrainerConfig::class.java)
    }

    private lateinit var trainerThread: TrainerThread

    override fun startUp() {
        TrainerInterruptor.isInterrupted = false
        trainerThread = TrainerThread(config)
        overlayManager.add(overlay)
        trainerThread.start()
    }

    override fun shutDown() {
        TrainerInterruptor.isInterrupted = true

        trainerThread.interrupt()
        trainerThread.join(5000)

        while (trainerThread.isAlive) {
            println("$CONFIG_GROUP: Thread took too long to stop, forcing unsafe stop.")
            trainerThread.stop();
        }

        overlayManager.remove(overlay)
    }
}
