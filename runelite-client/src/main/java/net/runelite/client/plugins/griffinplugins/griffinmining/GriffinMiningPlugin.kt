package net.runelite.client.plugins.griffinplugins.griffintrainer

import com.google.inject.Provides
import net.runelite.client.config.ConfigManager
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.plugins.PluginDescriptor.Griffin
import net.runelite.client.plugins.griffinplugins.griffinmining.GriffinMiningConfig
import net.runelite.client.plugins.griffinplugins.griffinmining.GriffinMiningOverlay
import net.runelite.client.ui.overlay.OverlayManager
import javax.inject.Inject

@PluginDescriptor(name = Griffin + GriffinMiningPlugin.CONFIG_GROUP, enabledByDefault = false)
class GriffinMiningPlugin : Plugin() {
    companion object {
        const val CONFIG_GROUP = "Mining Trainer"
        var countLabel = ""
        var count = 0
    }

    @Inject
    private lateinit var overlayManager: OverlayManager

    @Inject
    private lateinit var overlay: GriffinMiningOverlay

    @Inject
    private lateinit var config: GriffinMiningConfig

    @Provides
    fun provideConfig(configManager: ConfigManager): GriffinMiningConfig {
        return configManager.getConfig(GriffinMiningConfig::class.java)
    }

    @Inject
    lateinit var miningScript: GriffinMiningScript
    override fun startUp() {
        countLabel = ""
        count = 0
        GriffinCombatScript.state = GriffinCombatScript.State.SETUP

        overlayManager.add(overlay)
        miningScript.run(config)
    }

    override fun shutDown() {
        miningScript.shutdown()
        overlayManager.remove(overlay)
    }
}
