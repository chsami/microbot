/*
 * Copyright (c) 2021, Ferrariic, Seltzer Bro, Cyborger1
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.griffinplugins.botdetector.http;
import net.runelite.client.plugins.griffinplugins.botdetector.model.*;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.kit.KitType;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Class containing various methods to interact with the Bot Detector API.
 */
@Slf4j
@Singleton
public class BotDetectorClient
{
	private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	private static final String API_VERSION_FALLBACK_WORD = "latest";
	private static final HttpUrl BASE_HTTP_URL = HttpUrl.parse(
		System.getProperty("BotDetectorAPIPath", "https://api.prd.osrsbotdetector.com"));
	private static final Supplier<String> CURRENT_EPOCH_SUPPLIER = () -> String.valueOf(Instant.now().getEpochSecond());

	@Getter
	@AllArgsConstructor
	private enum ApiPath
	{
		DETECTION("v1/report"),
		PLAYER_STATS_PASSIVE("v1/report/count"),
		PLAYER_STATS_MANUAL("v1/report/manual/count"),
		PLAYER_STATS_FEEDBACK("v1/feedback/count"),
		PREDICTION("v1/prediction"),
		FEEDBACK("v1/feedback/"),
		VERIFY_DISCORD("site/discord_user/")
		;

		final String path;
	}

	public OkHttpClient okHttpClient;

	@Inject
	private Gson gson;

	@Getter
	@Setter
	private String pluginVersion;

	private final Supplier<String> pluginVersionSupplier = () ->
		(pluginVersion != null && !pluginVersion.isEmpty()) ? pluginVersion : API_VERSION_FALLBACK_WORD;

	/**
	 * Constructs a base URL for the given {@code path}.
	 * @param path The path to get the base URL for.
	 * @param addVersion Whether to add a version prefix.
	 * @return The base URL for the given {@code path}.
	 */
	private HttpUrl getUrl(ApiPath path, boolean addVersion)
	{
		HttpUrl.Builder builder = BASE_HTTP_URL.newBuilder();

		if (addVersion)
		{
			builder.addPathSegment(pluginVersionSupplier.get());
		}

		return builder.addPathSegments(path.getPath()).build();
	}

	/**
	 * Constructs a base URL for the given {@code path} with no version prefix.
	 * @param path The path to get the base URL for
	 * @return The base URL for the given {@code path}.
	 */
	private HttpUrl getUrl(ApiPath path)
	{
		return getUrl(path, false);
	}

	@Inject
	public BotDetectorClient(OkHttpClient rlClient)
	{
		okHttpClient = rlClient.newBuilder()
			.pingInterval(0, TimeUnit.SECONDS)
			.connectTimeout(30, TimeUnit.SECONDS)
			.readTimeout(30, TimeUnit.SECONDS)
			.addNetworkInterceptor(chain ->
			{
				Request headerRequest = chain.request()
					.newBuilder()
					.header("Request-Epoch", CURRENT_EPOCH_SUPPLIER.get())
					.header("Plugin-Version", pluginVersionSupplier.get())
					.build();
				return chain.proceed(headerRequest);
			})
			.build();
	}

	/**
	 * Sends a single {@link PlayerSighting} to the API to be persisted in the Bot Detector database.
	 * @param sighting The sighting to send.
	 * @param uploaderName The user's player name (See {@link BotDetectorPlugin#getUploaderName()}).
	 * @param manual Whether or not the given sighting is to be manually flagged as a bot by the user.
	 * @return A future that will eventually return a boolean indicating success.
	 */
	public CompletableFuture<Boolean> sendSighting(PlayerSighting sighting, String uploaderName, boolean manual)
	{
		return sendSightings(ImmutableList.of(sighting), uploaderName, manual);
	}

	/**
	 * Sends a collection of {@link PlayerSighting}s to the API to be persisted in the Bot Detector database.
	 * @param sightings The collection of sightings to send.
	 * @param uploaderName The user's player name (See {@link BotDetectorPlugin#getUploaderName()}).
	 * @param manual Whether or not the given sightings are to be manually flagged as bots by the user.
	 * @return A future that will eventually return a boolean indicating success.
	 */
	public CompletableFuture<Boolean> sendSightings(Collection<PlayerSighting> sightings, String uploaderName, boolean manual)
	{
		List<PlayerSightingWrapper> wrappedList = sightings.stream()
			.map(p -> new PlayerSightingWrapper(uploaderName, manual, p)).collect(Collectors.toList());

		Gson bdGson = gson.newBuilder().enableComplexMapKeySerialization()
			.registerTypeAdapter(PlayerSightingWrapper.class, new PlayerSightingWrapperSerializer())
			.registerTypeAdapter(KitType.class, new KitTypeSerializer())
			.registerTypeAdapter(Boolean.class, new BooleanToZeroOneConverter())
			.registerTypeAdapter(Instant.class, new InstantSecondsConverter())
			.create();

		Request request = new Request.Builder()
			.url(getUrl(ApiPath.DETECTION).newBuilder()
				.build())
			.post(RequestBody.create(JSON, bdGson.toJson(wrappedList)))
			.build();

		CompletableFuture<Boolean> future = new CompletableFuture<>();
		okHttpClient.newCall(request).enqueue(new Callback()
		{
			@Override
			public void onFailure(Call call, IOException e)
			{
				log.warn("Error sending player sighting data", e);
				future.completeExceptionally(e);
			}

			@Override
			public void onResponse(Call call, Response response)
			{
				try
				{
					if (!response.isSuccessful())
					{
						throw getIOException(response);
					}

					future.complete(true);
				}
				catch (IOException e)
				{
					log.warn("Error sending player sighting data", e);
					future.completeExceptionally(e);
				}
				finally
				{
					response.close();
				}
			}
		});

		return future;
	}

	/**
	 * Tokenized API route to verify the given player name and code pair for Discord linking.
	 * @param token The auth token to use.
	 * @param nameToVerify The player name up for verification.
	 * @param code The code given by the player.
	 * @return A future that will eventually return a boolean indicating success.
	 */
	public CompletableFuture<Boolean> verifyDiscord(String token, String nameToVerify, String code)
	{
		Request request = new Request.Builder()
			.url(getUrl(ApiPath.VERIFY_DISCORD, true).newBuilder()
				.addPathSegment(token)
				.build())
			.post(RequestBody.create(JSON, gson.toJson(new DiscordVerification(nameToVerify, code))))
			.build();

		CompletableFuture<Boolean> future = new CompletableFuture<>();
		okHttpClient.newCall(request).enqueue(new Callback()
		{
			@Override
			public void onFailure(Call call, IOException e)
			{
				log.warn("Error verifying discord user", e);
				future.completeExceptionally(e);
			}

			@Override
			public void onResponse(Call call, Response response)
			{
				try
				{
					// TODO: Differenciate between bad token and failed auth (return false)
					if (!response.isSuccessful())
					{
						if (response.code() == 401)
						{
							throw new UnauthorizedTokenException("Invalid or unauthorized token for operation");
						}
						else
						{
							throw getIOException(response);
						}
					}

					future.complete(true);
				}
				catch (UnauthorizedTokenException | IOException e)
				{
					log.warn("Error verifying discord user", e);
					future.completeExceptionally(e);
				}
				finally
				{
					response.close();
				}
			}
		});

		return future;
	}

	/**
	 * Sends a feedback to the API for the given prediction.
	 * @param pred The prediction object to give a feedback for.
	 * @param uploaderName The user's player name (See {@link BotDetectorPlugin#getUploaderName()}).
	 * @param proposedLabel The user's proposed label and feedback.
	 * @param feedbackText The user's feedback text to include with the feedback.
	 * @return A future that will eventually return a boolean indicating success.
	 */
	public CompletableFuture<Boolean> sendFeedback(Prediction pred, String uploaderName, FeedbackPredictionLabel proposedLabel, String feedbackText)
	{
		Request request = new Request.Builder()
			.url(getUrl(ApiPath.FEEDBACK))
			.post(RequestBody.create(JSON, gson.toJson(new PredictionFeedback(
				uploaderName,
				proposedLabel.getFeedbackValue().getApiValue(),
				pred.getPredictionLabel(),
				Optional.ofNullable(pred.getConfidence()).orElse(0.0),
				pred.getPlayerId(),
				proposedLabel.getLabel(),
				proposedLabel.getLabelConfidence(),
				feedbackText
			)))).build();

		CompletableFuture<Boolean> future = new CompletableFuture<>();
//		okHttpClient.newCall(request).enqueue(new Callback()
//		{
//			@Override
//			public void onFailure(Call call, IOException e)
//			{
//				log.warn("Error sending prediction feedback", e);
//				future.completeExceptionally(e);
//			}
//
//			@Override
//			public void onResponse(Call call, Response response)
//			{
//				try
//				{
//					if (!response.isSuccessful())
//					{
//						throw getIOException(response);
//					}
//
//					future.complete(true);
//				}
//				catch (IOException e)
//				{
//					log.warn("Error sending prediction feedback", e);
//					future.completeExceptionally(e);
//				}
//				finally
//				{
//					response.close();
//				}
//			}
//		});

		return future;
	}

	/**
	 * Requests a bot prediction for the given {@code playerName}.
	 * Breakdown will be provided by default in special cases (see {@link BotDetectorClient#requestPrediction(String, boolean)}).
	 * @param playerName The player name to predict.
	 * @return A future that will eventually return the player's bot prediction.
	 */
	public CompletableFuture<Prediction> requestPrediction(String playerName)
	{
		return requestPrediction(playerName, true);
	}

	/**
	 * Requests a bot prediction for the given {@code playerName}.
	 * @param playerName The player name to predict.
	 * @param receiveBreakdownOnSpecialCases Whether to receive a prediction breakdown in special cases, such as "Player Stats Too Low".
	 * @return A future that will eventually return the player's bot prediction.
	 */
	public CompletableFuture<Prediction> requestPrediction(String playerName, boolean receiveBreakdownOnSpecialCases)
	{
		Request request = new Request.Builder()
			.url(getUrl(ApiPath.PREDICTION).newBuilder()
				.addQueryParameter("name", playerName)
				.addQueryParameter("breakdown", Boolean.toString(receiveBreakdownOnSpecialCases))
				.build())
			.build();

		CompletableFuture<Prediction> future = new CompletableFuture<>();
		okHttpClient.newCall(request).enqueue(new Callback()
		{
			@Override
			public void onFailure(Call call, IOException e)
			{
				log.warn("Error obtaining player prediction data", e);
				future.completeExceptionally(e);
			}

			@Override
			public void onResponse(Call call, Response response)
			{
				try
				{
					future.complete(processResponse(gson, response, Prediction.class));
				}
				catch (IOException e)
				{
					log.warn("Error obtaining player prediction data", e);
					future.completeExceptionally(e);
				}
				finally
				{
					response.close();
				}
			}
		});

		return future;
	}

	/**
	 * Requests the uploading contributions for the given {@code playerName}.
	 * @param playerName The name to request the uploading contributions.
	 * @return A future that will eventually return the player's statistics.
	 */
	public CompletableFuture<Map<PlayerStatsType, PlayerStats>> requestPlayerStats(String playerName)
	{
		Gson bdGson = gson.newBuilder()
			.registerTypeAdapter(boolean.class, new BooleanToZeroOneConverter())
			.create();

		Request requestP = new Request.Builder()
			.url(getUrl(ApiPath.PLAYER_STATS_PASSIVE).newBuilder()
				.addQueryParameter("name", playerName)
				.build())
			.build();

		Request requestM = new Request.Builder()
			.url(getUrl(ApiPath.PLAYER_STATS_MANUAL).newBuilder()
				.addQueryParameter("name", playerName)
				.build())
			.build();

		Request requestF = new Request.Builder()
			.url(getUrl(ApiPath.PLAYER_STATS_FEEDBACK).newBuilder()
				.addQueryParameter("name", playerName)
				.build())
			.build();

		CompletableFuture<Collection<PlayerStatsAPIItem>> passiveFuture = new CompletableFuture<>();
		CompletableFuture<Collection<PlayerStatsAPIItem>> manualFuture = new CompletableFuture<>();
		CompletableFuture<Collection<PlayerStatsAPIItem>> feedbackFuture = new CompletableFuture<>();

		okHttpClient.newCall(requestP).enqueue(new PlayerStatsCallback(passiveFuture, bdGson));
		okHttpClient.newCall(requestM).enqueue(new PlayerStatsCallback(manualFuture, bdGson));
		okHttpClient.newCall(requestF).enqueue(new PlayerStatsCallback(feedbackFuture, bdGson));

		CompletableFuture<Map<PlayerStatsType, PlayerStats>> finalFuture = new CompletableFuture<>();

		// Doing this so we log only the first future failing, not all 3 within the callback.
		CompletableFuture.allOf(passiveFuture, manualFuture, feedbackFuture).whenComplete((v, e) ->
		{
			if (e != null)
			{
				// allOf will send a CompletionException when one of the futures fail, just get the cause.
				log.warn("Error obtaining player stats data", e.getCause());
				finalFuture.completeExceptionally(e.getCause());
			}
			else
			{
				finalFuture.complete(processPlayerStats(
					passiveFuture.join(), manualFuture.join(), feedbackFuture.join()));
			}
		});

		return finalFuture;
	}

	/**
	 * Utility class intended for {@link BotDetectorClient#requestPlayerStats(String)}.
	 */
	private class PlayerStatsCallback implements Callback
	{
		private final CompletableFuture<Collection<PlayerStatsAPIItem>> future;
		private final Gson gson;

		public PlayerStatsCallback(CompletableFuture<Collection<PlayerStatsAPIItem>> future, Gson gson)
		{
			this.future = future;
			this.gson = gson;
		}

		@Override
		public void onFailure(Call call, IOException e)
		{
			future.completeExceptionally(e);
		}

		@Override
		public void onResponse(Call call, Response response) throws IOException
		{
			try
			{
				future.complete(processResponse(gson, response,
					new TypeToken<Collection<PlayerStatsAPIItem>>()
					{
					}.getType()));
			}
			catch (IOException e)
			{
				future.completeExceptionally(e);
			}
			finally
			{
				response.close();
			}
		}
	}

	/**
	 * Processes the body of the given response and parses out the contained JSON object.
	 * @param gson The {@link Gson} instance to use for parsing the JSON object in the {@code response}.
	 * @param response The response containing the object to parse in {@link Response#body()}.
	 * @param type The type of the JSON object to parse.
	 * @param <T> The type of the JSON object to parse, inferred from {@code type}.
	 * @return The parsed object, or {@code null} if the API returned a 404.
	 * @throws IOException If the response is unsuccessful or the {@link Response#body()} contains malformed data.
	 */
	private <T> T processResponse(Gson gson, Response response, Type type) throws IOException
	{
		if (!response.isSuccessful())
		{
			if (response.code() == 404)
			{
				return null;
			}

			throw getIOException(response);
		}

		try
		{
			return gson.fromJson(response.body().string(), type);
		}
		catch (IOException | IllegalStateException | JsonSyntaxException ex)
		{
			throw new IOException("Error parsing API response body", ex);
		}
	}

	/**
	 * Gets the {@link IOException} to return for when {@link Response#isSuccessful()} returns false.
	 * @param response The response object to get the {@link IOException} for.
	 * @return The {@link IOException} with the appropriate message for the given {@code response}.
	 */
	private IOException getIOException(Response response)
	{
		int code = response.code();
		if (code >= 400 && code < 500)
		{
			try
			{
				String body = response.body().string();
				try
				{
					Map<String, String> map = gson.fromJson(body,
						new TypeToken<Map<String, String>>()
						{
						}.getType());

					// "error" has priority if it exists, else use "detail" (FastAPI)
					String error = map.get("error");
					if (Strings.isNullOrEmpty(error))
					{
						error = map.getOrDefault("detail", "Unknown " + code + " error from API");
					}
					return new IOException(error);
				}
				catch (JsonSyntaxException ex)
				{
					// If can't parse, just log the response body
					// TODO: Parse actual error info received from FastAPI (details -> loc, msg, ctx, etc.) especially for 422 errors
					log.warn("Received HTTP error code " + code + " from API with the following response body:\n" + body);
					return new IOException("Error " + code + ", see log for more info");
				}
			}
			catch (IOException ex)
			{
				return new IOException("Error " + code + " with no error info", ex);
			}
		}

		return new IOException("Error " + code + " from API");
	}

	/**
	 * Collects the given {@link PlayerStatsAPIItem} into a combined map that the plugin expects.
	 * @param passive The passive usage stats from the API.
	 * @param manual The manual flagging stats from the API.
	 * @param feedback The feedback stats from the API.
	 * @return The combined processed map expected by the plugin.
	 */
	private Map<PlayerStatsType, PlayerStats> processPlayerStats(Collection<PlayerStatsAPIItem> passive, Collection<PlayerStatsAPIItem> manual, Collection<PlayerStatsAPIItem> feedback)
	{
		if (passive == null || manual == null || feedback == null)
		{
			return null;
		}

		PlayerStats passiveStats = countStats(passive, false);
		PlayerStats manualStats = countStats(manual, true);
		PlayerStats feedbackStats = countStats(feedback, false);

		PlayerStats totalStats = PlayerStats.builder()
			.namesUploaded(passiveStats.getNamesUploaded() + manualStats.getNamesUploaded())
			.confirmedBans(passiveStats.getConfirmedBans() + manualStats.getConfirmedBans())
			.possibleBans(passiveStats.getPossibleBans() + manualStats.getPossibleBans())
			.feedbackSent(feedbackStats.getNamesUploaded()) // Might change the total/passive/manual thing in the future.
			.build();

		return ImmutableMap.of(
			PlayerStatsType.TOTAL, totalStats,
			PlayerStatsType.PASSIVE, passiveStats,
			PlayerStatsType.MANUAL, manualStats
		);
	}

	/**
	 * Utility function for {@link BotDetectorClient#processPlayerStats(Collection, Collection, Collection)}.
	 * Compile each element from the API into a {@link PlayerStats} object.
	 * @param fromAPI The returned collections of player stats from the API to accumulate.
	 * @param countIncorrect Intended for manual flagging stats. If true, count confirmed players into {@link PlayerStats#getIncorrectFlags()}.
	 * @return The stats object with accumulated counts from the API.
	 */
	private PlayerStats countStats(Collection<PlayerStatsAPIItem> fromAPI, boolean countIncorrect)
	{
		long total = 0, confirmedBans = 0, possibleBans = 0, incorrectFlags = 0;
		for (PlayerStatsAPIItem item : fromAPI)
		{
			if (item.isBanned())
			{
				confirmedBans += item.getCount();
			}
			else
			{
				if (item.isPossibleBanned())
				{
					possibleBans += item.getCount();
				}

				if (countIncorrect && item.isPlayer())
				{
					incorrectFlags += item.getCount();
				}
			}

			total += item.getCount();
		}

		return PlayerStats.builder()
			.namesUploaded(total)
			.confirmedBans(confirmedBans)
			.possibleBans(possibleBans)
			.incorrectFlags(incorrectFlags)
			.build();
	}

	/**
	 * For use with {@link PlayerSightingWrapperSerializer}.
	 */
	@Value
	private static class PlayerSightingWrapper
	{
		String uploaderName;
		boolean manualDetect;
		PlayerSighting sightingData;
	}

	@Value
	private static class DiscordVerification
	{
		@SerializedName("player_name")
		String nameToVerify;
		String code;
	}

	@Value
	private static class PredictionFeedback
	{
		@SerializedName("player_name")
		String playerName;
		int vote;
		@SerializedName("prediction")
		String predictionLabel;
		// Important: API requires this to be non-null!
		@SerializedName("confidence")
		double predictionConfidence;
		@SerializedName("subject_id")
		long targetId;
		@SerializedName("proposed_label")
		String proposedLabel;
		@SerializedName("proposed_label_confidence")
		Double proposedLabelConfidence;
		@SerializedName("feedback_text")
		String feedbackText;
	}

	@Value
	private static class PlayerStatsAPIItem
	{
		@SerializedName("possible_ban")
		boolean possibleBanned;
		@SerializedName("confirmed_ban")
		boolean banned;
		@SerializedName("confirmed_player")
		boolean player;
		long count;
	}

	/**
	 * Wrapper around the {@link PlayerSighting}'s json serializer.
	 * Adds the reporter name as an element on the same level as the {@link PlayerSighting}'s fields.
	 */
	private static class PlayerSightingWrapperSerializer implements JsonSerializer<PlayerSightingWrapper>
	{
		@Override
		public JsonElement serialize(PlayerSightingWrapper src, Type typeOfSrc, JsonSerializationContext context)
		{
			JsonElement json = context.serialize(src.getSightingData());
			JsonObject jo = json.getAsJsonObject();
			jo.addProperty("reporter", src.getUploaderName());
			jo.add("manual_detect", context.serialize(src.isManualDetect()));
			return json;
		}
	}

	/**
	 * Serializes a {@link KitType} for the API.
	 */
	private static class KitTypeSerializer implements JsonSerializer<KitType>
	{
		@Override
		public JsonElement serialize(KitType kitType, Type typeOfSrc, JsonSerializationContext context)
		{
			return context.serialize("equip_" + kitType.name().toLowerCase() + "_id");
		}
	}

	/**
	 * Serializes/Deserializes a {@link Boolean} as the integers {@code 0} or {@code 1}.
	 */
	private static class BooleanToZeroOneConverter implements JsonSerializer<Boolean>, JsonDeserializer<Boolean>
	{
		@Override
		public JsonElement serialize(Boolean src, Type typeOfSrc, JsonSerializationContext context)
		{
			return context.serialize(src ? 1 : 0);
		}

		@Override
		public Boolean deserialize(JsonElement json, Type type, JsonDeserializationContext context)
			throws JsonParseException
		{
			return json.getAsInt() != 0;
		}
	}

	/**
	 * Serializes/Unserializes {@link Instant} using {@link Instant#getEpochSecond()}/{@link Instant#ofEpochSecond(long)}
	 */
	private static class InstantSecondsConverter implements JsonSerializer<Instant>, JsonDeserializer<Instant>
	{
		@Override
		public JsonElement serialize(Instant src, Type srcType, JsonSerializationContext context)
		{
			return new JsonPrimitive(src.getEpochSecond());
		}

		@Override
		public Instant deserialize(JsonElement json, Type type, JsonDeserializationContext context)
			throws JsonParseException
		{
			return Instant.ofEpochSecond(json.getAsLong());
		}
	}
}
