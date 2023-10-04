package net.runelite.client.plugins.griffinplugins.griffinantibotdetector

import com.google.gson.Gson
import com.google.gson.JsonObject
import net.runelite.client.RuneLite
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.Instant
import java.util.concurrent.TimeUnit

class BotDetectorAPI {

    private val baseUrl = "api.prd.osrsbotdetector.com"
    private var okHttpClient: OkHttpClient

    init {
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
    }

    private fun generateRequestEpoch(): String {
        return Instant.now().getEpochSecond().toString()
    }

    fun predict(playerName: String): Prediction? {
        val url = HttpUrl.Builder()
            .scheme("https")
            .host(baseUrl)
            .addPathSegment("v1")
            .addPathSegment("prediction")
            .addQueryParameter("name", playerName)
            .addQueryParameter("breakdown", "true")
            .build()

        val request: Request = Request.Builder().url(url).build()
        okHttpClient.newCall(request).execute().use { response ->
            if (response.code() == 404) return null
            if (!response.isSuccessful) throw Exception("Unexpected code $response")

            response.body()?.let {
                val data = Gson().fromJson(it.string(), JsonObject::class.java)
                return Prediction(
                    data.get("player_id").asInt,
                    data.get("player_name").asString,
                    data.get("prediction_label").asString,
                    data.get("prediction_confidence").asFloat,
                    data.get("predictions_breakdown").getAsJsonObject().get("Real_Player").asFloat
                )
            } ?: throw Exception("Response body is null")
        }
    }

    fun report(reporterPlayerName: String, prediction: Prediction) {
        val postData = JsonObject()
        postData.addProperty("player_name", reporterPlayerName)
        postData.addProperty("vote", 1)
        postData.addProperty("prediction", "Real_Player")
        postData.addProperty("confidence", prediction.predictionConfidence)
        postData.addProperty("subject_id", prediction.playerId)
        postData.addProperty("proposed_label", "Real_Player")
        postData.addProperty("proposed_label_confidence", prediction.realPlayerConfidence)
        postData.addProperty("feedback_text", "")

        val url = HttpUrl.Builder()
            .scheme("https")
            .host(baseUrl)
            .addPathSegment("v1")
            .addPathSegment("feedback/")
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .post(
                okhttp3.RequestBody.create(
                    okhttp3.MediaType.parse("application/json"),
                    postData.toString()
                )
            )
            .build()

        okHttpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("Unexpected code $response")
        }
    }
}

data class Prediction(
    val playerId: Int,
    val playerName: String,
    val predictionLabel: String,
    val predictionConfidence: Float,
    val realPlayerConfidence: Float
)