package net.dev.nickplugin.utils.scoreboard;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.dev.nickplugin.api.NickManager;
import net.dev.nickplugin.main.Main;
import net.dev.nickplugin.utils.ReflectUtils;
import net.dev.nickplugin.utils.Utils;

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
		
		if(!(Utils.scoreboardTeamContents.contains(p.getName())))
			Utils.scoreboardTeamContents.add(p.getName());
	}
	
	public ScoreboardTeamManager(Player p, String name, String prefix, String suffix) {
		this.p = p;
		this.prefix = prefix;
		this.suffix = suffix;
		this.teamName = new NickManager(p).getRealName();
		
		if(!(Utils.scoreboardTeamContents.contains(name)))
			Utils.scoreboardTeamContents.add(name);
	}
	
	public void destroyTeam() {
		try {
			packet = ReflectUtils.getNMSClass("PacketPlayOutScoreboardTeam").getConstructor(new Class[0]).newInstance(new Object[0]);
			
			if(!(Main.version.equalsIgnoreCase("1_7_R4"))) {
				if(Main.version.startsWith("1_1")) {
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
			
			for(Player t : Bukkit.getOnlinePlayers()) {
				sendPacket(t, packet);
			}
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
			packet = ReflectUtils.getNMSClass("PacketPlayOutScoreboardTeam").getConstructor(new Class[0]).newInstance(new Object[0]);
			
			if(!(Main.version.equalsIgnoreCase("1_7_R4"))) {
				if(Main.version.startsWith("1_1")) {
					try {
						ReflectUtils.setField(packet, "a", teamName);
						ReflectUtils.setField(packet, "b", getAsIChatBaseComponent(teamName));
						ReflectUtils.setField(packet, "c", getAsIChatBaseComponent(prefix));
						ReflectUtils.setField(packet, "d", getAsIChatBaseComponent(suffix));
						ReflectUtils.setField(packet, "e", "ALWAYS");
						ReflectUtils.setField(packet, "g", Utils.scoreboardTeamContents);
						ReflectUtils.setField(packet, "i", 0);
					} catch (Exception ex) {
						ReflectUtils.setField(packet, "a", teamName);
						ReflectUtils.setField(packet, "b", getAsIChatBaseComponent(teamName));
						ReflectUtils.setField(packet, "c", getAsIChatBaseComponent(prefix));
						ReflectUtils.setField(packet, "d", getAsIChatBaseComponent(suffix));
						ReflectUtils.setField(packet, "e", "ALWAYS");
						ReflectUtils.setField(packet, "h", Utils.scoreboardTeamContents);
						ReflectUtils.setField(packet, "j", 0);
					}
				} else {
					try {
						ReflectUtils.setField(packet, "a", teamName);
						ReflectUtils.setField(packet, "b", teamName);
						ReflectUtils.setField(packet, "c", prefix);
						ReflectUtils.setField(packet, "d", suffix);
						ReflectUtils.setField(packet, "e", "ALWAYS");
						ReflectUtils.setField(packet, "g", Utils.scoreboardTeamContents);
						ReflectUtils.setField(packet, "h", 0);
					} catch (Exception ex) {
						ReflectUtils.setField(packet, "a", teamName);
						ReflectUtils.setField(packet, "b", teamName);
						ReflectUtils.setField(packet, "c", prefix);
						ReflectUtils.setField(packet, "d", suffix);
						ReflectUtils.setField(packet, "e", "ALWAYS");
						ReflectUtils.setField(packet, "h", Utils.scoreboardTeamContents);
						ReflectUtils.setField(packet, "i", 0);
					}
				}
			} else {
				ReflectUtils.setField(packet, "a", teamName);
				ReflectUtils.setField(packet, "b", teamName);
				ReflectUtils.setField(packet, "c", prefix);
				ReflectUtils.setField(packet, "d", suffix);
				ReflectUtils.setField(packet, "e", Utils.scoreboardTeamContents);
				ReflectUtils.setField(packet, "f", 0);
			}
			
			for(Player t : Bukkit.getOnlinePlayers()) {
				sendPacket(t, packet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void removePlayerFromTeam() {
		if(Utils.scoreboardTeamContents.contains(p.getName())) {
			Utils.scoreboardTeamContents.remove(p.getName());
		}
	}
	
	private void sendPacket(Player p, Object packet) {
		try {
			Object playerHandle = p.getClass().getMethod("getHandle", new Class[0]).invoke(p, new Object[0]);
			Object playerConnection = playerHandle.getClass().getField("playerConnection").get(playerHandle);
			playerConnection.getClass().getMethod("sendPacket", new Class[] { ReflectUtils.getNMSClass("Packet") })
					.invoke(playerConnection, new Object[] { packet });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
