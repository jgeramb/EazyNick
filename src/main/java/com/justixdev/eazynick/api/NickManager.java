package com.justixdev.eazynick.api;

import com.google.common.hash.Hashing;
import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.hooks.TABHook;
import com.justixdev.eazynick.nms.ReflectionHelper;
import com.justixdev.eazynick.nms.ScoreboardTeamHandler;
import com.justixdev.eazynick.sql.MySQLNickManager;
import com.justixdev.eazynick.utilities.*;
import com.justixdev.eazynick.utilities.AsyncTask.AsyncRunnable;
import com.justixdev.eazynick.utilities.configuration.yaml.LanguageYamlFile;
import com.justixdev.eazynick.utilities.configuration.yaml.SetupYamlFile;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.nametagedit.plugin.NametagEdit;
import com.nametagedit.plugin.api.INametagApi;
import com.nametagedit.plugin.api.data.Nametag;
import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.permission.PermissionEntity;
import me.clip.placeholderapi.PlaceholderAPI;
import net.milkbowl.vault.chat.Chat;
import net.skinsrestorer.api.PlayerWrapper;
import net.skinsrestorer.api.SkinsRestorerAPI;
import net.skinsrestorer.api.property.GenericProperty;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

@SuppressWarnings({"JavaReflectionInvocation", "unused"})
public class NickManager extends ReflectionHelper {

	private final EazyNick eazyNick;
	private final SetupYamlFile setupYamlFile;
	private final Utils utils;
	private final Player player;
	
	public NickManager(Player player) {
		this.eazyNick = EazyNick.getInstance();
		this.setupYamlFile = eazyNick.getSetupYamlFile();
		this.utils = eazyNick.getUtils();
		this.player = player;
	}
	
	private void sendPacket(Player nickedPlayer, Player player, Object packet) {
		if(!(player.canSee(nickedPlayer) || player.getWorld().getName().equals(nickedPlayer.getWorld().getName()))) return;

		if(player.getEntityId() != nickedPlayer.getEntityId()) {
			// Send packet to player who is not the player being nicked
			if(!(player.hasPermission("eazynick.bypass") && setupYamlFile.getConfiguration().getBoolean("EnableBypassPermission")))
				sendPacketNMS(player, packet);
		} else if(setupYamlFile.getConfiguration().getBoolean("SeeNickSelf"))
			// Send packet to player being nicked
			sendPacketNMS(player, packet);
	}
	
	private void sendPacketExceptSelf(Player player, Object packet, Collection<? extends Player> players) {
		// Send packet to player who is not the player being nicked
		players.stream()
			.filter(Player::isOnline)
			.filter(currentPlayer -> currentPlayer.getWorld().getName().equals(player.getWorld().getName()))
			.filter(currentPlayer -> (eazyNick.getVersion().startsWith("1_7") || !(player.getGameMode().equals(GameMode.SPECTATOR))))
			.filter(currentPlayer -> (currentPlayer.getEntityId() != player.getEntityId()))
			.filter(currentPlayer -> !(currentPlayer.hasPermission("eazynick.bypass") && setupYamlFile.getConfiguration().getBoolean("EnableBypassPermission")))
			.forEach(currentPlayer -> sendPacketNMS(currentPlayer, packet));
	}
	
	private void sendPacketNMS(Player player, Object packet) {
		try {
			// Send packet to player connection
			boolean is1_17 = eazyNick.getVersion().startsWith("1_17"), is1_18 = eazyNick.getVersion().startsWith("1_18"), is1_19 = eazyNick.getVersion().startsWith("1_19");
			Object handle = player.getClass().getMethod("getHandle").invoke(player);
			Object playerConnection = handle.getClass().getDeclaredField((is1_17 || is1_18 || is1_19) ? "b" : "playerConnection").get(handle);
			playerConnection.getClass().getMethod(
					(is1_18 || is1_19)
							? "a"
							: "sendPacket",
					getNMSClass((is1_17 || is1_18 || is1_19)
							? "network.protocol.Packet"
							: "Packet")
			).invoke(playerConnection, packet);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void setPlayerListName(String name) {
		String serverVersion = eazyNick.getVersion();
		
		if(serverVersion.equals("1_7_R4")) {
			try {
				Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);

				// Make sure name is not longer than 16 characters
				name = name.substring(0, Math.min(name.length(), 16));
				
				// Set listName field of EntityPlayer -> getPlayerListName()
				Field listNameField = getNMSClass("EntityPlayer").getDeclaredField("listName");
				listNameField.setAccessible(true);
				listNameField.set(entityPlayer, name);

				Class<?> playOutPlayerInfo = getNMSClass("PacketPlayOutPlayerInfo");
				Object packetPlayOutPlayerInfoUpdate = playOutPlayerInfo
						.getMethod(
								"updateDisplayName",
								getNMSClass("EntityPlayer")
						).invoke(playOutPlayerInfo, entityPlayer);
				Object packetPlayOutPlayerInfoRemove = playOutPlayerInfo
						.getMethod(
								"removePlayer",
								getNMSClass("EntityPlayer")
						).invoke(playOutPlayerInfo, entityPlayer);
				Object packetPlayOutPlayerInfoAdd = playOutPlayerInfo
						.getMethod(
								"addPlayer",
								getNMSClass("EntityPlayer")
						).invoke(playOutPlayerInfo, entityPlayer);
				
				for(Player currentPlayer : Bukkit.getOnlinePlayers()) {
					if(!(currentPlayer.canSee(player))) return;

					if(!(currentPlayer.getUniqueId().equals(player.getUniqueId()))) {
						if(currentPlayer.hasPermission("eazynick.bypass") && setupYamlFile.getConfiguration().getBoolean("EnableBypassPermission")) return;

						Object playerConenction = getNMSClass("EntityPlayer")
								.getDeclaredField("playerConnection")
								.get(currentPlayer.getClass().getMethod("getHandle").invoke(currentPlayer));
						Object networkManager = playerConenction
								.getClass()
								.getDeclaredField("networkManager")
								.get(playerConenction);
						int version = (int) networkManager
								.getClass()
								.getMethod("getVersion")
								.invoke(networkManager);

						if (version < 28) {
							// Send packets to remove player from tablist and add it again
							sendPacketNMS(currentPlayer, packetPlayOutPlayerInfoRemove);
							sendPacketNMS(currentPlayer, packetPlayOutPlayerInfoAdd);
						} else
							// Send packet to update tablist name
							sendPacketNMS(currentPlayer, packetPlayOutPlayerInfoUpdate);
					} else if(setupYamlFile.getConfiguration().getBoolean("SeeNickSelf")) {
						Object playerConenction = entityPlayer
								.getClass()
								.getDeclaredField("playerConnection")
								.get(entityPlayer);
						Object networkManager = playerConenction
								.getClass()
								.getDeclaredField("networkManager")
								.get(playerConenction);
						int version = (int) networkManager
								.getClass()
								.getMethod("getVersion")
								.invoke(networkManager);

						if (version < 28) {
							//Send packets to remove player from tablist and add it again
							sendPacketNMS(currentPlayer, packetPlayOutPlayerInfoRemove);
							sendPacketNMS(currentPlayer, packetPlayOutPlayerInfoAdd);
						} else
							//Send packet to update tablist name
							sendPacketNMS(currentPlayer, packetPlayOutPlayerInfoUpdate);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			final String finalName = name;
			boolean is1_17To1_19 = serverVersion.startsWith("1_17") || serverVersion.startsWith("1_18") || serverVersion.startsWith("1_19");

			(serverVersion.equals("1_8_R1")
					? Optional.of(getNMSClass("EnumPlayerInfoAction"))
					: getSubClass(getNMSClass(
							is1_17To1_19
									? "network.protocol.game.PacketPlayOutPlayerInfo"
									: "PacketPlayOutPlayerInfo"
							),
							"EnumPlayerInfoAction"
					)
			).ifPresent(enumPlayerInfoActionClass -> {
				try {
					Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
					Object entityPlayerArray = Array.newInstance(entityPlayer.getClass(), 1);

					// Push player to entity player array
					Array.set(entityPlayerArray, 0, entityPlayer);

					// Run task synchronously
					Bukkit.getScheduler().runTask(eazyNick, () -> {
						try {
							// Set listName field of EntityPlayer -> getPlayerListName()
							Class<?> craftChatMessage = getCraftClass("util.CraftChatMessage");
							Field f = getNMSClass(
									is1_17To1_19
											? "server.level.EntityPlayer"
											: "EntityPlayer"
							).getDeclaredField("listName");
							f.setAccessible(true);
							f.set(
									entityPlayer,
									utils.isVersion13OrLater()
											? craftChatMessage
													.getMethod("fromStringOrNull", String.class)
													.invoke(craftChatMessage, finalName)
											: ((Object[]) craftChatMessage
													.getMethod("fromString", String.class)
													.invoke(craftChatMessage, finalName))[0]
							);

							for(Player currentPlayer : Bukkit.getOnlinePlayers())
								// Send packet to update tablist name
								//noinspection JavaReflectionInvocation
								sendPacket(
										player,
										currentPlayer,
										getNMSClass(
												is1_17To1_19
														? "network.protocol.game.PacketPlayOutPlayerInfo"
														: "PacketPlayOutPlayerInfo"
										).getConstructor(
												enumPlayerInfoActionClass,
												entityPlayerArray.getClass()
										).newInstance(
												enumPlayerInfoActionClass.getDeclaredField(
														is1_17To1_19
																? "d"
																: "UPDATE_DISPLAY_NAME"
												).get(enumPlayerInfoActionClass),
												entityPlayerArray)
								);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					});
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			});
		}
	}

	public void updatePlayer() {
		try {
			final boolean skinsRestorer = utils.isPluginInstalled("SkinsRestorer") && setupYamlFile.getConfiguration().getBoolean("ChangeSkinsRestorerSkin");
			final Collection<? extends Player> players = Bukkit.getOnlinePlayers();
			String version = eazyNick.getVersion();
			boolean is1_17 = version.startsWith("1_17"), is1_18 = version.startsWith("1_18"), is1_19 = version.startsWith("1_19");
			Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
			Object entityPlayerArray = Array.newInstance(entityPlayer.getClass(), 1);
			Array.set(entityPlayerArray, 0, entityPlayer);

			Object worldClient = entityPlayer.getClass().getMethod(Bukkit.getVersion().contains("1.19.2") ? "cC" : (is1_19 ? "cD" : (is1_18 ? "cA" : "getWorld"))).invoke(entityPlayer),
					worldData = worldClient.getClass().getMethod(
							(is1_18 || is1_19)
									? "n_"
									: "getWorldData"
					).invoke(worldClient),
					interactManager = entityPlayer.getClass()
							.getField((is1_17 || is1_18 || is1_19)
									? "d"
									: "playerInteractManager"
							).get(entityPlayer);

			// Despawn and remove from tablist
			players.stream().filter(Player::isOnline).forEach(currentPlayer -> {
				try {
					if(!(utils.getSoonNickedPlayers().contains(player.getUniqueId()))) {
						// Destroy entity
						if(
								!(
										(utils.isPluginInstalled("ViaRewind") || utils.isPluginInstalled("ViaBackwards") || utils.isPluginInstalled("ProtocolSupport"))
										&& currentPlayer.equals(player)
								) && !(skinsRestorer)
						)
							//noinspection SuspiciousTernaryOperatorInVarargsCall
							sendPacket(player, currentPlayer, getNMSClass(
									(is1_17 || is1_18 || is1_19)
											? "network.protocol.game.PacketPlayOutEntityDestroy"
											: "PacketPlayOutEntityDestroy"
							).getConstructor(
									(is1_17 && !(Bukkit.getVersion().contains("1.17.1")))
											? int.class
											: int[].class
							).newInstance(
									(is1_17 && !(Bukkit.getVersion().contains("1.17.1")))
											? player.getEntityId()
											: new int[] { player.getEntityId() }
							));
					}

					if(!(utils.getSoonNickedPlayers().contains(player.getUniqueId()))) {
						// Remove player from tablist
						Object packetPlayOutPlayerInfoRemove = null;

						if(version.equals("1_7_R4")) {
							Class<?> playOutPlayerInfo = getNMSClass("PacketPlayOutPlayerInfo");

							packetPlayOutPlayerInfoRemove = playOutPlayerInfo
									.getMethod("removePlayer", getNMSClass("EntityPlayer"))
									.invoke(playOutPlayerInfo, entityPlayer);
						} else {
							Optional<Class<?>> enumPlayerInfoActionClass = (version.equals("1_8_R1")
									? Optional.of(getNMSClass("EnumPlayerInfoAction"))
									: getSubClass(
											getNMSClass(
													(is1_17 || is1_18 || is1_19)
															? "network.protocol.game.PacketPlayOutPlayerInfo"
															: "PacketPlayOutPlayerInfo"
											),
											"EnumPlayerInfoAction"
									)
							);

							if(enumPlayerInfoActionClass.isPresent())
								//noinspection JavaReflectionInvocation
								packetPlayOutPlayerInfoRemove = getNMSClass(
										(is1_17 || is1_18 || is1_19)
												? "network.protocol.game.PacketPlayOutPlayerInfo"
												: "PacketPlayOutPlayerInfo"
								).getConstructor(
										enumPlayerInfoActionClass.get(),
										entityPlayerArray.getClass()
								).newInstance(
										enumPlayerInfoActionClass.get().getDeclaredField(
												(is1_17 || is1_18 || is1_19)
														? "e"
														: "REMOVE_PLAYER"
										).get(enumPlayerInfoActionClass),
										entityPlayerArray
								);
						}

						if(packetPlayOutPlayerInfoRemove != null)
							sendPacket(player, currentPlayer, packetPlayOutPlayerInfoRemove);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			});

			new AsyncTask(new AsyncRunnable() {

				@SuppressWarnings("JavaReflectionInvocation")
				@Override
				public void run() {
					if(!(eazyNick.isEnabled()) || !(player.isOnline()))
						return;

					try {
						utils.getSoonNickedPlayers().remove(player.getUniqueId());

						// Add to tablist and spawn
						if(!(utils.getSoonNickedPlayers().contains(player.getUniqueId()))) {
							players.stream().filter(Player::isOnline).forEach(currentPlayer -> {
								try {
									// Add player to tablist
									Object packetPlayOutPlayerInfoAdd = null;

									if(version.equals("1_7_R4")) {
										Class<?> playOutPlayerInfo = getNMSClass("PacketPlayOutPlayerInfo");

										packetPlayOutPlayerInfoAdd = playOutPlayerInfo
												.getMethod("addPlayer", getNMSClass("EntityPlayer"))
												.invoke(playOutPlayerInfo, entityPlayer);
									} else {
										Optional<Class<?>> enumPlayerInfoActionClass =
												(version.equals("1_8_R1")
														? Optional.of(getNMSClass("EnumPlayerInfoAction"))
														: getSubClass(
																getNMSClass(
																		(is1_17 || is1_18 || is1_19)
																				? "network.protocol.game.PacketPlayOutPlayerInfo"
																				: "PacketPlayOutPlayerInfo"
																),
																"EnumPlayerInfoAction"
														)
												);

										if(enumPlayerInfoActionClass.isPresent())
											packetPlayOutPlayerInfoAdd = getNMSClass(
													(is1_17 || is1_18 || is1_19)
															? "network.protocol.game.PacketPlayOutPlayerInfo"
															: "PacketPlayOutPlayerInfo"
											).getConstructor(
													enumPlayerInfoActionClass.get(),
													entityPlayerArray.getClass()
											).newInstance(
													enumPlayerInfoActionClass.get().getDeclaredField(
															(is1_17 || is1_18 || is1_19)
																	? "a"
																	: "ADD_PLAYER"
													).get(enumPlayerInfoActionClass),
													entityPlayerArray
											);
									}

									if(packetPlayOutPlayerInfoAdd != null)
										sendPacket(player, currentPlayer, packetPlayOutPlayerInfoAdd);
								} catch(Exception ex) {
									ex.printStackTrace();
								}
							});
						}

						// Spawn player
						sendPacketExceptSelf(
								player,
								getNMSClass(
										(is1_17 || is1_18 || is1_19)
												? "network.protocol.game.PacketPlayOutNamedEntitySpawn"
												: "PacketPlayOutNamedEntitySpawn"
								).getConstructor(getNMSClass(
										(is1_17 || is1_18 || is1_19)
												? "world.entity.player.EntityHuman"
												: "EntityHuman"
								)).newInstance(entityPlayer), players);

						// Fix head and body rotation (Yaw + Pitch)
						Object packetHeadRotation;

						if(is1_17 || is1_18 || is1_19)
							packetHeadRotation = getNMSClass("network.protocol.game.PacketPlayOutEntityHeadRotation")
									.getConstructor(
											getNMSClass("world.entity.Entity"),
											byte.class
									).newInstance(
											entityPlayer,
											(byte) ((int) (player.getLocation().getYaw() * 256.0F / 360.0F))
									);
						else {
							packetHeadRotation = getNMSClass("PacketPlayOutEntityHeadRotation").getDeclaredConstructor().newInstance();
							setField(packetHeadRotation, "a", player.getEntityId());
							setField(packetHeadRotation, "b", (byte) ((int) (player.getLocation().getYaw() * 256.0F / 360.0F)));
						}

						Class<?> packetPlayOutEntityLook = (version.equals("1_7_R4") || version.equals("1_8_R1")) ? getNMSClass("PacketPlayOutEntityLook") : null;

						if(packetPlayOutEntityLook == null) {
							for (Class<?> clazz : getNMSClass(
									(is1_17 || is1_18 || is1_19)
											? "network.protocol.game.PacketPlayOutEntity"
											: "PacketPlayOutEntity"
							).getDeclaredClasses()) {
								if(clazz.getSimpleName().equals("PacketPlayOutEntityLook"))
									packetPlayOutEntityLook = clazz;
							}
						}

						if(packetPlayOutEntityLook != null) {
							sendPacketExceptSelf(
									player,
									packetPlayOutEntityLook.getConstructor(
											int.class,
											byte.class,
											byte.class,
											boolean.class
									).newInstance(
											player.getEntityId(),
											(byte) ((int) (player.getLocation().getYaw() * 256.0F / 360.0F)),
											(byte) ((int) (player.getLocation().getPitch() * 256.0F / 360.0F)),
											true
									), players);
						}

						sendPacketExceptSelf(
								player,
								packetHeadRotation,
								players
						);

						if(
								!(utils.isPluginInstalled("ViaRewind")
										|| utils.isPluginInstalled("ViaBackwards")
										|| utils.isPluginInstalled("ProtocolSupport"))
								&& setupYamlFile.getConfiguration().getBoolean("SeeNickSelf")
								&& !(skinsRestorer)
						) {
							// Self skin update
							Object packetRespawnPlayer;

							if(is1_19) {
								Object worldServer = player.getWorld().getClass().getMethod("getHandle").invoke(player.getWorld());
								Class<?> enumGameMode = getNMSClass("world.level.EnumGamemode");

								packetRespawnPlayer = getNMSClass("network.protocol.game.PacketPlayOutRespawn")
										.getConstructor(
												getNMSClass("resources.ResourceKey"),
												getNMSClass("resources.ResourceKey"),
												long.class,
												enumGameMode,
												enumGameMode,
												boolean.class,
												boolean.class,
												boolean.class,
												Optional.class
										).newInstance(
												worldServer.getClass().getMethod("Z").invoke(worldServer),
												worldServer.getClass().getMethod("ab").invoke(worldServer),
												getNMSClass("world.level.biome.BiomeManager")
														.getMethod("a", long.class)
														.invoke(null, player.getWorld().getSeed()),
												interactManager.getClass().getMethod("b").invoke(interactManager),
												interactManager.getClass().getMethod("c").invoke(interactManager),
												worldServer.getClass().getMethod("ae").invoke(worldServer),
												worldServer.getClass().getMethod("A").invoke(worldServer),
												true,
												entityPlayer.getClass().getMethod("ga").invoke(entityPlayer)
										);
							} else if(version.equals("1_18_R2")) {
								Object worldServer = player.getWorld().getClass().getMethod("getHandle").invoke(player.getWorld());
								Class<?> enumGameMode = getNMSClass("world.level.EnumGamemode");

								packetRespawnPlayer = getNMSClass("network.protocol.game.PacketPlayOutRespawn")
										.getConstructor(
												getNMSClass("core.Holder"),
												getNMSClass("resources.ResourceKey"),
												long.class,
												enumGameMode,
												enumGameMode,
												boolean.class,
												boolean.class,
												boolean.class
										).newInstance(
												worldServer.getClass().getMethod("Z").invoke(worldServer),
												worldServer.getClass().getMethod("aa").invoke(worldServer),
												getNMSClass("world.level.biome.BiomeManager")
														.getMethod("a", long.class)
														.invoke(null, player.getWorld().getSeed()),
												interactManager.getClass().getMethod("b").invoke(interactManager),
												interactManager.getClass().getMethod("c").invoke(interactManager),
												worldServer.getClass().getMethod("ad").invoke(worldServer),
												worldServer.getClass().getMethod("C").invoke(worldServer),
												true
										);
							} else if(version.startsWith("1_16") || is1_17 || is1_18) {
								Object worldServer = player
										.getWorld()
										.getClass()
										.getMethod("getHandle")
										.invoke(player.getWorld());
								Class<?> enumGameMode = getNMSClass(
										(is1_17 || is1_18)
												? "world.level.EnumGamemode"
												: "EnumGamemode"
								);

								packetRespawnPlayer = version.equals("1_16_R1")
										? getNMSClass("PacketPlayOutRespawn").getConstructor(
												getNMSClass("ResourceKey"),
												getNMSClass("ResourceKey"),
												long.class,
												enumGameMode,
												enumGameMode,
												boolean.class,
												boolean.class,
												boolean.class
										).newInstance(
												worldServer.getClass()
														.getMethod("getTypeKey")
														.invoke(worldServer),
												worldServer.getClass()
														.getMethod("getDimensionKey")
														.invoke(worldServer),
												getNMSClass("BiomeManager")
														.getMethod("a", long.class)
														.invoke(null, player.getWorld().getSeed()),
												interactManager.getClass()
														.getMethod("getGameMode")
														.invoke(interactManager),
												interactManager.getClass()
														.getMethod("c")
														.invoke(interactManager),
												worldServer.getClass()
														.getMethod("isDebugWorld")
														.invoke(worldServer),
												worldServer.getClass()
														.getMethod("isFlatWorld").
														invoke(worldServer),
												true
										)
										: getNMSClass(
												(is1_17 || is1_18)
														? "network.protocol.game.PacketPlayOutRespawn"
														: "PacketPlayOutRespawn"
										).getConstructor(
												getNMSClass(
														(is1_17 || is1_18)
																? "world.level.dimension.DimensionManager"
																: "DimensionManager"
												),
												getNMSClass(
														(is1_17 || is1_18)
																? "resources.ResourceKey"
																: "ResourceKey"
												),
												long.class,
												enumGameMode,
												enumGameMode,
												boolean.class,
												boolean.class,
												boolean.class
										).newInstance(
												worldServer.getClass().getMethod(
														is1_18
																? "q_"
																: "getDimensionManager"
												).invoke(worldServer),
												worldServer.getClass().getMethod(
														is1_18
																? "aa"
																: "getDimensionKey"
												).invoke(worldServer),
												getNMSClass(
														(is1_17 || is1_18)
																? "world.level.biome.BiomeManager"
																: "BiomeManager"
												).getMethod("a", long.class).invoke(
														null,
														player.getWorld().getSeed()
												),
												interactManager.getClass().getMethod(
														is1_18
																? "b"
																: "getGameMode"
												).invoke(interactManager),
												interactManager.getClass().getMethod("c").invoke(interactManager),
												worldServer.getClass().getMethod(
														is1_18
																? "ad"
																: "isDebugWorld"
												).invoke(worldServer),
												worldServer.getClass().getMethod(
														is1_18
																? "D"
																: "isFlatWorld"
												).invoke(worldServer),
										true
								);
							} else {
								Object environment = player.getWorld().getEnvironment();
								int environmentId = (int) environment
										.getClass()
										.getMethod("getId")
										.invoke(environment);

								if(version.startsWith("1_15")) {
									Class<?> dimensionManager = getNMSClass("DimensionManager");
									Class<?> worldType = getNMSClass("WorldType");
									Class<?> enumGameMode = getNMSClass("EnumGamemode");
									Object worldTypeObj = World.class
											.getMethod("getWorldType")
											.invoke(player.getWorld());

									//noinspection UnstableApiUsage
									packetRespawnPlayer = getNMSClass("PacketPlayOutRespawn")
											.getConstructor(
													dimensionManager,
													long.class,
													worldType,
													enumGameMode
											).newInstance(
													dimensionManager.getMethod("a", int.class)
															.invoke(dimensionManager, environmentId),
													Hashing.sha256().hashLong(player.getWorld().getSeed()).asLong(),
													worldType.getMethod("getType", String.class).invoke(
															worldType,
															worldTypeObj.getClass()
																	.getMethod("getName")
																	.invoke(worldTypeObj)
													),
													enumGameMode.getMethod("getById", int.class).invoke(
															enumGameMode,
															player.getGameMode().getClass()
																	.getMethod("getValue")
																	.invoke(player.getGameMode())
													)
											);
								} else if(version.startsWith("1_14")) {
									Class<?> dimensionManager = getNMSClass("DimensionManager");
									Class<?> worldType = getNMSClass("WorldType");
									Class<?> enumGameMode = getNMSClass("EnumGamemode");
									Object worldTypeObj = World.class.getMethod("getWorldType").invoke(player.getWorld());

									packetRespawnPlayer = getNMSClass("PacketPlayOutRespawn")
											.getConstructor(dimensionManager, worldType, enumGameMode)
											.newInstance(
													dimensionManager.getMethod("a", int.class)
															.invoke(dimensionManager, environmentId),
													worldType.getMethod("getType", String.class).invoke(
															worldType,
															worldTypeObj.getClass().getMethod("getName").invoke(worldTypeObj)
													),
													enumGameMode.getMethod("getById", int.class).invoke(
															enumGameMode,
															player.getGameMode().getClass()
																	.getMethod("getValue")
																	.invoke(player.getGameMode())
													)
											);
								} else if(version.equals("1_13_R2")) {
									Object craftWorld = player.getWorld().getClass().getMethod("getHandle").invoke(player.getWorld());

									packetRespawnPlayer = getNMSClass("PacketPlayOutRespawn")
											.getConstructor(
													getNMSClass("DimensionManager"),
													getNMSClass("EnumDifficulty"),
													getNMSClass("WorldType"),
													getNMSClass("EnumGamemode")
											).newInstance(
													worldClient.getClass().getDeclaredField("dimension").get(craftWorld),
													worldClient.getClass().getMethod("getDifficulty").invoke(worldClient),
													worldData.getClass().getMethod("getType").invoke(worldData),
													interactManager.getClass().getMethod("getGameMode").invoke(interactManager)
											);
								} else {
									Class<?> enumGameMode = (version.equals("1_8_R2")
											|| version.equals("1_8_R3")
											|| version.equals("1_9_R1")
											|| version.equals("1_9_R2"))
													? getNMSClass("WorldSettings").getDeclaredClasses()[0]
													: getNMSClass("EnumGamemode");

									packetRespawnPlayer = getNMSClass("PacketPlayOutRespawn")
											.getConstructor(
													int.class,
													getNMSClass("EnumDifficulty"),
													getNMSClass("WorldType"),
													enumGameMode
											).newInstance(
													environmentId,
													(version.equals("1_7_R4")
															? getNMSClass("World").getDeclaredField("difficulty").get(worldClient)
															: worldClient.getClass().getMethod("getDifficulty").invoke(worldClient)
													),
													worldData.getClass().getMethod("getType").invoke(worldData),
													interactManager.getClass().getMethod("getGameMode").invoke(interactManager)
											);
								}
							}

							sendPacketNMS(player, packetRespawnPlayer);

							player.updateInventory();

							// Reload chunks
							if(version.startsWith("1_8") || version.startsWith("1_7")) {
								Bukkit.getScheduler().runTask(eazyNick, () -> {
									Chunk currentChunk = player.getLocation().getChunk();
									World world = player.getWorld();
									int viewDistance = Bukkit.getViewDistance(),
											currentChunkX = currentChunk.getX(),
											currentChunkZ = currentChunk.getZ();

									try {
										for(int x = currentChunkX - viewDistance;
											x <= (currentChunkZ + viewDistance);
											x++) {
											for(int z = currentChunkZ - viewDistance;
												z <= (currentChunkZ + viewDistance);
												z++)
												world.getClass()
														.getMethod("refreshChunk", int.class, int.class)
														.invoke(world, x, z);
										}
									} catch (Exception ignore) {
									}
								});
							}

							// Fix position
							Object playerConnection = entityPlayer.getClass().getDeclaredField(
									(is1_17 || is1_18 || is1_19)
											? "b"
											: "playerConnection"
							).get(entityPlayer);

							boolean teleportFar = is1_17 || version.equals("1_18_R1") || is1_19;

							Bukkit.getScheduler().runTask(eazyNick, () -> {
								try {
									playerConnection.getClass()
											.getMethod("teleport", Location.class)
											.invoke(
													playerConnection,
													new Location(
															player.getWorld(),
															player.getLocation().getX()
																	+ (teleportFar ? 100 : 0),
															player.getLocation().getY()
																	+ (teleportFar ? 100.25 : 0.25),
															player.getLocation().getZ()
																	+ (teleportFar ? 100 : 0),
															player.getLocation().getYaw(),
															player.getLocation().getPitch()
													)
											);
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							});

							if(teleportFar) {
								new AsyncTask(new AsyncRunnable() {

									@Override
									public void run() {
										Bukkit.getScheduler().runTask(eazyNick, () -> {
											try {
												playerConnection.getClass()
														.getMethod("teleport", Location.class)
														.invoke(
																playerConnection,
																new Location(
																		player.getWorld(),
																		player.getLocation().getX() - 100,
																		player.getLocation().getY() - 100,
																		player.getLocation().getZ() - 100,
																		player.getLocation().getYaw(),
																		player.getLocation().getPitch()
																)
														);
											} catch (Exception ex) {
												ex.printStackTrace();
											}
										});
									}
								}, 50).run();
							}

							if(setupYamlFile.getConfiguration().getBoolean("UpdatePlayerStats")) {
								// Fix armor, inventory, health, food level & experience level
								double oldHealth = player.getHealth(),
										oldHealthScale =
												player.isHealthScaled()
														? player.getHealthScale()
														: 0;
								int oldLevel = player.getLevel();
								ItemStack oldHelmet = player.getInventory().getHelmet(),
										oldChestplate = player.getInventory().getChestplate(),
										oldLeggings = player.getInventory().getLeggings(),
										oldBoots = player.getInventory().getBoots();

								if(oldHelmet != null)
									player.getInventory().setHelmet(
											new ItemBuilder(Material.LEATHER_HELMET)
													.setDurability(1)
													.setDisplayName("§r")
													.build()
									);

								if(oldChestplate != null)
									player.getInventory().setChestplate(
											new ItemBuilder(Material.LEATHER_CHESTPLATE)
													.setDurability(1)
													.setDisplayName("§r")
													.build()
									);

								if(oldLeggings != null)
									player.getInventory().setLeggings(
											new ItemBuilder(Material.LEATHER_LEGGINGS)
													.setDurability(1)
													.setDisplayName("§r")
													.build()
									);

								if(oldBoots != null)
									player.getInventory().setBoots(
											new ItemBuilder(Material.LEATHER_BOOTS)
													.setDurability(1)
													.setDisplayName("§r")
													.build()
									);

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
								}, 150).run();
							}
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}, 50L * (5 + (
					setupYamlFile.getConfiguration().getBoolean("RandomDisguiseDelay")
							? (20 * new Random().nextInt(3))
							: 0
			))).run();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void changeSkin(String skinName) {
		if(skinName == null) return;

		MySQLNickManager mysqlNickManager = eazyNick.getMySQLNickManager();
		UUID uniqueId = player.getUniqueId();

		if(utils.getNickedPlayers().containsKey(uniqueId))
			utils.getNickedPlayers().get(uniqueId).setSkinName(skinName);
		else
			utils.getNickedPlayers().put(
					uniqueId,
					new NickedPlayerData(
							uniqueId,
							uniqueId,
							player.getDisplayName(),
							player.getPlayerListName(),
							player.getName(),
							player.getName(),
							skinName,
							"",
							"",
							"",
							"",
							"",
							"",
							"default",
							9999
					)
			);

		if(utils.isPluginInstalled("SkinsRestorer") && setupYamlFile.getConfiguration().getBoolean("ChangeSkinsRestorerSkin")) {
			// Update skins restorer data
			try {
				Plugin skinsRestorer = Bukkit.getPluginManager().getPlugin("SkinsRestorer");

				if (getField(Objects.requireNonNull(skinsRestorer).getClass(), "proxyMode").getBoolean(skinsRestorer)) {
					if(!(skinName.startsWith("MINESKIN:"))) {
						try {
							ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
							DataOutputStream out = new DataOutputStream(byteArrayOutputStream);
							out.writeUTF("setSkin");
							out.writeUTF(player.getName());
							out.writeUTF(skinName);

							player.sendPluginMessage(eazyNick, "sr:messagechannel", byteArrayOutputStream.toByteArray());
						} catch (IOException ignore) {
						}
					}
				} else {
					SkinsRestorerAPI skinsRestorerAPI = SkinsRestorerAPI.getApi();
					Object skinProfile = utils.getNickedPlayers().get(player.getUniqueId()).getSkinProfile();
					String skinValue = utils.getDefaultSkinValue(), skinSignature = utils.getDefaultSkinSignature();

					if (skinName.startsWith("MINESKIN:")) {
						//Load skin from mineskin.org
						MineSkinAPI mineSkinAPI = eazyNick.getMineSkinAPI();

						if (eazyNick.getVersion().startsWith("1_7")) {
							Optional<?> texturesPropertyOptional = mineSkinAPI.getTextureProperties_1_7(skinName.equals("MINESKIN:RANDOM") ? utils.getRandomStringFromList(utils.getMineSkinUUIDs()) : skinName.split(":")[1]).stream().findAny();

							if (texturesPropertyOptional.isPresent()) {
								net.minecraft.util.com.mojang.authlib.properties.Property texturesProperty = (net.minecraft.util.com.mojang.authlib.properties.Property) texturesPropertyOptional.get();
								skinValue = texturesProperty.getValue();
								skinSignature = texturesProperty.getSignature();
							}
						} else {
							Optional<?> texturesPropertyOptional = mineSkinAPI.getTextureProperties(skinName.equals("MINESKIN:RANDOM") ? utils.getRandomStringFromList(utils.getMineSkinUUIDs()) : skinName.split(":")[1]).stream().findAny();

							if (texturesPropertyOptional.isPresent()) {
								Property texturesProperty = (Property) texturesPropertyOptional.get();
								skinValue = texturesProperty.getValue();
								skinSignature = texturesProperty.getSignature();
							}
						}
					} else if (eazyNick.getVersion().startsWith("1_7")) {
						Optional<?> texturesPropertyOptional = ((net.minecraft.util.com.mojang.authlib.GameProfile) skinProfile).getProperties().get("textures").stream().findAny();

						if (texturesPropertyOptional.isPresent()) {
							net.minecraft.util.com.mojang.authlib.properties.Property texturesProperty = (net.minecraft.util.com.mojang.authlib.properties.Property) texturesPropertyOptional.get();
							skinValue = texturesProperty.getValue();
							skinSignature = texturesProperty.getSignature();
						}
					} else {
						Optional<?> texturesPropertyOptional = ((GameProfile) skinProfile).getProperties().get("textures").stream().findAny();

						if (texturesPropertyOptional.isPresent()) {
							Property texturesProperty = (Property) texturesPropertyOptional.get();
							skinValue = texturesProperty.getValue();
							skinSignature = texturesProperty.getSignature();
						}
					}

					skinsRestorerAPI.setSkinData("custom", new GenericProperty("textures", skinValue, skinSignature), null);
					skinsRestorerAPI.setSkin(player.getName(), skinName.startsWith("MINESKIN:") ? player.getName() : skinName);
					skinsRestorerAPI.applySkin(new PlayerWrapper(player));
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		if (setupYamlFile.getConfiguration().getBoolean("BungeeCord")) {
			String nickName = mysqlNickManager.getNickName(uniqueId);

			if((nickName == null) || !(nickName.equals("NaN"))) {
				mysqlNickManager.removePlayer(uniqueId);
				mysqlNickManager.addPlayer(uniqueId, nickName, skinName);
			} else
				mysqlNickManager.addPlayer(uniqueId, player.getName(), skinName);
		}

		// Respawn player and update tablist
		updatePlayer();
	}

	public void changeSkinToMineSkinId(String mineSkinId) {
		if(mineSkinId == null) return;

		MySQLNickManager mysqlNickManager = eazyNick.getMySQLNickManager();
		UUID uniqueId = player.getUniqueId();

		if(utils.getNickedPlayers().containsKey(uniqueId))
			utils.getNickedPlayers().get(uniqueId).setSkinName("MINESKIN:" + mineSkinId);
		else
			utils.getNickedPlayers().put(
					uniqueId,
					new NickedPlayerData(
							uniqueId,
							uniqueId,
							player.getDisplayName(),
							player.getPlayerListName(),
							player.getName(),
							player.getName(),
							"MINESKIN:" + mineSkinId,
							"",
							"",
							"",
							"",
							"",
							"",
							"default",
							9999
					)
			);

		if (setupYamlFile.getConfiguration().getBoolean("BungeeCord")) {
			String nickName = mysqlNickManager.getNickName(uniqueId);

			if((nickName == null) || !(nickName.equals("NaN"))) {
				mysqlNickManager.removePlayer(uniqueId);
				mysqlNickManager.addPlayer(uniqueId, nickName, "MINESKIN:" + mineSkinId);
			} else
				mysqlNickManager.addPlayer(uniqueId, player.getName(), "MINESKIN:" + mineSkinId);
		}

		// Respawn player and update tablist
		updatePlayer();
	}

	public void setName(String nickName) {
		UUID uniqueId = player.getUniqueId();

		if(utils.getNickedPlayers().containsKey(uniqueId))
			utils.getNickedPlayers().get(uniqueId).setNickName(nickName);
		else
			utils.getNickedPlayers().put(
					uniqueId,
					new NickedPlayerData(
							uniqueId,
							uniqueId,
							player.getDisplayName(),
							player.getPlayerListName(),
							player.getName(),
							nickName,
							player.getName(),
							"",
							"",
							"",
							"",
							"",
							"",
							"default",
							9999
					)
			);

		// Respawn player and update tablist
		updatePlayer();
	}

	public void nickPlayer(String nickName) {
		// Use nickname as skin name
		nickPlayer(nickName, nickName);
	}

	public void nickPlayer(String nickName, String skinName) {
		LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();

		if (!(eazyNick.getVersion().equalsIgnoreCase("1_7_R4")))
			player.setCustomName(nickName);

		if(setupYamlFile.getConfiguration().getBoolean("BungeeCord"))
			eazyNick.getMySQLNickManager().addPlayer(player.getUniqueId(), nickName, skinName);

		int fakeExperienceLevel = setupYamlFile.getConfiguration().getInt("FakeExperienceLevel");

		if(fakeExperienceLevel > -1) {
			utils.getOldExperienceLevels().put(player.getUniqueId(), player.getLevel());

			player.setLevel(fakeExperienceLevel);
		}

		if(utils.isPluginInstalled("SkinsRestorer") && setupYamlFile.getConfiguration().getBoolean("ChangeSkinsRestorerSkin")) {
			// Respawn player and update skin
			changeSkin(skinName);
		} else {
			// Respawn player
			updatePlayer();
		}

		if(setupYamlFile.getConfiguration().getBoolean("NickActionBarMessage")) {
			// Show and update action bar frequently
			new AsyncTask(new AsyncRunnable() {

				@Override
				public void run() {
					ActionBarUtils actionBarUtils = eazyNick.getActionBarUtils();

					if(eazyNick.isEnabled()
							&& utils.getNickedPlayers().containsKey(player.getUniqueId())
							&& player.isOnline()
							&& !(utils.getWorldsWithDisabledActionBar().contains(player.getWorld().getName().toUpperCase()))) {
						NickedPlayerData nickedPlayerData = utils.getNickedPlayers().get(player.getUniqueId());

						actionBarUtils.sendActionBar(
								player,
								eazyNick.getLanguageYamlFile().getConfigString(player, player.hasPermission("eazynick.actionbar.other")
										? "NickActionBarMessageOther"
										: "NickActionBarMessage"
								)
										.replace("%nickName%", nickName)
										.replace("%nickname%", nickName)
										.replace("%nickPrefix%", nickedPlayerData.getChatPrefix())
										.replace("%nickprefix%", nickedPlayerData.getChatPrefix())
										.replace("%nickSuffix%", nickedPlayerData.getChatSuffix())
										.replace("%nicksuffix%", nickedPlayerData.getChatSuffix())
										.replace("%prefix%", utils.getPrefix())
						);
					} else {
						if(player != null)
							actionBarUtils.sendActionBar(player, "");

						cancel();
					}
				}
			}, 0, 1000).run();
		}

		// Update nick item
		if(setupYamlFile.getConfiguration().getBoolean("NickItem.getOnJoin") && (player.hasPermission("eazynick.item"))) {
			for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
				ItemStack item = player.getInventory().getItem(slot);

				if((item != null)
						&& (item.getType() != Material.AIR)
						&& (item.getItemMeta() != null)
						&& item.getItemMeta().hasDisplayName()) {
					if(item.getItemMeta().getDisplayName().equalsIgnoreCase(
							eazyNick.getLanguageYamlFile().getConfigString(player, "NickItem.DisplayName.Disabled")
					))
						//noinspection ConstantConditions
						player.getInventory().setItem(
								slot,
								new ItemBuilder(
										Material.getMaterial(setupYamlFile.getConfiguration().getString("NickItem.ItemType.Enabled")),
										setupYamlFile.getConfiguration().getInt("NickItem.ItemAmount.Enabled"),
										setupYamlFile.getConfiguration().getInt("NickItem.MetaData.Enabled")
								)
										.setDisplayName(languageYamlFile.getConfigString(player, "NickItem.DisplayName.Enabled"))
										.setLore(languageYamlFile.getConfigString(player, "NickItem.ItemLore.Enabled").split("&n"))
										.setEnchanted(setupYamlFile.getConfiguration().getBoolean("NickItem.Enchanted.Enabled"))
										.build()
						);
				}
			}
		}
	}

	public void unnickPlayer() {
		// Remove MySQL data
		if(setupYamlFile.getConfiguration().getBoolean("BungeeCord")) {
			eazyNick.getMySQLNickManager().removePlayer(player.getUniqueId());
			eazyNick.getMySQLPlayerDataManager().removeData(player.getUniqueId());
		}

		// Unnick player
		unnickPlayerWithoutRemovingMySQL(false, true);
	}

	public void unnickPlayerWithoutRemovingMySQL(boolean keepNick, boolean respawnPlayer) {
		LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();

		NickedPlayerData nickedPlayerData = utils.getNickedPlayers().get(player.getUniqueId());
		String nickName = nickedPlayerData.getNickName(), realName = getRealName();

		if (!(eazyNick.getVersion().equalsIgnoreCase("1_7_R4")))
			player.setCustomName(realName);

		// Respawn player
		if(respawnPlayer) {
			if(utils.isPluginInstalled("SkinsRestorer") && setupYamlFile.getConfiguration().getBoolean("ChangeSkinsRestorerSkin")) {
				// Respawn player and update skin
				changeSkin(player.getName());
			} else {
				// Respawn player
				updatePlayer();
			}
		}

		new AsyncTask(new AsyncRunnable() {

			@Override
			public void run() {
				utils.getNickedPlayers().remove(player.getUniqueId());
			}
		}, 50L * 3).run();

		if(keepNick)
			utils.getLastNickData().put(player.getUniqueId(), nickedPlayerData.clone());
		else
			utils.getLastNickData().remove(player.getUniqueId());

		// Reset CloudNet v2 prefix and suffix
		if(utils.isPluginInstalled("CloudNetAPI")
				&& setupYamlFile.getConfiguration().getBoolean("ServerIsUsingCloudNETPrefixesAndSuffixes")) {
			CloudPlayer cloudPlayer = CloudAPI.getInstance().getOnlinePlayer(player.getUniqueId());

			PermissionEntity entity = cloudPlayer.getPermissionEntity();
			de.dytanic.cloudnet.lib.player.permission.PermissionGroup highestPermissionGroup =
					entity.getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool());

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

			if(utils.getOldCloudNETTagIDs().containsKey(player.getUniqueId())) {
				highestPermissionGroup.setTagId(utils.getOldCloudNETTagIDs().get(player.getUniqueId()));
				utils.getOldCloudNETTagIDs().remove(player.getUniqueId());
			}
		}

		// Reset PermissionsEx prefix, suffix and group
		if(utils.isPluginInstalled("PermissionsEx")) {
			PermissionUser user = PermissionsEx.getUser(player);

			if(setupYamlFile.getConfiguration().getBoolean("SwitchPermissionsExGroupByNicking")) {
				if(utils.getOldPermissionsExGroups().containsKey(player.getUniqueId())) {
					for(String group : utils.getOldPermissionsExGroups().get(player.getUniqueId()))
						user.addGroup(group);

					utils.getOldPermissionsExGroups().remove(player.getUniqueId());
				}
			} else if(utils.getOldPermissionsExPrefixes().containsKey(player.getUniqueId())
					&& utils.getOldPermissionsExSuffixes().containsKey(player.getUniqueId())) {
				user.setPrefix(
						utils.getOldPermissionsExPrefixes().get(player.getUniqueId()),
						player.getWorld().getName()
				);
				user.setSuffix(
						utils.getOldPermissionsExSuffixes().get(player.getUniqueId()),
						player.getWorld().getName()
				);
			}
		}

		// Reset fake scoreboard team
		if(setupYamlFile.getConfiguration().getBoolean("Settings.ChangeOptions.NameTag")) {
			if(utils.getScoreboardTeamManagers().containsKey(player.getUniqueId())) {
				utils.getScoreboardTeamManagers().get(player.getUniqueId()).destroyTeam();
				utils.getScoreboardTeamManagers().remove(player.getUniqueId());
			}
		}

		// Reset NametagEdit prefix and suffix synchronously
		Bukkit.getScheduler().runTask(eazyNick, () -> {
			if(utils.isPluginInstalled("NametagEdit")) {
				if(utils.getNametagEditPrefixes().containsKey(player.getUniqueId())
						|| utils.getNametagEditSuffixes().containsKey(player.getUniqueId())) {
					String prefix = utils.getNametagEditPrefixes().get(player.getUniqueId()),
							suffix = utils.getNametagEditSuffixes().get(player.getUniqueId());
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

		// Reset chat and tablist name
		new AsyncTask(new AsyncRunnable() {

			@Override
			public void run() {
				if(!(player.isOnline())) return;

				String oldDisplayName = nickedPlayerData.getOldDisplayName(),
						oldPlayerListName = nickedPlayerData.getOldPlayerListName();
				boolean replaceInDisplayName = (oldDisplayName != null) && oldDisplayName.equals("NONE"),
						replaceInPlayerListName = (oldPlayerListName != null) && oldPlayerListName.equals("NONE");

				if(!(utils.getWorldsWithDisabledPrefixAndSuffix().contains(player.getWorld().getName().toUpperCase()))
						|| replaceInDisplayName
						|| replaceInPlayerListName
				) {
					player.setDisplayName(
							replaceInDisplayName
									? player.getDisplayName().replace(nickName, player.getName())
									: oldDisplayName
					);
					setPlayerListName(
							replaceInPlayerListName
									? player.getPlayerListName().replace(nickName, player.getName())
									: oldDisplayName
					);
				}

				if(utils.getOldExperienceLevels().containsKey(player.getUniqueId())) {
					player.setLevel(utils.getOldExperienceLevels().get(player.getUniqueId()));

					utils.getOldExperienceLevels().remove(player.getUniqueId());
				}
			}
		}, 1000 + (
				setupYamlFile.getConfiguration().getBoolean("RandomDisguiseDelay")
						? 2000
						: 0
		)).run();

		// Update nick item
		if(setupYamlFile.getConfiguration().getBoolean("NickItem.getOnJoin")
				&& (player.hasPermission("eazynick.item"))) {
			for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
				ItemStack item = player.getInventory().getItem(slot);

				if((item != null)
						&& (item.getType() != Material.AIR)
						&& (item.getItemMeta() != null)
						&& item.getItemMeta().hasDisplayName()) {
					if(item.getItemMeta().getDisplayName().equalsIgnoreCase(
							eazyNick.getLanguageYamlFile().getConfigString(player, "NickItem.DisplayName.Enabled")
					))
						//noinspection ConstantConditions
						player.getInventory().setItem(
								slot,
								new ItemBuilder(
										Material.getMaterial(setupYamlFile.getConfiguration().getString("NickItem.ItemType.Disabled")),
										setupYamlFile.getConfiguration().getInt("NickItem.ItemAmount.Disabled"),
										setupYamlFile.getConfiguration().getInt("NickItem.MetaData.Disabled")
								)
										.setDisplayName(languageYamlFile.getConfigString(player, "NickItem.DisplayName.Disabled"))
										.setLore(languageYamlFile.getConfigString(player, "NickItem.ItemLore.Disabled").split("&n"))
										.setEnchanted(setupYamlFile.getConfiguration().getBoolean("NickItem.Enchanted.Disabled"))
										.build()
						);
				}
			}
		}
	}

	public String getRealName() {
		return player.getName();
	}

	public String getChatPrefix() {
		return utils.getNickedPlayers().containsKey(player.getUniqueId())
				? utils.getNickedPlayers().get(player.getUniqueId()).getChatPrefix()
				: (
						(utils.isPluginInstalled("Vault") && setupYamlFile.getConfiguration().getBoolean("ServerIsUsingVaultPrefixesAndSuffixes"))
								? Objects.requireNonNull(Bukkit.getServer().getServicesManager().getRegistration(Chat.class)).getProvider().getPlayerPrefix(player)
								: "");
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
		return utils.getNickedPlayers().containsKey(player.getUniqueId())
				? utils.getNickedPlayers().get(player.getUniqueId()).getChatSuffix()
				: (
						(utils.isPluginInstalled("Vault") && setupYamlFile.getConfiguration().getBoolean("ServerIsUsingVaultPrefixesAndSuffixes"))
								? Objects.requireNonNull(Bukkit.getServer().getServicesManager().getRegistration(Chat.class)).getProvider().getPlayerSuffix(player)
								: "");
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
		return utils.getNickedPlayers().containsKey(player.getUniqueId())
				? utils.getNickedPlayers().get(player.getUniqueId()).getTabPrefix()
				: ((utils.isPluginInstalled("Vault") && setupYamlFile.getConfiguration().getBoolean("ServerIsUsingVaultPrefixesAndSuffixes"))
						? Objects.requireNonNull(Bukkit.getServer().getServicesManager().getRegistration(Chat.class)).getProvider().getPlayerPrefix(player)
						: "");
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
		return utils.getNickedPlayers().containsKey(player.getUniqueId())
				? utils.getNickedPlayers().get(player.getUniqueId()).getTabSuffix()
				: ((utils.isPluginInstalled("Vault") && setupYamlFile.getConfiguration().getBoolean("ServerIsUsingVaultPrefixesAndSuffixes"))
						? Objects.requireNonNull(Bukkit.getServer().getServicesManager().getRegistration(Chat.class)).getProvider().getPlayerSuffix(player)
						: "");
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
		return utils.getNickedPlayers().containsKey(player.getUniqueId())
				? utils.getNickedPlayers().get(player.getUniqueId()).getTagPrefix()
				: ((utils.isPluginInstalled("Vault") && setupYamlFile.getConfiguration().getBoolean("ServerIsUsingVaultPrefixesAndSuffixes"))
						? Objects.requireNonNull(Bukkit.getServer().getServicesManager().getRegistration(Chat.class)).getProvider().getPlayerPrefix(player)
						: "");
	}

	public void setTagPrefix(String tagPrefix) {
		if(utils.getScoreboardTeamManagers().containsKey(player.getUniqueId()))
			utils.getScoreboardTeamManagers().get(player.getUniqueId()).setPrefix(tagPrefix);

		if(utils.getNickedPlayers().containsKey(player.getUniqueId()))
			utils.getNickedPlayers().get(player.getUniqueId()).setTagPrefix(tagPrefix);
	}

	public String getTagSuffix() {
		return utils.getNickedPlayers().containsKey(player.getUniqueId())
				? utils.getNickedPlayers().get(player.getUniqueId()).getTagSuffix()
				: ((utils.isPluginInstalled("Vault") && setupYamlFile.getConfiguration().getBoolean("ServerIsUsingVaultPrefixesAndSuffixes"))
						? Objects.requireNonNull(Bukkit.getServer().getServicesManager().getRegistration(Chat.class)).getProvider().getPlayerSuffix(player)
						: "");
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

	public String getRandomName() {
		return utils.getRandomStringFromList(utils.getNickNames());
	}

	public String getNickName() {
		return utils.getNickedPlayers().containsKey(player.getUniqueId())
				? utils.getNickedPlayers().get(player.getUniqueId()).getNickName()
				: player.getName();
	}

	public String getNickFormat() {
		return getChatPrefix() + getNickName() + getChatSuffix();
	}

	public String getOldDisplayName() {
		return utils.getNickedPlayers().containsKey(player.getUniqueId())
				? utils.getNickedPlayers().get(player.getUniqueId()).getOldDisplayName()
				: player.getName();
	}

	public String getOldPlayerListName() {
		return utils.getNickedPlayers().containsKey(player.getUniqueId())
				? utils.getNickedPlayers().get(player.getUniqueId()).getOldPlayerListName()
				: player.getName();
	}

	public String getGroupName() {
		return utils.getNickedPlayers().containsKey(player.getUniqueId())
				? utils.getNickedPlayers().get(player.getUniqueId()).getGroupName()
				: "NONE";
	}

	public void setGroupName(String groupName) {
		if(utils.getNickedPlayers().containsKey(player.getUniqueId()))
			utils.getNickedPlayers().get(player.getUniqueId()).setGroupName(groupName);
	}

	@SuppressWarnings("deprecation")
	public void updatePrefixSuffix(String nickName, String realName, String tagPrefix, String tagSuffix, String chatPrefix, String chatSuffix, String tabPrefix, String tabSuffix, int sortID, String groupName) {
		String finalTabPrefix = tabPrefix, finalTabSuffix = tabSuffix;

		if(setupYamlFile.getConfiguration().getBoolean("Settings.ChangeOptions.NameTag")) {
			// Reset fake scoreboard team
			if(utils.getScoreboardTeamManagers().containsKey(player.getUniqueId())) {
				utils.getScoreboardTeamManagers().get(player.getUniqueId()).destroyTeam();
				utils.getScoreboardTeamManagers().remove(player.getUniqueId());
			}

			// Create new fake scoreboard team
			if(!(setupYamlFile.getConfiguration().getBoolean("ChangeGroupAndPrefixAndSuffixInTAB")))
				utils.getScoreboardTeamManagers().put(
						player.getUniqueId(),
						new ScoreboardTeamHandler(
								player,
								nickName,
								realName,
								tagPrefix,
								tagSuffix,
								sortID,
								groupName
						)
				);
		}

		new AsyncTask(
				new AsyncRunnable() {

					@Override
					public void run() {
						if(!(eazyNick.isEnabled()) || !(player.isOnline()) || !(isNicked())) {
							cancel();
							return;
						}

						UUID uuid = player.getUniqueId();

						// Update fake scoreboard team
						if(utils.getScoreboardTeamManagers().containsKey(uuid)) {
							ScoreboardTeamHandler scoreboardTeamHandler = utils.getScoreboardTeamManagers().get(player.getUniqueId());
							scoreboardTeamHandler.destroyTeam();
							scoreboardTeamHandler.createTeam();
						}

						boolean tabGroupPrefixSuffixChange = utils.isPluginInstalled("TAB", "NEZNAMY")
								&& setupYamlFile.getConfiguration().getBoolean("ChangeGroupAndPrefixAndSuffixInTAB");

						// Update TAB tablist prefix and suffix and nametag prefix, suffix and group
						if(tabGroupPrefixSuffixChange)
							new TABHook(player).update(finalTabPrefix, finalTabSuffix, tagPrefix, tagSuffix, groupName);

						if(!(setupYamlFile.getConfiguration().getBoolean("Settings.ChangeOptions.PlayerListName"))) return;

						// Update tablist name
						if(!(tabGroupPrefixSuffixChange)) {
							String tmpTabPrefix = finalTabPrefix,
									tmpTabSuffix = finalTabSuffix;

							// Replace PlaceholderAPI placeholders
							if(utils.isPluginInstalled("PlaceholderAPI")) {
								tmpTabPrefix = PlaceholderAPI.setPlaceholders(player, tmpTabPrefix);
								tmpTabSuffix = PlaceholderAPI.setPlaceholders(player, tmpTabSuffix);
							}

							setPlayerListName(tmpTabPrefix + nickName + tmpTabSuffix);
						}
					}
				},
				400 + (
						setupYamlFile.getConfiguration().getBoolean("RandomDisguiseDelay")
								? 2000 : 0
				),
				setupYamlFile.getConfiguration().getInt("PrefixSuffixUpdateDelay") * 50L
		).run();

		// Replace PlaceholderAPI placeholders
		if(utils.isPluginInstalled("PlaceholderAPI")) {
			chatPrefix = PlaceholderAPI.setPlaceholders(player, chatPrefix);
			chatSuffix = PlaceholderAPI.setPlaceholders(player, chatSuffix);
			tabPrefix = PlaceholderAPI.setPlaceholders(player, tabPrefix);
			tabSuffix = PlaceholderAPI.setPlaceholders(player, tabSuffix);
		}
		
		// Set chat name
		if(setupYamlFile.getConfiguration().getBoolean("Settings.ChangeOptions.DisplayName"))
			player.setDisplayName(chatPrefix + nickName + chatSuffix);

		Bukkit.getScheduler().runTask(eazyNick, () -> {
			String tmpTagPrefix = tagPrefix, tmpTagSuffix = tagSuffix;

			if (utils.isPluginInstalled("PlaceholderAPI")) {
				tmpTagPrefix = PlaceholderAPI.setPlaceholders(player, tmpTagPrefix);
				tmpTagSuffix = PlaceholderAPI.setPlaceholders(player, tmpTagSuffix);
			}

			// Update NametagEdit prefix and suffix synchronously
			if (utils.isPluginInstalled("NametagEdit")) {
				utils.getNametagEditPrefixes().remove(player.getUniqueId());
				utils.getNametagEditSuffixes().remove(player.getUniqueId());

				INametagApi nametagEditAPI = NametagEdit.getApi();
				Nametag nametag = nametagEditAPI.getNametag(player);

				utils.getNametagEditPrefixes().put(player.getUniqueId(), nametag.getPrefix());
				utils.getNametagEditSuffixes().put(player.getUniqueId(), nametag.getSuffix());

				nametagEditAPI.setPrefix(player, tmpTagPrefix);
				nametagEditAPI.setSuffix(player, tmpTagSuffix);
				nametagEditAPI.reloadNametag(player);
			}

			// Update CloudNet v2 prefix and suffix
			if (utils.isPluginInstalled("CloudNetAPI")) {
				CloudPlayer cloudPlayer = CloudAPI.getInstance().getOnlinePlayer(player.getUniqueId());

				if (setupYamlFile.getConfiguration().getBoolean("ServerIsUsingCloudNETPrefixesAndSuffixes")) {
					PermissionEntity entity = cloudPlayer.getPermissionEntity();
					de.dytanic.cloudnet.lib.player.permission.PermissionGroup highestPermissionGroup =
							entity.getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool());

					utils.getOldCloudNETPrefixes().put(player.getUniqueId(), entity.getPrefix());
					utils.getOldCloudNETSuffixes().put(player.getUniqueId(), entity.getSuffix());
					utils.getOldCloudNETTagIDs().put(player.getUniqueId(), highestPermissionGroup.getTagId());

					entity.setPrefix(tmpTagPrefix);
					entity.setSuffix(tmpTagSuffix);
					highestPermissionGroup.setPrefix(tmpTagPrefix);
					highestPermissionGroup.setSuffix(tmpTagSuffix);
					highestPermissionGroup.setTagId(Integer.MAX_VALUE);
				}
			}
		});
		
		// Update PermissionsEx prefix, suffix and group
		if(utils.isPluginInstalled("PermissionsEx")) {
			PermissionUser user = PermissionsEx.getUser(player);
		
			if(setupYamlFile.getConfiguration().getBoolean("SwitchPermissionsExGroupByNicking") && !(groupName.equalsIgnoreCase("NONE"))) {
				List<String> groupNames = new ArrayList<>();

				for (PermissionGroup group : user.getGroups()) {
					groupNames.add(group.getName());
					
					user.removeGroup(group);
				}
				
				if(!(utils.getOldPermissionsExGroups().containsKey(player.getUniqueId())))
					utils.getOldPermissionsExGroups().put(player.getUniqueId(), groupNames.toArray(new String[0]));
			} else {
				utils.getOldPermissionsExPrefixes().put(player.getUniqueId(), user.getPrefix());
				utils.getOldPermissionsExSuffixes().put(player.getUniqueId(), user.getSuffix());
				
				user.setPrefix(tabPrefix, player.getWorld().getName());
				user.setSuffix(tabSuffix, player.getWorld().getName());
			}
		}
	}

}
