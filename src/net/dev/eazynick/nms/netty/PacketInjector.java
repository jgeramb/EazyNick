package net.dev.eazynick.nms.netty;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.NickedPlayerData;
import net.dev.eazynick.nms.ReflectionHelper;
import net.dev.eazynick.utilities.Utils;
import net.dev.eazynick.utilities.configuration.yaml.SetupYamlFile;

import io.netty.channel.*;

public class PacketInjector {

	public void init() {
		EazyNick eazyNick = EazyNick.getInstance();
		ReflectionHelper reflectionHelper = eazyNick.getReflectUtils();
		Utils utils = eazyNick.getUtils();
		SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
		
		String version = eazyNick.getVersion();
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
					try {
						channel.pipeline().remove("nick_handler");
					} catch (NoSuchElementException ignored) {
					}
					
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
										UUID uuid = (UUID) reflectionHelper.getField(msg.getClass(), "b").get(msg);
										
										if(utils.getNickedPlayers().containsKey(uuid))
											reflectionHelper.setField(msg, "b", setupYamlFile.getConfiguration().getBoolean("Settings.ChangeOptions.UUID") ? utils.getNickedPlayers().get(uuid).getSpoofedUniqueId() : uuid);
										
										super.write(ctx, msg, promise);
									} else if(msg.getClass().getSimpleName().equals("PacketPlayOutPlayerInfo")) {
										Object b = reflectionHelper.getField(msg.getClass(), "b").get(msg);
										
										if(b != null) {
											for (Object playerInfoData : ((ArrayList<Object>) b)) {
												UUID uuid = ((GameProfile) reflectionHelper.getField(playerInfoData.getClass(), "d").get(playerInfoData)).getId();

												if(utils.getNickedPlayers().containsKey(uuid))
													reflectionHelper.setField(playerInfoData, "d", utils.getNickedPlayers().get(uuid).getFakeGameProfile(uuid.equals(player.getUniqueId()) ? false : setupYamlFile.getConfiguration().getBoolean("Settings.ChangeOptions.UUID")));
											}
										}
										
										super.write(ctx, msg, promise);
									} else if(msg.getClass().getSimpleName().equals("PacketPlayOutTabComplete") && (version.startsWith("1_8") || version.startsWith("1_9"))) {
										String[] completions = (String[]) reflectionHelper.getField(msg.getClass(), "a").get(msg);
										
										for (String completion : completions) {
											if(Bukkit.getOnlinePlayers().stream().filter(currentPlayer -> currentPlayer.getName().equalsIgnoreCase(completion)).count() != 0) {
												if(completions.length == Bukkit.getOnlinePlayers().size()) {
													ArrayList<String> playerCompletions = new ArrayList<>();
													
													Bukkit.getOnlinePlayers().forEach(currentPlayer -> playerCompletions.add(utils.getNickedPlayers().containsKey(currentPlayer.getUniqueId()) ? utils.getNickedPlayers().get(currentPlayer.getUniqueId()).getNickName() : currentPlayer.getName()));
													
													reflectionHelper.setField(msg, "a", playerCompletions.toArray(new String[0]));
												} else
													reflectionHelper.setField(msg, "a", new String[0]);
												
												break;
											}
										}
										
										super.write(ctx, msg, promise);
									} else if (msg.getClass().getSimpleName().equals("PacketPlayOutChat") && setupYamlFile.getConfiguration().getBoolean("OverwriteMessagePackets"))
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
												gameProfileArray[i] = (GameProfile) utils.getNickedPlayers().get(uuid).getFakeGameProfile(setupYamlFile.getConfiguration().getBoolean("Settings.ChangeOptions.UUID"));
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
		
		String version = eazyNick.getVersion();
		String lastChatMessage = ChatColor.stripColor(utils.getLastChatMessage());
		String prefix = ChatColor.stripColor(utils.getPrefix());
		
		try {
			Field field = packet.getClass().getDeclaredField("a");
			field.setAccessible(true);

			Object iChatBaseComponent = field.get(packet);
			Object editedComponent = null;
			
			if(iChatBaseComponent != null) {
				Class<?> iChatBaseComponentClass = reflectionHelper.getNMSClass("IChatBaseComponent");
				Class<?> chatSerializer = version.equals("1_8_R1") ? reflectionHelper.getNMSClass("ChatSerializer") : iChatBaseComponentClass.getDeclaredClasses()[0];
				
				String fullText = "";
				Method method = iChatBaseComponentClass.getDeclaredMethod((Bukkit.getVersion().contains("1.14.4") || version.startsWith("1_15") || version.startsWith("1_16")) ? "getSiblings" : "a");
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
						Player targetPlayer = Bukkit.getPlayer(nickedPlayerData.getUniqueId());
						
						if(targetPlayer != null) {
							String name = targetPlayer.getName();
							
							if(json.contains(name))
								json = json.replaceAll(name, nickedPlayerData.getNickName());
						}
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
