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
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.io.netty.channel.*;

public class OutgoingPacketInjector_1_7 {

	private ArrayList<Channel> channels;
	private String handlerName;
	
	public void init() {
		EazyNick eazyNick = EazyNick.getInstance();
		ReflectionHelper reflectionHelper = eazyNick.getReflectionHelper();
		Utils utils = eazyNick.getUtils();
		
		channels = new ArrayList<>();
		handlerName = eazyNick.getDescription().getName().toLowerCase() + "_handler";
		
		Field field = reflectionHelper.getFirstFieldByType(reflectionHelper.getNMSClass("NetworkManager"), Channel.class);
		field.setAccessible(true);
		
		try {
			Object craftServer = Bukkit.getServer();
			Field dedicatedServer = reflectionHelper.getCraftClass("CraftServer").getDeclaredField("console");
			dedicatedServer.setAccessible(true);
			
			Object minecraftServer = dedicatedServer.get(craftServer);
			
			for(Object manager : Collections.synchronizedList((List<?>) getNetworkManagerList(minecraftServer.getClass().getMethod("getServerConnection").invoke(minecraftServer))).toArray()) {
				Channel channel = (Channel) field.get(manager);
				
				if((channel.pipeline().context("packet_handler") != null)) {
					if (channel.pipeline().get(handlerName) != null)
						channel.pipeline().remove(handlerName);
					
					try {
						channels.add(channel);
						
						channel.pipeline().addBefore("packet_handler", handlerName, new ChannelDuplexHandler() {
							
							@Override
							public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
								InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
								String ip = inetSocketAddress.getAddress().getHostAddress();
								Player player = null;
								
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
													reflectionHelper.setField(msg, "b", utils.getNickedPlayers().get(uuid).getFakeGameProfile(false));
												
												super.write(ctx, msg, promise);
											}
										} else if(msg.getClass().getSimpleName().equals("PacketPlayOutPlayerInfo")) {
											Object playerObject = reflectionHelper.getField(msg.getClass(), "player").get(msg);
											
											if(playerObject != null) {
												UUID uuid = ((GameProfile) playerObject).getId();
												
												if(utils.getSoonNickedPlayers().containsKey(uuid) && utils.getSoonNickedPlayers().get(uuid).equals(NickReason.JOIN) && (reflectionHelper.getField(msg.getClass(), "action").getInt(msg) == 0))
													return;
												
												if(utils.getNickedPlayers().containsKey(uuid)) {
													NickedPlayerData nickedPlayerData = utils.getNickedPlayers().get(uuid);
													
													reflectionHelper.setField(msg, "player", nickedPlayerData.getFakeGameProfile(false));
													reflectionHelper.setField(msg, "username", nickedPlayerData.getNickName());
												}
											}
											
											super.write(ctx, msg, promise);
										} else if(msg.getClass().getSimpleName().equals("PacketPlayOutTabComplete")) {
											String textToComplete = utils.getTextsToComplete().get(player);
											String[] splitTextToComplete = textToComplete.trim().split(" ");
											ArrayList<String> newCompletions = new ArrayList<>(), playerNames = new ArrayList<>();
											
											if(splitTextToComplete.length < 2)
												textToComplete = "";
											else
												textToComplete = splitTextToComplete[splitTextToComplete.length - 1];
											
											Bukkit.getOnlinePlayers().stream().filter(currentPlayer -> !(new NickManager(currentPlayer).isNicked())).forEach(currentPlayer -> playerNames.add(currentPlayer.getName()));
											
											utils.getNickedPlayers().values().forEach(currentNickedPlayerData -> playerNames.add(currentNickedPlayerData.getNickName()));
	
											newCompletions.addAll(Arrays.asList((String[]) reflectionHelper.getField(msg.getClass(), "a").get(msg)));
											newCompletions.removeIf(currentCompletion -> (Bukkit.getOnlinePlayers().stream().filter(currentPlayer -> currentPlayer.getName().equalsIgnoreCase(currentCompletion)).count() != 0));
											newCompletions.addAll(StringUtil.copyPartialMatches(textToComplete, playerNames, new ArrayList<>()));
											
											Collections.sort(newCompletions);
											
											reflectionHelper.setField(msg, "a", newCompletions.toArray(new String[0]));
											
											super.write(ctx, msg, promise);
										} else if (msg.getClass().getSimpleName().equals("PacketPlayOutChat") && eazyNick.getSetupYamlFile().getConfiguration().getBoolean("OverwriteMessagePackets"))
											super.write(ctx, constructChatPacket(msg), promise);
										else
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
													gameProfileArray[i] = (GameProfile) utils.getNickedPlayers().get(uuid).getFakeGameProfile(false);
											}
											
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
	
	public Object constructChatPacket(Object packet) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		ReflectionHelper reflectionHelper = eazyNick.getReflectionHelper();
		
		String lastChatMessage = ChatColor.stripColor(utils.getLastChatMessage());
		String prefix = ChatColor.stripColor(utils.getPrefix());
		
		try {
			Field field = packet.getClass().getDeclaredField("a");
			field.setAccessible(true);

			Object iChatBaseComponent = field.get(packet);
			Object editedComponent = null;
			
			if(iChatBaseComponent != null) {
				Class<?> iChatBaseComponentClass = reflectionHelper.getNMSClass("IChatBaseComponent");
				Class<?> chatSerializer = reflectionHelper.getNMSClass("ChatSerializer");
				
				String fullText = "";
				Method method = iChatBaseComponentClass.getDeclaredMethod("a");
				method.setAccessible(true);
				
				for (Object partlyIChatBaseComponent : ((List<Object>) method.invoke(iChatBaseComponent))) {
					if(partlyIChatBaseComponent.getClass().getSimpleName().equals("ChatComponentText")) {
						String[] json = ((String) chatSerializer.getMethod("a", iChatBaseComponentClass).invoke(null, partlyIChatBaseComponent)).replace("\"", "").replace("{", "").replace("}", "").split(",");
						
						for (String s : json) {
							if(s.startsWith("text:"))
								fullText += s.replaceFirst("text:", "");
						}
					}
				}
				
				if(!(fullText.contains(lastChatMessage) || fullText.startsWith(prefix))) {
					String json = (String) chatSerializer.getMethod("a", iChatBaseComponentClass).invoke(null, iChatBaseComponent);
					
					for (NickedPlayerData nickedPlayerData : utils.getNickedPlayers().values()) {
						String name = Bukkit.getPlayer(nickedPlayerData.getUniqueId()).getName();
						
						if(json.contains(name))
							json = json.replaceAll(name, nickedPlayerData.getNickName());
					}
					
					editedComponent = chatSerializer.getMethod("a", String.class).invoke(null, json);
				}
			}
				
			if(editedComponent != null)
				reflectionHelper.setField(packet, "a", editedComponent);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return packet;
	}
	
	public void unregister() {
		channels.stream().filter(currentChannel -> ((currentChannel != null) && (currentChannel.pipeline().get(handlerName) != null))).forEach(currentChannel -> currentChannel.pipeline().remove(handlerName));
	}
	
}
