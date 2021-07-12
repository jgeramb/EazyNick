package net.dev.eazynick.utilities.mojang;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;

import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import com.google.gson.*;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.util.UUIDTypeAdapter;

public class GameProfileBuilder {

	private final String SERVICE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";
	private final String JSON_SKIN = "{\"timestamp\":%d,\"profileId\":\"%s\",\"profileName\":\"%s\",\"isPublic\":true,\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}";
	private final String JSON_CAPE = "{\"timestamp\":%d,\"profileId\":\"%s\",\"profileName\":\"%s\",\"isPublic\":true,\"textures\":{\"SKIN\":{\"url\":\"%s\"},\"CAPE\":{\"url\":\"%s\"}}}";

	private Gson gson = new GsonBuilder().disableHtmlEscaping().registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).registerTypeAdapter(GameProfile.class, new GameProfileSerializer()).registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer()).create();

	private HashMap<UUID, CachedProfile> cache = new HashMap<UUID, CachedProfile>();

	private long cacheTime = -1;

	public GameProfile fetch(UUID uuid) throws IOException {
		return fetch(uuid, false);
	}

	public GameProfile fetch(UUID uuid, boolean forceNew) throws IOException {
		// Check for cached profile
		if (!(forceNew) && cache.containsKey(uuid) && cache.get(uuid).isValid())
			return cache.get(uuid).profile;
		else {
			// Open http connection
			HttpURLConnection connection = (HttpURLConnection) new URL(String.format(SERVICE_URL, UUIDTypeAdapter.fromUUID(uuid))).openConnection();
			connection.setReadTimeout(5000);

			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				// Parse response
				String json = "", line = "";
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

				while ((line = bufferedReader.readLine()) != null)
					json += line;

				GameProfile result = gson.fromJson(json, GameProfile.class);

				// Cache profile
				cache.put(uuid, new CachedProfile(result));

				return result;
			}

			JsonObject error = (JsonObject) new JsonParser().parse(new BufferedReader(new InputStreamReader(connection.getErrorStream())).readLine());

			throw new IOException(error.get("error").getAsString() + ": " + error.get("errorMessage").getAsString());
		}
	}

	public GameProfile getProfile(UUID uuid, String name, String skin) {
		return getProfile(uuid, name, skin, null);
	}

	public GameProfile getProfile(UUID uuid, String name, String skinUrl, String capeUrl) {
		// Create profile from properties
		GameProfile profile = new GameProfile(uuid, name);
		boolean cape = capeUrl != null && !(capeUrl.isEmpty());

		List<Object> args = new ArrayList<Object>();
		args.add(System.currentTimeMillis());
		args.add(UUIDTypeAdapter.fromUUID(uuid));
		args.add(name);
		args.add(skinUrl);

		if (cape)
			args.add(capeUrl);

		profile.getProperties().put("textures", new Property("textures", Base64Coder.encodeString(String.format(cape ? JSON_CAPE : JSON_SKIN, args.toArray(new Object[args.size()])))));

		return profile;
	}

	public void setCacheTime(long time) {
		cacheTime = time;
	}

	private class GameProfileSerializer implements JsonSerializer<GameProfile>, JsonDeserializer<GameProfile> {

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

	private class CachedProfile {

		private long timestamp = System.currentTimeMillis();
		private GameProfile profile;

		public CachedProfile(GameProfile profile) {
			this.profile = profile;
		}

		public boolean isValid() {
			return cacheTime < 0 ? true : (System.currentTimeMillis() - timestamp) < cacheTime;
		}

	}

}