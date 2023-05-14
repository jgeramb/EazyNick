package com.justixdev.eazynick.utilities;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.justixdev.eazynick.EazyNick;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MineSkinAPI {

    private static final Logger LOGGER = Bukkit.getLogger();
    private static final String URL_FORMAT = "https://api.mineskin.org/get/uuid/%s";

    private final String pluginName, pluginVersion;

    public MineSkinAPI() {
        EazyNick eazyNick = EazyNick.getInstance();

        this.pluginName = eazyNick.getName();
        this.pluginVersion = eazyNick.getDescription().getVersion();
    }

    public Collection<Property> getTextureProperties(String id) {
        ArrayList<Property> props = new ArrayList<>();

        try {
            // Open api connection
            HttpURLConnection textureConnection = (HttpURLConnection) new URL(String.format(
                    URL_FORMAT,
                    id
            )).openConnection();
            textureConnection.setRequestProperty("User-Agent", this.pluginName + "/MineSkinHook " + this.pluginVersion);
            textureConnection.setRequestMethod("GET");
            textureConnection.setReadTimeout((int) TimeUnit.SECONDS.toMillis(5));

            // Parse response to GameProfile Property
            JsonObject jsonObject = new Gson().fromJson(
                    new BufferedReader(new InputStreamReader(textureConnection.getInputStream())),
                    JsonObject.class);
            JsonObject texture = jsonObject.get("data")
                    .getAsJsonObject()
                    .get("texture")
                    .getAsJsonObject();

            props.add(new Property(
                    "textures",
                    texture.get("value").getAsString(),
                    texture.get("signature").getAsString())
            );
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Could not download MineSkin textures: " + ex.getMessage());
        }

        return props;
    }

    public Collection<net.minecraft.util.com.mojang.authlib.properties.Property> getTextureProperties_1_7(String id) {
        Collection<net.minecraft.util.com.mojang.authlib.properties.Property> props = new ArrayList<>();

        try {
            // Open api connection
            HttpURLConnection textureConnection = (HttpURLConnection) new URL(String.format(
                    URL_FORMAT,
                    id
            )).openConnection();
            textureConnection.setRequestProperty("User-Agent", pluginName + "/MineSkinHook " + pluginVersion);
            textureConnection.setRequestMethod("GET");
            textureConnection.setReadTimeout((int) TimeUnit.SECONDS.toMillis(5));

            // Parse response to GameProfile Property
            net.minecraft.util.com.google.gson.JsonObject jsonObject = new net.minecraft.util.com.google.gson.Gson().fromJson(
                    new BufferedReader(new InputStreamReader(textureConnection.getInputStream())),
                    net.minecraft.util.com.google.gson.JsonObject.class);
            net.minecraft.util.com.google.gson.JsonObject texture = jsonObject.get("data")
                    .getAsJsonObject()
                    .get("texture")
                    .getAsJsonObject();

            props.add(new net.minecraft.util.com.mojang.authlib.properties.Property(
                    "textures",
                    texture.get("value").getAsString(),
                    texture.get("signature").getAsString())
            );
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Could not download MineSkin textures: " + ex.getMessage());
        }

        return props;
    }

}
