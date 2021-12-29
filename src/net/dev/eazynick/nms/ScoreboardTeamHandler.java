package net.dev.eazynick.nms;

import java.lang.reflect.Constructor;
import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.utilities.configuration.yaml.SetupYamlFile;

import me.clip.placeholderapi.PlaceholderAPI;

public class ScoreboardTeamHandler {

	private EazyNick eazyNick;
	private ReflectionHelper reflectionHelper;
	
	private Player player;
	private String teamName, nickName, realName, prefix, suffix;
	private Object packet;
	private ArrayList<Player> receivedPacket;
	
	public ScoreboardTeamHandler(Player player, String nickName, String realName, String prefix, String suffix, int sortID, String rank) {
		this.eazyNick = EazyNick.getInstance();
		this.reflectionHelper = eazyNick.getReflectionHelper();
		
		this.player = player;
		this.nickName = nickName;
		this.realName = realName;
		this.prefix = prefix;
		this.suffix = suffix;
		this.teamName = sortID + rank + player.getUniqueId().toString().substring(0, 14);
		this.receivedPacket = new ArrayList<>(Bukkit.getOnlinePlayers());
		
		if(this.teamName.length() > 16)
			this.teamName = this.teamName.substring(0, 16);
		
		if(this.prefix == null)
			this.prefix = "";
		
		if(this.suffix == null)
			this.suffix = "";
	}
	
	public void destroyTeam() {
		destroyTeam(false);
	}
	
	private void destroyTeam(boolean skipFilter) {
		try {
			String version = eazyNick.getVersion();
			boolean is17 = version.startsWith("1_17"), is18 = version.startsWith("1_18");
			
			//Create packet instance
			Constructor<?> constructor = reflectionHelper.getNMSClass((is17 || is18) ? "network.protocol.game.PacketPlayOutScoreboardTeam" : "PacketPlayOutScoreboardTeam").getDeclaredConstructor((is17 || is18) ? new Class[] { String.class, int.class, Optional.class, Collection.class } : new Class[0]);
			constructor.setAccessible(true);
			
			packet = constructor.newInstance((is17 || is18) ? new Object[] { null, 0, null, new ArrayList<>() } : new Object[0]);
			
			//Set packet fields
			if(!(version.equals("1_7_R4") || version.equals("1_8_R1"))) {
				if(eazyNick.getUtils().isVersion13OrLater()) {
					try {
						reflectionHelper.setField(packet, "a", teamName);
						reflectionHelper.setField(packet, "b", getAsIChatBaseComponent(teamName));
						reflectionHelper.setField(packet, "e", "ALWAYS");
						reflectionHelper.setField(packet, "i", 1);
					} catch (Exception ex) {
						if(is17 || is18) {
							reflectionHelper.setField(packet, "h", 1);
							reflectionHelper.setField(packet, "i", teamName);
							
							Object scoreboardTeam = reflectionHelper.getNMSClass("world.scores.ScoreboardTeam").getConstructor(reflectionHelper.getNMSClass("world.scores.Scoreboard"), String.class).newInstance(null, teamName);
							reflectionHelper.setField(scoreboardTeam, is18 ? "d" : "e", teamName);
							
							reflectionHelper.setField(packet, "k", Optional.of(reflectionHelper.getSubClass(packet.getClass(), "b").getConstructor(scoreboardTeam.getClass()).newInstance(scoreboardTeam)));
						} else {
							reflectionHelper.setField(packet, "a", teamName);
							reflectionHelper.setField(packet, "b", getAsIChatBaseComponent(teamName));
							reflectionHelper.setField(packet, "e", "ALWAYS");
							reflectionHelper.setField(packet, "j", 1);
						}
					}
				} else {
					try {
						reflectionHelper.setField(packet, "a", teamName);
						reflectionHelper.setField(packet, "b", teamName);
						reflectionHelper.setField(packet, "e", "ALWAYS");
						reflectionHelper.setField(packet, "h", 1);
					} catch (Exception ex) {
						reflectionHelper.setField(packet, "a", teamName);
						reflectionHelper.setField(packet, "b", teamName);
						reflectionHelper.setField(packet, "e", "ALWAYS");
						reflectionHelper.setField(packet, "i", 1);
					}
				}
			}/* else {
				try {
					reflectionHelper.setField(packet, "a", teamName);
					reflectionHelper.setField(packet, "f", 1);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}*/
			
			//Send packet to destroy team
			if(skipFilter)
				Bukkit.getOnlinePlayers().stream().forEach(currentPlayer -> sendPacket(currentPlayer, packet));
			else
				Bukkit.getOnlinePlayers().stream().filter(receivedPacket::contains).forEach(currentPlayer -> sendPacket(currentPlayer, packet));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void createTeam() {
		SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
		
		String version = eazyNick.getVersion();
		boolean is17 = version.startsWith("1_17"), is18 = version.startsWith("1_18");
		
		Bukkit.getOnlinePlayers().forEach(currentPlayer -> {
			try {
				//Create packet instance
				Constructor<?> constructor = reflectionHelper.getNMSClass((is17 || is18) ? "network.protocol.game.PacketPlayOutScoreboardTeam" : "PacketPlayOutScoreboardTeam").getDeclaredConstructor((is17 || is18) ? new Class[] { String.class, int.class, Optional.class, Collection.class } : new Class[0]);
				constructor.setAccessible(true);
				
				packet = constructor.newInstance((is17 || is18) ? new Object[] { null, 0, null, new ArrayList<>() } : new Object[0]);
				
				//Determine which prefix should be shown
				String prefixForPlayer = prefix;
				String suffixForPlayer = suffix;
				List<String> contents = Arrays.asList(nickName, realName);
				
				if(currentPlayer.hasPermission("eazynick.bypass") && setupYamlFile.getConfiguration().getBoolean("EnableBypassPermission") && setupYamlFile.getConfiguration().getBoolean("BypassFormat.Show")) {
					prefixForPlayer = setupYamlFile.getConfigString(player, "BypassFormat.NameTagPrefix");
					suffixForPlayer = setupYamlFile.getConfigString(player, "BypassFormat.NameTagSuffix");
				}

				//Replace placeholders
				if(eazyNick.getUtils().isPluginInstalled("PlaceholderAPI")) {
					prefixForPlayer = PlaceholderAPI.setPlaceholders(player, prefixForPlayer);
					suffixForPlayer = PlaceholderAPI.setPlaceholders(player, suffixForPlayer);
				}
				
				//Make sure the prefix and suffix are not longer than 16 characters
				if(prefixForPlayer.length() > 16)
					prefixForPlayer = prefixForPlayer.substring(0, 16);
				
				if(suffixForPlayer.length() > 16)
					suffixForPlayer = suffixForPlayer.substring(0, 16);
				
				//Set packet fields
				if(!(version.equals("1_7_R4") || version.equals("1_8_R1"))) {
					if(eazyNick.getUtils().isVersion13OrLater()) {
						try {
							reflectionHelper.setField(packet, "a", teamName);
							reflectionHelper.setField(packet, "b", getAsIChatBaseComponent(teamName));
							reflectionHelper.setField(packet, "c", getAsIChatBaseComponent(prefixForPlayer));
							reflectionHelper.setField(packet, "d", getAsIChatBaseComponent(suffixForPlayer));
							reflectionHelper.setField(packet, "e", "ALWAYS");
							reflectionHelper.setField(packet, "g", contents);
							reflectionHelper.setField(packet, "i", 0);
						} catch (Exception ex) {
							String colorName = (is17 || is18) ? "v" : "RESET";
							
							if(prefix.length() > 1) {
								for (int i = prefix.length() - 1; i >= 0; i--) {
									if(i < (prefix.length() - 1)) {
										if(prefix.charAt(i) == '§') {
											char c = prefix.charAt(i + 1);
											
											if((c != 'k') && (c != 'l') && (c != 'm') && (c != 'n') && (c != 'o')) {
												switch (c) {
													case '0':
														colorName = (is17 || is18) ? "a" : "BLACK";
														break;
													case '1':
														colorName = (is17 || is18) ? "b" : "DARK_BLUE";
														break;
													case '2':
														colorName = (is17 || is18) ? "c" : "DARK_GREEN";
														break;
													case '3':
														colorName = (is17 || is18) ? "d" : "DARK_AQUA";
														break;
													case '4':
														colorName = (is17 || is18) ? "e" : "DARK_RED";
														break;
													case '5':
														colorName = (is17 || is18) ? "f" : "DARK_PURPLE";
														break;
													case '6':
														colorName = (is17 || is18) ? "g" : "GOLD";
														break;
													case '7':
														colorName = (is17 || is18) ? "h" : "GRAY";
														break;
													case '8':
														colorName = (is17 || is18) ? "i" : "DARK_GRAY";
														break;
													case '9':
														colorName = (is17 || is18) ? "j" : "BLUE";
														break;
													case 'a':
														colorName = (is17 || is18) ? "k" : "GREEN";
														break;
													case 'b':
														colorName = (is17 || is18) ? "l" : "AQUA";
														break;
													case 'c':
														colorName = (is17 || is18) ? "m" : "RED";
														break;
													case 'd':
														colorName = (is17 || is18) ? "n" : "LIGHT_PURPLE";
														break;
													case 'e':
														colorName = (is17 || is18) ? "o" : "YELLOW";
														break;
													case 'f':
														colorName = (is17 || is18) ? "p" : "WHITE";
														break;
													case 'r':
														colorName = (is17 || is18) ? "v" : "RESET";
														break;
													default:
														break;
												}
												
												break;
											}
										}
									}
								}
							}
							
							if(is17 || is18) {
								reflectionHelper.setField(packet, "h", 0);
								reflectionHelper.setField(packet, "i", teamName);
								reflectionHelper.setField(packet, "j", contents);
								
								Object scoreboardTeam = reflectionHelper.getNMSClass("world.scores.ScoreboardTeam").getConstructor(reflectionHelper.getNMSClass("world.scores.Scoreboard"), String.class).newInstance(null, teamName);
								reflectionHelper.setField(scoreboardTeam, is18 ? "d" : "e", teamName);
								reflectionHelper.setField(scoreboardTeam, is18 ? "g" : "h", getAsIChatBaseComponent(prefix));							
								reflectionHelper.setField(scoreboardTeam, is18 ? "h" : "i", getAsIChatBaseComponent(suffix));							
								reflectionHelper.setField(scoreboardTeam, is18 ? "i" : "j", false);							
								reflectionHelper.setField(scoreboardTeam, is18 ? "j" : "k", false);							
								reflectionHelper.setField(scoreboardTeam, is18 ? "m" : "n", reflectionHelper.getField(reflectionHelper.getNMSClass("EnumChatFormat"), colorName).get(null));
								
								reflectionHelper.setField(packet, "k", Optional.of(reflectionHelper.getSubClass(packet.getClass(), "b").getConstructor(scoreboardTeam.getClass()).newInstance(scoreboardTeam)));
							} else {
								reflectionHelper.setField(packet, "a", teamName);
								reflectionHelper.setField(packet, "b", getAsIChatBaseComponent(teamName));
								reflectionHelper.setField(packet, "c", getAsIChatBaseComponent(prefixForPlayer));
								reflectionHelper.setField(packet, "d", getAsIChatBaseComponent(suffixForPlayer));
								reflectionHelper.setField(packet, "e", "ALWAYS");
								reflectionHelper.setField(packet, "g", reflectionHelper.getField(reflectionHelper.getNMSClass("EnumChatFormat"), colorName).get(null));
								reflectionHelper.setField(packet, "h", contents);
								reflectionHelper.setField(packet, "j", 0);
							}
						}
					} else {
						try {
							reflectionHelper.setField(packet, "a", teamName);
							reflectionHelper.setField(packet, "b", teamName);
							reflectionHelper.setField(packet, "c", prefixForPlayer);
							reflectionHelper.setField(packet, "d", suffixForPlayer);
							reflectionHelper.setField(packet, "e", "ALWAYS");
							reflectionHelper.setField(packet, "g", contents);
							reflectionHelper.setField(packet, "h", 0);
						} catch (Exception ex) {
							reflectionHelper.setField(packet, "a", teamName);
							reflectionHelper.setField(packet, "b", teamName);
							reflectionHelper.setField(packet, "c", prefixForPlayer);
							reflectionHelper.setField(packet, "d", suffixForPlayer);
							reflectionHelper.setField(packet, "e", "ALWAYS");
							reflectionHelper.setField(packet, "h", contents);
							reflectionHelper.setField(packet, "i", 0);
						}
					}
				}
			
				//Send packet to create team
				sendPacket(currentPlayer, packet);
				
				if(!(receivedPacket.contains(currentPlayer)))
					receivedPacket.add(currentPlayer);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}
	
	private Object getAsIChatBaseComponent(String text) {
		String version = eazyNick.getVersion();
		
		try {
			//Create IChatBaseComponent from String using ChatSerializer
			return reflectionHelper.getNMSClass((version.startsWith("1_17") || version.startsWith("1_18")) ? "network.chat.IChatBaseComponent" : "IChatBaseComponent").getDeclaredClasses()[0].getMethod(version.startsWith("1_18") ? "b" : "a", String.class).invoke(null, "{\"text\":\"" + text + "\"}");
		} catch (Exception e) {
			return null;
		}
	}
	
	private void sendPacket(Player player, Object packet) {
		String version = eazyNick.getVersion();
		boolean is17 = version.startsWith("1_17"), is18 = version.startsWith("1_18");
		
		try {
			//Send packet to player connection
			Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
			Object playerConnection = entityPlayer.getClass().getField((is17 || is18) ? "b" : "playerConnection").get(entityPlayer);
			playerConnection.getClass().getMethod(is18 ? "a" : "sendPacket", reflectionHelper.getNMSClass((is17 || is18) ? "network.protocol.Packet" : "Packet")).invoke(playerConnection, packet);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public String getNickName() {
		return nickName;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public String getSuffix() {
		return suffix;
	}
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

}