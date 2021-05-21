package net.dev.eazynick.nms;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.utilities.configuration.yaml.SetupYamlFile;

import me.clip.placeholderapi.PlaceholderAPI;

public class ScoreboardTeamHandler {

	private EazyNick eazyNick;

	private Player player;
	private String teamName, nickName, realName, prefix, suffix;
	private Object packet;
	private ArrayList<Player> receivedPacket;
	
	public ScoreboardTeamHandler(Player player, String nickName, String realName, String prefix, String suffix, int sortID, String rank) {
		this.eazyNick = EazyNick.getInstance();
		this.player = player;
		this.nickName = nickName;
		this.realName = realName;
		this.prefix = prefix;
		this.suffix = suffix;
		this.teamName = sortID + rank + player.getUniqueId().toString().substring(0, 14);
		this.receivedPacket = new ArrayList<>();
		
		if(this.teamName.length() > 16)
			this.teamName = this.teamName.substring(0, 16);
		
		if(this.prefix == null)
			this.prefix = "";
		
		if(this.suffix == null)
			this.suffix = "";
		
		if(this.prefix.length() > 16)
			this.prefix = this.prefix.substring(0, 16);
		
		if(this.suffix.length() > 16)
			this.suffix = this.suffix.substring(0, 16);
	}
	
	public void destroyTeam() {
		ReflectionHelper reflectionHelper = eazyNick.getReflectUtils();
		
		try {
			packet = reflectionHelper.getNMSClass("PacketPlayOutScoreboardTeam").getConstructor().newInstance();
			
			if(!(eazyNick.getVersion().equalsIgnoreCase("1_7_R4"))) {
				if(eazyNick.getUtils().isNewVersion()) {
					try {
						reflectionHelper.setField(packet, "a", teamName);
						reflectionHelper.setField(packet, "b", getAsIChatBaseComponent(teamName));
						reflectionHelper.setField(packet, "e", "ALWAYS");
						reflectionHelper.setField(packet, "i", 1);
					} catch (Exception ex) {
						reflectionHelper.setField(packet, "a", teamName);
						reflectionHelper.setField(packet, "b", getAsIChatBaseComponent(teamName));
						reflectionHelper.setField(packet, "e", "ALWAYS");
						reflectionHelper.setField(packet, "j", 1);
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
			} else {
				try {
					reflectionHelper.setField(packet, "a", teamName);
					reflectionHelper.setField(packet, "f", 1);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			
			Bukkit.getOnlinePlayers().stream().filter(receivedPacket::contains).forEach(currentPlayer -> sendPacket(currentPlayer, packet));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void createTeam() {
		SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
		ReflectionHelper reflectionHelper = eazyNick.getReflectUtils();
		
		Bukkit.getOnlinePlayers().forEach(currentPlayer -> {
			try {
				packet = reflectionHelper.getNMSClass("PacketPlayOutScoreboardTeam").getConstructor().newInstance();
				
				String prefixForPlayer = prefix;
				String suffixForPlayer = suffix;
				List<String> contents = Arrays.asList(nickName, realName);
				
				if(currentPlayer.hasPermission("nick.bypass") && setupYamlFile.getConfiguration().getBoolean("EnableBypassPermission") && setupYamlFile.getConfiguration().getBoolean("BypassFormat.Show")) {
					prefixForPlayer = setupYamlFile.getConfigString(player, "BypassFormat.NameTagPrefix");
					suffixForPlayer = setupYamlFile.getConfigString(player, "BypassFormat.NameTagSuffix");
				}
				
				if(eazyNick.getUtils().placeholderAPIStatus()) {
					prefixForPlayer = PlaceholderAPI.setPlaceholders(player, prefixForPlayer);
					suffixForPlayer = PlaceholderAPI.setPlaceholders(player, suffixForPlayer);
				}
				
				if(!(eazyNick.getVersion().equalsIgnoreCase("1_7_R4"))) {
					if(eazyNick.getUtils().isNewVersion()) {
						try {
							reflectionHelper.setField(packet, "a", teamName);
							reflectionHelper.setField(packet, "b", getAsIChatBaseComponent(teamName));
							reflectionHelper.setField(packet, "c", getAsIChatBaseComponent(prefixForPlayer));
							reflectionHelper.setField(packet, "d", getAsIChatBaseComponent(suffixForPlayer));
							reflectionHelper.setField(packet, "e", "ALWAYS");
							reflectionHelper.setField(packet, "g", contents);
							reflectionHelper.setField(packet, "i", 0);
						} catch (Exception ex) {
							reflectionHelper.setField(packet, "a", teamName);
							reflectionHelper.setField(packet, "b", getAsIChatBaseComponent(teamName));
							reflectionHelper.setField(packet, "c", getAsIChatBaseComponent(prefixForPlayer));
							reflectionHelper.setField(packet, "d", getAsIChatBaseComponent(suffixForPlayer));
							reflectionHelper.setField(packet, "e", "ALWAYS");
							
							String colorName = "RESET";
							
							if(prefixForPlayer.length() > 1) {
								for (int i = prefixForPlayer.length() - 1; i >= 0; i--) {
									if(i < (prefixForPlayer.length() - 1)) {
										if(prefixForPlayer.charAt(i) == '§') {
											char c = prefixForPlayer.charAt(i + 1);
											
											if((c != 'k') && (c != 'l') && (c != 'm') && (c != 'n') && (c != 'o')) {
												switch (c) {
													case '0':
														colorName = "BLACK";
														break;
													case '1':
														colorName = "DARK_BLUE";
														break;
													case '2':
														colorName = "DARK_GREEN";
														break;
													case '3':
														colorName = "DARK_AQUA";
														break;
													case '4':
														colorName = "DARK_RED";
														break;
													case '5':
														colorName = "DARK_PURPLE";
														break;
													case '6':
														colorName = "GOLD";
														break;
													case '7':
														colorName = "GRAY";
														break;
													case '8':
														colorName = "DARK_GRAY";
														break;
													case '9':
														colorName = "BLUE";
														break;
													case 'a':
														colorName = "GREEN";
														break;
													case 'b':
														colorName = "AQUA";
														break;
													case 'c':
														colorName = "RED";
														break;
													case 'd':
														colorName = "LIGHT_PURPLE";
														break;
													case 'e':
														colorName = "YELLOW";
														break;
													case 'f':
														colorName = "WHITE";
														break;
													case 'r':
														colorName = "RESET";
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
							
							reflectionHelper.setField(packet, "g", reflectionHelper.getField(reflectionHelper.getNMSClass("EnumChatFormat"), colorName).get(null));
							reflectionHelper.setField(packet, "h", contents);
							reflectionHelper.setField(packet, "j", 0);
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
				} else {
					reflectionHelper.setField(packet, "a", teamName);
					reflectionHelper.setField(packet, "b", teamName);
					reflectionHelper.setField(packet, "c", prefixForPlayer);
					reflectionHelper.setField(packet, "d", suffixForPlayer);
					reflectionHelper.setField(packet, "e", contents);
					reflectionHelper.setField(packet, "f", 0);
					reflectionHelper.setField(packet, "g", 0);
				}
			
				sendPacket(currentPlayer, packet);
				
				receivedPacket.add(currentPlayer);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}
	
	private Object getAsIChatBaseComponent(String txt) {
		ReflectionHelper reflectionHelper = eazyNick.getReflectUtils();
		
		try {
			return reflectionHelper.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(reflectionHelper.getNMSClass("IChatBaseComponent"), "{\"text\":\"" + txt + "\"}");
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
	
	private void sendPacket(Player player, Object packet) {
		try {
			Object playerHandle = player.getClass().getMethod("getHandle", new Class[0]).invoke(player, new Object[0]);
			Object playerConnection = playerHandle.getClass().getField("playerConnection").get(playerHandle);
			playerConnection.getClass().getMethod("sendPacket", new Class[] { eazyNick.getReflectUtils().getNMSClass("Packet") }).invoke(playerConnection, new Object[] { packet });
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