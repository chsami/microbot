package net.runelite.client.plugins.griffinplugins.griffintrickortreat

import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.plugins.PluginDescriptor.Griffin
import net.runelite.client.ui.overlay.OverlayManager
import javax.inject.Inject

@PluginDescriptor(name = Griffin + GriffinTrickOrTreatPlugin.CONFIG_GROUP, enabledByDefault = false)
class GriffinTrickOrTreatPlugin : Plugin() {
    companion object {
        const val CONFIG_GROUP = "Trick Or Treat"
    }

    @Inject
    private lateinit var overlayManager: OverlayManager

    @Inject
    private lateinit var overlay: GriffinTrickOrTreatOverlay

    lateinit var script: GriffinTrickOrTreatScript

    override fun startUp() {
        GriffinTrickOrTreatScript.killScript = false
        script = GriffinTrickOrTreatScript()
        overlayManager.add(overlay)
        script.run()
    }

    override fun shutDown() {
        GriffinTrickOrTreatScript.killScript = true
        script.shutdown()
        overlayManager.remove(overlay)
    }
}
