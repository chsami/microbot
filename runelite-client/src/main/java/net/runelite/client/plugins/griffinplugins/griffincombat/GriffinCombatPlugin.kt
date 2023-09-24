package net.runelite.client.plugins.griffinplugins.griffintrainer

import com.google.inject.Provides
import net.runelite.client.config.ConfigManager
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.plugins.PluginDescriptor.Griffin
import net.runelite.client.plugins.griffinplugins.griffincombat.GriffinCombatConfig
import net.runelite.client.plugins.griffinplugins.griffincombat.GriffinCombatOverlay
import net.runelite.client.ui.overlay.OverlayManager
import javax.inject.Inject

@PluginDescriptor(name = Griffin + GriffinCombatPlugin.CONFIG_GROUP, enabledByDefault = false)
class GriffinCombatPlugin : Plugin() {
    companion object {
        const val CONFIG_GROUP = "Griffin Combat"
        var countLabel = ""
        var count = 0
    }

    @Inject
    private lateinit var overlayManager: OverlayManager

    @Inject
    private lateinit var overlay: GriffinCombatOverlay

    @Inject
    private lateinit var config: GriffinCombatConfig

    @Provides
    fun provideConfig(configManager: ConfigManager): GriffinCombatConfig {
        return configManager.getConfig(GriffinCombatConfig::class.java)
    }

    @Inject
    lateinit var trainerScript: GriffinCombatScript
    override fun startUp() {
        overlayManager.add(overlay)
        trainerScript.run(config)
    }

    override fun shutDown() {
        trainerScript.shutdown()
        overlayManager.remove(overlay)
    }
}
