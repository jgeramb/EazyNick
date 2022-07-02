package com.justixdev.eazynick.nms;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.utilities.configuration.yaml.SetupYamlFile;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.logging.Level;

public class ScoreboardTeamHandler {

	private final EazyNick eazyNick;
	private final ReflectionHelper reflectionHelper;
	
	private final Player player;
	private final String nickName, realName;
	private final List<Player> receivedPacket;
	private String teamName, prefix, suffix;
	private Object packet;

	public ScoreboardTeamHandler(Player player, String nickName, String realName, String prefix, String suffix, int sortID, String rank) {
		this.eazyNick = EazyNick.getInstance();
		this.reflectionHelper = eazyNick.getReflectionHelper();
		
		this.player = player;
		this.nickName = nickName;
		this.realName = realName;
		this.receivedPacket = new ArrayList<>(Bukkit.getOnlinePlayers());

		this.teamName = sortID + rank.substring(0, 14 - String.valueOf(sortID).length()) + player.getName();
		this.teamName = this.teamName.substring(0, Math.min(teamName.length(), 16));

		this.prefix = (prefix == null) ? "" : prefix;
		this.suffix = (suffix == null) ? "" : suffix;
	}

	public void destroyTeam() {
		try {
			String version = eazyNick.getVersion();
			boolean is1_17 = version.startsWith("1_17"), is1_18 = version.startsWith("1_18"), is1_19 = version.startsWith("1_19");
			
			// Create packet instance
			Constructor<?> constructor = reflectionHelper.getNMSClass(
					(is1_17 || is1_18 || is1_19)
							? "network.protocol.game.PacketPlayOutScoreboardTeam"
							: "PacketPlayOutScoreboardTeam"
			).getDeclaredConstructor(
					(is1_17 || is1_18 || is1_19)
							? new Class[] { String.class, int.class, Optional.class, Collection.class }
							: new Class[0]
			);
			constructor.setAccessible(true);
			
			packet = constructor.newInstance(
					(is1_17 || is1_18 || is1_19)
							? new Object[] { null, 0, null, new ArrayList<>() }
							: new Object[0]
			);
			
			// Set packet fields
			if(!(version.equals("1_7_R4") || version.equals("1_8_R1"))) {
				if(eazyNick.getUtils().isVersion13OrLater()) {
					try {
						reflectionHelper.setField(packet, "a", teamName);
						reflectionHelper.setField(packet, "b", getAsIChatBaseComponent(teamName));
						reflectionHelper.setField(packet, "e", "ALWAYS");
						reflectionHelper.setField(packet, "i", 1);
					} catch (Exception ex) {
						if(is1_17 || is1_18 || is1_19) {
							reflectionHelper.setField(packet, "h", 1);
							reflectionHelper.setField(packet, "i", teamName);
							
							Object scoreboardTeam = reflectionHelper.getNMSClass("world.scores.ScoreboardTeam")
									.getConstructor(
											reflectionHelper.getNMSClass("world.scores.Scoreboard"),
											String.class
									)
									.newInstance(null, teamName);
							reflectionHelper.setField(
									scoreboardTeam,
									(is1_18 || is1_19)
											? "d"
											: "e",
									teamName
							);

							//noinspection OptionalGetWithoutIsPresent
							reflectionHelper.setField(
									packet,
									"k",
									Optional.of(
											reflectionHelper.getSubClass(
													packet.getClass(),
													"b"
											)
													.get()
													.getConstructor(scoreboardTeam.getClass())
													.newInstance(scoreboardTeam)
									)
							);
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
			
			// Send packet to destroy team
			Bukkit.getOnlinePlayers()
					.stream()
					.filter(receivedPacket::contains)
					.forEach(currentPlayer -> sendPacket(currentPlayer, packet));
		} catch (Exception ex) {
			Bukkit.getLogger().log(
					Level.SEVERE,
					"Could not send packet to destroy scoreboard team of "
							+ realName
							+ " ("
							+ nickName
							+ "): "
							+ ex.getMessage()
			);
		}
	}

	public void createTeam() {
		SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
		
		String version = eazyNick.getVersion();
		boolean is1_17 = version.startsWith("1_17"), is1_18 = version.startsWith("1_18"), is1_19 = version.startsWith("1_19");
		
		Bukkit.getOnlinePlayers().forEach(currentPlayer -> {
			try {
				// Create packet instance
				Constructor<?> constructor = reflectionHelper.getNMSClass(
						(is1_17 || is1_18 || is1_19)
								? "network.protocol.game.PacketPlayOutScoreboardTeam"
								: "PacketPlayOutScoreboardTeam"
				).getDeclaredConstructor(
						(is1_17 || is1_18 || is1_19)
								? new Class[] { String.class, int.class, Optional.class, Collection.class }
								: new Class[0]
				);
				constructor.setAccessible(true);
				
				packet = constructor.newInstance(
						(is1_17 || is1_18 || is1_19)
								? new Object[] { null, 0, null, new ArrayList<>() }
								: new Object[0]
				);
				
				// Determine which prefix should be shown
				String prefixForPlayer = prefix;
				String suffixForPlayer = suffix;
				List<String> contents = Arrays.asList(nickName, realName);
				
				if(currentPlayer.hasPermission("eazynick.bypass")
						&& setupYamlFile.getConfiguration().getBoolean("EnableBypassPermission")
						&& setupYamlFile.getConfiguration().getBoolean("BypassFormat.Show")) {
					prefixForPlayer = setupYamlFile.getConfigString(player, "BypassFormat.NameTagPrefix");
					suffixForPlayer = setupYamlFile.getConfigString(player, "BypassFormat.NameTagSuffix");
				}

				// Replace placeholders
				if(eazyNick.getUtils().isPluginInstalled("PlaceholderAPI")) {
					prefixForPlayer = PlaceholderAPI.setPlaceholders(player, prefixForPlayer);
					suffixForPlayer = PlaceholderAPI.setPlaceholders(player, suffixForPlayer);
				}
				
				// Make sure the prefix and suffix are not longer than 16 characters
				prefixForPlayer = prefixForPlayer.substring(0, Math.min(prefixForPlayer.length(), 16));
				suffixForPlayer = suffixForPlayer.substring(0, Math.min(suffixForPlayer.length(), 16));
				
				// Set packet fields
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
							String colorName = (is1_17 || is1_18 || is1_19) ? "v" : "RESET";
							
							if(prefix.length() > 1) {
								for (int i = prefix.length() - 1; i >= 0; i--) {
									if(i < (prefix.length() - 1)) {
										if(prefix.charAt(i) == '§') {
											char c = prefix.charAt(i + 1);
											
											if((c != 'k') && (c != 'l') && (c != 'm') && (c != 'n') && (c != 'o')) {
												switch (c) {
													case '0':
														colorName = (is1_17 || is1_18 || is1_19) ? "a" : "BLACK";
														break;
													case '1':
														colorName = (is1_17 || is1_18 || is1_19) ? "b" : "DARK_BLUE";
														break;
													case '2':
														colorName = (is1_17 || is1_18 || is1_19) ? "c" : "DARK_GREEN";
														break;
													case '3':
														colorName = (is1_17 || is1_18 || is1_19) ? "d" : "DARK_AQUA";
														break;
													case '4':
														colorName = (is1_17 || is1_18 || is1_19) ? "e" : "DARK_RED";
														break;
													case '5':
														colorName = (is1_17 || is1_18 || is1_19) ? "f" : "DARK_PURPLE";
														break;
													case '6':
														colorName = (is1_17 || is1_18 || is1_19) ? "g" : "GOLD";
														break;
													case '7':
														colorName = (is1_17 || is1_18 || is1_19) ? "h" : "GRAY";
														break;
													case '8':
														colorName = (is1_17 || is1_18 || is1_19) ? "i" : "DARK_GRAY";
														break;
													case '9':
														colorName = (is1_17 || is1_18 || is1_19) ? "j" : "BLUE";
														break;
													case 'a':
														colorName = (is1_17 || is1_18 || is1_19) ? "k" : "GREEN";
														break;
													case 'b':
														colorName = (is1_17 || is1_18 || is1_19) ? "l" : "AQUA";
														break;
													case 'c':
														colorName = (is1_17 || is1_18 || is1_19) ? "m" : "RED";
														break;
													case 'd':
														colorName = (is1_17 || is1_18 || is1_19) ? "n" : "LIGHT_PURPLE";
														break;
													case 'e':
														colorName = (is1_17 || is1_18 || is1_19) ? "o" : "YELLOW";
														break;
													case 'f':
														colorName = (is1_17 || is1_18 || is1_19) ? "p" : "WHITE";
														break;
													case 'r':
														colorName = (is1_17 || is1_18 || is1_19) ? "v" : "RESET";
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
							
							if(is1_17 || is1_18 || is1_19) {
								reflectionHelper.setField(packet, "h", 0);
								reflectionHelper.setField(packet, "i", teamName);
								reflectionHelper.setField(packet, "j", contents);
								
								Object scoreboardTeam = reflectionHelper.getNMSClass("world.scores.ScoreboardTeam")
										.getConstructor(
												reflectionHelper.getNMSClass("world.scores.Scoreboard"),
												String.class
										).newInstance(null, teamName);
								reflectionHelper.setField(scoreboardTeam, (is1_18 || is1_19) ? "d" : "e", teamName);
								reflectionHelper.setField(scoreboardTeam, (is1_18 || is1_19) ? "g" : "h", getAsIChatBaseComponent(prefix));
								reflectionHelper.setField(scoreboardTeam, (is1_18 || is1_19) ? "h" : "i", getAsIChatBaseComponent(suffix));
								reflectionHelper.setField(scoreboardTeam, (is1_18 || is1_19) ? "i" : "j", false);
								reflectionHelper.setField(scoreboardTeam, (is1_18 || is1_19) ? "j" : "k", false);
								reflectionHelper.setField(
										scoreboardTeam,
										(is1_18 || is1_19)
												? "m"
												: "n",
										reflectionHelper.getField(reflectionHelper.getNMSClass("EnumChatFormat"), colorName).get(null)
								);

								//noinspection OptionalGetWithoutIsPresent
								reflectionHelper.setField(
										packet,
										"k",
										Optional.of(reflectionHelper.getSubClass(packet.getClass(), "b").get()
												.getConstructor(scoreboardTeam.getClass())
												.newInstance(scoreboardTeam))
								);
							} else {
								reflectionHelper.setField(packet, "a", teamName);
								reflectionHelper.setField(packet, "b", getAsIChatBaseComponent(teamName));
								reflectionHelper.setField(packet, "c", getAsIChatBaseComponent(prefixForPlayer));
								reflectionHelper.setField(packet, "d", getAsIChatBaseComponent(suffixForPlayer));
								reflectionHelper.setField(packet, "e", "ALWAYS");
								reflectionHelper.setField(
										packet,
										"g",
										reflectionHelper.getField(
												reflectionHelper.getNMSClass("EnumChatFormat"),
												colorName
										).get(null)
								);
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
			
				// Send packet to create team
				sendPacket(currentPlayer, packet);

				if(!(receivedPacket.contains(currentPlayer)))
					receivedPacket.add(currentPlayer);
			} catch (Exception ex) {
				Bukkit.getLogger().log(
						Level.SEVERE,
						"Could not send packet to create scoreboard team of "
								+ realName
								+ " ("
								+ nickName
								+ "): "
								+ ex.getMessage()
				);
			}
		});
	}
	
	private Object getAsIChatBaseComponent(String text) {
		String version = eazyNick.getVersion();
		
		try {
			// Create IChatBaseComponent from String using ChatSerializer
			return reflectionHelper.getNMSClass(
					(version.startsWith("1_17") || version.startsWith("1_18") || version.startsWith("1_19"))
							? "network.chat.IChatBaseComponent"
							: "IChatBaseComponent"
			)
					.getDeclaredClasses()[0]
					.getMethod((version.startsWith("1_18") || version.startsWith("1_19")) ? "b" : "a", String.class)
					.invoke(null, ComponentSerializer.toString(TextComponent.fromLegacyText(text)));
		} catch (Exception ex) {
			return null;
		}
	}
	
	private void sendPacket(Player player, Object packet) {
		String version = eazyNick.getVersion();
		boolean is1_17 = version.startsWith("1_17"), is1_18 = version.startsWith("1_18"), is1_19 = version.startsWith("1_19");
		
		try {
			// Send packet to player
			Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
			Object playerConnection = entityPlayer.getClass().getField(
					(is1_17 || is1_18 || is1_19)
							? "b"
							: "playerConnection"
			).get(entityPlayer);
			playerConnection.getClass().getMethod(
					(is1_18 || is1_19)
							? "a"
							: "sendPacket",
					reflectionHelper.getNMSClass(
							(is1_17 || is1_18 || is1_19)
									? "network.protocol.Packet"
									: "Packet"
					)).invoke(playerConnection, packet);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public String getNickName() {
		return nickName;
	}
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

}