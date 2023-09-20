package net.runelite.client.plugins.microbot.staticwalker.pathfinder

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.runelite.client.RuneLite
import net.runelite.client.plugins.microbot.Microbot
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.logging.Logger

class WorldDataDownloader {
    companion object {
        val logger = Logger.getLogger(WorldDataDownloader::class.java.name)
        val githubReleaseUrl = "https://api.github.com/repos/GriffinBoris/OSRSWorldData/releases/latest"
//        val worldDataFile = File(RuneLite.CACHE_DIR, "worlddata.public.sqlite3")
        val worldDataFile = File("/home/griffin/PycharmProjects/OSRSWorld/world/", "db.sqlite3")
        val worldDataVersionFile = File(RuneLite.CACHE_DIR, "worlddata.public.version")
    }

    fun run() {
        if (Microbot.getDisableWalkerUpdateForKotlin()) {
            logger.info("Static Walker: Updating disabled")
            logger.warning("Static Walker: Warning! Using the static walker with this option enabled will fail if no previous world data exists in cache.")
            return
        }

        val latestReleaseInformation = getLatestReleaseInformation()
        val tagVersion = latestReleaseInformation.get("tag_name").asString
        val worldDataUrl = latestReleaseInformation.get("assets").asJsonArray[0].asJsonObject.get("browser_download_url").asString

        if (!worldDataFile.exists() || !worldDataVersionFile.exists() || worldDataVersionFile.readText() != tagVersion) {
            logger.info("Static Walker: Updating world data")
            downloadWorldData(worldDataUrl)
            writeVersionFile(tagVersion)
        } else {
            logger.info("Static Walker: Up to date")
        }
    }

    private fun getLatestReleaseInformation(): JsonObject {
        val request = Request.Builder().url(githubReleaseUrl).build()

        try {
            OkHttpClient().newCall(request).execute().use { response ->
                logger.info("Static Walker: Checking latest release information")

                if (!response.isSuccessful) {
                    logger.warning("Static Walker: Failed to check latest release information")
                    throw IOException("Failed to check latest release information")
                }

                val inputStream: InputStream = response.body()?.byteStream() ?: throw IOException("No response body when checking latest release information")
                val responseBody = inputStream.bufferedReader().use { it.readText() }
                return JsonParser().parse(responseBody).getAsJsonObject()

            }
        } catch (ex: IOException) {
            throw IOException(ex)
        }
    }

    private fun downloadWorldData(worldDataUrl: String) {
        val request = Request.Builder().url(worldDataUrl).build()

        try {
            OkHttpClient().newCall(request).execute().use { response ->
                logger.info("Static Walker: Downloading world data")

                if (!response.isSuccessful) {
                    logger.warning("Static Walker: Failed to download world data")
                    throw IOException("unsuccessful response looking up worlds")
                }

                val inputStream: InputStream = response.body()?.byteStream() ?: throw IOException("No response body when downloading world data")
                FileOutputStream(worldDataFile, false).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        } catch (ex: IOException) {
            throw IOException(ex)
        }
    }

    private fun writeVersionFile(version: String) {
        worldDataVersionFile.writeText(version)
    }

}