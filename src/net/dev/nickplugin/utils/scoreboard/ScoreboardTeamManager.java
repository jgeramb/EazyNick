package net.dev.nickplugin.utils.scoreboard;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.dev.nickplugin.NickPlugin;
import net.dev.nickplugin.api.NickManager;
import net.dev.nickplugin.utils.FileUtils;
import net.dev.nickplugin.utils.ReflectUtils;

public class ScoreboardTeamManager {

	private String teamName;
	
	public Player p;
	public String prefix;
	public String suffix;
	
	private Object packet;
	
	public ScoreboardTeamManager(Player p, String prefix, String suffix) {
		this.p = p;
		this.prefix = prefix;
		this.suffix = suffix;
		this.teamName = new NickManager(p).getRealName();
	}
	
	public void destroyTeam() {
		try {
			packet = ReflectUtils.getNMSClass("PacketPlayOutScoreboardTeam").getConstructor(new Class[0]).newInstance(new Object[0]);
			
			if(!(NickPlugin.version.equalsIgnoreCase("1_7_R4"))) {
				if(NickPlugin.version.startsWith("1_13") || NickPlugin.version.startsWith("1_14") || NickPlugin.version.startsWith("1_15")) {
					try {
						ReflectUtils.setField(packet, "a", teamName);
						ReflectUtils.setField(packet, "b", getAsIChatBaseComponent(teamName));
						ReflectUtils.setField(packet, "e", "ALWAYS");
						ReflectUtils.setField(packet, "i", 1);
					} catch (Exception ex) {
						ReflectUtils.setField(packet, "a", teamName);
						ReflectUtils.setField(packet, "b", getAsIChatBaseComponent(teamName));
						ReflectUtils.setField(packet, "e", "ALWAYS");
						ReflectUtils.setField(packet, "j", 1);
					}
				} else {
					try {
						ReflectUtils.setField(packet, "a", teamName);
						ReflectUtils.setField(packet, "b", teamName);
						ReflectUtils.setField(packet, "e", "ALWAYS");
						ReflectUtils.setField(packet, "h", 1);
					} catch (Exception ex) {
						ReflectUtils.setField(packet, "a", teamName);
						ReflectUtils.setField(packet, "b", teamName);
						ReflectUtils.setField(packet, "e", "ALWAYS");
						ReflectUtils.setField(packet, "i", 1);
					}
				}
			} else {
				try {
					ReflectUtils.setField(packet, "a", teamName);
					ReflectUtils.setField(packet, "b", teamName);
					ReflectUtils.setField(packet, "f", 1);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			
			for(Player t : Bukkit.getOnlinePlayers())
				sendPacket(t, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private Object getAsIChatBaseComponent(String txt) {
		try {
			return ReflectUtils.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(ReflectUtils.getNMSClass("IChatBaseComponent"), "{\"text\":\"" + txt + "\"}");
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public void createTeam() {
		try {
			for(Player t : Bukkit.getOnlinePlayers()) {
				packet = ReflectUtils.getNMSClass("PacketPlayOutScoreboardTeam").getConstructor(new Class[0]).newInstance(new Object[0]);
				
				String prefixForPlayer = prefix;
				String suffixForPlayer = suffix;
				List<String> contents = Arrays.asList(p.getName());
				
				if(t.hasPermission("nick.bypass") && FileUtils.cfg.getBoolean("BypassFormat.Show")) {
					contents = Arrays.asList(new NickManager(p).getRealName());
					prefixForPlayer = FileUtils.getConfigString("BypassFormat.NameTagPrefix");
					suffixForPlayer = FileUtils.getConfigString("BypassFormat.NameTagSuffix");
				}
				
				if(!(NickPlugin.version.equalsIgnoreCase("1_7_R4"))) {
					if(NickPlugin.version.startsWith("1_13") || NickPlugin.version.startsWith("1_14") || NickPlugin.version.startsWith("1_15")) {
						try {
							ReflectUtils.setField(packet, "a", teamName);
							ReflectUtils.setField(packet, "b", getAsIChatBaseComponent(teamName));
							ReflectUtils.setField(packet, "c", getAsIChatBaseComponent(prefixForPlayer));
							ReflectUtils.setField(packet, "d", getAsIChatBaseComponent(suffixForPlayer));
							ReflectUtils.setField(packet, "e", "ALWAYS");
							ReflectUtils.setField(packet, "g", contents);
							ReflectUtils.setField(packet, "i", 0);
						} catch (Exception ex) {
							ReflectUtils.setField(packet, "a", teamName);
							ReflectUtils.setField(packet, "b", getAsIChatBaseComponent(teamName));
							ReflectUtils.setField(packet, "c", getAsIChatBaseComponent(prefixForPlayer));
							ReflectUtils.setField(packet, "d", getAsIChatBaseComponent(suffixForPlayer));
							ReflectUtils.setField(packet, "e", "ALWAYS");
							ReflectUtils.setField(packet, "h", contents);
							ReflectUtils.setField(packet, "j", 0);
						}
					} else {
						try {
							ReflectUtils.setField(packet, "a", teamName);
							ReflectUtils.setField(packet, "b", teamName);
							ReflectUtils.setField(packet, "c", prefixForPlayer);
							ReflectUtils.setField(packet, "d", suffixForPlayer);
							ReflectUtils.setField(packet, "e", "ALWAYS");
							ReflectUtils.setField(packet, "g", contents);
							ReflectUtils.setField(packet, "h", 0);
						} catch (Exception ex) {
							ReflectUtils.setField(packet, "a", teamName);
							ReflectUtils.setField(packet, "b", teamName);
							ReflectUtils.setField(packet, "c", prefixForPlayer);
							ReflectUtils.setField(packet, "d", suffixForPlayer);
							ReflectUtils.setField(packet, "e", "ALWAYS");
							ReflectUtils.setField(packet, "h", contents);
							ReflectUtils.setField(packet, "i", 0);
						}
					}
				} else {
					ReflectUtils.setField(packet, "a", teamName);
					ReflectUtils.setField(packet, "b", teamName);
					ReflectUtils.setField(packet, "c", prefixForPlayer);
					ReflectUtils.setField(packet, "d", suffixForPlayer);
					ReflectUtils.setField(packet, "e", contents);
					ReflectUtils.setField(packet, "f", 0);
				}
			
				sendPacket(t, packet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void sendPacket(Player p, Object packet) {
		try {
			Object playerHandle = p.getClass().getMethod("getHandle", new Class[0]).invoke(p, new Object[0]);
			Object playerConnection = playerHandle.getClass().getField("playerConnection").get(playerHandle);
			playerConnection.getClass().getMethod("sendPacket", new Class[] { ReflectUtils.getNMSClass("Packet") }).invoke(playerConnection, new Object[] { packet });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}