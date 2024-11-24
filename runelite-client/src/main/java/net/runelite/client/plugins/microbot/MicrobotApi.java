package net.runelite.client.plugins.microbot;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import net.runelite.client.RuneLiteProperties;
import okhttp3.*;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static net.runelite.http.api.RuneLiteAPI.JSON;

public class MicrobotApi {

    private final OkHttpClient client;
    private final HttpUrl sessionUrl;
    private final Gson gson;

    private final String microbotApiUrl = "https://microbot-api.azurewebsites.net/api";


    @Inject
    MicrobotApi(OkHttpClient client, @Named("runelite.session") HttpUrl sessionUrl, Gson gson) {
        this.client = client;
        this.sessionUrl = sessionUrl;
        this.gson = gson;
    }

    public UUID microbotOpen() throws IOException {
        try (Response response = client.newCall(new Request.Builder().url(microbotApiUrl + "/session").build()).execute()) {
            ResponseBody body = response.body();

            InputStream in = body.byteStream();
            return gson.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), UUID.class);
        } catch (JsonParseException | IllegalArgumentException ex) // UUID.fromString can throw IllegalArgumentException
        {
            throw new IOException(ex);
        }
    }

    public void microbotPing(UUID uuid, boolean loggedIn) throws IOException {
        try (Response response = client.newCall(new Request.Builder().url(microbotApiUrl + "/session?sessionId=" + uuid.toString()
                + "&isLoggedIn=" + loggedIn
                + "&version=" + RuneLiteProperties.getMicrobotVersion()).build()).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unsuccessful ping");
            }
        }
    }

    public void microbotDelete(UUID uuid) throws IOException {
        Request request = new Request.Builder()
                .delete()
                .url(microbotApiUrl + "/session?sessionId=" + uuid)
                .build();

        client.newCall(request).execute().close();
    }

    public void sendScriptStatistics() throws IOException {
        if (Microbot.isDebug()) return;

        Map<String, Integer> scriptRuntimeMap = Microbot.getActiveScripts().stream()
                .collect(Collectors.toMap(
                        x -> x.getClass().getSimpleName(), // Key: Simple name of the class
                        x -> (int) x.getRunTime().toMinutes()   // Value: Runtime in seconds
                ));
        try (Response response = client.newCall(new Request.Builder().url(microbotApiUrl + "/script/runtime")
                .post(RequestBody.create(JSON, gson.toJson(new ScriptStats(scriptRuntimeMap))))
                .build())
                .execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unsuccessful ping");
            }
        }
    }
}


class ScriptStats {
    Map<String, Integer> scriptRunTimes;

    ScriptStats(Map<String, Integer> scriptRunTimes) {
        this.scriptRunTimes = scriptRunTimes;
    }
}