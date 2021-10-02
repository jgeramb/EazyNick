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
			boolean is17 = eazyNick.getVersion().startsWith("1_17");
			
			//Create packet instance
			Constructor<?> constructor = reflectionHelper.getNMSClass(is17 ? "network.protocol.game.PacketPlayOutScoreboardTeam" : "PacketPlayOutScoreboardTeam").getDeclaredConstructor(is17 ? new Class[] { String.class, int.class, Optional.class, Collection.class } : new Class[0]);
			constructor.setAccessible(true);
			
			packet = constructor.newInstance(is17 ? new Object[] { null, 0, null, new ArrayList<>() } : new Object[0]);
			
			//Set packet fields
			if(!(eazyNick.getVersion().equals("1_7_R4") || eazyNick.getVersion().equals("1_8_R1"))) {
				if(eazyNick.getUtils().isVersion13OrLater()) {
					try {
						reflectionHelper.setField(packet, "a", teamName);
						reflectionHelper.setField(packet, "b", getAsIChatBaseComponent(teamName));
						reflectionHelper.setField(packet, "e", "ALWAYS");
						reflectionHelper.setField(packet, "i", 1);
					} catch (Exception ex) {
						if(is17) {
							reflectionHelper.setField(packet, "h", 1);
							reflectionHelper.setField(packet, "i", teamName);
							
							Object scoreboardTeam = reflectionHelper.getNMSClass("world.scores.ScoreboardTeam").getConstructor(reflectionHelper.getNMSClass("world.scores.Scoreboard"), String.class).newInstance(null, teamName);
							reflectionHelper.setField(scoreboardTeam, "e", teamName);
							
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
		
		Bukkit.getOnlinePlayers().forEach(currentPlayer -> {
			try {
				boolean is17 = eazyNick.getVersion().startsWith("1_17");
				
				//Create packet instance
				Constructor<?> constructor = reflectionHelper.getNMSClass(is17 ? "network.protocol.game.PacketPlayOutScoreboardTeam" : "PacketPlayOutScoreboardTeam").getDeclaredConstructor(is17 ? new Class[] { String.class, int.class, Optional.class, Collection.class } : new Class[0]);
				constructor.setAccessible(true);
				
				packet = constructor.newInstance(is17 ? new Object[] { null, 0, null, new ArrayList<>() } : new Object[0]);
				
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
				if(!(eazyNick.getVersion().equals("1_7_R4") || eazyNick.getVersion().equals("1_8_R1"))) {
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
							String colorName = is17 ? "v" : "RESET";
							
							if(prefix.length() > 1) {
								for (int i = prefix.length() - 1; i >= 0; i--) {
									if(i < (prefix.length() - 1)) {
										if(prefix.charAt(i) == '§') {
											char c = prefix.charAt(i + 1);
											
											if((c != 'k') && (c != 'l') && (c != 'm') && (c != 'n') && (c != 'o')) {
												switch (c) {
													case '0':
														colorName = is17 ? "a" : "BLACK";
														break;
													case '1':
														colorName = is17 ? "b" : "DARK_BLUE";
														break;
													case '2':
														colorName = is17 ? "c" : "DARK_GREEN";
														break;
													case '3':
														colorName = is17 ? "d" : "DARK_AQUA";
														break;
													case '4':
														colorName = is17 ? "e" : "DARK_RED";
														break;
													case '5':
														colorName = is17 ? "f" : "DARK_PURPLE";
														break;
													case '6':
														colorName = is17 ? "g" : "GOLD";
														break;
													case '7':
														colorName = is17 ? "h" : "GRAY";
														break;
													case '8':
														colorName = is17 ? "i" : "DARK_GRAY";
														break;
													case '9':
														colorName = is17 ? "j" : "BLUE";
														break;
													case 'a':
														colorName = is17 ? "k" : "GREEN";
														break;
													case 'b':
														colorName = is17 ? "l" : "AQUA";
														break;
													case 'c':
														colorName = is17 ? "m" : "RED";
														break;
													case 'd':
														colorName = is17 ? "n" : "LIGHT_PURPLE";
														break;
													case 'e':
														colorName = is17 ? "o" : "YELLOW";
														break;
													case 'f':
														colorName = is17 ? "p" : "WHITE";
														break;
													case 'r':
														colorName = is17 ? "v" : "RESET";
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
							
							if(is17) {
								reflectionHelper.setField(packet, "h", 0);
								reflectionHelper.setField(packet, "i", teamName);
								reflectionHelper.setField(packet, "j", contents);
								
								Object scoreboardTeam = reflectionHelper.getNMSClass("world.scores.ScoreboardTeam").getConstructor(reflectionHelper.getNMSClass("world.scores.Scoreboard"), String.class).newInstance(null, teamName);
								reflectionHelper.setField(scoreboardTeam, "e", teamName);
								reflectionHelper.setField(scoreboardTeam, "h", getAsIChatBaseComponent(prefix));							
								reflectionHelper.setField(scoreboardTeam, "i", getAsIChatBaseComponent(suffix));							
								reflectionHelper.setField(scoreboardTeam, "j", false);							
								reflectionHelper.setField(scoreboardTeam, "k", false);							
								reflectionHelper.setField(scoreboardTeam, "n", reflectionHelper.getField(reflectionHelper.getNMSClass("EnumChatFormat"), colorName).get(null));
								
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
				}/* else {
					reflectionHelper.setField(packet, "a", teamName);
					reflectionHelper.setField(packet, "b", teamName);
					reflectionHelper.setField(packet, "c", prefixForPlayer);
					reflectionHelper.setField(packet, "d", suffixForPlayer);
					reflectionHelper.setField(packet, "e", contents);
					reflectionHelper.setField(packet, "f", 0);
					reflectionHelper.setField(packet, "g", 0);
				}*/
			
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
		try {
			//Create IChatBaseComponent from String using ChatSerializer
			return reflectionHelper.getNMSClass(eazyNick.getVersion().startsWith("1_17") ? "network.chat.IChatBaseComponent" : "IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + text + "\"}");
		} catch (Exception e) {
			return null;
		}
	}
	
	private void sendPacket(Player p, Object packet) {
		boolean is17 = eazyNick.getVersion().startsWith("1_17");
		
		try {
			//Send packet to player connection
			Object playerHandle = p.getClass().getMethod("getHandle", new Class[0]).invoke(p, new Object[0]);
			Object playerConnection = playerHandle.getClass().getField(is17 ? "b" : "playerConnection").get(playerHandle);
			playerConnection.getClass().getMethod("sendPacket", new Class[] { reflectionHelper.getNMSClass(is17 ? "network.protocol.Packet" : "Packet") }).invoke(playerConnection, new Object[] { packet });
		} catch (Exception e) {
			e.printStackTrace();
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