package net.runelite.client.plugins.griffinplugins.worlddatacollection

import net.runelite.client.plugins.microbot.util.Global
import java.util.concurrent.Executors

class WorldDataCollectionThread : Thread() {
    companion object {
        var started = 0
        var completed = 0
    }

    val executor = Executors.newFixedThreadPool(100)
    override fun run() {
        started = 0
        completed = 0

        while (true) {
            started++
            executor.execute {
                val tileCollector = TileCollector()
                tileCollector.collect()
                completed++
            }
            Global.sleep(3000)
        }
    }
}
