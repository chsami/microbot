package net.runelite.client.plugins.griffinplugins.griffintrainer

import com.google.inject.Provides
import net.runelite.client.config.ConfigManager
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.plugins.PluginDescriptor.Griffin
import net.runelite.client.plugins.griffinplugins.griffintrainer.scripts.CombatTrainerScript
import net.runelite.client.ui.overlay.OverlayManager
import javax.inject.Inject

@PluginDescriptor(name = Griffin + GriffinTrainerPlugin.CONFIG_GROUP, enabledByDefault = false)
class GriffinTrainerPlugin : Plugin() {
    companion object {
        const val CONFIG_GROUP = "Griffin Trainer"
    }

    @Inject
    private lateinit var overlayManager: OverlayManager

//    @Inject
//    private lateinit var overlay: GerberOverlay

    @Inject
    private lateinit var config: GriffinTrainerConfig

    @Provides
    fun provideConfig(configManager: ConfigManager): GriffinTrainerConfig {
        return configManager.getConfig(GriffinTrainerConfig::class.java)
    }

    @Inject
    lateinit var trainerScript: CombatTrainerScript

    //    private var gerberThread: GerberThread? = null
    override fun startUp() {
//        if (overlayManager != null) {
//            overlayManager.add()
//        }
        trainerScript.run(config)
//        overlayManager.add(overlay)
//        gerberThread = GerberThread(config)
//        gerberThread!!.start()
    }

    override fun shutDown() {
        trainerScript.shutdown()
//        overlayManager.remove(exampleOverlay)
//        gerberThread!!.stop()
//        overlayManager.remove(overlay)
    }
}
