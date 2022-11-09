package com.justixdev.eazynick.nms.netty.server;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.api.NickManager;
import com.justixdev.eazynick.api.NickedPlayerData;
import com.justixdev.eazynick.nms.ReflectionHelper;
import com.justixdev.eazynick.utilities.AsyncTask;
import com.justixdev.eazynick.utilities.Utils;
import com.justixdev.eazynick.utilities.configuration.yaml.SetupYamlFile;
import com.mojang.authlib.GameProfile;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.*;

public class OutgoingPacketInjector {

	private final EazyNick eazyNick;
	private final ReflectionHelper reflectionHelper;
	
	private ArrayList<Channel> channels;
	private String handlerName;
	
	public OutgoingPacketInjector() {
		this.eazyNick = EazyNick.getInstance();
		this.reflectionHelper = eazyNick.getReflectionHelper();
	}
	
	@SuppressWarnings({"unused", "CommentedOutCode"})
	public void init() {
		channels = new ArrayList<>();
		handlerName = eazyNick.getDescription().getName().toLowerCase() + "_handler";
		
		String version = eazyNick.getVersion();
		boolean is1_18 = version.startsWith("1_18"), is1_19 = version.startsWith("1_19");
		
		//Get Channel from NetworkManager
		reflectionHelper.getFirstFieldByType(
				(version.startsWith("1_17") || is1_18 || is1_19)
						? reflectionHelper.getNMSClass("network.NetworkManager")
						: reflectionHelper.getNMSClass("NetworkManager"),
				Channel.class
		).ifPresent(field -> {
			field.setAccessible(true);

			try {
				//Get MinecraftServer from CraftServer
				Object craftServer = Bukkit.getServer();
				Field dedicatedServer = reflectionHelper
						.getCraftClass("CraftServer")
						.getDeclaredField("console");
				dedicatedServer.setAccessible(true);

				Object minecraftServer = dedicatedServer.get(craftServer);

				//Add packet handler to every ServerConnection and remove old ones
				for(Object manager : Collections.synchronizedList((List<?>) getNetworkManagerList(
						minecraftServer.getClass().getMethod(
								(is1_18 || is1_19)
										? "ad"
										: "getServerConnection"
						).invoke(minecraftServer)
				)).toArray()) {
					Channel channel = (Channel) field.get(manager);

					if(channel == null) continue;
					if(channel.pipeline() == null) continue;

					if(channel.pipeline().get("packet_handler") != null) {
						channels.add(channel);

						Runnable addPacketHandlerTask = () -> addPacketHandler(channel);

						if (channel.pipeline().get(handlerName) == null)
							addPacketHandlerTask.run();

					/*if (channel.pipeline().get(handlerName) != null) {
						AtomicBoolean taskExecuted = new AtomicBoolean();

						Thread killThread = new Thread(() -> {
							try {
								channel.pipeline().remove(handlerName);
							} catch (Exception ignore) {
							} finally {
								if(!(taskExecuted.get())) {
									taskExecuted.set(true);

									addPacketHandlerTask.run();
								}
							}
						});

						killThread.start();

						new AsyncTask(new AsyncTask.AsyncRunnable() {

							@Override
							public void run() {
								killThread.interrupt();

								if(!(taskExecuted.get()))
									addPacketHandlerTask.run();
							}
						}, 10).run();
					} else
						addPacketHandlerTask.run();*/
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}
	
	private void addPacketHandler(Channel channel) {
		Utils utils = eazyNick.getUtils();
		SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
		
		String version = eazyNick.getVersion();
		boolean is1_17 = version.startsWith("1_17"), is1_18 = version.startsWith("1_18"), is1_19 = version.startsWith("1_19");
		
		try {
			//Add new packet handler
			channel.pipeline().addAfter(
					"packet_handler",
					handlerName,
					new ChannelDuplexHandler() {
				
				@SuppressWarnings("unchecked")
						@Override
						public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
							//Determine player from ip
							InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
							String ip = inetSocketAddress.getAddress().getHostAddress();
							Optional<? extends Player> playerOptional = Bukkit.getOnlinePlayers().stream().filter(currentPlayer -> {
								InetSocketAddress currentInetSocketAddress = currentPlayer.getAddress();

								if(currentInetSocketAddress != null)
									return (ip.equals("127.0.0.1")
											? (currentInetSocketAddress.getPort() == inetSocketAddress.getPort())
											: (
													currentInetSocketAddress.getAddress().getHostAddress().equals(ip)
													&& (currentInetSocketAddress.getPort() == inetSocketAddress.getPort())
											)
									);

								return false;
							}).findAny();

							if(playerOptional.isPresent()) {
								Player player = playerOptional.get();

								try {
									if (msg.getClass().getSimpleName().equals("PacketPlayOutNamedEntitySpawn")) {
										UUID uuid = (UUID) reflectionHelper.getField(msg.getClass(), "b").get(msg);

										if(!(utils.getSoonNickedPlayers().contains(uuid))) {
											if(utils.getNickedPlayers().containsKey(uuid))
												//Replace uuid with fake uuid (spoofed uuid)
												reflectionHelper.setField(
														msg,
														"b",
														setupYamlFile.getConfiguration().getBoolean("Settings.ChangeOptions.UUID")
																? utils.getNickedPlayers().get(uuid).getSpoofedUniqueId()
																: uuid
												);

											super.write(ctx, msg, promise);
										}
									} else if(msg.getClass().getSimpleName().equals("PacketPlayOutPlayerInfo")) {
										Object b = reflectionHelper.getField(msg.getClass(), "b").get(msg);

										if(b != null) {
											for (Object playerInfoData : ((List<?>) b)) {
												UUID uuid = ((GameProfile) reflectionHelper.getField(
														playerInfoData.getClass(),
														(is1_17 || is1_18 || is1_19)
																? "c"
																: "d"
												).get(playerInfoData)).getId();

												if(utils.getSoonNickedPlayers().contains(uuid)
														&& setupYamlFile.getConfiguration().getBoolean("SeeNickSelf")
														&& reflectionHelper.getField(
																msg.getClass(),
																"a"
														).get(msg).toString().endsWith("ADD_PLAYER")
												)
													return;

												if(utils.getNickedPlayers().containsKey(uuid)) {
													//Replace game profile with fake game profile (nicked player profile)
													reflectionHelper.setField(
															playerInfoData,
															(is1_17 || is1_18 || is1_19)
																	? "c"
																	: "d",
															utils.getNickedPlayers().get(uuid).getFakeGameProfile(
																	!(uuid.equals(player.getUniqueId()))
																	&& setupYamlFile.getConfiguration().getBoolean("Settings.ChangeOptions.UUID")
															)
													);
												}
											}
										}

										super.write(ctx, msg, promise);
									} else if(msg.getClass().getSimpleName().equals("PacketPlayOutTabComplete")) {
										if(!(player.hasPermission("eazynick.bypass")
												&& setupYamlFile.getConfiguration().getBoolean("EnableBypassPermission"))
										) {
											if(utils.isVersion13OrLater()) {
												Object suggestions = reflectionHelper.getField(msg.getClass(), "b").get(msg);
												Object suggestionsRange = suggestions.getClass().getMethod("getRange").invoke(suggestions);
												int suggestionsStart = (int) suggestionsRange.getClass().getMethod("getStart").invoke(suggestionsRange);
												ArrayList<Object> suggestionsList = new ArrayList<>((List<Object>) suggestions.getClass().getMethod("getList").invoke(suggestions));
												Map<Object, String> texts = new HashMap<>();
												String buffer = null;
												boolean modified = false;

												for(Object suggestion : suggestionsList) {
													try {
														String text = (String) suggestion.getClass().getMethod("getText").invoke(suggestion);
														Object range = suggestion.getClass().getMethod("getRange").invoke(suggestion);

														buffer = text.substring(0, (int) range.getClass().getMethod("getEnd").invoke(range) - (int) range.getClass().getMethod("getStart").invoke(range));

														texts.put(suggestion, text);
													} catch (IllegalAccessException ex) {
														ex.printStackTrace();
													}
												}

												List<String> playerNames = new ArrayList<>();

												Bukkit.getOnlinePlayers().forEach(currentPlayer -> {
													if(utils.getNickedPlayers().containsKey(currentPlayer.getUniqueId())) {
														String nickName = utils.getNickedPlayers().get(currentPlayer.getUniqueId()).getNickName();

														if(texts.values().stream().anyMatch(nickName::equalsIgnoreCase)) {
															playerNames.add(nickName);
															return;
														}
													}

													String name = currentPlayer.getName();

													if(texts.values().stream().anyMatch(name::equalsIgnoreCase))
														playerNames.add(name);
												});

												if(!(playerNames.isEmpty()) || (buffer == null) || texts.containsKey("@p")) {
													// Player names are in the suggestions
													suggestionsList.removeIf(suggestion -> playerNames.stream().anyMatch(playerName -> playerName.equalsIgnoreCase(texts.get(suggestion))));

													if(buffer != null)
														buffer = buffer.toLowerCase();
													else
														buffer = "";

													for (Player currentPlayer : Bukkit.getOnlinePlayers()) {
														String name = utils.getNickedPlayers().containsKey(currentPlayer.getUniqueId())
																? utils.getNickedPlayers().get(currentPlayer.getUniqueId()).getNickName()
																: currentPlayer.getName();

														name = name.toLowerCase();

														if(buffer.isEmpty() || name.startsWith(buffer)) {
															int matchingChars = 0;

															for (int i = 0; i < buffer.length(); i++) {
																if(name.charAt(i) == buffer.charAt(i))
																	matchingChars++;
																else
																	break;
															}

															Class<?> stringRangeClass = Class.forName("com.mojang.brigadier.context.StringRange");
															Object suggestion = Class.forName("com.mojang.brigadier.suggestion.Suggestion").getConstructor(stringRangeClass, String.class).newInstance(stringRangeClass.getConstructor(int.class, int.class).newInstance(suggestionsStart, suggestionsStart + matchingChars), name);

															suggestionsList.add(suggestion);
														}

														modified = true;
													}
												}

												if(modified) {
													StringBuilder command = new StringBuilder();

													for (int i = 1; i <= (suggestionsStart - 2 /* slash & space */); i++)
														command.append("?");

													reflectionHelper.setField(msg, "b", Class.forName("com.mojang.brigadier.suggestion.Suggestions").getMethod("create", String.class, Collection.class).invoke(null, "/" + command + " " + buffer, suggestionsList));
												}
											} else {
												String textToComplete = utils.getTextsToComplete().get(player);

												if(textToComplete != null) {
													String[] splitTextToComplete = textToComplete.trim().split(" ");

													if(!(textToComplete.startsWith("/")) || (splitTextToComplete.length > 1)) {
														List<String> playerNames = new ArrayList<>();

														if(splitTextToComplete.length < 2)
															textToComplete = "";
														else
															textToComplete = splitTextToComplete[splitTextToComplete.length - 1];

														//Collect nicknames
														Bukkit.getOnlinePlayers()
																.stream()
																.filter(currentPlayer -> !(new NickManager(currentPlayer).isNicked()))
																.forEach(currentPlayer -> playerNames.add(currentPlayer.getName()));

														utils.getNickedPlayers()
																.values()
																.forEach(currentNickedPlayerData -> playerNames.add(currentNickedPlayerData.getNickName()));

														//Process completions
														List<String> newCompletions = new ArrayList<>(Arrays.asList((String[]) reflectionHelper.getField(
																msg.getClass(),
																"a"
														).get(msg)));
														newCompletions.removeIf(currentCompletion -> Bukkit.getOnlinePlayers()
																.stream()
																.anyMatch(currentPlayer -> currentPlayer.getName().equalsIgnoreCase(currentCompletion))
														);
														newCompletions.addAll(StringUtil.copyPartialMatches(
																textToComplete,
																playerNames,
																new ArrayList<>()
														));

														//Sort completions alphabetically
														Collections.sort(newCompletions);

														//Replace completions
														reflectionHelper.setField(
																msg,
																"a",
																newCompletions.toArray(new String[0])
														);
													}
												}
											}
										}

										super.write(ctx, msg, promise);
									} else if (msg.getClass().getSimpleName().equals("PacketPlayOutChat")
											&& setupYamlFile.getConfiguration().getBoolean("OverwriteMessagePackets")) {
										//Get chat message from packet and replace names
										Object editedComponent = replaceNames(
												reflectionHelper.getField(
														msg.getClass(),
														"a"
												).get(msg),
												true
										);

										//Overwrite chat message
										if(editedComponent != null)
											reflectionHelper.setField(msg, "a", editedComponent);

										super.write(ctx, msg, promise);
									} else if (msg.getClass().getSimpleName().equals("ClientboundSystemChatPacket")
											&& setupYamlFile.getConfiguration().getBoolean("OverwriteMessagePackets")) {
										// Get content
										String content = (String) reflectionHelper.getField(msg.getClass(), "content").get(msg);

										// Replace names
										Object editedComponent = replaceNames(deserialize(content), true);

										// Overwrite chat message
										if (editedComponent != null)
											//noinspection JavaReflectionInvocation
											msg = msg.getClass().getConstructor(Class.forName("net.minecraft.network.chat.IChatBaseComponent"), boolean.class).newInstance(editedComponent, msg.getClass().getMethod("c").invoke(msg));

										super.write(ctx, msg, promise);
									} else if(msg.getClass().getSimpleName().equals("PacketPlayOutScoreboardObjective")) {
										//Replace name
										if(utils.isVersion13OrLater())
											reflectionHelper.setField(msg,
													(is1_17 || is1_18 || is1_19)
															? "e"
															: "b",
													replaceNames(
															reflectionHelper.getField(
																	msg.getClass(),
																	(is1_17 || is1_18 || is1_19)
																			? "e"
																			: "b"
															).get(msg),
															false
													)
											);
										else {
											String name = (String) reflectionHelper.getField(
													msg.getClass(),
													"b"
											).get(msg);

											if(name != null) {
												for (NickedPlayerData currentNickedPlayerData : utils.getNickedPlayers().values()) {
													if(name.contains(currentNickedPlayerData.getRealName())) {
														name = name.replace(currentNickedPlayerData.getRealName(), currentNickedPlayerData.getNickName());
														break;
													}
												}

												reflectionHelper.setField(msg, "b", name);
											}
										}

										super.write(ctx, msg, promise);
									} else if(msg.getClass().getSimpleName().equals("PacketPlayOutScoreboardScore")) {
										//Replace name
										String name = (String) reflectionHelper.getField(
												msg.getClass(),
												"a"
										).get(msg);

										if(name != null) {
											for (NickedPlayerData currentNickedPlayerData : utils.getNickedPlayers().values()) {
												if(name.contains(currentNickedPlayerData.getRealName())) {
													name = name.replace(currentNickedPlayerData.getRealName(), currentNickedPlayerData.getNickName());
													break;
												}
											}

											reflectionHelper.setField(msg, "a", name);
										}

										super.write(ctx, msg, promise);
									} else if(msg.getClass().getSimpleName().equals("PacketPlayOutScoreboardTeam")) {
										if(
												!(
														player.hasPermission("eazynick.bypass")
														&& setupYamlFile.getConfiguration().getBoolean("EnableBypassPermission")
												)
												&& !(
														utils.isPluginInstalled("TAB", "NEZNAMY")
														&& setupYamlFile.getConfiguration().getBoolean("ChangeGroupAndPrefixAndSuffixInTAB")
												)
												&& setupYamlFile.getConfiguration().getBoolean("Settings.ChangeOptions.NameTag")
										) {
											String contentsFieldName = version.equals("1_8_R1")
													? "e"
													: (
															(version.equals("1_8_R2") || version.equals("1_8_R3") || version.equals("1_9_R1"))
																	? "g"
																	: (
																			(is1_17 || is1_18 || is1_19)
																					? "j"
																					: "h"
																	)
													);
											Object contentsList = reflectionHelper.getField(msg.getClass(), contentsFieldName).get(msg);

											if(contentsList != null) {
												//Replace names
												List<String> contents = new ArrayList<>((List<String>) contentsList);

												for (NickedPlayerData currentNickedPlayerData : utils.getNickedPlayers().values()) {
													for (String currentName : new ArrayList<>(contents)) {
														if(currentName.contains(currentNickedPlayerData.getRealName())) {
															contents.remove(currentName);

															if(!(contents.contains(currentNickedPlayerData.getNickName())))
																contents.add(
																		currentName.replace(
																				currentNickedPlayerData.getRealName(),
																				currentNickedPlayerData.getNickName()
																		)
																);

															break;
														}
													}
												}

												reflectionHelper.setField(msg, contentsFieldName, contents);
											}
										}

										super.write(ctx, msg, promise);
									} else
										super.write(ctx, msg, promise);
								} catch (Exception ex) {
									ex.printStackTrace();

									try {
										super.write(ctx, msg, promise);
									} catch (Exception ex2) {
										utils.sendConsole(
												"§4Could not write packet to network connection§8: §e"
														+ ex2.getMessage()
										);
									}
								}
							} else {
								try {
									if (msg.getClass().getSimpleName().equals("PacketStatusOutServerInfo")) {
										Object serverPing = reflectionHelper.getField(msg.getClass(), "b").get(msg);
										Object serverPingPlayerSample = reflectionHelper.getField(
												serverPing.getClass(),
												(is1_17 || is1_18 || is1_19)
														? "d"
														: "b"
										).get(serverPing);
										GameProfile[] gameProfileArray = (GameProfile[]) reflectionHelper.getField(
												serverPingPlayerSample.getClass(),
												"c"
										).get(serverPingPlayerSample);

										for (int i = 0; i < gameProfileArray.length; i++) {
											UUID uuid = gameProfileArray[i].getId();

											if(utils.getNickedPlayers().containsKey(uuid))
												//Replace game profile with fake game profile (nicked player profile)
												gameProfileArray[i] = (GameProfile) utils.getNickedPlayers()
														.get(uuid)
														.getFakeGameProfile(setupYamlFile.getConfiguration().getBoolean("Settings.ChangeOptions.UUID"));
										}

										//Replace game profiles in ServerPingPlayerSample
										reflectionHelper.setField(
												serverPingPlayerSample,
												"c", gameProfileArray
										);

										super.write(ctx, msg, promise);
									} else
										super.write(ctx, msg, promise);
								} catch (Exception ex) {
									utils.sendConsole(
											"§4Could not write packet to network connection (while logging in or pinging server)§8: §e"
													+ ex.getMessage()
									);
								}
							}
						}

						@Override
						public void close(ChannelHandlerContext ctx, ChannelPromise future) {
						}

					}
			);
		} catch (Exception ex) {
			//Hide "Duplicate handler" and "NoSuchElementException" errors
			if(!(
					ex.getMessage().contains("Duplicate handler")
					|| ex.getMessage().contains("java.util.NoSuchElementException: packet_handler")
			))
				ex.printStackTrace();
		}
	}
	
	public Object getNetworkManagerList(Object serverConnection) {
		try {
			//Get NetworkManager list from ServerConnection
			List<Field> fields = Arrays.asList(serverConnection.getClass().getDeclaredFields());
			Collections.reverse(fields);
			
			for(Field field : fields) {
				field.setAccessible(true);
				
				if(field.getType() == List.class)
					return field.get(serverConnection);
			}
		} catch(IllegalAccessException ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private Object replaceNames(Object iChatBaseComponent, boolean isChatPacket) {
		Utils utils = eazyNick.getUtils();
		
		String version = eazyNick.getVersion();
		boolean is1_17 = version.startsWith("1_17"), is1_18 = version.startsWith("1_18"), is1_19 = version.startsWith("1_19");
		Object editedComponent = iChatBaseComponent;

		try {
			if(iChatBaseComponent != null) {
				//Collect raw text from message
				Class<?> iChatBaseComponentClass = reflectionHelper.getNMSClass(
						(is1_17 || is1_18 || is1_19)
								? "network.chat.IChatBaseComponent"
								: "IChatBaseComponent"
				);
				
				StringBuilder fullText = new StringBuilder();
				Method method = iChatBaseComponentClass.getDeclaredMethod(
						is1_19
								? "c"
								: (is1_18
										? "b"
										: (
												(Bukkit.getVersion().contains("1.14.4") || version.startsWith("1_15") || version.startsWith("1_16") || is1_17)
														? "getSiblings"
														: "a"
										)
								)
				);
				method.setAccessible(true);
				
				for (Object partlyIChatBaseComponent : ((List<Object>) method.invoke(iChatBaseComponent))) {
					if(partlyIChatBaseComponent.getClass().getSimpleName().equals("ChatComponentText")) {
						String[] json = serialize(partlyIChatBaseComponent)
								.replace("\"", "")
								.replace("{", "")
								.replace("}", "")
								.split(",");
						
						for (String s : json) {
							if(s.startsWith("text:"))
								fullText.append(s.replaceFirst("text:", ""));
						}
					}
				}

				//Replace real names with nicknames
				if(!(
						(
								fullText.toString().contains(ChatColor.stripColor(utils.getLastChatMessage()))
								&& isChatPacket
						)
						|| fullText.toString().startsWith(ChatColor.stripColor(utils.getPrefix()))
				)) {
					String json = serialize(iChatBaseComponent);
					
					for (NickedPlayerData nickedPlayerData : utils.getNickedPlayers().values()) {
						Player targetPlayer = Bukkit.getPlayer(nickedPlayerData.getUniqueId());
						
						if(targetPlayer != null) {
							String name = targetPlayer.getName();
							
							if(json.contains(name))
								json = json.replace(name, nickedPlayerData.getNickName());
						}
					}

					editedComponent = deserialize(json);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return editedComponent;
	}
	
	private String serialize(Object iChatBaseComponent) {
		try {
			String version = eazyNick.getVersion();
			Class<?> iChatBaseComponentClass = reflectionHelper.getNMSClass(
					(version.startsWith("1_17") || version.startsWith("1_18") || version.startsWith("1_19"))
							? "network.chat.IChatBaseComponent"
							: "IChatBaseComponent"
			);
			
			return ((String) (version.equals("1_8_R1")
					? reflectionHelper.getNMSClass("ChatSerializer")
					: iChatBaseComponentClass.getDeclaredClasses()[0])
							.getMethod("a", iChatBaseComponentClass)
							.invoke(null, iChatBaseComponent));
		} catch (Exception ex) {
			return "";
		}
	}
	
	public Object deserialize(String json) {
		try {
			String version = eazyNick.getVersion();
			
			return (version.equals("1_8_R1")
					? reflectionHelper.getNMSClass("ChatSerializer")
					: (reflectionHelper.getNMSClass(
							(version.startsWith("1_17") || version.startsWith("1_18") || version.startsWith("1_19"))
									? "network.chat.IChatBaseComponent"
									: "IChatBaseComponent"
					)).getDeclaredClasses()[0]
			).getMethod(
					(version.startsWith("1_18") || version.startsWith("1_19"))
							? "b"
							: "a",
					String.class
			).invoke(null, json);
		} catch (Exception ex) {
			return "";
		}
	}
	
	@SuppressWarnings("unused")
	public void unregister() {
		Thread killThread = new Thread(() -> channels
				.stream()
				.filter(currentChannel -> ((currentChannel != null) && (currentChannel.pipeline().get(handlerName) != null)))
				.forEach(currentChannel -> {
					try {
						currentChannel.pipeline().remove(handlerName);
					} catch (Exception ignore) {
					}
				})
		);
		
		killThread.start();
		
		new AsyncTask(new AsyncTask.AsyncRunnable() {
			
			@Override
			public void run() {
				killThread.interrupt();
			}
		}, 50).run();
	}
	
}
