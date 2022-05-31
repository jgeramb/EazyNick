package com.justixdev.eazynick.utilities.mojang;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.utilities.Utils;
import com.justixdev.eazynick.utilities.configuration.yaml.NickNameYamlFile;
import com.mojang.util.UUIDTypeAdapter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class UUIDFetcher {

	private final Gson GSON = new GsonBuilder().create();
	private static final Map<String, UUID> UUID_CACHE = new HashMap<>();
	private static final Map<UUID, String> NAME_CACHE = new HashMap<>();

	public UUID getUUID(String name) {
		return getUUIDAt(name, System.currentTimeMillis());
	}

	public UUID getUUIDAt(String name, long timestamp) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		
		name = name.toLowerCase();

		// Check for cached uuid
		if (UUID_CACHE.containsKey(name))
			return UUID_CACHE.get(name);

		try {
			// Open api connection
			String UUID_URL = "https://api.mojang.com/users/profiles/minecraft/%s?at=%d";
			HttpURLConnection connection = (HttpURLConnection) new URL(String.format(
					UUID_URL,
					name,
					timestamp / 1000
			)).openConnection();
			connection.setReadTimeout(5000);

			// Parse response
			try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
				StringBuilder response = new StringBuilder();
				String line;
				
				while((line = bufferedReader.readLine()) != null)
					response.append(line);
				
				try {
					// Parse response
					JsonObject data = GSON.fromJson(response.toString(), JsonObject.class);
					UUID uniqueId = UUID.fromString(data.get("id")
							.getAsString()
							.replaceFirst(
									"(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
									"$1-$2-$3-$4-$5"
							));
					
					// Cache data
					UUID_CACHE.put(name, uniqueId);
					NAME_CACHE.put(uniqueId, data.get("name").getAsString());
	
					return uniqueId;
				} catch(VerifyError ignore) {
				}
			}
		} catch (Exception ex) {
			// Remove nickname from 'nickNames.yml' file
			NickNameYamlFile nickNameYamlFile = eazyNick.getNickNameYamlFile();

			List<String> list = nickNameYamlFile.getConfiguration().getStringList("NickNames");
			final String finalName = name;

			new ArrayList<>(list)
					.stream()
					.filter(currentNickName -> currentNickName.equalsIgnoreCase(finalName))
					.forEach(currentNickName -> {
				list.remove(currentNickName);
				utils.getNickNames().remove(currentNickName);
			});

			nickNameYamlFile.getConfiguration().set("NickNames", list);
			nickNameYamlFile.save();
			
			// Show error message
			if(eazyNick.getSetupYamlFile().getConfiguration().getBoolean("ShowProfileErrorMessages")) {
				if(utils.isSupportMode()) {
					utils.sendConsole("§cAn error occurred while trying to fetch uuid of §6" + name + "§7:");
					
					ex.printStackTrace();
				} else
					utils.sendConsole("§cThere is no account with username §6" + name + " §cin the mojang database");
			}
		}

		return null;
	}

	public String getName(String name, UUID uuid) {
		//C heck for cached name
		if (NAME_CACHE.containsKey(uuid))
			return NAME_CACHE.get(uuid);

		try {
			// Open api connection
			String NAME_URL = "https://api.mojang.com/user/profiles/%s/names";
			HttpURLConnection connection = (HttpURLConnection) new URL(String.format(
					NAME_URL,
					UUIDTypeAdapter.fromUUID(uuid)
			)).openConnection();
			connection.setReadTimeout(5000);

			// Parse response
			try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
				StringBuilder response = new StringBuilder();
				String line;
				
				while((line = bufferedReader.readLine()) != null)
					response.append(line);
				
				JsonArray data = GSON.fromJson(response.toString(), JsonArray.class);
				JsonObject currentNameData = (JsonObject) data.get(data.size() - 1);
				
				//Cache data
				UUID_CACHE.put(currentNameData.get("name").getAsString().toLowerCase(), uuid);
				NAME_CACHE.put(uuid, currentNameData.get("name").getAsString());
	
				return currentNameData.get("name").getAsString();
			}
		} catch (Exception ignore) {
		}

		return name;
	}

}