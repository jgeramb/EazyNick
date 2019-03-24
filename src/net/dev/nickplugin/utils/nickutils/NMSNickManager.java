package net.dev.nickplugin.utils.nickutils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import net.dev.nickplugin.main.Main;
import net.dev.nickplugin.utils.FileUtils;
import net.dev.nickplugin.utils.Utils;

public class NMSNickManager {

	public static void updatePlayerListName(Player p, String name) {
		try {
			Object entityPlayer = p.getClass().getMethod("getHandle").invoke(p);
			Class<?> craftChatMessage = getCraftClass("util.CraftChatMessage");
			
			if(name == null)
				name = (String) entityPlayer.getClass().getMethod("getName").invoke(entityPlayer);
			
			Field f = getNMSClass("EntityPlayer").getDeclaredField("listName");
			f.setAccessible(true);
			f.set(entityPlayer, ((Object[]) craftChatMessage.getMethod("fromString", String.class).invoke(craftChatMessage, name))[0]);
			f.setAccessible(false);

			Object entityPlayerArray = Array.newInstance(entityPlayer.getClass(), 1);
			Array.set(entityPlayerArray, 0, entityPlayer);
			
			Class<?> enumPlayerInfoAction = (Main.version == "1_8_R1") ? getNMSClass("EnumPlayerInfoAction") : getNMSClass("PacketPlayOutPlayerInfo").getDeclaredClasses()[((Main.version == "1_11_R1") || (Main.version == "1_12_R1") || (Main.version == "1_13_R1") || (Main.version == "1_13_R2")) ? 1 : 2];
			Object packet = getNMSClass("PacketPlayOutPlayerInfo").getConstructor(enumPlayerInfoAction, entityPlayerArray.getClass()).newInstance(enumPlayerInfoAction.getDeclaredField("UPDATE_DISPLAY_NAME").get(enumPlayerInfoAction), entityPlayerArray);
			
			for(Player all : Bukkit.getOnlinePlayers()) {
				if(!(all.getUniqueId().equals(p.getUniqueId()))) {
					if(!(all.hasPermission("nick.bypass"))) {
						sendPacketNMS(all, packet);
					}
				} else {
					if(FileUtils.cfg.getBoolean("SeeNickSelf")) {
						sendPacketNMS(all, packet);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void updatePlayerListName_1_7_R4(Player p, String name) {
		try {
			Object entityPlayer = p.getClass().getMethod("getHandle").invoke(p);
			
			if (name.length() > 16)
				name = name.substring(0, 16);
				
			Field f = getNMSClass("EntityPlayer").getDeclaredField("listName");
			f.setAccessible(true);
			f.set(entityPlayer, name);
			f.setAccessible(false);

			Class<?> playOutPlayerInfo = getNMSClass("PacketPlayOutPlayerInfo");
			Object packet = playOutPlayerInfo.getMethod("updateDisplayName", getNMSClass("EntityPlayer")).invoke(playOutPlayerInfo, entityPlayer);
			Object packetPlayOutPlayerInfoRemove = playOutPlayerInfo.getMethod("removePlayer", getNMSClass("EntityPlayer")).invoke(playOutPlayerInfo, entityPlayer);
			Object packetPlayOutPlayerInfoAdd = playOutPlayerInfo.getMethod("addPlayer", getNMSClass("EntityPlayer")).invoke(playOutPlayerInfo, entityPlayer);
			
			for(Player all : Bukkit.getOnlinePlayers()) {
				if(!(all.getUniqueId().equals(p.getUniqueId()))) {
					if(!(all.hasPermission("nick.bypass"))) {
						Object entityPlayerAll = all.getClass().getMethod("getHandle").invoke(all);
						Object playerConenction = entityPlayerAll.getClass().getDeclaredField("playerConnection").get(entityPlayerAll);
						Object networkManager = playerConenction.getClass().getDeclaredField("networkManager").get(playerConenction);
						int version = (int) networkManager.getClass().getMethod("getVersion").invoke(networkManager);
						
						if (version < 28) {
							sendPacketNMS(all, packetPlayOutPlayerInfoRemove);
							sendPacketNMS(all, packetPlayOutPlayerInfoAdd);
						} else {
							sendPacketNMS(all, packet);
						}
					}
				} else {
					if(FileUtils.cfg.getBoolean("SeeNickSelf")) {
						Object playerConenction = entityPlayer.getClass().getDeclaredField("playerConnection").get(entityPlayer);
						Object networkManager = playerConenction.getClass().getDeclaredField("networkManager").get(playerConenction);
						int version = (int) networkManager.getClass().getMethod("getVersion").invoke(networkManager);
						
						if (version < 28) {
							sendPacketNMS(all, packetPlayOutPlayerInfoRemove);
							sendPacketNMS(all, packetPlayOutPlayerInfoAdd);
						} else {
							sendPacketNMS(all, packet);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void updateName(Player p, String nickName) {
		try {
			Object gameProfile = p.getClass().getMethod("getProfile").invoke(p);
			Field nameField = Utils.field;
			nameField.setAccessible(true);
			nameField.set(gameProfile, nickName);
			nameField.setAccessible(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void updateSkin(Player p, String skinName) {
		try {
			Object gameProfile = p.getClass().getMethod("getProfile").invoke(p);
			GameProfile gp = (GameProfile) gameProfile;
			
			try {
				gp = GameProfileBuilder.fetch(UUIDFetcher.getUUID(skinName));
			} catch (Exception e) {
			}

			Collection<Property> props = gp.getProperties().get("textures");
			((GameProfile) gameProfile).getProperties().removeAll("textures");
			((GameProfile) gameProfile).getProperties().putAll("textures", props);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public static void updateSkin_1_8_R1(Player p, String skinName) {
		try {
			Object gameProfile = p.getClass().getMethod("getProfile").invoke(p);
			GameProfile gp = (GameProfile) gameProfile;
			
			try {
				gp = GameProfileBuilder_1_8_R1.fetch(UUIDFetcher_1_8_R1.getUUID(skinName));
			} catch (Exception e) {
			}

			Collection<Property> props = gp.getProperties().get("textures");
			((GameProfile) gameProfile).getProperties().removeAll("textures");
			((GameProfile) gameProfile).getProperties().putAll("textures", props);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public static void updateSkin_1_7_R4(Player p, String skinName) {
		try {
			Object gameProfile = p.getClass().getMethod("getProfile").invoke(p);
			net.minecraft.util.com.mojang.authlib.GameProfile gp = (net.minecraft.util.com.mojang.authlib.GameProfile) gameProfile;
			
			try {
				gp = GameProfileBuilder_1_7.fetch(UUIDFetcher_1_7.getUUID(skinName));
			} catch (Exception e) {
			}

			Collection<net.minecraft.util.com.mojang.authlib.properties.Property> props = gp.getProperties().get("textures");
			((net.minecraft.util.com.mojang.authlib.GameProfile) gameProfile).getProperties().removeAll("textures");
			((net.minecraft.util.com.mojang.authlib.GameProfile) gameProfile).getProperties().putAll("textures", props);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	@SuppressWarnings({ "deprecation" })
	public static void updatePlayer(Player p) {
		try {
			Object entityPlayer = p.getClass().getMethod("getHandle").invoke(p);
			Object worldClient = entityPlayer.getClass().getMethod("getWorld").invoke(entityPlayer);
			Object worldData = worldClient.getClass().getMethod("getWorldData").invoke(worldClient);
			Object interactManager = entityPlayer.getClass().getField("playerInteractManager").get(entityPlayer);
			Object entityPlayerArray = Array.newInstance(entityPlayer.getClass(), 1);
			Array.set(entityPlayerArray, 0, entityPlayer);
			
			Object packetEntityDestroy = getNMSClass("PacketPlayOutEntityDestroy").getConstructor(int[].class).newInstance(new int[] { p.getEntityId() });
			Object packetPlayOutPlayerInfoRemove;
			Object packetPlayOutPlayerInfoAdd;
			
			if(Main.version == "1_7_R4") {
				Class<?> playOutPlayerInfo = getNMSClass("PacketPlayOutPlayerInfo");
				
				packetPlayOutPlayerInfoRemove = playOutPlayerInfo.getMethod("removePlayer", getNMSClass("EntityPlayer")).invoke(playOutPlayerInfo, entityPlayer);
				packetPlayOutPlayerInfoAdd = playOutPlayerInfo.getMethod("addPlayer", getNMSClass("EntityPlayer")).invoke(playOutPlayerInfo, entityPlayer);
			} else {
				Class<?> enumPlayerInfoAction = (Main.version == "1_8_R1") ? getNMSClass("EnumPlayerInfoAction") : getNMSClass("PacketPlayOutPlayerInfo").getDeclaredClasses()[((Main.version == "1_11_R1") || (Main.version == "1_12_R1") || (Main.version == "1_13_R1") || (Main.version == "1_13_R2")) ? 1 : 2];
				
				packetPlayOutPlayerInfoRemove = getNMSClass("PacketPlayOutPlayerInfo").getConstructor(enumPlayerInfoAction, entityPlayerArray.getClass()).newInstance(enumPlayerInfoAction.getDeclaredField("REMOVE_PLAYER").get(enumPlayerInfoAction), entityPlayerArray);
				packetPlayOutPlayerInfoAdd = getNMSClass("PacketPlayOutPlayerInfo").getConstructor(enumPlayerInfoAction, entityPlayerArray.getClass()).newInstance(enumPlayerInfoAction.getDeclaredField("ADD_PLAYER").get(enumPlayerInfoAction), entityPlayerArray);
			}
			
			Object packetNamedEntitySpawn = getNMSClass("PacketPlayOutNamedEntitySpawn").getConstructor(getNMSClass("EntityHuman")).newInstance(entityPlayer);
			
			sendPacket(p, packetEntityDestroy);
			sendPacket(p, packetPlayOutPlayerInfoRemove);
			
			Object packetRespawnPlayer = null;

			if(Main.version == "1_13_R2") {
				Object craftWorld = p.getWorld().getClass().getMethod("getHandle").invoke(p.getWorld());
				
				packetRespawnPlayer = getNMSClass("PacketPlayOutRespawn").getConstructor(getNMSClass("DimensionManager"), getNMSClass("EnumDifficulty"), getNMSClass("WorldType"), getNMSClass("EnumGamemode")).newInstance(worldClient.getClass().getDeclaredField("dimension").get(craftWorld), worldClient.getClass().getMethod("getDifficulty").invoke(worldClient), worldData.getClass().getMethod("getType").invoke(worldData), interactManager.getClass().getMethod("getGameMode").invoke(interactManager));
			} else {
				Class<?> enumGameMode = ((Main.version == "1_8_R2") || (Main.version == "1_8_R3") || (Main.version == "1_9_R1") || (Main.version == "1_9_R2")) ? getNMSClass("WorldSettings").getDeclaredClasses()[0] : getNMSClass("EnumGamemode");
				
				packetRespawnPlayer = getNMSClass("PacketPlayOutRespawn").getConstructor(int.class, getNMSClass("EnumDifficulty"), getNMSClass("WorldType"), enumGameMode).newInstance(p.getWorld().getEnvironment().getId(), (Main.version == "1_7_R4") ? getNMSClass("World").getDeclaredField("difficulty").get(worldClient) : worldClient.getClass().getMethod("getDifficulty").invoke(worldClient), worldData.getClass().getMethod("getType").invoke(worldData), interactManager.getClass().getMethod("getGameMode").invoke(interactManager));
			}
				
			sendPacketNMS(p, packetRespawnPlayer);
			p.teleport(new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), p.getLocation().getYaw(), p.getLocation().getPitch()));
			p.updateInventory();
			
			Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), new Runnable() {
				
				@Override
				public void run() {
					sendPacket(p, packetPlayOutPlayerInfoAdd);
					sendPacketExceptSelf(p, packetNamedEntitySpawn);
				}
			}, 5);
			
			if(!((Main.version == "1_7_R4") || (Main.version == "1_8_R1") || (Main.version == "1_8_R2")))
				updatePlayerCache(p);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void updatePlayerCache(Player p) {
		try {
			Class<?> minecraftServer = getNMSClass("MinecraftServer");
			Object server = minecraftServer.getMethod("getServer").invoke(minecraftServer);
			Object playerList = server.getClass().getMethod("getPlayerList").invoke(server);
			Field f = getNMSClass("PlayerList").getDeclaredField("playersByName");
			
			f.setAccessible(true);
			
			Map<String, Object> map = (Map<String, Object>) f.get(playerList);
			ArrayList<String> toRemove = new ArrayList<>();
			
			for (String cachedName : map.keySet()) {
				Object entityPlayer = map.get(cachedName);
				
				if(entityPlayer.getClass().getMethod("getUniqueID").invoke(entityPlayer).equals(p.getUniqueId()))
					toRemove.add(cachedName);
			}
			
			for (String string : toRemove) {
				map.remove(string);
			}
			
			map.put(p.getName(), p.getClass().getMethod("getHandle").invoke(p));
			
			f.set(playerList, map);
			f.setAccessible(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void sendPacket(Player p, Object packet) {
		for (Player all : Bukkit.getOnlinePlayers()) {
			if(!(all.getUniqueId().equals(p.getUniqueId()))) {
				if(!(all.hasPermission("nick.bypass"))) {
					sendPacketNMS(all, packet);
				}
			} else {
				if(FileUtils.cfg.getBoolean("SeeNickSelf")) {
					sendPacketNMS(all, packet);
				}
			}
		}
	}
	
	public static void sendPacketExceptSelf(Player p, Object packet) {
		for (Player all : Bukkit.getOnlinePlayers()) {
			if(!(all.getUniqueId().equals(p.getUniqueId()))) {
				if(!(all.hasPermission("nick.bypass"))) {
					sendPacketNMS(all, packet);
				}
			}
		}
	}

	public static void sendPacketNMS(Player p, Object packet) {
		try {
			Object handle = p.getClass().getMethod("getHandle").invoke(p);
			Object playerConnection = handle.getClass().getDeclaredField("playerConnection").get(handle);
			playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Class<?> getNMSClass(String name) {
		try {
			return Class.forName("net.minecraft.server." + getVersion() + "." + name);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Class<?> getCraftClass(String name) {
		try {
			return Class.forName("org.bukkit.craftbukkit." + getVersion() + "." + name);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public static String getVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	}

}
