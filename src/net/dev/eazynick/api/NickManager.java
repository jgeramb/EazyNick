package net.dev.eazynick.api;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.hash.Hashing;
import com.nametagedit.plugin.NametagEdit;
import com.nametagedit.plugin.api.INametagApi;
import com.nametagedit.plugin.api.data.Nametag;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.nms.ReflectionHelper;
import net.dev.eazynick.nms.ScoreboardTeamHandler;
import net.dev.eazynick.utilities.*;
import net.dev.eazynick.utilities.AsyncTask.AsyncRunnable;
import net.dev.eazynick.utilities.configuration.yaml.LanguageYamlFile;
import net.dev.eazynick.utilities.configuration.yaml.SetupYamlFile;
import net.milkbowl.vault.chat.Chat;
import net.skinsrestorer.api.PlayerWrapper;
import net.skinsrestorer.api.SkinsRestorerAPI;

import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.UltraPermissionsAPI;
import me.clip.placeholderapi.PlaceholderAPI;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.permission.PermissionEntity;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class NickManager extends ReflectionHelper {

	private EazyNick eazyNick;
	private SetupYamlFile setupYamlFile;
	private Utils utils;
	private Player player;
	
	public NickManager(Player player) {
		this.eazyNick = EazyNick.getInstance();
		this.setupYamlFile = eazyNick.getSetupYamlFile();
		this.utils = eazyNick.getUtils();
		this.player = player;
	}
	
	private void sendPacket(Player nickedPlayer, Player player, Object packet) {
		if((player.canSee(nickedPlayer) && player.getWorld().getName().equals(nickedPlayer.getWorld().getName()))) {
			if(player.getEntityId() != nickedPlayer.getEntityId()) {
				if(!(player.hasPermission("nick.bypass") && setupYamlFile.getConfiguration().getBoolean("EnableBypassPermission")))
					sendPacketNMS(player, packet);
			} else if(setupYamlFile.getConfiguration().getBoolean("SeeNickSelf"))
				sendPacketNMS(player, packet);
		}
	}
	
	private void sendPacketExceptSelf(Player player, Object packet) {
		for (Player currentPlayer : Bukkit.getOnlinePlayers()) {
			if(currentPlayer.getWorld().getName().equals(player.getWorld().getName())) {
				if((eazyNick.getVersion().startsWith("1_7") ? true : (!(player.getGameMode().equals(GameMode.SPECTATOR)) || currentPlayer.getGameMode().equals(GameMode.SPECTATOR))) && (currentPlayer.getEntityId() != player.getEntityId())) {
					if(!(currentPlayer.hasPermission("nick.bypass") && setupYamlFile.getConfiguration().getBoolean("EnableBypassPermission")))
						sendPacketNMS(currentPlayer, packet);
				}
			}
		}
	}
	
	private void sendPacketNMS(Player player, Object packet) {
		try {
			Object handle = player.getClass().getMethod("getHandle").invoke(player);
			Object playerConnection = handle.getClass().getDeclaredField("playerConnection").get(handle);
			playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void setPlayerListName(String name) {
		if(setupYamlFile.getConfiguration().getBoolean("Settings.ChangeOptions.PlayerListName")) {
			if(eazyNick.getVersion().equals("1_7_R4")) {
				try {
					Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);

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
					
					for(Player currentPlayer : Bukkit.getOnlinePlayers()) {
						if(currentPlayer.canSee(player)) {
							if(!(currentPlayer.getUniqueId().equals(player.getUniqueId()))) {
								if(!(currentPlayer.hasPermission("nick.bypass") && setupYamlFile.getConfiguration().getBoolean("EnableBypassPermission"))) {
									Object playerConenction = getNMSClass("EntityPlayer").getDeclaredField("playerConnection").get(currentPlayer.getClass().getMethod("getHandle").invoke(currentPlayer));
									Object networkManager = playerConenction.getClass().getDeclaredField("networkManager").get(playerConenction);
									int version = (int) networkManager.getClass().getMethod("getVersion").invoke(networkManager);
									
									if (version < 28) {
										sendPacketNMS(currentPlayer, packetPlayOutPlayerInfoRemove);
										sendPacketNMS(currentPlayer, packetPlayOutPlayerInfoAdd);
									} else
										sendPacketNMS(currentPlayer, packet);
								}
							} else {
								if(setupYamlFile.getConfiguration().getBoolean("SeeNickSelf")) {
									Object playerConenction = entityPlayer.getClass().getDeclaredField("playerConnection").get(entityPlayer);
									Object networkManager = playerConenction.getClass().getDeclaredField("networkManager").get(playerConenction);
									int version = (int) networkManager.getClass().getMethod("getVersion").invoke(networkManager);
									
									if (version < 28) {
										sendPacketNMS(currentPlayer, packetPlayOutPlayerInfoRemove);
										sendPacketNMS(currentPlayer, packetPlayOutPlayerInfoAdd);
									} else
										sendPacketNMS(currentPlayer, packet);
								}
							}
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else {
				try {
					Class<?> enumPlayerInfoAction = eazyNick.getVersion().equals("1_8_R1") ? getNMSClass("EnumPlayerInfoAction") : getNMSClass("PacketPlayOutPlayerInfo").getDeclaredClasses()[(eazyNick.getVersion().equals("1_11_R1") || eazyNick.getVersion().equals("1_12_R1") || utils.isNewVersion()) ? 1 : 2];
					Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
					Object entityPlayerArray = Array.newInstance(entityPlayer.getClass(), 1);
					
					Array.set(entityPlayerArray, 0, entityPlayer);
					
					final String finalName = name;
					
					Bukkit.getScheduler().runTask(eazyNick, () -> {
						try {
							Class<?> craftChatMessage = getCraftClass("util.CraftChatMessage");
							Field f = getNMSClass("EntityPlayer").getDeclaredField("listName");
							f.setAccessible(true);
							f.set(entityPlayer, utils.isNewVersion() ? craftChatMessage.getMethod("fromStringOrNull", String.class).invoke(craftChatMessage, finalName) : ((Object[]) craftChatMessage.getMethod("fromString", String.class).invoke(craftChatMessage, finalName))[0]);
							
							for(Player currentPlayer : Bukkit.getOnlinePlayers())
								sendPacket(player, currentPlayer, getNMSClass("PacketPlayOutPlayerInfo").getConstructor(enumPlayerInfoAction, entityPlayerArray.getClass()).newInstance(enumPlayerInfoAction.getDeclaredField("UPDATE_DISPLAY_NAME").get(enumPlayerInfoAction), entityPlayerArray));
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					});
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	public void updatePlayer(boolean spawnPlayer) {
		try {
			String version = eazyNick.getVersion();
			Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
			Object entityPlayerArray = Array.newInstance(entityPlayer.getClass(), 1);
			Array.set(entityPlayerArray, 0, entityPlayer);
			
			Object worldClient = entityPlayer.getClass().getMethod("getWorld").invoke(entityPlayer), worldData = worldClient.getClass().getMethod("getWorldData").invoke(worldClient), interactManager = entityPlayer.getClass().getField("playerInteractManager").get(entityPlayer);
			
			//Despawn and remove from tablist
			for(Player currentPlayer : Bukkit.getOnlinePlayers()) {
				sendPacket(player, currentPlayer, getNMSClass("PacketPlayOutEntityDestroy").getConstructor(int[].class).newInstance(new int[] { player.getEntityId() }));
				
				Object packetPlayOutPlayerInfoRemove;
				
				if(version.equals("1_7_R4")) {
					Class<?> playOutPlayerInfo = getNMSClass("PacketPlayOutPlayerInfo");
					
					packetPlayOutPlayerInfoRemove = playOutPlayerInfo.getMethod("removePlayer", getNMSClass("EntityPlayer")).invoke(playOutPlayerInfo, entityPlayer);
				} else {
					Class<?> enumPlayerInfoAction = (version.equals("1_8_R1") ? getNMSClass("EnumPlayerInfoAction") : getNMSClass("PacketPlayOutPlayerInfo").getDeclaredClasses()[(version.startsWith("1_1") && !(version.equals("1_10_R1"))) ? 1 : 2]);
					
					packetPlayOutPlayerInfoRemove = getNMSClass("PacketPlayOutPlayerInfo").getConstructor(enumPlayerInfoAction, entityPlayerArray.getClass()).newInstance(enumPlayerInfoAction.getDeclaredField("REMOVE_PLAYER").get(enumPlayerInfoAction), entityPlayerArray);
				}
				
				sendPacket(player, currentPlayer, packetPlayOutPlayerInfoRemove);
			}
			
			new BukkitRunnable() {
				
				@Override
				public void run() {
					if(!(eazyNick.isEnabled()) || !(player.isOnline()))
						return;
					
					try {
						//Add to tablist and spawn
						for(Player currentPlayer : Bukkit.getOnlinePlayers()) {
							Object packetPlayOutPlayerInfoAdd;
							
							if(version.equals("1_7_R4")) {
								Class<?> playOutPlayerInfo = getNMSClass("PacketPlayOutPlayerInfo");
								
								packetPlayOutPlayerInfoAdd = playOutPlayerInfo.getMethod("addPlayer", getNMSClass("EntityPlayer")).invoke(playOutPlayerInfo, entityPlayer);
							} else {
								Class<?> enumPlayerInfoAction = (version.equals("1_8_R1") ? getNMSClass("EnumPlayerInfoAction") : getNMSClass("PacketPlayOutPlayerInfo").getDeclaredClasses()[(version.startsWith("1_1") && !(version.equals("1_10_R1"))) ? 1 : 2]);
								
								packetPlayOutPlayerInfoAdd = getNMSClass("PacketPlayOutPlayerInfo").getConstructor(enumPlayerInfoAction, entityPlayerArray.getClass()).newInstance(enumPlayerInfoAction.getDeclaredField("ADD_PLAYER").get(enumPlayerInfoAction), entityPlayerArray);
							}
							
							sendPacket(player, currentPlayer, packetPlayOutPlayerInfoAdd);
						}
						
						if(spawnPlayer) {
							sendPacketExceptSelf(player, getNMSClass("PacketPlayOutNamedEntitySpawn").getConstructor(getNMSClass("EntityHuman")).newInstance(entityPlayer));
							
							//Head rotation (Yaw + Pitch)
							Object packetHeadRotation = getNMSClass("PacketPlayOutEntityHeadRotation").newInstance();
							setField(packetHeadRotation, "a", player.getEntityId());
							setField(packetHeadRotation, "b", (byte) ((int) (player.getLocation().getYaw() * 256.0F / 360.0F)));
							
							Class<?> packetPlayOutEntityLook = (version.equals("1_7_R4") || version.equals("1_8_R1")) ? getNMSClass("PacketPlayOutEntityLook") : null;
							
							if(packetPlayOutEntityLook == null) {
								for (Class<?> clazz : getNMSClass("PacketPlayOutEntity").getDeclaredClasses()) {
									if(clazz.getSimpleName().equals("PacketPlayOutEntityLook"))
										packetPlayOutEntityLook = clazz;
								}
							}
							
							sendPacketExceptSelf(player, packetPlayOutEntityLook.getConstructor(int.class, byte.class, byte.class, boolean.class).newInstance(player.getEntityId(), (byte) ((int) (player.getLocation().getYaw() * 256.0F / 360.0F)), (byte) ((int) (player.getLocation().getPitch() * 256.0F / 360.0F)), true));
							sendPacketExceptSelf(player, packetHeadRotation);
						}
						
						//Self update
						Object packetRespawnPlayer;
						
						if(version.startsWith("1_16")) {
							Object craftWorld = player.getWorld().getClass().getMethod("getHandle").invoke(player.getWorld());
							Class<?> enumGameMode = getNMSClass("EnumGamemode");
	
							packetRespawnPlayer = version.equals("1_16_R1") ? getNMSClass("PacketPlayOutRespawn").getConstructor(getNMSClass("ResourceKey"), getNMSClass("ResourceKey"), long.class, enumGameMode, enumGameMode, boolean.class, boolean.class, boolean.class).newInstance(craftWorld.getClass().getMethod("getTypeKey").invoke(craftWorld), craftWorld.getClass().getMethod("getDimensionKey").invoke(craftWorld), getNMSClass("BiomeManager").getMethod("a", long.class).invoke(null, player.getWorld().getSeed()), interactManager.getClass().getMethod("getGameMode").invoke(interactManager), interactManager.getClass().getMethod("c").invoke(interactManager), craftWorld.getClass().getMethod("isDebugWorld").invoke(craftWorld), craftWorld.getClass().getMethod("isFlatWorld").invoke(craftWorld), true) : getNMSClass("PacketPlayOutRespawn").getConstructor(getNMSClass("DimensionManager"), getNMSClass("ResourceKey"), long.class, enumGameMode, enumGameMode, boolean.class, boolean.class, boolean.class).newInstance(craftWorld.getClass().getMethod("getDimensionManager").invoke(craftWorld), craftWorld.getClass().getMethod("getDimensionKey").invoke(craftWorld), getNMSClass("BiomeManager").getMethod("a", long.class).invoke(null, player.getWorld().getSeed()), interactManager.getClass().getMethod("getGameMode").invoke(interactManager), interactManager.getClass().getMethod("c").invoke(interactManager), craftWorld.getClass().getMethod("isDebugWorld").invoke(craftWorld), craftWorld.getClass().getMethod("isFlatWorld").invoke(craftWorld), true);
						} else if(version.startsWith("1_15")) {
							Class<?> dimensionManager = getNMSClass("DimensionManager");
							Class<?> worldType = getNMSClass("WorldType");
							Class<?> enumGameMode = getNMSClass("EnumGamemode");
							
							packetRespawnPlayer = getNMSClass("PacketPlayOutRespawn").getConstructor(dimensionManager, long.class, worldType, enumGameMode).newInstance(dimensionManager.getMethod("a", int.class).invoke(dimensionManager, player.getWorld().getEnvironment().getId()), Hashing.sha256().hashLong(player.getWorld().getSeed()).asLong(), worldType.getMethod("getType", String.class).invoke(worldType, player.getWorld().getWorldType().getName()), enumGameMode.getMethod("getById", int.class).invoke(enumGameMode, player.getGameMode().getValue()));
						} else if(version.startsWith("1_14")) {
							Class<?> dimensionManager = getNMSClass("DimensionManager");
							Class<?> worldType = getNMSClass("WorldType");
							Class<?> enumGameMode = getNMSClass("EnumGamemode");
							
							packetRespawnPlayer = getNMSClass("PacketPlayOutRespawn").getConstructor(dimensionManager, worldType, enumGameMode).newInstance(dimensionManager.getMethod("a", int.class).invoke(dimensionManager, player.getWorld().getEnvironment().getId()), worldType.getMethod("getType", String.class).invoke(worldType, player.getWorld().getWorldType().getName()), enumGameMode.getMethod("getById", int.class).invoke(enumGameMode, player.getGameMode().getValue()));
						} else if(version.equals("1_13_R2")) {
							Object craftWorld = player.getWorld().getClass().getMethod("getHandle").invoke(player.getWorld());
							
							packetRespawnPlayer = getNMSClass("PacketPlayOutRespawn").getConstructor(getNMSClass("DimensionManager"), getNMSClass("EnumDifficulty"), getNMSClass("WorldType"), getNMSClass("EnumGamemode")).newInstance(worldClient.getClass().getDeclaredField("dimension").get(craftWorld), worldClient.getClass().getMethod("getDifficulty").invoke(worldClient), worldData.getClass().getMethod("getType").invoke(worldData), interactManager.getClass().getMethod("getGameMode").invoke(interactManager));
						} else {
							Class<?> enumGameMode = (version.equals("1_8_R2") || version.equals("1_8_R3") || version.equals("1_9_R1") || version.equals("1_9_R2")) ? getNMSClass("WorldSettings").getDeclaredClasses()[0] : getNMSClass("EnumGamemode");
							
							packetRespawnPlayer = getNMSClass("PacketPlayOutRespawn").getConstructor(int.class, getNMSClass("EnumDifficulty"), getNMSClass("WorldType"), enumGameMode).newInstance(player.getWorld().getEnvironment().getId(), (version.equals("1_7_R4") ? getNMSClass("World").getDeclaredField("difficulty").get(worldClient) : worldClient.getClass().getMethod("getDifficulty").invoke(worldClient)), worldData.getClass().getMethod("getType").invoke(worldData), interactManager.getClass().getMethod("getGameMode").invoke(interactManager));
						}
						
						sendPacketNMS(player, packetRespawnPlayer);
						
						//Position
						Object playerConnection = entityPlayer.getClass().getDeclaredField("playerConnection").get(entityPlayer);
						playerConnection.getClass().getMethod("teleport", Location.class).invoke(playerConnection, new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch()));
						
						//Armor, Inventory, Health, Foodlevel & Level
						double oldHealth = player.getHealth(), oldHealthScale = player.isHealthScaled() ? player.getHealthScale() : 0;
						int oldLevel = player.getLevel();
						ItemStack oldHelmet = player.getInventory().getHelmet(), oldChestplate = player.getInventory().getChestplate(), oldLeggings = player.getInventory().getLeggings(), oldBoots = player.getInventory().getBoots();
						
						if(oldHelmet != null)
							player.getInventory().setHelmet(new ItemBuilder(Material.LEATHER_HELMET).setDurability(1).setDisplayName("§r").build());
						
						if(oldChestplate != null)
							player.getInventory().setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE).setDurability(1).setDisplayName("§r").build());
						
						if(oldLeggings != null)
							player.getInventory().setLeggings(new ItemBuilder(Material.LEATHER_LEGGINGS).setDurability(1).setDisplayName("§r").build());
						
						if(oldBoots != null)
							player.getInventory().setBoots(new ItemBuilder(Material.LEATHER_BOOTS).setDurability(1).setDisplayName("§r").build());
						
						player.updateInventory();
						
						if(player.getFoodLevel() != 20)
							player.setFoodLevel(player.getFoodLevel() + 1);
						
						player.setLevel((oldLevel == 10) ? 5 : 10);
						
						if(player.isHealthScaled())
							player.setHealthScale((oldHealthScale == 10) ? 20 : 10);
						
						player.setHealth((oldHealth == 10) ? 5 : 10);
						
						new AsyncTask(new AsyncRunnable() {
							
							@Override
							public void run() {
								if(oldHelmet != null)
									player.getInventory().setHelmet(oldHelmet);
								
								if(oldChestplate != null)
									player.getInventory().setChestplate(oldChestplate);
								
								if(oldLeggings != null)
									player.getInventory().setLeggings(oldLeggings);
								
								if(oldBoots != null)
									player.getInventory().setBoots(oldBoots);
								
								if(player.isHealthScaled())
									player.setHealthScale(oldHealthScale);
											
								player.setHealth(oldHealth);
								player.setLevel(oldLevel);
							}
						}, 250).run();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}.runTaskLater(eazyNick, 5 + (setupYamlFile.getConfiguration().getBoolean("RandomDisguiseDelay") ? (20 * new Random().nextInt(3)) : 0));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void changeSkin(String skinName) {
		if(skinName != null) {
			if(utils.getNickedPlayers().containsKey(player.getUniqueId()))
				utils.getNickedPlayers().get(player.getUniqueId()).setSkinName(skinName);
			else
				utils.getNickedPlayers().put(player.getUniqueId(), new NickedPlayerData(player.getUniqueId(), player.getUniqueId(), player.getDisplayName(), player.getPlayerListName(), player.getName(), player.getName(), skinName, "", "", "", "", "", "", "default", 9999));
			
			updatePlayer(true);
			
			if(utils.skinsRestorerStatus()) {
				Bukkit.getScheduler().runTaskLater(eazyNick, () -> {
					SkinsRestorerAPI skinsRestorerAPI = SkinsRestorerAPI.getApi();
					
					try {
						skinsRestorerAPI.setSkin(player.getName(), skinName);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					
					skinsRestorerAPI.applySkin(new PlayerWrapper(player), skinName);
				}, 6 + (setupYamlFile.getConfiguration().getBoolean("RandomDisguiseDelay") ? (20 * 2) : 0));
			}
		}
	}
	
	public void setName(String nickName) {
		if(utils.getNickedPlayers().containsKey(player.getUniqueId()))
			utils.getNickedPlayers().get(player.getUniqueId()).setNickName(nickName);
		else
			utils.getNickedPlayers().put(player.getUniqueId(), new NickedPlayerData(player.getUniqueId(), player.getUniqueId(), player.getDisplayName(), player.getPlayerListName(), player.getName(), nickName, player.getName(), "", "", "", "", "", "", "default", 9999));
		
		updatePlayer(true);
	}

	public void nickPlayer(String nickName) {
		nickPlayer(nickName, nickName);
	}
	
	public void nickPlayer(String nickName, String skinName) {
		LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
		
		if (!(eazyNick.getVersion().equalsIgnoreCase("1_7_R4")))
			player.setCustomName(nickName);
		
		if(setupYamlFile.getConfiguration().getBoolean("BungeeCord"))
			eazyNick.getMySQLNickManager().addPlayer(player.getUniqueId(), nickName, skinName);
		
		setName(new StringUtils(nickName).removeColorCodes().getString());
		
		if(setupYamlFile.getConfiguration().getBoolean("NickActionBarMessage")) {
			new AsyncTask(new AsyncRunnable() {
				
				@Override
				public void run() {
					ActionBarUtils actionBarUtils = eazyNick.getActionBarUtils();
					
					if(eazyNick.isEnabled() && utils.getNickedPlayers().containsKey(player.getUniqueId()) && (player != null) && player.isOnline() && !(utils.getWorldsWithDisabledActionBar().contains(player.getWorld().getName().toUpperCase()))) {
						NickedPlayerData nickedPlayerData = utils.getNickedPlayers().get(player.getUniqueId());
						
						actionBarUtils.sendActionBar(player, eazyNick.getLanguageYamlFile().getConfigString(player, player.hasPermission("nick.otheractionbarmessage") ? "NickActionBarMessageOther" : "NickActionBarMessage").replace("%nickName%", nickName).replace("%nickname%", nickName).replace("%nickPrefix%", nickedPlayerData.getChatPrefix()).replace("%nickprefix%", nickedPlayerData.getChatPrefix()).replace("%nickSuffix%", nickedPlayerData.getChatSuffix()).replace("%nicksuffix%", nickedPlayerData.getChatSuffix()).replace("%prefix%", utils.getPrefix()));
					} else {
						if(player != null)
							actionBarUtils.sendActionBar(player, "");

						cancel();
					}
				}
			}, 0, 1000).run();
		}
		
		if(setupYamlFile.getConfiguration().getBoolean("NickItem.getOnJoin")  && (player.hasPermission("nick.item"))) {
			for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
				ItemStack item = player.getInventory().getItem(slot);
				
				if((item != null) && (item.getType() != Material.AIR) && (item.getItemMeta() != null) && (item.getItemMeta().getDisplayName() != null)) {
					if(item.getItemMeta().getDisplayName().equalsIgnoreCase(eazyNick.getLanguageYamlFile().getConfigString(player, "NickItem.DisplayName.Disabled")))
						player.getInventory().setItem(slot, new ItemBuilder(Material.getMaterial(setupYamlFile.getConfiguration().getString("NickItem.ItemType.Enabled")), setupYamlFile.getConfiguration().getInt("NickItem.ItemAmount.Enabled"), setupYamlFile.getConfiguration().getInt("NickItem.MetaData.Enabled")).setDisplayName(languageYamlFile.getConfigString(player, "NickItem.DisplayName.Enabled")).setLore(languageYamlFile.getConfigString(player, "NickItem.ItemLore.Enabled").split("&n")).setEnchanted(setupYamlFile.getConfiguration().getBoolean("NickItem.Enchanted.Enabled")).build());
				}
			}
		}
	}
	
	public void unnickPlayer() {
		if(setupYamlFile.getConfiguration().getBoolean("BungeeCord")) {
			eazyNick.getMySQLNickManager().removePlayer(player.getUniqueId());
			eazyNick.getMySQLPlayerDataManager().removeData(player.getUniqueId());
		}
		
		unnickPlayerWithoutRemovingMySQL(false, true);
	}
	
	public void unnickPlayerWithoutRemovingMySQL(boolean keepNick, boolean spawnPlayer) {
		LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
		
		NickedPlayerData nickedPlayerData = utils.getNickedPlayers().get(player.getUniqueId());
		String nickName = getRealName();
		
		if (!(eazyNick.getVersion().equalsIgnoreCase("1_7_R4")))
			player.setCustomName(nickName);
		
		if(keepNick)
			utils.getLastNickDatas().put(player.getUniqueId(), nickedPlayerData.clone());
		else
			utils.getLastNickDatas().remove(player.getUniqueId());
		
		Bukkit.getScheduler().runTaskLater(eazyNick, () -> utils.getNickedPlayers().remove(player.getUniqueId()), 3);
		
		updatePlayer(spawnPlayer);
		resetCloudNET();
		
		if(utils.ultraPermissionsStatus()) {
			UltraPermissionsAPI api = UltraPermissions.getAPI();
			Optional<me.TechsCode.UltraPermissions.storage.objects.User> userOptional = api.getUsers().uuid(player.getUniqueId());
		
			if(userOptional.isPresent()) {
				me.TechsCode.UltraPermissions.storage.objects.User user = userOptional.get();
				
				if(utils.getOldUltraPermissionsGroups().containsKey(player.getUniqueId())) {
					HashMap<String, Long> data = utils.getOldUltraPermissionsGroups().get(player.getUniqueId());
					
					data.keySet().forEach(group -> user.addGroup(api.getGroups().name(group).get(), data.get(group)));
					
					utils.getOldUltraPermissionsGroups().remove(player.getUniqueId());
				}
			
				if(utils.getUltraPermissionsPrefixes().containsKey(player.getUniqueId())) {
					user.setPrefix(utils.getUltraPermissionsPrefixes().get(player.getUniqueId()));
					
					utils.getUltraPermissionsPrefixes().remove(player.getUniqueId());
				}
				
				if(utils.getUltraPermissionsSuffixes().containsKey(player.getUniqueId())) {
					user.setSuffix(utils.getUltraPermissionsSuffixes().get(player.getUniqueId()));
					
					utils.getUltraPermissionsSuffixes().remove(player.getUniqueId());
				}
			}
		}
		
		if(utils.permissionsExStatus()) {
			PermissionUser user = PermissionsEx.getUser(player);
		
			if(setupYamlFile.getConfiguration().getBoolean("SwitchPermissionsExGroupByNicking")) {
				if(utils.getOldPermissionsExGroups().containsKey(player.getUniqueId())) {
					user.setGroups(utils.getOldPermissionsExGroups().get(player.getUniqueId()));
					
					utils.getOldPermissionsExGroups().remove(player.getUniqueId());
				}
			} else if(utils.getOldPermissionsExPrefixes().containsKey(player.getUniqueId()) && utils.getOldPermissionsExSuffixes().containsKey(player.getUniqueId())) {
				user.setPrefix(utils.getOldPermissionsExPrefixes().get(player.getUniqueId()), player.getWorld().getName());
				user.setSuffix(utils.getOldPermissionsExSuffixes().get(player.getUniqueId()), player.getWorld().getName());
			}
		}
		
		if(setupYamlFile.getConfiguration().getBoolean("Settings.ChangeOptions.NameTag")) {
			if(utils.getScoreboardTeamManagers().containsKey(player.getUniqueId())) {
				utils.getScoreboardTeamManagers().get(player.getUniqueId()).destroyTeam();
				utils.getScoreboardTeamManagers().remove(player.getUniqueId());
			}
		}
		
		Bukkit.getScheduler().runTask(eazyNick, () -> {
			if(utils.nameTagEditStatus()) {
				if(utils.getNametagEditPrefixes().containsKey(player.getUniqueId()) || utils.getNametagEditSuffixes().containsKey(player.getUniqueId())) {
					String prefix = utils.getNametagEditPrefixes().get(player.getUniqueId()), suffix = utils.getNametagEditSuffixes().get(player.getUniqueId());
					INametagApi nametagEditAPI = NametagEdit.getApi();
					
					if((prefix != null) && !(prefix.isEmpty()))
						nametagEditAPI.setPrefix(player, prefix);
					
					if((suffix != null) && !(suffix.isEmpty()))
						nametagEditAPI.setSuffix(player, suffix);
					
					nametagEditAPI.reloadNametag(player);
					
					utils.getNametagEditPrefixes().remove(player.getUniqueId());
					utils.getNametagEditSuffixes().remove(player.getUniqueId());
				}
			}
		});
		
		new AsyncTask(new AsyncRunnable() {
			
			@Override
			public void run() {
				if(player.isOnline()) {
					player.setDisplayName(nickedPlayerData.getOldDisplayName());
					setPlayerListName(nickedPlayerData.getOldPlayerListName());
				}
			}
		}, 1000 + (setupYamlFile.getConfiguration().getBoolean("RandomDisguiseDelay") ? 2000 : 0)).run();
		
		if(setupYamlFile.getConfiguration().getBoolean("NickItem.getOnJoin")  && (player.hasPermission("nick.item"))) {
			for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
				ItemStack item = player.getInventory().getItem(slot);
				
				if((item != null) && (item.getType() != Material.AIR) && (item.getItemMeta() != null) && (item.getItemMeta().getDisplayName() != null)) {
					if(item.getItemMeta().getDisplayName().equalsIgnoreCase(eazyNick.getLanguageYamlFile().getConfigString(player, "NickItem.DisplayName.Enabled")))
						player.getInventory().setItem(slot, new ItemBuilder(Material.getMaterial(setupYamlFile.getConfiguration().getString("NickItem.ItemType.Disabled")), setupYamlFile.getConfiguration().getInt("NickItem.ItemAmount.Disabled"), setupYamlFile.getConfiguration().getInt("NickItem.MetaData.Disabled")).setDisplayName(languageYamlFile.getConfigString(player, "NickItem.DisplayName.Disabled")).setLore(languageYamlFile.getConfigString(player, "NickItem.ItemLore.Disabled").split("&n")).setEnchanted(setupYamlFile.getConfiguration().getBoolean("NickItem.Enchanted.Disabled")).build());
				}
			}
		}
	}
	
	public String getRealName() {
		return player.getName();
	}
	
	public String getChatPrefix() {
		return utils.getNickedPlayers().containsKey(player.getUniqueId()) ? utils.getNickedPlayers().get(player.getUniqueId()).getChatPrefix() : ((utils.vaultStatus() && setupYamlFile.getConfiguration().getBoolean("ServerIsUsingVaultPrefixesAndSuffixes")) ? ((Chat) Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class).getProvider()).getPlayerPrefix(player) : "");
	}

	public void setChatPrefix(String chatPrefix) {
		if(utils.getNickedPlayers().containsKey(player.getUniqueId()))
			utils.getNickedPlayers().get(player.getUniqueId()).setChatPrefix(chatPrefix);
		
		if(eazyNick.getVersion().equals("1_7_R4")) {
			String nameFormatChat = getChatPrefix() + getNickName() + getChatSuffix();
			String nameFormatTab = getTabPrefix() + getNickName() + getTabSuffix();
			
			if(nameFormatTab.length() <= 16) {
				player.setDisplayName(nameFormatChat);
				setPlayerListName(nameFormatTab);
			} else {
				player.setDisplayName(nameFormatChat.substring(0, 16));
				setPlayerListName(getNickName());
			}
		} else {
			player.setDisplayName(getChatPrefix() + getNickName() + getChatSuffix());
			setPlayerListName(getTabPrefix() + getNickName() + getTabSuffix());
		}
	}

	public String getChatSuffix() {
		return utils.getNickedPlayers().containsKey(player.getUniqueId()) ? utils.getNickedPlayers().get(player.getUniqueId()).getChatSuffix() : ((utils.vaultStatus() && setupYamlFile.getConfiguration().getBoolean("ServerIsUsingVaultPrefixesAndSuffixes")) ? ((Chat) Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class).getProvider()).getPlayerSuffix(player) : "");
	}

	public void setChatSuffix(String chatSuffix) {
		if(utils.getNickedPlayers().containsKey(player.getUniqueId()))
			utils.getNickedPlayers().get(player.getUniqueId()).setChatSuffix(chatSuffix);
		
		if(eazyNick.getVersion().equals("1_7_R4")) {
			String nameFormatChat = getChatPrefix() + getNickName() + getChatSuffix();
			String nameFormatTab = getTabPrefix() + getNickName() + getTabSuffix();
			
			if(nameFormatTab.length() <= 16) {
				player.setDisplayName(nameFormatChat);
				setPlayerListName(nameFormatTab);
			} else {
				player.setDisplayName(nameFormatChat.substring(0, 16));
				setPlayerListName(getNickName());
			}
		} else {
			player.setDisplayName(getChatPrefix() + getNickName() + getChatSuffix());
			setPlayerListName(getTabPrefix() + getNickName() + getTabSuffix());
		}
	}

	public String getTabPrefix() {
		return utils.getNickedPlayers().containsKey(player.getUniqueId()) ? utils.getNickedPlayers().get(player.getUniqueId()).getTabPrefix() : ((utils.vaultStatus() && setupYamlFile.getConfiguration().getBoolean("ServerIsUsingVaultPrefixesAndSuffixes")) ? ((Chat) Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class).getProvider()).getPlayerPrefix(player) : "");
	}

	public void setTabPrefix(String tabPrefix) {
		if(utils.getNickedPlayers().containsKey(player.getUniqueId()))
			utils.getNickedPlayers().get(player.getUniqueId()).setTabPrefix(tabPrefix);
		
		if(eazyNick.getVersion().equals("1_7_R4")) {
			String nameFormatChat = getChatPrefix() + getNickName() + getChatSuffix();
			String nameFormatTab = getTabPrefix() + getNickName() + getTabSuffix();
			
			if(nameFormatTab.length() <= 16) {
				player.setDisplayName(nameFormatChat);
				setPlayerListName(nameFormatTab);
			} else {
				player.setDisplayName(nameFormatChat.substring(0, 16));
				setPlayerListName(getNickName());
			}
		} else {
			player.setDisplayName(getChatPrefix() + getNickName() + getChatSuffix());
			setPlayerListName(getTabPrefix() + getNickName() + getTabSuffix());
		}
	}

	public String getTabSuffix() {
		return utils.getNickedPlayers().containsKey(player.getUniqueId()) ? utils.getNickedPlayers().get(player.getUniqueId()).getTabSuffix() : ((utils.vaultStatus() && setupYamlFile.getConfiguration().getBoolean("ServerIsUsingVaultPrefixesAndSuffixes")) ? ((Chat) Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class).getProvider()).getPlayerSuffix(player) : "");
	}

	public void setTabSuffix(String tabSuffix) {
		if(utils.getNickedPlayers().containsKey(player.getUniqueId()))
			utils.getNickedPlayers().get(player.getUniqueId()).setTabSuffix(tabSuffix);
		
		if(eazyNick.getVersion().equals("1_7_R4")) {
			String nameFormatChat = getChatPrefix() + getNickName() + getChatSuffix();
			String nameFormatTab = getTabPrefix() + getNickName() + getTabSuffix();
			
			if(nameFormatTab.length() <= 16) {
				player.setDisplayName(nameFormatChat);
				setPlayerListName(nameFormatTab);
			} else {
				player.setDisplayName(nameFormatChat.substring(0, 16));
				setPlayerListName(getNickName());
			}
		} else {
			player.setDisplayName(getChatPrefix() + getNickName() + getChatSuffix());
			setPlayerListName(getTabPrefix() + getNickName() + getTabSuffix());
		}
	}
	
	public String getTagPrefix() {
		return utils.getNickedPlayers().containsKey(player.getUniqueId()) ? utils.getNickedPlayers().get(player.getUniqueId()).getTagPrefix() : ((utils.vaultStatus() && setupYamlFile.getConfiguration().getBoolean("ServerIsUsingVaultPrefixesAndSuffixes")) ? ((Chat) Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class).getProvider()).getPlayerPrefix(player) : "");
	}

	public void setTagPrefix(String tagPrefix) {
		if(utils.getScoreboardTeamManagers().containsKey(player.getUniqueId()))
			utils.getScoreboardTeamManagers().get(player.getUniqueId()).setPrefix(tagPrefix);
		
		if(utils.getNickedPlayers().containsKey(player.getUniqueId()))
			utils.getNickedPlayers().get(player.getUniqueId()).setTagPrefix(tagPrefix);
	}

	public String getTagSuffix() {
		return utils.getNickedPlayers().containsKey(player.getUniqueId()) ? utils.getNickedPlayers().get(player.getUniqueId()).getTagSuffix() : ((utils.vaultStatus() && setupYamlFile.getConfiguration().getBoolean("ServerIsUsingVaultPrefixesAndSuffixes")) ? ((Chat) Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class).getProvider()).getPlayerSuffix(player) : "");
	}

	public void setTagSuffix(String tagSuffix) {
		if(utils.getScoreboardTeamManagers().containsKey(player.getUniqueId()))
			utils.getScoreboardTeamManagers().get(player.getUniqueId()).setSuffix(tagSuffix);
		
		if(utils.getNickedPlayers().containsKey(player.getUniqueId()))
			utils.getNickedPlayers().get(player.getUniqueId()).setTagSuffix(tagSuffix);
	}

	public boolean isNicked() {
		return utils.getNickedPlayers().containsKey(player.getUniqueId());
	}
	
	public String getRandomStringFromList(ArrayList<String> list) {
		return (list.isEmpty() ? player.getName() : list.get((new Random()).nextInt(list.size())));
	}
	
	public String getRandomName() {
		return utils.getNickNames().get((new Random()).nextInt(utils.getNickNames().size()));
	}
	
	public String getNickName() {
		return utils.getNickedPlayers().containsKey(player.getUniqueId()) ? utils.getNickedPlayers().get(player.getUniqueId()).getNickName() : player.getName();
	}
	
	public String getNickFormat() {
		return getChatPrefix() + getNickName() + getChatSuffix();
	}
	
	public String getOldDisplayName() {
		return utils.getNickedPlayers().containsKey(player.getUniqueId()) ? utils.getNickedPlayers().get(player.getUniqueId()).getOldDisplayName() : player.getName();
	}
	
	public String getOldPlayerListName() {
		return utils.getNickedPlayers().containsKey(player.getUniqueId()) ? utils.getNickedPlayers().get(player.getUniqueId()).getOldPlayerListName() : player.getName();
	}
	
	public String getGroupName() {
		return utils.getNickedPlayers().containsKey(player.getUniqueId()) ? utils.getNickedPlayers().get(player.getUniqueId()).getGroupName() : "NONE";
	}
	
	public void setGroupName(String groupName) {
		if(utils.getNickedPlayers().containsKey(player.getUniqueId()))
			utils.getNickedPlayers().get(player.getUniqueId()).setGroupName(groupName);
	}
	
	public void updatePrefixSuffix(String nickName, String realName, String tagPrefix, String tagSuffix, String chatPrefix, String chatSuffix, String tabPrefix, String tabSuffix, int sortID, String groupName) {
		String finalTabPrefix = tabPrefix, finalTabSuffix = tabSuffix;
		
		if(setupYamlFile.getConfiguration().getBoolean("Settings.ChangeOptions.NameTag")) {
			if(utils.getScoreboardTeamManagers().containsKey(player.getUniqueId()))
				utils.getScoreboardTeamManagers().remove(player.getUniqueId());
				
			utils.getScoreboardTeamManagers().put(player.getUniqueId(), new ScoreboardTeamHandler(player, nickName, realName, tagPrefix, tagSuffix, sortID, groupName));
			
			ScoreboardTeamHandler scoreboardTeamHandler = utils.getScoreboardTeamManagers().get(player.getUniqueId());
			
			new AsyncTask(new AsyncRunnable() {
				
				@Override
				public void run() {
					UUID uuid = player.getUniqueId();
					
					if(eazyNick.isEnabled() && utils.getScoreboardTeamManagers().containsKey(uuid) && player.isOnline()) {
						scoreboardTeamHandler.destroyTeam();
						scoreboardTeamHandler.createTeam();
						
						if(setupYamlFile.getConfiguration().getBoolean("Settings.ChangeOptions.PlayerListName")) {
							String tmpTabPrefix = finalTabPrefix, tmpTabSuffix = finalTabSuffix;
							
							if(utils.placeholderAPIStatus()) {
								tmpTabPrefix = PlaceholderAPI.setPlaceholders(player, tmpTabPrefix);
								tmpTabSuffix = PlaceholderAPI.setPlaceholders(player, tmpTabSuffix);
							}
							
							setPlayerListName(tmpTabPrefix + nickName + tmpTabSuffix);
						}
					} else
						cancel();
				}
			}, 100L, setupYamlFile.getConfiguration().getInt("NameTagPrefixSuffixUpdateDelay") * 50L).run();
		}
		
		if(utils.placeholderAPIStatus()) {
			tagPrefix = PlaceholderAPI.setPlaceholders(player, tagPrefix);
			tagSuffix = PlaceholderAPI.setPlaceholders(player, tagSuffix);
			tabPrefix = PlaceholderAPI.setPlaceholders(player, tabPrefix);
			tabSuffix = PlaceholderAPI.setPlaceholders(player, tabSuffix);
		}
		
		changeCloudNET(tagPrefix, tagSuffix);
		
		if(setupYamlFile.getConfiguration().getBoolean("Settings.ChangeOptions.PlayerListName")) {
			Bukkit.getScheduler().runTaskLater(eazyNick, () -> {
				String tmpTabPrefix = finalTabPrefix;
				String tmpTabSuffix = finalTabSuffix;
				
				if(utils.placeholderAPIStatus()) {
					tmpTabPrefix = PlaceholderAPI.setPlaceholders(player, tmpTabPrefix);
					tmpTabSuffix = PlaceholderAPI.setPlaceholders(player, tmpTabSuffix);
				}
				
				setPlayerListName(tmpTabPrefix + nickName + tmpTabSuffix);
			}, 20);
		}
		
		if(setupYamlFile.getConfiguration().getBoolean("Settings.ChangeOptions.DisplayName"))
			player.setDisplayName(chatPrefix + nickName + chatSuffix);
		
		String finalTagPrefix = tagPrefix, finalTagSuffix = tagSuffix;
		
		if(utils.nameTagEditStatus()) {
			Bukkit.getScheduler().runTask(eazyNick, () -> {
				utils.getNametagEditPrefixes().remove(player.getUniqueId());
				utils.getNametagEditSuffixes().remove(player.getUniqueId());
				
				INametagApi nametagEditAPI = NametagEdit.getApi();
				Nametag nametag = nametagEditAPI.getNametag(player);
				
				utils.getNametagEditPrefixes().put(player.getUniqueId(), nametag.getPrefix());
				utils.getNametagEditSuffixes().put(player.getUniqueId(), nametag.getSuffix());

				nametagEditAPI.setPrefix(player, finalTagPrefix);
				nametagEditAPI.setSuffix(player, finalTagSuffix);
				nametagEditAPI.reloadNametag(player);
			});
		}
		
		if(utils.ultraPermissionsStatus()) {
			UltraPermissionsAPI api = UltraPermissions.getAPI();
			Optional<me.TechsCode.UltraPermissions.storage.objects.User> userOptional = api.getUsers().uuid(player.getUniqueId());
			
			if(userOptional.isPresent()) {
				me.TechsCode.UltraPermissions.storage.objects.User user = userOptional.get();
				Optional<String> prefix = user.getPrefix(), suffix = user.getSuffix();
				
				utils.getUltraPermissionsPrefixes().put(player.getUniqueId(), prefix.isPresent() ? (prefix.get().trim().isEmpty() ? null : prefix.get()) : null);
				utils.getUltraPermissionsSuffixes().put(player.getUniqueId(), suffix.isPresent() ? (suffix.get().trim().isEmpty() ? null : suffix.get()) : null);
				
				user.setPrefix(tabPrefix);
				user.setSuffix(tabSuffix);
				
				if(setupYamlFile.getConfiguration().getBoolean("SwitchUltraPermissionsGroupByNicking") && !(groupName.equalsIgnoreCase("NONE"))) {
					if(!(utils.getOldUltraPermissionsGroups().containsKey(player.getUniqueId())))
						utils.getOldUltraPermissionsGroups().put(player.getUniqueId(), new HashMap<>());
					
					for (me.TechsCode.UltraPermissions.base.storage.Stored<me.TechsCode.UltraPermissions.storage.objects.Group> groupStored : user.getGroups()) {
						if(groupStored.isPresent()) {
							Optional<me.TechsCode.UltraPermissions.storage.objects.Group> groupOptional = groupStored.get();
							
							if(groupOptional.isPresent()) {
								me.TechsCode.UltraPermissions.storage.objects.Group group = groupOptional.get();
								utils.getOldUltraPermissionsGroups().get(player.getUniqueId()).put(group.getName(), user.getGroupExpiry(groupStored));
								
								user.removeGroup(group);
							}
						}
					}
					
					Optional<me.TechsCode.UltraPermissions.storage.objects.Group> group = api.getGroups().name(groupName);
					
					if(group.isPresent())
						user.addGroup(group.get());
				}
			}
		}
		
		if(utils.permissionsExStatus()) {
			PermissionUser user = PermissionsEx.getUser(player);
		
			if(setupYamlFile.getConfiguration().getBoolean("SwitchPermissionsExGroupByNicking") && !(groupName.equalsIgnoreCase("NONE"))) {
				String groupNames = "";

				for (PermissionGroup group : user.getGroups())
					groupNames += (" " + group.getName());
				
				if(!(utils.getOldPermissionsExGroups().containsKey(player.getUniqueId())))
					utils.getOldPermissionsExGroups().put(player.getUniqueId(), groupNames.trim().split(" "));
				
				user.setGroups(new String[] { groupName });
			} else {
				utils.getOldPermissionsExPrefixes().put(player.getUniqueId(), user.getPrefix());
				utils.getOldPermissionsExSuffixes().put(player.getUniqueId(), user.getSuffix());
				
				user.setPrefix(tabPrefix, player.getWorld().getName());
				user.setSuffix(tabSuffix, player.getWorld().getName());
			}
		}
	}
	
	public void changeCloudNET(String prefix, String suffix) {
		if(utils.cloudNetStatus()) {
			CloudPlayer cloudPlayer = CloudAPI.getInstance().getOnlinePlayer(player.getUniqueId());
			
			if(setupYamlFile.getConfiguration().getBoolean("ServerIsUsingCloudNETPrefixesAndSuffixes")) {
				PermissionEntity entity = cloudPlayer.getPermissionEntity();
				de.dytanic.cloudnet.lib.player.permission.PermissionGroup highestPermissionGroup = entity.getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool());
				
				if(utils.getOldCloudNETPrefixes().containsKey(player.getUniqueId()))
					utils.getOldCloudNETPrefixes().remove(player.getUniqueId());
				
				if(utils.getOldCloudNETSuffixes().containsKey(player.getUniqueId()))
					utils.getOldCloudNETSuffixes().remove(player.getUniqueId());
				
				if(utils.getOldCloudNETTagIDS().containsKey(player.getUniqueId()))
					utils.getOldCloudNETTagIDS().remove(player.getUniqueId());
				
				utils.getOldCloudNETPrefixes().put(player.getUniqueId(), entity.getPrefix());
				utils.getOldCloudNETSuffixes().put(player.getUniqueId(), entity.getSuffix());
				utils.getOldCloudNETTagIDS().put(player.getUniqueId(), highestPermissionGroup.getTagId());
				
				entity.setPrefix(prefix);
				entity.setSuffix(suffix);
				highestPermissionGroup.setPrefix(prefix);
				highestPermissionGroup.setSuffix(suffix);
				highestPermissionGroup.setTagId(Integer.MAX_VALUE);
			}
		}
	}
	
	public void resetCloudNET() {
		if(utils.cloudNetStatus()) {
			CloudPlayer cloudPlayer = CloudAPI.getInstance().getOnlinePlayer(player.getUniqueId());
			
			if(setupYamlFile.getConfiguration().getBoolean("ServerIsUsingCloudNETPrefixesAndSuffixes")) {
				PermissionEntity entity = cloudPlayer.getPermissionEntity();
				de.dytanic.cloudnet.lib.player.permission.PermissionGroup highestPermissionGroup = entity.getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool());
				
				if(utils.getOldCloudNETPrefixes().containsKey(player.getUniqueId())) {
					entity.setPrefix(utils.getOldCloudNETPrefixes().get(player.getUniqueId()));
					highestPermissionGroup.setPrefix(utils.getOldCloudNETPrefixes().get(player.getUniqueId()));
					utils.getOldCloudNETPrefixes().remove(player.getUniqueId());
				}
				
				if(utils.getOldCloudNETSuffixes().containsKey(player.getUniqueId())) {
					entity.setSuffix(utils.getOldCloudNETSuffixes().get(player.getUniqueId()));
					highestPermissionGroup.setSuffix(utils.getOldCloudNETSuffixes().get(player.getUniqueId()));
					utils.getOldCloudNETSuffixes().remove(player.getUniqueId());
				}
				
				if(utils.getOldCloudNETTagIDS().containsKey(player.getUniqueId())) {
					highestPermissionGroup.setTagId(utils.getOldCloudNETTagIDS().get(player.getUniqueId()));
					utils.getOldCloudNETTagIDS().remove(player.getUniqueId());
				}
			}
		}
	}

}
