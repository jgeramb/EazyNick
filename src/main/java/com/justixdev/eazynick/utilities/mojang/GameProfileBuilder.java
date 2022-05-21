package com.justixdev.eazynick.utilities.mojang;

import com.google.gson.*;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.util.UUIDTypeAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class GameProfileBuilder {

	private final Gson GSON = new GsonBuilder().disableHtmlEscaping().registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).registerTypeAdapter(GameProfile.class, new GameProfileSerializer()).registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer()).create();
	private final HashMap<UUID, CachedProfile> CACHE = new HashMap<>();

	public GameProfile fetch(UUID uuid) throws IOException {
		return fetch(uuid, false);
	}

	public GameProfile fetch(UUID uuid, boolean forceNew) throws IOException {
		// Check for cached profile
		if (!(forceNew) && CACHE.containsKey(uuid) && CACHE.get(uuid).isValid())
			return CACHE.get(uuid).profile;
		else {
			// Open http connection
			String SERVICE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";
			HttpURLConnection connection = (HttpURLConnection) new URL(String.format(SERVICE_URL, UUIDTypeAdapter.fromUUID(uuid))).openConnection();
			connection.setReadTimeout(5000);

			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				// Parse response
				StringBuilder json = new StringBuilder();
				String line;

				try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {

					while ((line = bufferedReader.readLine()) != null)
						json.append(line);

					GameProfile result = GSON.fromJson(json.toString(), GameProfile.class);

					// Cache profile
					CACHE.put(uuid, new CachedProfile(result));

					return result;
				}
			}

			JsonObject error = GSON.fromJson(new BufferedReader(new InputStreamReader(connection.getErrorStream())).readLine(), JsonObject.class);

			throw new IOException(error.get("error").getAsString() + ": " + error.get("errorMessage").getAsString());
		}
	}

	private static class GameProfileSerializer implements JsonSerializer<GameProfile>, JsonDeserializer<GameProfile> {

		@Override
		public GameProfile deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
			JsonObject object = (JsonObject) json;
			UUID id = object.has("id") ? (UUID) context.deserialize(object.get("id"), UUID.class) : null;
			String name = object.has("name") ? object.getAsJsonPrimitive("name").getAsString() : null;
			GameProfile profile = new GameProfile(id, name);

			if (object.has("properties")) {
				for (Entry<String, Property> prop : ((PropertyMap) context.deserialize(object.get("properties"), PropertyMap.class)).entries())
					profile.getProperties().put(prop.getKey(), prop.getValue());
			}

			return profile;
		}

		@Override
		public JsonElement serialize(GameProfile profile, Type type, JsonSerializationContext context) {
			JsonObject result = new JsonObject();

			if (profile.getId() != null)
				result.add("id", context.serialize(profile.getId()));

			if (profile.getName() != null)
				result.addProperty("name", profile.getName());

			if (!(profile.getProperties().isEmpty()))
				result.add("properties", context.serialize(profile.getProperties()));

			return result;
		}
	}

	private static class CachedProfile {

		private final long timestamp = System.currentTimeMillis();
		private final GameProfile profile;

		public CachedProfile(GameProfile profile) {
			this.profile = profile;
		}

		public boolean isValid() {
			return ((System.currentTimeMillis() - timestamp) < TimeUnit.HOURS.toMillis(6));
		}

	}

}