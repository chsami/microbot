package net.runelite.client.plugins.griffinplugins.griffintrainer

import com.google.gson.Gson
import com.google.gson.JsonObject
import net.runelite.client.RuneLite
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.plugins.PluginDescriptor.Griffin
import net.runelite.client.plugins.microbot.Microbot
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.Instant
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

    private val BASE_HTTP_URL = HttpUrl.parse("https://api.prd.osrsbotdetector.com")
    private lateinit var okHttpClient: OkHttpClient

    override fun startUp() {
        okHttpClient = OkHttpClient()
        okHttpClient.newBuilder()
            .pingInterval(0, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addNetworkInterceptor { chain ->
                val headerRequest: Request = chain.request()
                    .newBuilder()
                    .header("Request-Epoch", generateRequestEpoch())
                    .header("Plugin-Version", "latest")
                    .header("User-Agent", RuneLite.USER_AGENT)
                    .build()
                chain.proceed(headerRequest)
            }
            .build()

        process()
    }

    override fun shutDown() {
        scheduledExecutorService.shutdownNow()
    }

    private fun process() {
        scheduledExecutorService.scheduleWithFixedDelay({
            val fiveMinutes = 1000 * 60 * 5
            val tenMinutes = 1000 * 60 * 10

            Thread.sleep(Random.nextInt(fiveMinutes, tenMinutes).toLong())

            val results = getLocalPlayerPredictionResults()
            results ?: return@scheduleWithFixedDelay
            reportSelfAsRealPlayer(results)

        }, 5, 25, TimeUnit.MINUTES)
    }

    private fun generateRequestEpoch(): String {
        return Instant.now().getEpochSecond().toString()
    }

    private fun getReportingPlayerName(): String? {
        val reportingPlayerName = "AnonymousUser_${UUID.randomUUID()}"

        if (Random.nextInt(1, 101) < 95) {
            return reportingPlayerName
        }

        val randomPlayer = Microbot.getClientForKotlin().players.randomOrNull()
        randomPlayer ?: return reportingPlayerName
        return randomPlayer.name
    }

    private fun getLocalPlayerPredictionResults(): JsonObject? {
        val gson = Gson()
        val localPlayer = Microbot.getClientForKotlin().localPlayer

        val request: Request = Request.Builder()
            .url(
                BASE_HTTP_URL!!.newBuilder()
                    .addPathSegment("v1")
                    .addPathSegment("prediction")
                    .addQueryParameter("name", localPlayer.name)
                    .addQueryParameter("breakdown", "true")
                    .build()
            )
            .build()

        okHttpClient.newCall(request).execute().use { response ->

            if (response.code() == 404) {
                return null
            }

            if (response.isSuccessful) {
                response.body()?.let {
                    return gson.fromJson(it.string(), JsonObject::class.java)
                }

            } else {
                throw Exception("Error obtaining player prediction data")
            }
        }
        return null
    }

    private fun reportSelfAsRealPlayer(results: JsonObject) {
        val breakdownResults = results.get("predictions_breakdown").asJsonObject

        val postData = JsonObject()
        postData.addProperty("player_name", getReportingPlayerName())
        postData.addProperty("vote", 1)
        postData.addProperty("prediction", "Real_Player")
        postData.addProperty("confidence", results.get("prediction_confidence").asFloat)
        postData.addProperty("subject_id", results.get("player_id").asString)
        postData.addProperty("proposed_label", "Real_Player")
        postData.addProperty("proposed_label_confidence", breakdownResults.get("Real_Player").asFloat)
        postData.addProperty("feedback_text", "")

        val request: Request = Request.Builder()
            .url(
                BASE_HTTP_URL!!.newBuilder()
                    .addPathSegment("v1")
                    .addPathSegment("feedback/")
                    .build()
            )
            .post(
                okhttp3.RequestBody.create(
                    okhttp3.MediaType.parse("application/json"),
                    postData.toString()
                )
            )
            .build()

        okHttpClient.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                println("Successfully reported player as real")
            } else {
                println("Error reporting player as real")
            }
        }
    }
}
