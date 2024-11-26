package net.runelite.client.plugins.microbot;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import net.runelite.client.RuneLiteProperties;
import okhttp3.*;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static net.runelite.http.api.RuneLiteAPI.JSON;

/**
 * Class that communicates with the microbot api
 */
public class MicrobotApi {

    private final OkHttpClient client;
    private final Gson gson;

    private final String microbotApiUrl = "https://microbot-api.azurewebsites.net/api";
    @Inject
    MicrobotApi(OkHttpClient client, Gson gson) {
        this.client = client;
        this.gson = gson;
    }

    /**
     * Opens a new session by sending a request to the microbot API and retrieves the session UUID.
     *
     * Steps:
     * 1. Sends an HTTP GET request to the `/session` endpoint.
     * 2. Reads the response body as a stream and parses it as a `UUID` using Gson.
     * 3. If the response contains invalid JSON or the parsed UUID is invalid, an exception is thrown.
     * 4. Uses a try-with-resources block to ensure the HTTP response and input stream are properly closed.
     *
     * @return the UUID of the newly opened session.
     * @throws IOException if the HTTP request fails, the response body is invalid, or the UUID parsing fails.
     */
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

    /**
     * Sends a ping request to the microbot API to update the session status.
     *
     * Steps:
     * 1. Constructs an HTTP GET request with query parameters:
     *    - `sessionId`: The unique identifier of the session (UUID).
     *    - `isLoggedIn`: A boolean indicating if the user is logged in.
     *    - `version`: The version of the microbot (retrieved from `RuneLiteProperties`).
     * 2. Sends the request using the HTTP client and checks the response.
     * 3. If the response is unsuccessful, an `IOException` is thrown to indicate the failure.
     * 4. Uses a try-with-resources block to automatically close the HTTP response.
     *
     * @param uuid the unique identifier of the session to ping.
     * @param loggedIn the login status to send with the ping.
     * @throws IOException if the HTTP request fails or the response is unsuccessful.
     */
    public void microbotPing(UUID uuid, boolean loggedIn) throws IOException {
        try (Response response = client.newCall(new Request.Builder().url(microbotApiUrl + "/session?sessionId=" + uuid.toString()
                + "&isLoggedIn=" + loggedIn
                + "&version=" + RuneLiteProperties.getMicrobotVersion()).build()).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unsuccessful ping");
            }
        }
    }

    /**
     * Sends a DELETE request to remove a microbot session by its UUID.
     *
     * Steps:
     * 1. Constructs an HTTP DELETE request to the endpoint with the session ID as a query parameter.
     * 2. Sends the request using the HTTP client and closes the response to free resources.
     *
     * @param uuid the unique identifier of the session to be deleted.
     * @throws IOException if an I/O error occurs during the HTTP request.
     */
    public void microbotDelete(UUID uuid) throws IOException {
        Request request = new Request.Builder()
                .delete()
                .url(microbotApiUrl + "/session?sessionId=" + uuid)
                .build();

        client.newCall(request).execute().close();
    }

    /**
     * Sends the runtime statistics of active scripts to a remote API endpoint.
     *
     * This method collects the runtime information of all active scripts, converts it into a map of
     * script names and their corresponding runtimes in minutes, and sends the data to a predefined
     * API endpoint as a JSON payload.
     *
     * Key Steps:
     * 1. If the application is in debug mode, the method returns early without executing.
     * 2. Collects active script runtime data:
     *    - The script class names are used as keys in the map.
     *    - The runtime of each script is converted to minutes and used as the map's values.
     * 3. Prepares and sends an HTTP POST request with the runtime data serialized into JSON format.
     *    - The payload is sent to the endpoint defined by `microbotApiUrl + "/script/runtime"`.
     *    - The API request uses the `gson` library to serialize the `ScriptStats` object.
     * 4. Handles the response:
     *    - If the response indicates failure, an `IOException` is thrown to signal an error.
     *    - The HTTP client connection is closed automatically using try-with-resources.
     *
     * @throws IOException if the HTTP request fails or if the response indicates an error.
     */
    public void sendScriptStatistics() throws IOException {
        if (Microbot.isDebug()) return;

        Map<String, Integer> scriptRuntimeMap = Microbot.getActiveScripts().stream()
                .collect(Collectors.toMap(
                        x -> x.getClass().getSimpleName(), // Key: Simple name of the class
                        x -> (int) x.getRunTime().toMinutes()   // Value: Runtime in minutes
                ));
        try (Response response = client.newCall(new Request.Builder().url(microbotApiUrl + "/script/runtime")
                .post(RequestBody.create(JSON, gson.toJson(new ScriptStats(scriptRuntimeMap))))
                .build())
                .execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unsuccessful in sending runtime statistics to api");
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