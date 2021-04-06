package net.dev.eazynick.nms.netty;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.NickedPlayerData;
import net.dev.eazynick.nms.ReflectionHelper;
import net.dev.eazynick.utilities.Utils;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.io.netty.channel.*;

public class PacketInjector_1_7 {

	public void init() {
		EazyNick eazyNick = EazyNick.getInstance();
		ReflectionHelper reflectionHelper = eazyNick.getReflectUtils();
		Utils utils = eazyNick.getUtils();
		
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
					if(channel.pipeline().context("nick_handler") != null)
						channel.pipeline().remove("nick_handler");
				
					channel.pipeline().addBefore("packet_handler", "nick_handler", new ChannelDuplexHandler() {
						
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
										
										if(utils.getNickedPlayers().containsKey(uuid))
											reflectionHelper.setField(msg, "b", utils.getNickedPlayers().get(uuid).getFakeGameProfile(false));
										
										super.write(ctx, msg, promise);
									} else if(msg.getClass().getSimpleName().equals("PacketPlayOutPlayerInfo")) {
										Object playerObject = reflectionHelper.getField(msg.getClass(), "player").get(msg);
										
										if(playerObject != null) {
											UUID uuid = ((GameProfile) playerObject).getId();
											
											if(utils.getNickedPlayers().containsKey(uuid)) {
												NickedPlayerData nickedPlayerData = utils.getNickedPlayers().get(uuid);
												
												reflectionHelper.setField(msg, "player", nickedPlayerData.getFakeGameProfile(false));
												reflectionHelper.setField(msg, "username", nickedPlayerData.getNickName());
											}
										}
										
										super.write(ctx, msg, promise);
									} else if(msg.getClass().getSimpleName().equals("PacketPlayOutTabComplete")) {
										List<String> tabCompletions = new ArrayList<>();
										
										for(Player currentPlayer : Bukkit.getOnlinePlayers())
											tabCompletions.add(utils.getNickedPlayers().containsKey(currentPlayer.getUniqueId()) ? utils.getNickedPlayers().get(currentPlayer.getUniqueId()).getNickName() : currentPlayer.getName());
										
										String[] suggestions = (String[]) reflectionHelper.getField(msg.getClass(), "a").get(msg);
										
										if(suggestions.length > 1) {
											String text = suggestions[0], text2 = suggestions[1];
											int range = 0;
											
											for (int i = 0; i < text.length(); i++) {
												if(text.charAt(i) != text2.charAt(i))
													break;
												else
													range++;
											}
											
											String similarText = text.substring(0, range);
											
											tabCompletions.removeIf(tabCompletion -> !(StringUtil.copyPartialMatches(similarText, tabCompletions, new ArrayList<String>()).contains(tabCompletion)));
											Collections.sort(tabCompletions);
										
											reflectionHelper.setField(msg, "a", tabCompletions.toArray(new String[0]));
										} else if(suggestions.length > 0) {
											for(NickedPlayerData nickedPlayerData : utils.getNickedPlayers().values()) {
												if(suggestions[0].equals(nickedPlayerData.getRealName()))
													reflectionHelper.setField(msg, "a", new String[] { nickedPlayerData.getNickName() });
											}
										}
										
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
		ReflectionHelper reflectionHelper = eazyNick.getReflectUtils();
		
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
	
}
