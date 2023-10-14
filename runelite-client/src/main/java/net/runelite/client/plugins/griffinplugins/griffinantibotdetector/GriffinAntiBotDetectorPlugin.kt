package net.runelite.client.plugins.griffinplugins.griffintrainer

import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.plugins.PluginDescriptor.Griffin
import net.runelite.client.plugins.griffinplugins.griffinantibotdetector.GriffinAntiBotDetectorScript
import javax.inject.Inject


@PluginDescriptor(name = Griffin + GriffinAntiBotDetectorPlugin.CONFIG_GROUP, enabledByDefault = false)
class GriffinAntiBotDetectorPlugin : Plugin() {
    companion object {
        const val CONFIG_GROUP = "Anti Bot Detector"
    }

    @Inject
    lateinit var antiBotScript: GriffinAntiBotDetectorScript

    override fun startUp() {
        antiBotScript.run()
    }

    override fun shutDown() {
        antiBotScript.shutdown()
    }
}
