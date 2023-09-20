package net.runelite.client.plugins.griffinplugins.griffintrainer

import com.google.inject.Provides
import net.runelite.client.config.ConfigManager
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.plugins.PluginDescriptor.Griffin
import net.runelite.client.plugins.griffinplugins.griffintrainer.models.PlayTimer
import net.runelite.client.ui.overlay.OverlayManager
import javax.inject.Inject

@PluginDescriptor(name = Griffin + GriffinTrainerPlugin.CONFIG_GROUP, enabledByDefault = false)
class GriffinTrainerPlugin : Plugin() {
    companion object {
        const val CONFIG_GROUP = "Griffin Trainer"
        val overallTimer = PlayTimer()
        val taskTimer = PlayTimer()
        var countLabel = ""
        var count = 0
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

    @Inject
    lateinit var trainerScript: GriffinTrainerScript
    override fun startUp() {
        overlayManager.add(overlay)
        trainerScript.run(config)
    }

    override fun shutDown() {
        trainerScript.shutdown()
        overlayManager.remove(overlay)
    }
}
