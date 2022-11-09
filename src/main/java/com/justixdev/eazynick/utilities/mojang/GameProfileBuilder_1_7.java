package com.justixdev.eazynick.utilities.mojang;

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

public class GameProfileBuilder_1_7 {

    private final com.google.gson.Gson GSON = new com.google.gson.GsonBuilder()
            .disableHtmlEscaping()
            .registerTypeAdapter(UUID.class, new com.mojang.util.UUIDTypeAdapter())
            .registerTypeAdapter(com.mojang.authlib.GameProfile.class, new GameProfileBuilder_1_7.GameProfileSerializer())
            .registerTypeAdapter(com.mojang.authlib.properties.PropertyMap.class, new com.mojang.authlib.properties.PropertyMap.Serializer())
            .create();
    private final HashMap<UUID, GameProfileBuilder_1_7.CachedProfile> CACHE = new HashMap<>();

    public com.mojang.authlib.GameProfile fetch(UUID uuid) throws IOException {
        return fetch(uuid, false);
    }

    public com.mojang.authlib.GameProfile fetch(UUID uuid, boolean forceNew) throws IOException {
        // Check for cached profile
        if (!(forceNew) && CACHE.containsKey(uuid) && CACHE.get(uuid).isValid())
            return CACHE.get(uuid).profile;
        else {
            // Open http connection
            String SERVICE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";
            HttpURLConnection connection = (HttpURLConnection) new URL(String.format(
                    SERVICE_URL,
                    UUIDTypeAdapter.fromUUID(uuid)
            )).openConnection();
            connection.setReadTimeout(5000);

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Parse response
                StringBuilder json = new StringBuilder();
                String line;

                try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {

                    while ((line = bufferedReader.readLine()) != null)
                        json.append(line);

                    com.mojang.authlib.GameProfile result = GSON.fromJson(json.toString(), com.mojang.authlib.GameProfile.class);

                    // Cache profile
                    CACHE.put(uuid, new GameProfileBuilder_1_7.CachedProfile(result));

                    return result;
                }
            }

            com.google.gson.JsonObject error = GSON.fromJson(
                    new BufferedReader(new InputStreamReader(connection.getErrorStream())).readLine(),
                    com.google.gson.JsonObject.class
            );

            throw new IOException(error.get("error").getAsString() + ": " + error.get("errorMessage").getAsString());
        }
    }

    private static class GameProfileSerializer implements com.google.gson.JsonSerializer<com.mojang.authlib.GameProfile>, com.google.gson.JsonDeserializer<com.mojang.authlib.GameProfile> {

        @Override
        public com.mojang.authlib.GameProfile deserialize(com.google.gson.JsonElement json, Type type, com.google.gson.JsonDeserializationContext context) throws com.google.gson.JsonParseException {
            com.google.gson.JsonObject object = (com.google.gson.JsonObject) json;
            UUID id = object.has("id") ? (UUID) context.deserialize(object.get("id"), UUID.class) : null;
            String name = object.has("name") ? object.getAsJsonPrimitive("name").getAsString() : null;
            com.mojang.authlib.GameProfile profile = new com.mojang.authlib.GameProfile(id, name);

            if (object.has("properties")) {
                for (Entry<String, Property> prop : ((com.mojang.authlib.properties.PropertyMap) context.deserialize(
                        object.get("properties"),
                        PropertyMap.class
                )).entries())
                    profile.getProperties().put(prop.getKey(), prop.getValue());
            }

            return profile;
        }

        @Override
        public com.google.gson.JsonElement serialize(com.mojang.authlib.GameProfile profile, Type type, com.google.gson.JsonSerializationContext context) {
            com.google.gson.JsonObject result = new com.google.gson.JsonObject();

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
        private final com.mojang.authlib.GameProfile profile;

        public CachedProfile(GameProfile profile) {
            this.profile = profile;
        }

        public boolean isValid() {
            return ((System.currentTimeMillis() - timestamp) < TimeUnit.HOURS.toMillis(6));
        }

    }

}