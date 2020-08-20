package net.dev.eazynick.utils.nickutils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
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
import net.dev.eazynick.utils.MineSkinAPI;
import net.dev.eazynick.utils.ReflectUtils;
import net.dev.eazynick.utils.Utils;

import me.clip.placeholderapi.PlaceholderAPI;

public class NMSNickManager extends ReflectUtils {

	private EazyNick eazyNick;
	private FileUtils fileUtils;
	private MineSkinAPI mineSkinAPI;
	private Utils utils;
	
	public NMSNickManager() {
		eazyNick = EazyNick.getInstance();
		mineSkinAPI = eazyNick.getMineSkinAPI();
		utils = eazyNick.getUtils();
		fileUtils = eazyNick.getFileUtils();
	}
	
	public Object getAsCraftChatMessage(String s) {
		Class<?> craftChatMessage = getCraftClass("util.CraftChatMessage");
		
		try {
			return (utils.isNewVersion() ? craftChatMessage.getMethod("fromStringOrNull", String.class).invoke(craftChatMessage, s) : ((Object[]) craftChatMessage.getMethod("fromString", String.class).invoke(craftChatMessage, s))[0]);
		} catch (Exception e) {
			return null;
		}
	}
	
	public void updatePlayerListName(Player p, String name) {
		try {
			Class<?> enumPlayerInfoAction = eazyNick.getVersion().equals("1_8_R1") ? getNMSClass("EnumPlayerInfoAction") : getNMSClass("PacketPlayOutPlayerInfo").getDeclaredClasses()[(eazyNick.getVersion().equals("1_11_R1") || eazyNick.getVersion().equals("1_12_R1") || utils.isNewVersion()) ? 1 : 2];
			Object entityPlayer = p.getClass().getMethod("getHandle").invoke(p);
			Object entityPlayerArray = Array.newInstance(entityPlayer.getClass(), 1);
			
			Array.set(entityPlayerArray, 0, entityPlayer);
			
			Bukkit.getScheduler().runTask(eazyNick, () -> {
				try {
					Field f = getNMSClass("EntityPlayer").getDeclaredField("listName");
					f.setAccessible(true);
					f.set(entityPlayer, getAsCraftChatMessage(name));
					
					sendPacket(p, getNMSClass("PacketPlayOutPlayerInfo").getConstructor(enumPlayerInfoAction, entityPlayerArray.getClass()).newInstance(enumPlayerInfoAction.getDeclaredField("UPDATE_DISPLAY_NAME").get(enumPlayerInfoAction), entityPlayerArray), false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updatePlayerListName_1_7_R4(Player p, String name) {
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
						if(!(all.hasPermission("nick.bypass") && fileUtils.getConfig().getBoolean("EnableBypassPermission"))) {
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
						if(fileUtils.getConfig().getBoolean("SeeNickSelf")) {
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
	
	public void updateName(Player p, String nickName) {
		try {
			Object gameProfile = p.getClass().getMethod("getProfile").invoke(p);
			Field nameField = utils.getNameField();
			nameField.setAccessible(true);
			nameField.set(gameProfile, nickName);
			nameField.setAccessible(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updateUniqueId(Player p, UUID uniqueId) {
		if(uniqueId == null)
			uniqueId = p.getUniqueId();
		
		try {
			Object gameProfile = p.getClass().getMethod("getProfile").invoke(p);
			Field uuidField = utils.getUUIDField();
			uuidField.setAccessible(true);
			uuidField.set(gameProfile, uniqueId);
			uuidField.setAccessible(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updateSkin(Player p, String skinName) {
		try {
			GameProfile gameProfile = (GameProfile) p.getClass().getMethod("getProfile").invoke(p);
			
			gameProfile.getProperties().removeAll("textures");

			if(skinName.equals("MineSkin"))
				gameProfile.getProperties().putAll("textures", mineSkinAPI.getTextureProperties(utils.getMineSkinIds().get(new Random().nextInt(utils.getMineSkinIds().size()))));
			else {
				GameProfile gp = null;
				
				try {
					gp = eazyNick.getGameProfileBuilder().fetch(eazyNick.getUUIDFetcher().getUUID(skinName));
				} catch (Exception e) {
					if(fileUtils.getConfig().getBoolean("ShowProfileErrorMessages"))
						p.sendMessage(utils.getPrefix() + "§cAn error occured while preparing new profile");
				}
				
				if(gp == null)
					gp = utils.getDefaultGameProfile();
	
				Collection<Property> props = gp.getProperties().get("textures");
				gameProfile.getProperties().putAll("textures", props);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public void updateSkin_1_8_R1(Player p, String skinName) {
		try {
			GameProfile gameProfile = (GameProfile) p.getClass().getMethod("getProfile").invoke(p);

			gameProfile.getProperties().removeAll("textures");
			
			if(skinName.equals("MineSkin"))
				gameProfile.getProperties().putAll("textures", mineSkinAPI.getTextureProperties(utils.getMineSkinIds().get(new Random().nextInt(utils.getMineSkinIds().size()))));
			else {
				GameProfile gp = null;
				
				try {
					gp = eazyNick.getGameProfileBuilder_1_8_R1().fetch(eazyNick.getUUIDFetcher_1_8_R1().getUUID(skinName));
				} catch (Exception e) {
					if(fileUtils.getConfig().getBoolean("ShowProfileErrorMessages"))
						p.sendMessage(utils.getPrefix() + "§cAn error occured while preparing new profile");
				}
				
				if(gp == null)
					gp = utils.getDefaultGameProfile();
	
				Collection<Property> props = gp.getProperties().get("textures");
				gameProfile.getProperties().putAll("textures", props);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public void updateSkin_1_7_R4(Player p, String skinName) {
		try {
			net.minecraft.util.com.mojang.authlib.GameProfile gameProfile = (net.minecraft.util.com.mojang.authlib.GameProfile) p.getClass().getMethod("getProfile").invoke(p);
			
			gameProfile.getProperties().removeAll("textures");
			
			if(skinName.equals("MineSkin"))
				gameProfile.getProperties().putAll("textures", mineSkinAPI.getTextureProperties_1_7(utils.getMineSkinIds().get(new Random().nextInt(utils.getMineSkinIds().size()))));
			else {
				net.minecraft.util.com.mojang.authlib.GameProfile gp = null;
				
				try {
					gp = eazyNick.getGameProfileBuilder_1_7().fetch(eazyNick.getUUIDFetcher_1_7().getUUID(skinName));
				} catch (Exception e) {
					if(fileUtils.getConfig().getBoolean("ShowProfileErrorMessages"))
						p.sendMessage(utils.getPrefix() + "§cAn error occured while preparing new profile");
				}
				
				if(gp == null)
					gp = utils.getDefaultGameProfile_1_7();
	
				Collection<net.minecraft.util.com.mojang.authlib.properties.Property> props = gp.getProperties().get("textures");
				gameProfile.getProperties().putAll("textures", props);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public void updatePlayer(Player p, UpdateType type, String skinName, boolean forceUpdate) {
		String version = eazyNick.getVersion();
		NickManager api = new NickManager(p);
		String nickName = api.getNickName();
		UUID uuidBefore = p.getUniqueId();
		boolean uuidSpoof = fileUtils.getConfig().getBoolean("Settings.ChangeOptions.UUID");
		UUID spoofedUUID = version.equals("1_7_R4") ? eazyNick.getUUIDFetcher_1_7().getUUID(nickName) : (version.equals("1_8_R1") ? eazyNick.getUUIDFetcher_1_8_R1().getUUID(nickName) : eazyNick.getUUIDFetcher().getUUID(nickName));
		
		try {
			Object entityPlayer = p.getClass().getMethod("getHandle").invoke(p);
			Object entityPlayerArray = Array.newInstance(entityPlayer.getClass(), 1);
			Array.set(entityPlayerArray, 0, entityPlayer);
			
			Object packetEntityDestroy = getNMSClass("PacketPlayOutEntityDestroy").getConstructor(int[].class).newInstance(new int[] { p.getEntityId() });
			Object packetPlayOutPlayerInfoRemove;
			
			sendPacket(p, packetEntityDestroy, forceUpdate);
			
			if(uuidSpoof && !(type.equals(UpdateType.NICK) || type.equals(UpdateType.UPDATE)))
				updateUniqueId(p, spoofedUUID);
			
			if(version.equals("1_7_R4")) {
				Class<?> playOutPlayerInfo = getNMSClass("PacketPlayOutPlayerInfo");
				
				packetPlayOutPlayerInfoRemove = playOutPlayerInfo.getMethod("removePlayer", getNMSClass("EntityPlayer")).invoke(playOutPlayerInfo, entityPlayer);
			} else {
				Class<?> enumPlayerInfoAction = (version.equals("1_8_R1") ? getNMSClass("EnumPlayerInfoAction") : getNMSClass("PacketPlayOutPlayerInfo").getDeclaredClasses()[(version.startsWith("1_1") && !(version.equals("1_10_R1"))) ? 1 : 2]);
				
				packetPlayOutPlayerInfoRemove = getNMSClass("PacketPlayOutPlayerInfo").getConstructor(enumPlayerInfoAction, entityPlayerArray.getClass()).newInstance(enumPlayerInfoAction.getDeclaredField("REMOVE_PLAYER").get(enumPlayerInfoAction), entityPlayerArray);
			}
			
			sendPacket(p, packetPlayOutPlayerInfoRemove, forceUpdate);
			
			if(!(type.equals(UpdateType.QUIT))) {
				if(!(eazyNick.isEnabled()) || !(p.isOnline()))
					return;
				
				if(type.equals(UpdateType.NICK)) {
					if(fileUtils.getConfig().getBoolean("NickMessage.OnNnick")) {
						for(Player all : Bukkit.getOnlinePlayers())
							all.sendMessage(fileUtils.getConfigString("NickMessage.Nick.Quit").replace("%displayName%", p.getDisplayName()).replace("%name%", api.getRealName()));
					}
				} else if(type.equals(UpdateType.UNNICK)) {
					if(fileUtils.getConfig().getBoolean("NickMessage.OnUnnick")) {
						for(Player all : Bukkit.getOnlinePlayers())
							all.sendMessage(fileUtils.getConfigString("NickMessage.Unnick.Quit").replace("%displayName%", p.getDisplayName()).replace("%name%", api.getNickName()));
					}
				}
				
				Bukkit.getScheduler().runTaskLater(eazyNick, () -> {
					try {
						Object packetNamedEntitySpawn;
						Object packetPlayOutPlayerInfoAdd;
						Object worldClient = entityPlayer.getClass().getMethod("getWorld").invoke(entityPlayer);
						Object worldData = worldClient.getClass().getMethod("getWorldData").invoke(worldClient);
						Object interactManager = entityPlayer.getClass().getField("playerInteractManager").get(entityPlayer);
						Object packetRespawnPlayer;

						if(uuidSpoof)
							sendPacket(p, packetPlayOutPlayerInfoRemove, forceUpdate);
						
						if(uuidSpoof && !(type.equals(UpdateType.NICK) || type.equals(UpdateType.UPDATE)))
							updateUniqueId(p, uuidBefore);

						api.changeSkin(skinName);
						
						if(version.startsWith("1_16")) {
							Object craftWorld = p.getWorld().getClass().getMethod("getHandle").invoke(p.getWorld());
							Class<?> enumGameMode = getNMSClass("EnumGamemode");

							packetRespawnPlayer = version.equals("1_16_R1") ? getNMSClass("PacketPlayOutRespawn").getConstructor(getNMSClass("ResourceKey"), getNMSClass("ResourceKey"), long.class, enumGameMode, enumGameMode, boolean.class, boolean.class, boolean.class).newInstance(craftWorld.getClass().getMethod("getTypeKey").invoke(craftWorld), craftWorld.getClass().getMethod("getDimensionKey").invoke(craftWorld), getNMSClass("BiomeManager").getMethod("a", long.class).invoke(null, p.getWorld().getSeed()), interactManager.getClass().getMethod("getGameMode").invoke(interactManager), interactManager.getClass().getMethod("c").invoke(interactManager), craftWorld.getClass().getMethod("isDebugWorld").invoke(craftWorld), craftWorld.getClass().getMethod("isFlatWorld").invoke(craftWorld), true) : getNMSClass("PacketPlayOutRespawn").getConstructor(getNMSClass("DimensionManager"), getNMSClass("ResourceKey"), long.class, enumGameMode, enumGameMode, boolean.class, boolean.class, boolean.class).newInstance(craftWorld.getClass().getMethod("getDimensionManager").invoke(craftWorld), craftWorld.getClass().getMethod("getDimensionKey").invoke(craftWorld), getNMSClass("BiomeManager").getMethod("a", long.class).invoke(null, p.getWorld().getSeed()), interactManager.getClass().getMethod("getGameMode").invoke(interactManager), interactManager.getClass().getMethod("c").invoke(interactManager), craftWorld.getClass().getMethod("isDebugWorld").invoke(craftWorld), craftWorld.getClass().getMethod("isFlatWorld").invoke(craftWorld), true);
						} else if(version.startsWith("1_15")) {
							Class<?> dimensionManager = getNMSClass("DimensionManager");
							Class<?> worldType = getNMSClass("WorldType");
							Class<?> enumGameMode = getNMSClass("EnumGamemode");
							
							packetRespawnPlayer = getNMSClass("PacketPlayOutRespawn").getConstructor(dimensionManager, long.class, worldType, enumGameMode).newInstance(dimensionManager.getMethod("a", int.class).invoke(dimensionManager, p.getWorld().getEnvironment().getId()), Hashing.sha256().hashLong(p.getWorld().getSeed()).asLong(), worldType.getMethod("getType", String.class).invoke(worldType, p.getWorld().getWorldType().getName()), enumGameMode.getMethod("getById", int.class).invoke(enumGameMode, p.getGameMode().getValue()));
						} else if(version.startsWith("1_14")) {
							Class<?> dimensionManager = getNMSClass("DimensionManager");
							Class<?> worldType = getNMSClass("WorldType");
							Class<?> enumGameMode = getNMSClass("EnumGamemode");
							
							packetRespawnPlayer = getNMSClass("PacketPlayOutRespawn").getConstructor(dimensionManager, worldType, enumGameMode).newInstance(dimensionManager.getMethod("a", int.class).invoke(dimensionManager, p.getWorld().getEnvironment().getId()), worldType.getMethod("getType", String.class).invoke(worldType, p.getWorld().getWorldType().getName()), enumGameMode.getMethod("getById", int.class).invoke(enumGameMode, p.getGameMode().getValue()));
						} else if(version.equals("1_13_R2")) {
							Object craftWorld = p.getWorld().getClass().getMethod("getHandle").invoke(p.getWorld());
							
							packetRespawnPlayer = getNMSClass("PacketPlayOutRespawn").getConstructor(getNMSClass("DimensionManager"), getNMSClass("EnumDifficulty"), getNMSClass("WorldType"), getNMSClass("EnumGamemode")).newInstance(worldClient.getClass().getDeclaredField("dimension").get(craftWorld), worldClient.getClass().getMethod("getDifficulty").invoke(worldClient), worldData.getClass().getMethod("getType").invoke(worldData), interactManager.getClass().getMethod("getGameMode").invoke(interactManager));
						} else {
							Class<?> enumGameMode = (version.equals("1_8_R2") || version.equals("1_8_R3") || version.equals("1_9_R1") || version.equals("1_9_R2")) ? getNMSClass("WorldSettings").getDeclaredClasses()[0] : getNMSClass("EnumGamemode");
							
							packetRespawnPlayer = getNMSClass("PacketPlayOutRespawn").getConstructor(int.class, getNMSClass("EnumDifficulty"), getNMSClass("WorldType"), enumGameMode).newInstance(p.getWorld().getEnvironment().getId(), (version.equals("1_7_R4") ? getNMSClass("World").getDeclaredField("difficulty").get(worldClient) : worldClient.getClass().getMethod("getDifficulty").invoke(worldClient)), worldData.getClass().getMethod("getType").invoke(worldData), interactManager.getClass().getMethod("getGameMode").invoke(interactManager));
						}
						
						sendPacketNMS(p, packetRespawnPlayer);
						
						if(uuidSpoof && (type.equals(UpdateType.NICK) || type.equals(UpdateType.UPDATE)))
							updateUniqueId(p, spoofedUUID);
						
						packetNamedEntitySpawn = getNMSClass("PacketPlayOutNamedEntitySpawn").getConstructor(getNMSClass("EntityHuman")).newInstance(entityPlayer);
						
						if(version.equals("1_7_R4")) {
							Class<?> playOutPlayerInfo = getNMSClass("PacketPlayOutPlayerInfo");
							
							packetPlayOutPlayerInfoAdd = playOutPlayerInfo.getMethod("addPlayer", getNMSClass("EntityPlayer")).invoke(playOutPlayerInfo, entityPlayer);
						} else {
							Class<?> enumPlayerInfoAction = (version.equals("1_8_R1") ? getNMSClass("EnumPlayerInfoAction") : getNMSClass("PacketPlayOutPlayerInfo").getDeclaredClasses()[(version.startsWith("1_1") && !(version.equals("1_10_R1"))) ? 1 : 2]);
							
							packetPlayOutPlayerInfoAdd = getNMSClass("PacketPlayOutPlayerInfo").getConstructor(enumPlayerInfoAction, entityPlayerArray.getClass()).newInstance(enumPlayerInfoAction.getDeclaredField("ADD_PLAYER").get(enumPlayerInfoAction), entityPlayerArray);
						}
						
						p.teleport(new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), p.getLocation().getYaw(), p.getLocation().getPitch()));
						p.updateInventory();
						
						if(!(eazyNick.isEnabled()) || !(p.isOnline()))
							return;
						
						Bukkit.getScheduler().runTaskLater(eazyNick, () -> {
							sendPacket(p, packetPlayOutPlayerInfoAdd, forceUpdate);
							sendPacketExceptSelf(p, packetNamedEntitySpawn, forceUpdate);
							
							if(uuidSpoof && (type.equals(UpdateType.NICK) || type.equals(UpdateType.UPDATE))) {
								Bukkit.getOnlinePlayers().stream().filter(all -> (!(all.hasPermission("nick.bypass") && fileUtils.getConfig().getBoolean("EnableBypassPermission")) && (all != p))).forEach(all -> {
									all.hidePlayer(p);
									all.showPlayer(p);
									
									sendPacketNMS(all, packetPlayOutPlayerInfoRemove);
								});
							}
							
							if(uuidSpoof)
								updateUniqueId(p, uuidBefore);
							
							try {
								Object packetEntityLook = ((version.equals("1_7_R4") || version.equals("1_8_R1")) ? getNMSClass("PacketPlayOutEntityLook") : getNMSClass("PacketPlayOutEntity").getDeclaredClasses()[0]).getConstructor(int.class, byte.class, byte.class, boolean.class).newInstance(p.getEntityId(), (byte) ((int) (p.getLocation().getYaw() * 256.0F / 360.0F)), (byte) ((int) (p.getLocation().getPitch() * 256.0F / 360.0F)), true);
								Object packetHeadRotation = getNMSClass("PacketPlayOutEntityHeadRotation").newInstance();
								setField(packetHeadRotation, "a", p.getEntityId());
								setField(packetHeadRotation, "b", (byte) ((int) (p.getLocation().getYaw() * 256.0F / 360.0F)));
								
								sendPacketExceptSelf(p, packetEntityLook, forceUpdate);
								sendPacketExceptSelf(p, packetHeadRotation, forceUpdate);
							} catch (Exception e) {
								e.printStackTrace();
							}

							if(type.equals(UpdateType.NICK)) {
								if(fileUtils.getConfig().getBoolean("NickMessage.OnNnick")) {
									for(Player all : Bukkit.getOnlinePlayers())
										all.sendMessage(fileUtils.getConfigString("NickMessage.Nick.Join").replace("%displayName%", p.getDisplayName()).replace("%name%", p.getName()));
								}
							} else if(type.equals(UpdateType.UNNICK)) {
								if(fileUtils.getConfig().getBoolean("NickMessage.OnUnnick")) {
									for(Player all : Bukkit.getOnlinePlayers())
										all.sendMessage(fileUtils.getConfigString("NickMessage.Unnick.Join").replace("%displayName%", p.getDisplayName()).replace("%name%", p.getName()));
								}
							}
						}, 4 + (fileUtils.getConfig().getBoolean("RandomDisguiseDelay") ? (20 * new Random().nextInt(3)) : 0));
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}, 1);
			}
				
			if(!(version.equals("1_7_R4") || version.equals("1_8_R1") || version.equals("1_8_R2")))
				updatePlayerCache(p);
			
			if(type.equals(UpdateType.NICK)) {
				if(fileUtils.getConfig().getBoolean("NickCommands.OnNick")) {
					if(utils.placeholderAPIStatus())
						fileUtils.getConfig().getStringList("NickCommands.Nick").forEach(cmd -> Bukkit.dispatchCommand(fileUtils.getConfig().getBoolean("NickCommands.SendAsConsole") ? Bukkit.getConsoleSender() : p, PlaceholderAPI.setPlaceholders(p, cmd)));
					else
						fileUtils.getConfig().getStringList("NickCommands.Nick").forEach(cmd -> Bukkit.dispatchCommand(fileUtils.getConfig().getBoolean("NickCommands.SendAsConsole") ? Bukkit.getConsoleSender() : p, cmd));
				}
			} else {
				if(fileUtils.getConfig().getBoolean("NickCommands.OnUnnick")) {
					if(utils.placeholderAPIStatus())
						fileUtils.getConfig().getStringList("NickCommands.Unnick").forEach(cmd -> Bukkit.dispatchCommand(fileUtils.getConfig().getBoolean("NickCommands.SendAsConsole") ? Bukkit.getConsoleSender() : p, PlaceholderAPI.setPlaceholders(p, cmd)));
					else
						fileUtils.getConfig().getStringList("NickCommands.Unnick").forEach(cmd -> Bukkit.dispatchCommand(fileUtils.getConfig().getBoolean("NickCommands.SendAsConsole") ? Bukkit.getConsoleSender() : p, cmd));
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void updatePlayerCache(Player p) {
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
			
			map.put(eazyNick.getVersion().startsWith("1_16") ? p.getName().toLowerCase(Locale.ROOT) : p.getName(), p.getClass().getMethod("getHandle").invoke(p));
			
			f.set(playerList, map);
			f.setAccessible(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendPacket(Player p, Object packet, boolean forceUpdate) {
		for (Player all : Bukkit.getOnlinePlayers()) {
			if((all.canSee(p) && all.getWorld().getName().equals(p.getWorld().getName())) || forceUpdate) {
				if(all.getEntityId() != p.getEntityId()) {
					if(!(all.hasPermission("nick.bypass") && fileUtils.getConfig().getBoolean("EnableBypassPermission")) || forceUpdate)
						sendPacketNMS(all, packet);
				} else if(fileUtils.getConfig().getBoolean("SeeNickSelf") || forceUpdate)
					sendPacketNMS(all, packet);
			}
		}
	}
	
	public void sendPacketExceptSelf(Player p, Object packet, boolean forceUpdate) {
		for (Player all : Bukkit.getOnlinePlayers()) {
			if(all.getWorld().getName().equals(p.getWorld().getName()) || forceUpdate) {
				if(all.getEntityId() != p.getEntityId()) {
					if(!(all.hasPermission("nick.bypass") && fileUtils.getConfig().getBoolean("EnableBypassPermission")) || forceUpdate)
						sendPacketNMS(all, packet);
				}
			}
		}
	}

	public void sendPacketNMS(Player p, Object packet) {
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