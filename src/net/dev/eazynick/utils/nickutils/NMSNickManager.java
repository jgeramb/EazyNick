package net.dev.eazynick.utils.nickutils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.NickManager;
import net.dev.eazynick.utils.FileUtils;
import net.dev.eazynick.utils.ReflectUtils;
import net.dev.eazynick.utils.Utils;

import me.clip.placeholderapi.PlaceholderAPI;

public class NMSNickManager extends ReflectUtils {

	public static void updatePlayerListName(Player p, String name) {
		try {
			Object entityPlayer = p.getClass().getMethod("getHandle").invoke(p);
			Class<?> craftChatMessage = getCraftClass("util.CraftChatMessage");
			String playerName = (String) entityPlayer.getClass().getMethod("getName").invoke(entityPlayer);
			
			if(name == null)
				name = playerName;
			
			Field f = getNMSClass("EntityPlayer").getDeclaredField("listName");
			f.setAccessible(true);
			f.set(entityPlayer, name.equals(playerName) ? null : ((Object[]) craftChatMessage.getMethod("fromString", String.class).invoke(craftChatMessage, name))[0]);
			f.setAccessible(false);

			Object entityPlayerArray = Array.newInstance(entityPlayer.getClass(), 1);
			Array.set(entityPlayerArray, 0, entityPlayer);
			
			Class<?> enumPlayerInfoAction = EazyNick.version.equals("1_8_R1") ? getNMSClass("EnumPlayerInfoAction") : getNMSClass("PacketPlayOutPlayerInfo").getDeclaredClasses()[(EazyNick.version.equals("1_11_R1") || EazyNick.version.equals("1_12_R1") || EazyNick.version.startsWith("1_13") || EazyNick.version.startsWith("1_14") || EazyNick.version.startsWith("1_15")) ? 1 : 2];
			Object packet = getNMSClass("PacketPlayOutPlayerInfo").getConstructor(enumPlayerInfoAction, entityPlayerArray.getClass()).newInstance(enumPlayerInfoAction.getDeclaredField("UPDATE_DISPLAY_NAME").get(enumPlayerInfoAction), entityPlayerArray);
			
			for(Player all : Bukkit.getOnlinePlayers()) {
				if(all.canSee(p))
					sendPacket(p, packet, !(Utils.nickedPlayers.contains(p.getUniqueId())));
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
				if(all.canSee(p)) {
					if(!(all.getUniqueId().equals(p.getUniqueId()))) {
						if(!(all.hasPermission("nick.bypass"))) {
							Object entityPlayerAll = all.getClass().getMethod("getHandle").invoke(all);
							Object playerConenction = entityPlayerAll.getClass().getDeclaredField("playerConnection").get(entityPlayerAll);
							Object networkManager = playerConenction.getClass().getDeclaredField("networkManager").get(playerConenction);
							int version = (int) networkManager.getClass().getMethod("getVersion").invoke(networkManager);
							
							if (version < 28) {
								sendPacketNMS(all, packetPlayOutPlayerInfoRemove);
								sendPacketNMS(all, packetPlayOutPlayerInfoAdd);
							} else
								sendPacketNMS(all, packet);
						}
					} else {
						if(FileUtils.cfg.getBoolean("SeeNickSelf")) {
							Object playerConenction = entityPlayer.getClass().getDeclaredField("playerConnection").get(entityPlayer);
							Object networkManager = playerConenction.getClass().getDeclaredField("networkManager").get(playerConenction);
							int version = (int) networkManager.getClass().getMethod("getVersion").invoke(networkManager);
							
							if (version < 28) {
								sendPacketNMS(all, packetPlayOutPlayerInfoRemove);
								sendPacketNMS(all, packetPlayOutPlayerInfoAdd);
							} else
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
			Field nameField = Utils.nameField;
			nameField.setAccessible(true);
			nameField.set(gameProfile, nickName);
			nameField.setAccessible(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void updateUniqueId(Player p, UUID uniqueId) {
		if(uniqueId == null)
			uniqueId = p.getUniqueId();
		
		try {
			Object gameProfile = p.getClass().getMethod("getProfile").invoke(p);
			Field uuidField = Utils.uuidField;
			uuidField.setAccessible(true);
			uuidField.set(gameProfile, uniqueId);
			uuidField.setAccessible(false);
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
	
	public static void updatePlayer(Player p, UpdateType type, boolean forceUpdate) {
		NickManager api = new NickManager(p);
		UUID uuidBefore = p.getUniqueId();
		String nickName = api.getNickName();
		
		try {
			Object entityPlayer = p.getClass().getMethod("getHandle").invoke(p);
			Object worldClient = entityPlayer.getClass().getMethod("getWorld").invoke(entityPlayer);
			Object worldData = worldClient.getClass().getMethod("getWorldData").invoke(worldClient);
			Object interactManager = entityPlayer.getClass().getField("playerInteractManager").get(entityPlayer);
			Object entityPlayerArray = Array.newInstance(entityPlayer.getClass(), 1);
			Array.set(entityPlayerArray, 0, entityPlayer);
			
			Object packetEntityDestroy = getNMSClass("PacketPlayOutEntityDestroy").getConstructor(int[].class).newInstance(new int[] { p.getEntityId() });
			Object packetPlayOutPlayerInfoRemove;
			
			if(EazyNick.version.equals("1_7_R4")) {
				Class<?> playOutPlayerInfo = getNMSClass("PacketPlayOutPlayerInfo");
				
				packetPlayOutPlayerInfoRemove = playOutPlayerInfo.getMethod("removePlayer", getNMSClass("EntityPlayer")).invoke(playOutPlayerInfo, entityPlayer);
			} else {
				Class<?> enumPlayerInfoAction = (EazyNick.version.equals("1_8_R1") ? getNMSClass("EnumPlayerInfoAction") : getNMSClass("PacketPlayOutPlayerInfo").getDeclaredClasses()[(EazyNick.version.startsWith("1_1") && !(EazyNick.version.equals("1_10_R1"))) ? 1 : 2]);
				
				packetPlayOutPlayerInfoRemove = getNMSClass("PacketPlayOutPlayerInfo").getConstructor(enumPlayerInfoAction, entityPlayerArray.getClass()).newInstance(enumPlayerInfoAction.getDeclaredField("REMOVE_PLAYER").get(enumPlayerInfoAction), entityPlayerArray);
			}
			
			sendPacket(p, packetEntityDestroy, forceUpdate);
			sendPacket(p, packetPlayOutPlayerInfoRemove, forceUpdate);

			if(type.equals(UpdateType.NICK)) {
				if(FileUtils.cfg.getBoolean("NickMessage.OnNnick")) {
					for(Player all : Bukkit.getOnlinePlayers())
						all.sendMessage(FileUtils.getConfigString("NickMessage.Nick.Quit").replace("%displayName%", p.getDisplayName()).replace("%name%", api.getRealName()));
				}
			} else if(type.equals(UpdateType.UNNICK)) {
				if(FileUtils.cfg.getBoolean("NickMessage.OnUnnick")) {
					for(Player all : Bukkit.getOnlinePlayers())
						all.sendMessage(FileUtils.getConfigString("NickMessage.Unnick.Quit").replace("%displayName%", p.getDisplayName()).replace("%name%", api.getNickName()));
				}
			}
			
			if(!(type.equals(UpdateType.QUIT))) {
				if(!(EazyNick.getInstance().isEnabled()))
					return;
				
				Bukkit.getScheduler().runTaskLater(EazyNick.getInstance(), () -> {
					try {
						Object packetRespawnPlayer;
						Object packetNamedEntitySpawn;
						Object packetPlayOutPlayerInfoAdd;
						
						boolean uuidSpoof = (System.getProperty("eazynick.uuidspoof") != null) ? System.getProperty("eazynick.uuidspoof").equals("true") : false;
						
						if(uuidSpoof) {
							updateUniqueId(p, UUIDFetcher.getUUID(nickName));
							
							if(!(type.equals(UpdateType.NICK))) {
								if(EazyNick.version.equals("1_7_R4")) {
									Class<?> playOutPlayerInfo = getNMSClass("PacketPlayOutPlayerInfo");
									
									sendPacket(p, playOutPlayerInfo.getMethod("removePlayer", getNMSClass("EntityPlayer")).invoke(playOutPlayerInfo, entityPlayer), true);
								} else {
									Class<?> enumPlayerInfoAction = (EazyNick.version.equals("1_8_R1") ? getNMSClass("EnumPlayerInfoAction") : getNMSClass("PacketPlayOutPlayerInfo").getDeclaredClasses()[(EazyNick.version.startsWith("1_1") && !(EazyNick.version.equals("1_10_R1"))) ? 1 : 2]);
									
									sendPacket(p, getNMSClass("PacketPlayOutPlayerInfo").getConstructor(enumPlayerInfoAction, entityPlayerArray.getClass()).newInstance(enumPlayerInfoAction.getDeclaredField("REMOVE_PLAYER").get(enumPlayerInfoAction), entityPlayerArray), true);
								}
								
								updateUniqueId(p, p.getUniqueId());
							}
						}
						
						if(EazyNick.version.startsWith("1_15")) {
							Class<?> dimensionManager = getNMSClass("DimensionManager");
							Class<?> worldType = getNMSClass("WorldType");
							Class<?> enumGameMode = getNMSClass("EnumGamemode");
							
							packetRespawnPlayer = getNMSClass("PacketPlayOutRespawn").getConstructor(dimensionManager, long.class, worldType, enumGameMode).newInstance(dimensionManager.getMethod("a", int.class).invoke(dimensionManager, p.getWorld().getEnvironment().getId()), Hashing.sha256().hashLong(p.getWorld().getSeed()).asLong(), worldType.getMethod("getType", String.class).invoke(worldType, p.getWorld().getWorldType().getName()), enumGameMode.getMethod("getById", int.class).invoke(enumGameMode, p.getGameMode().getValue()));
						} else if(EazyNick.version.startsWith("1_14")) {
							Class<?> dimensionManager = getNMSClass("DimensionManager");
							Class<?> worldType = getNMSClass("WorldType");
							Class<?> enumGameMode = getNMSClass("EnumGamemode");
							
							packetRespawnPlayer = getNMSClass("PacketPlayOutRespawn").getConstructor(dimensionManager, worldType, enumGameMode).newInstance(dimensionManager.getMethod("a", int.class).invoke(dimensionManager, p.getWorld().getEnvironment().getId()), worldType.getMethod("getType", String.class).invoke(worldType, p.getWorld().getWorldType().getName()), enumGameMode.getMethod("getById", int.class).invoke(enumGameMode, p.getGameMode().getValue()));
						} else if(EazyNick.version.equals("1_13_R2")) {
							Object craftWorld = p.getWorld().getClass().getMethod("getHandle").invoke(p.getWorld());
							
							packetRespawnPlayer = getNMSClass("PacketPlayOutRespawn").getConstructor(getNMSClass("DimensionManager"), getNMSClass("EnumDifficulty"), getNMSClass("WorldType"), getNMSClass("EnumGamemode")).newInstance(worldClient.getClass().getDeclaredField("dimension").get(craftWorld), worldClient.getClass().getMethod("getDifficulty").invoke(worldClient), worldData.getClass().getMethod("getType").invoke(worldData), interactManager.getClass().getMethod("getGameMode").invoke(interactManager));
						} else {
							Class<?> enumGameMode = (EazyNick.version.equals("1_8_R2") || EazyNick.version.equals("1_8_R3") || EazyNick.version.equals("1_9_R1") || EazyNick.version.equals("1_9_R2")) ? getNMSClass("WorldSettings").getDeclaredClasses()[0] : getNMSClass("EnumGamemode");
							
							packetRespawnPlayer = getNMSClass("PacketPlayOutRespawn").getConstructor(int.class, getNMSClass("EnumDifficulty"), getNMSClass("WorldType"), enumGameMode).newInstance(p.getWorld().getEnvironment().getId(), (EazyNick.version.equals("1_7_R4") ? getNMSClass("World").getDeclaredField("difficulty").get(worldClient) : worldClient.getClass().getMethod("getDifficulty").invoke(worldClient)), worldData.getClass().getMethod("getType").invoke(worldData), interactManager.getClass().getMethod("getGameMode").invoke(interactManager));
						}
						
						packetNamedEntitySpawn = getNMSClass("PacketPlayOutNamedEntitySpawn").getConstructor(getNMSClass("EntityHuman")).newInstance(entityPlayer);
						
						if(EazyNick.version.equals("1_7_R4")) {
							Class<?> playOutPlayerInfo = getNMSClass("PacketPlayOutPlayerInfo");
							
							packetPlayOutPlayerInfoAdd = playOutPlayerInfo.getMethod("addPlayer", getNMSClass("EntityPlayer")).invoke(playOutPlayerInfo, entityPlayer);
						} else {
							Class<?> enumPlayerInfoAction = (EazyNick.version.equals("1_8_R1") ? getNMSClass("EnumPlayerInfoAction") : getNMSClass("PacketPlayOutPlayerInfo").getDeclaredClasses()[(EazyNick.version.startsWith("1_1") && !(EazyNick.version.equals("1_10_R1"))) ? 1 : 2]);
							
							packetPlayOutPlayerInfoAdd = getNMSClass("PacketPlayOutPlayerInfo").getConstructor(enumPlayerInfoAction, entityPlayerArray.getClass()).newInstance(enumPlayerInfoAction.getDeclaredField("ADD_PLAYER").get(enumPlayerInfoAction), entityPlayerArray);
						}
						
						sendPacketNMS(p, packetRespawnPlayer);
						
						p.teleport(new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), p.getLocation().getYaw(), p.getLocation().getPitch()));
						p.updateInventory();
						
						if(!(EazyNick.getInstance().isEnabled()))
							return;
						
						Bukkit.getScheduler().runTaskLater(EazyNick.getInstance(), () -> {
							sendPacket(p, packetPlayOutPlayerInfoAdd, true);
							sendPacketExceptSelf(p, packetNamedEntitySpawn, forceUpdate);
							
							if(uuidSpoof)
								updateUniqueId(p, uuidBefore);
							
							try {
								Object packetEntityLook = ((EazyNick.version.equals("1_7_R4") || EazyNick.version.equals("1_8_R1")) ? getNMSClass("PacketPlayOutEntityLook") : getNMSClass("PacketPlayOutEntity").getDeclaredClasses()[0]).getConstructor(int.class, byte.class, byte.class, boolean.class).newInstance(p.getEntityId(), (byte) ((int) (p.getLocation().getYaw() * 256.0F / 360.0F)), (byte) ((int) (p.getLocation().getPitch() * 256.0F / 360.0F)), true);
								Object packetHeadRotation = getNMSClass("PacketPlayOutEntityHeadRotation").newInstance();
								setField(packetHeadRotation, "a", p.getEntityId());
								setField(packetHeadRotation, "b", (byte) ((int) (p.getLocation().getYaw() * 256.0F / 360.0F)));
								
								sendPacketExceptSelf(p, packetEntityLook, forceUpdate);
								sendPacketExceptSelf(p, packetHeadRotation, forceUpdate);
							} catch (Exception e) {
								e.printStackTrace();
							}
							
							if(type.equals(UpdateType.NICK)) {
								if(FileUtils.cfg.getBoolean("NickMessage.OnNnick")) {
									for(Player all : Bukkit.getOnlinePlayers())
										all.sendMessage(FileUtils.getConfigString("NickMessage.Nick.Join").replace("%displayName%", p.getDisplayName()).replace("%name%", p.getName()));
								}
							} else if(type.equals(UpdateType.UNNICK)) {
								if(FileUtils.cfg.getBoolean("NickMessage.OnUnnick")) {
									for(Player all : Bukkit.getOnlinePlayers())
										all.sendMessage(FileUtils.getConfigString("NickMessage.Unnick.Join").replace("%displayName%", p.getDisplayName()).replace("%name%", p.getName()));
								}
							}
						}, 4 + (FileUtils.cfg.getBoolean("RandomDisguiseDelay") ? (20 * new Random().nextInt(3)) : 0));
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}, 1);
			}
				
			if(!(EazyNick.version.equals("1_7_R4") || EazyNick.version.equals("1_8_R1") || EazyNick.version.equals("1_8_R2")))
				updatePlayerCache(p);
			
			if(Utils.oldDisplayNames.containsKey(p.getUniqueId())) {
				if(FileUtils.cfg.getBoolean("NickCommands.OnNick")) {
					if(Utils.placeholderAPIStatus())
						FileUtils.cfg.getStringList("NickCommands.Nick").forEach(cmd -> Bukkit.dispatchCommand(FileUtils.cfg.getBoolean("NickCommands.SendAsConsole") ? Bukkit.getConsoleSender() : p, PlaceholderAPI.setPlaceholders(p, cmd)));
					else
						FileUtils.cfg.getStringList("NickCommands.Nick").forEach(cmd -> Bukkit.dispatchCommand(FileUtils.cfg.getBoolean("NickCommands.SendAsConsole") ? Bukkit.getConsoleSender() : p, cmd));
				}
			} else {
				if(FileUtils.cfg.getBoolean("NickCommands.OnUnnick")) {
					if(Utils.placeholderAPIStatus())
						FileUtils.cfg.getStringList("NickCommands.Unnick").forEach(cmd -> Bukkit.dispatchCommand(FileUtils.cfg.getBoolean("NickCommands.SendAsConsole") ? Bukkit.getConsoleSender() : p, PlaceholderAPI.setPlaceholders(p, cmd)));
					else
						FileUtils.cfg.getStringList("NickCommands.Unnick").forEach(cmd -> Bukkit.dispatchCommand(FileUtils.cfg.getBoolean("NickCommands.SendAsConsole") ? Bukkit.getConsoleSender() : p, cmd));
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
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
				if(cachedName != null) {
					Object entityPlayer = map.get(cachedName);
					
					if((entityPlayer == null) || entityPlayer.getClass().getMethod("getUniqueID").invoke(entityPlayer).equals(p.getUniqueId()))
						toRemove.add(cachedName);
				}
			}
			
			for (String string : toRemove)
				map.remove(string);
			
			map.put(p.getName(), p.getClass().getMethod("getHandle").invoke(p));
			
			f.set(playerList, map);
			f.setAccessible(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void sendPacket(Player p, Object packet, boolean forceUpdate) {
		for (Player all : Bukkit.getOnlinePlayers()) {
			if((all.canSee(p) && all.getWorld().getName().equals(p.getWorld().getName())) || forceUpdate) {
				if(all.getEntityId() != p.getEntityId()) {
					if(!(all.hasPermission("nick.bypass")) || forceUpdate)
						sendPacketNMS(all, packet);
				} else {
					if(FileUtils.cfg.getBoolean("SeeNickSelf") || forceUpdate)
						sendPacketNMS(all, packet);
				}
			}
		}
	}
	
	public static void sendPacketExceptSelf(Player p, Object packet, boolean forceUpdate) {
		for (Player all : Bukkit.getOnlinePlayers()) {
			if((all.canSee(p) && all.getWorld().getName().equals(p.getWorld().getName())) || forceUpdate) {
				if(all.getEntityId() != p.getEntityId()) {
					if(!(all.hasPermission("nick.bypass")) || forceUpdate)
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
	
	public enum UpdateType {
		
		NICK, UNNICK, QUIT, UPDATE;
		
	}
	
}