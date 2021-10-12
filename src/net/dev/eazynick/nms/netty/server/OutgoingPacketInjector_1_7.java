package net.dev.eazynick.nms.netty.server;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.NickManager;
import net.dev.eazynick.api.NickedPlayerData;
import net.dev.eazynick.nms.ReflectionHelper;
import net.dev.eazynick.utilities.NickReason;
import net.dev.eazynick.utilities.Utils;
import net.dev.eazynick.utilities.configuration.yaml.SetupYamlFile;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.io.netty.channel.*;

public class OutgoingPacketInjector_1_7 {

	private EazyNick eazyNick;
	private ReflectionHelper reflectionHelper;
	
	private ArrayList<Channel> channels;
	private String handlerName;
	
	public OutgoingPacketInjector_1_7() {
		this.eazyNick = EazyNick.getInstance();
		this.reflectionHelper = eazyNick.getReflectionHelper();
	}
	
	public void init() {
		Utils utils = eazyNick.getUtils();
		SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
		
		channels = new ArrayList<>();
		handlerName = eazyNick.getDescription().getName().toLowerCase() + "_handler";
		
		//Get Channel from NetworkManager
		Field field = reflectionHelper.getFirstFieldByType(reflectionHelper.getNMSClass("NetworkManager"), Channel.class);
		field.setAccessible(true);
		
		try {
			//Get MinecraftServer from CraftServer
			Object craftServer = Bukkit.getServer();
			Field dedicatedServer = reflectionHelper.getCraftClass("CraftServer").getDeclaredField("console");
			dedicatedServer.setAccessible(true);
			
			Object minecraftServer = dedicatedServer.get(craftServer);
			
			//Add packet handler to every ServerConnection and remove old ones
			for(Object manager : Collections.synchronizedList((List<?>) getNetworkManagerList(minecraftServer.getClass().getMethod("getServerConnection").invoke(minecraftServer))).toArray()) {
				Channel channel = (Channel) field.get(manager);
				
				if((channel.pipeline().context("packet_handler") != null)) {
					channels.add(channel);
					
					if (channel.pipeline().get(handlerName) != null)
						channel.pipeline().remove(handlerName);
					
					try {
						//Add new packet handler
						channel.pipeline().addBefore("packet_handler", handlerName, new ChannelDuplexHandler() {
							
							@Override
							public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
								InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
								String ip = inetSocketAddress.getAddress().getHostAddress();
								Player player = null;
								
								//Determine player from ip
								for (Player currentPlayer : Bukkit.getOnlinePlayers()) {
									InetSocketAddress currentInetSocketAddress = currentPlayer.getAddress();
	
									if(ip.equals("127.0.0.1") ? (currentInetSocketAddress.getPort() == inetSocketAddress.getPort()) : (currentInetSocketAddress.getAddress().getHostAddress().equals(ip) || (currentInetSocketAddress.getPort() == inetSocketAddress.getPort())))
										player = currentPlayer;
								}
	
								if(player != null) {
									try {
										if (msg.getClass().getSimpleName().equals("PacketPlayOutNamedEntitySpawn")) {
											UUID uuid = ((GameProfile) reflectionHelper.getField(msg.getClass(), "b").get(msg)).getId();
												
											if(!(utils.getSoonNickedPlayers().containsKey(uuid))) {
												if(utils.getNickedPlayers().containsKey(uuid))
													//Replace game profile with fake game profile (nicked player profile)
													reflectionHelper.setField(msg, "b", utils.getNickedPlayers().get(uuid).getFakeGameProfile(false));
												
												super.write(ctx, msg, promise);
											}
										} else if(msg.getClass().getSimpleName().equals("PacketPlayOutPlayerInfo")) {
											Object playerObject = reflectionHelper.getField(msg.getClass(), "player").get(msg);
											
											if(playerObject != null) {
												GameProfile playerProfile = (GameProfile) playerObject;
												UUID uuid = playerProfile.getId();
												
												if(utils.getSoonNickedPlayers().containsKey(uuid) && utils.getSoonNickedPlayers().get(uuid).equals(NickReason.JOIN) && (reflectionHelper.getField(msg.getClass(), "action").getInt(msg) == 0))
													return;
												
												if(utils.getNickedPlayers().containsKey(uuid)) {
													//Replace game profile with fake game profile (nicked player profile)
													NickedPlayerData nickedPlayerData = utils.getNickedPlayers().get(uuid);
													boolean tabOverride = utils.isPluginInstalled("TAB", "NEZNAMY") && setupYamlFile.getConfiguration().getBoolean("ChangeNameAndPrefixAndSuffixInTAB");
													String name = playerProfile.getName();
													
													reflectionHelper.setField(msg, "player", tabOverride ? nickedPlayerData.getTABGameProfile(name, false) : nickedPlayerData.getFakeGameProfile(false));
													reflectionHelper.setField(msg, "username", tabOverride ? name : nickedPlayerData.getNickName());
												}
											}
											
											super.write(ctx, msg, promise);
										} else if(msg.getClass().getSimpleName().equals("PacketPlayOutTabComplete")) {
											String textToComplete = utils.getTextsToComplete().get(player);
											String[] splitTextToComplete = textToComplete.trim().split(" ");
											
											if(!(textToComplete.startsWith("/")) || (splitTextToComplete.length > 1)) {
												ArrayList<String> newCompletions = new ArrayList<>(), playerNames = new ArrayList<>();
												
												if(splitTextToComplete.length < 2)
													textToComplete = "";
												else
													textToComplete = splitTextToComplete[splitTextToComplete.length - 1];
												
												//Collect nicknames
												Bukkit.getOnlinePlayers().stream().filter(currentPlayer -> !(new NickManager(currentPlayer).isNicked())).forEach(currentPlayer -> playerNames.add(currentPlayer.getName()));
												
												utils.getNickedPlayers().values().forEach(currentNickedPlayerData -> playerNames.add(currentNickedPlayerData.getNickName()));
		
												//Process completions
												newCompletions.addAll(Arrays.asList((String[]) reflectionHelper.getField(msg.getClass(), "a").get(msg)));
												newCompletions.removeIf(currentCompletion -> (Bukkit.getOnlinePlayers().stream().filter(currentPlayer -> currentPlayer.getName().equalsIgnoreCase(currentCompletion)).count() != 0));
												newCompletions.addAll(StringUtil.copyPartialMatches(textToComplete, playerNames, new ArrayList<>()));
												
												//Sort completions alphabetically
												Collections.sort(newCompletions);
												
												//Replace completions
												reflectionHelper.setField(msg, "a", newCompletions.toArray(new String[0]));
											}
											
											super.write(ctx, msg, promise);
										} else if (msg.getClass().getSimpleName().equals("PacketPlayOutChat") && setupYamlFile.getConfiguration().getBoolean("OverwriteMessagePackets")) {
											//Get chat message from packet and replace names
											Object editedComponent = replaceNames(reflectionHelper.getField(msg.getClass(), "a").get(msg), true);
											
											//Overwrite chat message
											if(editedComponent != null)
												reflectionHelper.setField(msg, "a", editedComponent);
											
											super.write(ctx, msg, promise);
										} else if(msg.getClass().getSimpleName().equals("PacketPlayOutScoreboardObjective")) {
											String name = (String) reflectionHelper.getField(msg.getClass(), "b").get(msg);
											
											for (NickedPlayerData currentNickedPlayerData : utils.getNickedPlayers().values()) {
												if(name.contains(currentNickedPlayerData.getRealName())) {
													name = name.replace(currentNickedPlayerData.getRealName(), currentNickedPlayerData.getNickName());
													break;
												}
											}
											
											reflectionHelper.setField(msg, "b", name);
											
											super.write(ctx, msg, promise);
										} else if(msg.getClass().getSimpleName().equals("PacketPlayOutScoreboardScore")) {
											//Replace name
											String name = (String) reflectionHelper.getField(msg.getClass(), "a").get(msg);
											
											for (NickedPlayerData currentNickedPlayerData : utils.getNickedPlayers().values()) {
												if(name.contains(currentNickedPlayerData.getRealName())) {
													name = name.replace(currentNickedPlayerData.getRealName(), currentNickedPlayerData.getNickName());
													break;
												}
											}
											
											reflectionHelper.setField(msg, "a", name);
											
											super.write(ctx, msg, promise);
										} else if(msg.getClass().getSimpleName().equals("PacketPlayOutScoreboardTeam")) {
											if(!(player.hasPermission("eazynick.bypass") && setupYamlFile.getConfiguration().getBoolean("EnableBypassPermission"))) {
												//Replace names
												ArrayList<String> contents = new ArrayList<>((List<String>) reflectionHelper.getField(msg.getClass(), "e").get(msg));
												
												for (NickedPlayerData currentNickedPlayerData : utils.getNickedPlayers().values()) {
													for (String currentName : new ArrayList<>(contents)) {
														if(currentName.contains(currentNickedPlayerData.getRealName())) {
															contents.remove(currentName);
															
															if(!(contents.contains(currentNickedPlayerData.getNickName())))
																contents.add(currentName.replace(currentNickedPlayerData.getRealName(), currentNickedPlayerData.getNickName()));
															
															break;
														}
													}
												}
												
												reflectionHelper.setField(msg, "e", contents);
											}
											
											super.write(ctx, msg, promise);
										} else
											super.write(ctx, msg, promise);
									} catch (Exception ex) {
										ex.printStackTrace();
										
										try {
											super.write(ctx, msg, promise);
										} catch (Exception ex2) {
											utils.sendConsole("§4Could not write packet to network connection§8: §e" + ex2.getMessage());
										}
									}
								} else {
									try {
										if (msg.getClass().getSimpleName().equals("PacketStatusOutServerInfo")) {
											Object serverPing = reflectionHelper.getField(msg.getClass(), "b").get(msg);
											Object serverPingPlayerSample = reflectionHelper.getField(serverPing.getClass(), "b").get(serverPing);
											GameProfile[] gameProfileArray = (GameProfile[]) reflectionHelper.getField(serverPingPlayerSample.getClass(), "c").get(serverPingPlayerSample);
											
											for (int i = 0; i < gameProfileArray.length; i++) {
												UUID uuid = gameProfileArray[i].getId();
												
												if(utils.getNickedPlayers().containsKey(uuid))
													//Replace game profile with fake game profile (nicked player profile)
													gameProfileArray[i] = (GameProfile) utils.getNickedPlayers().get(uuid).getFakeGameProfile(false);
											}
											
											//Replace game profiles in ServerPingPlayerSample
											reflectionHelper.setField(serverPingPlayerSample, "c", gameProfileArray);
											
											super.write(ctx, msg, promise);
										} else
											super.write(ctx, msg, promise);
									} catch (Exception ex) {
										utils.sendConsole("§4Could not write packet to network connection (while logging in or pinging server)§8: §e" + ex.getMessage());
									}
								}
							}
							
							@Override
						    public void close(ChannelHandlerContext ctx, ChannelPromise future) throws Exception {
						    }
							
						});
					} catch (Exception ex) {
						//Hide "Duplicate handler" errors
						if(!(ex.getMessage().contains("Duplicate handler")))
							ex.printStackTrace();
					}
					
					break;
				}
			}
		} catch (Exception ex) {
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
	
	private Object replaceNames(Object iChatBaseComponent, boolean isChatPacket) {
		Utils utils = eazyNick.getUtils();

		Object editedComponent = iChatBaseComponent;
		
		try {
			if(iChatBaseComponent != null) {
				//Collect raw text from message
				Class<?> iChatBaseComponentClass = reflectionHelper.getNMSClass("IChatBaseComponent");
				
				String fullText = "";
				Method method = iChatBaseComponentClass.getDeclaredMethod("a");
				method.setAccessible(true);
				
				for (Object partlyIChatBaseComponent : ((List<Object>) method.invoke(iChatBaseComponent))) {
					if(partlyIChatBaseComponent.getClass().getSimpleName().equals("ChatComponentText")) {
						String[] json = serialize(partlyIChatBaseComponent).replace("\"", "").replace("{", "").replace("}", "").split(",");
						
						for (String s : json) {
							if(s.startsWith("text:"))
								fullText += s.replaceFirst("text:", "");
						}
					}
				}
				
				//Replace real names with nicknames
				if((!(fullText.contains(ChatColor.stripColor(utils.getLastChatMessage())) || !(isChatPacket)) || fullText.startsWith(ChatColor.stripColor(utils.getPrefix())))) {
					String json = serialize(iChatBaseComponent);
					
					for (NickedPlayerData nickedPlayerData : utils.getNickedPlayers().values()) {
						String name = Bukkit.getPlayer(nickedPlayerData.getUniqueId()).getName();
						
						if(json.contains(name))
							json = json.replace(name, nickedPlayerData.getNickName());
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
			return ((String) reflectionHelper.getNMSClass("ChatSerializer").getMethod("a", reflectionHelper.getNMSClass("IChatBaseComponent")).invoke(null, iChatBaseComponent));
		} catch (Exception ex) {
			return "";
		}
	}
	
	public Object deserialize(String json) {
		try {
			return reflectionHelper.getNMSClass("ChatSerializer").getMethod("a", String.class).invoke(null, json);
		} catch (Exception ex) {
			return "";
		}
	}

	public void unregister() {
		try {
			channels.stream().filter(currentChannel -> ((currentChannel != null) && (currentChannel.pipeline().get(handlerName) != null))).forEach(currentChannel -> currentChannel.pipeline().remove(handlerName));
		} catch (Exception ignore) {
		}
	}
	
}
