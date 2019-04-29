package net.dev.nickplugin.utils.nickUtils; 

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.dev.nickplugin.utils.NickNameFileUtils;
import net.dev.nickplugin.utils.Utils;
import net.minecraft.util.com.google.gson.Gson;
import net.minecraft.util.com.google.gson.GsonBuilder;
import net.minecraft.util.com.mojang.util.UUIDTypeAdapter;

public class UUIDFetcher_1_7 {
   
   private static Gson gson = new GsonBuilder().registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();
   
   private static final String UUID_URL = "https://api.mojang.com/users/profiles/minecraft/%s?at=%d";
   private static final String NAME_URL = "https://api.mojang.com/user/profiles/%s/names";
   private static Map<String, UUID> uuidCache = new HashMap<String, UUID>();
   private static Map<UUID, String> nameCache = new HashMap<UUID, String>();
   
   private String name;
   private UUID id;
   
   public static UUID getUUID(String name) {
	   return getUUIDAt(name, System.currentTimeMillis());
   }
   
   public static UUID getUUIDAt(String name, long timestamp) {
	   name = name.toLowerCase();
	   
	   if(uuidCache.containsKey(name))
		   return uuidCache.get(name);
	      
	   try {
		   HttpURLConnection connection = (HttpURLConnection) new URL(String.format(UUID_URL, name, timestamp/1000)).openConnection();
		   connection.setReadTimeout(5000);
		   
		   UUIDFetcher_1_7 data = gson.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), UUIDFetcher_1_7.class);
	         
		   uuidCache.put(name, data.id);
		   nameCache.put(data.id, data.name);
		   
		   return data.id;
	   } catch (Exception e) {
		   for(String nickName : Utils.nickNames)
			   if(name.equalsIgnoreCase(nickName))
				   Utils.nickNames.remove(nickName);
		   
		   List<String> list = NickNameFileUtils.cfg.getStringList("NickNames");
		   
		   for(String nickName : list)
			   if(name.equalsIgnoreCase(nickName))
				   list.remove(nickName);
		   
		   NickNameFileUtils.cfg.set("NickNames", list);
		   NickNameFileUtils.saveFile();
		   
		   Utils.sendConsole("§cThe player §e" + name + " §cis §4§lnot existing§c!");
	   }
	   
	   return null;
   }
   
   public static String getName(UUID uuid) {
	   if(nameCache.containsKey(uuid))
		   return nameCache.get(uuid);
      
	   try {
		   HttpURLConnection connection = (HttpURLConnection) new URL(String.format(NAME_URL, UUIDTypeAdapter.fromUUID(uuid))).openConnection();
		   connection.setReadTimeout(5000);
		   
		   UUIDFetcher_1_7[] nameHistory = gson.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), UUIDFetcher_1_7[].class);
		   UUIDFetcher_1_7 currentNameData = nameHistory[nameHistory.length - 1];
		   uuidCache.put(currentNameData.name.toLowerCase(), uuid);
		   nameCache.put(uuid, currentNameData.name);
         
		   return currentNameData.name;
	   } catch (Exception e) {
	   }
      
	   return null;
   }
}