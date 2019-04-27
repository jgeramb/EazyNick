package net.dev.nickplugin.utils.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.nametagedit.plugin.NametagEdit;

import net.dev.nickplugin.api.NickManager;
import net.dev.nickplugin.main.Main;
import net.dev.nickplugin.utils.FileUtils;
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
		
		if(!(Utils.scoreboardTeamContents.contains(p.getName()))) {
			Utils.scoreboardTeamContents.add(p.getName());
		}
	}
	
	public ScoreboardTeamManager(Player p, String name, String prefix, String suffix) {
		this.p = p;
		this.prefix = prefix;
		this.suffix = suffix;
		this.teamName = new NickManager(p).getRealName();
		
		if(!(Utils.scoreboardTeamContents.contains(name))) {
			Utils.scoreboardTeamContents.add(name);
		}
	}
	
	public void destroyTeam() {
		try {
			packet = ReflectUtils.getNMSClass("PacketPlayOutScoreboardTeam").getConstructor(new Class[0]).newInstance(new Object[0]);
			
			if(!(Main.version.equalsIgnoreCase("1_7_R4"))) {
				if(Main.version.equalsIgnoreCase("1_13_R1")) {
					try {
						ReflectUtils.setField(packet, "a", teamName);
						ReflectUtils.setField(packet, "b", getAsIChatBaseComponent_1_13_R1(teamName));
						ReflectUtils.setField(packet, "e", "ALWAYS");
						ReflectUtils.setField(packet, "i", 1);
					} catch (Exception ex) {
						ReflectUtils.setField(packet, "a", teamName);
						ReflectUtils.setField(packet, "b", getAsIChatBaseComponent_1_13_R1(teamName));
						ReflectUtils.setField(packet, "e", "ALWAYS");
						ReflectUtils.setField(packet, "j", 1);
					}
				} else if(Main.version.equalsIgnoreCase("1_13_R2")) {
					try {
						ReflectUtils.setField(packet, "a", teamName);
						ReflectUtils.setField(packet, "b", getAsIChatBaseComponent_1_13_R2(teamName));
						ReflectUtils.setField(packet, "e", "ALWAYS");
						ReflectUtils.setField(packet, "i", 1);
					} catch (Exception ex) {
						ReflectUtils.setField(packet, "a", teamName);
						ReflectUtils.setField(packet, "b", getAsIChatBaseComponent_1_13_R2(teamName));
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


	@SuppressWarnings("static-access")
	private net.minecraft.server.v1_13_R1.IChatBaseComponent getAsIChatBaseComponent_1_13_R1(String txt) {
		return new net.minecraft.server.v1_13_R1.IChatBaseComponent.ChatSerializer().a("{\"text\":\"" + txt + "\"}");
	}
	
	@SuppressWarnings("static-access")
	private net.minecraft.server.v1_13_R2.IChatBaseComponent getAsIChatBaseComponent_1_13_R2(String txt) {
		return new net.minecraft.server.v1_13_R2.IChatBaseComponent.ChatSerializer().a("{\"text\":\"" + txt + "\"}");
	}

	public void createTeam() {
		try {
			packet = ReflectUtils.getNMSClass("PacketPlayOutScoreboardTeam").getConstructor(new Class[0]).newInstance(new Object[0]);
			
			if(!(Main.version.equalsIgnoreCase("1_7_R4"))) {
				if(Main.version.equalsIgnoreCase("1_13_R1")) {
					try {
						ReflectUtils.setField(packet, "a", teamName);
						ReflectUtils.setField(packet, "b", getAsIChatBaseComponent_1_13_R1(teamName));
						ReflectUtils.setField(packet, "c", getAsIChatBaseComponent_1_13_R1(prefix));
						ReflectUtils.setField(packet, "d", getAsIChatBaseComponent_1_13_R1(suffix));
						ReflectUtils.setField(packet, "e", "ALWAYS");
						ReflectUtils.setField(packet, "g", Utils.scoreboardTeamContents);
						ReflectUtils.setField(packet, "i", 0);
					} catch (Exception ex) {
						ReflectUtils.setField(packet, "a", teamName);
						ReflectUtils.setField(packet, "b", getAsIChatBaseComponent_1_13_R1(teamName));
						ReflectUtils.setField(packet, "c", getAsIChatBaseComponent_1_13_R1(prefix));
						ReflectUtils.setField(packet, "d", getAsIChatBaseComponent_1_13_R1(suffix));
						ReflectUtils.setField(packet, "e", "ALWAYS");
						ReflectUtils.setField(packet, "h", Utils.scoreboardTeamContents);
						ReflectUtils.setField(packet, "j", 0);
					}
				} else if(Main.version.equalsIgnoreCase("1_13_R2")) {
					try {
						ReflectUtils.setField(packet, "a", teamName);
						ReflectUtils.setField(packet, "b", getAsIChatBaseComponent_1_13_R2(teamName));
						ReflectUtils.setField(packet, "c", getAsIChatBaseComponent_1_13_R2(prefix));
						ReflectUtils.setField(packet, "d", getAsIChatBaseComponent_1_13_R2(suffix));
						ReflectUtils.setField(packet, "e", "ALWAYS");
						ReflectUtils.setField(packet, "g", Utils.scoreboardTeamContents);
						ReflectUtils.setField(packet, "i", 0);
					} catch (Exception ex) {
						ReflectUtils.setField(packet, "a", teamName);
						ReflectUtils.setField(packet, "b", getAsIChatBaseComponent_1_13_R2(teamName));
						ReflectUtils.setField(packet, "c", getAsIChatBaseComponent_1_13_R2(prefix));
						ReflectUtils.setField(packet, "d", getAsIChatBaseComponent_1_13_R2(suffix));
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
		
		if(Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers()) {
			if(FileUtils.cfg.getBoolean("Settings.NameChangeOptions.DisplayNameColored")) {
				String nameFormatChat = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Prefix") + p.getName() + FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Suffix"));
				
				p.setDisplayName(nameFormatChat);
			}
			
			if(Utils.nameTagEditStatus()) {
				NametagEdit.getApi().setPrefix(p.getPlayer(), ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Prefix")));
				NametagEdit.getApi().setSuffix(p.getPlayer(), ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Suffix")));
			}
		} else {
			if(FileUtils.cfg.getBoolean("Settings.NameChangeOptions.DisplayNameColored")) {
				String nameFormatChat = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.Chat.Prefix") + p.getName() + FileUtils.cfg.getString("Settings.NickFormat.Chat.Suffix"));
				
				p.setDisplayName(nameFormatChat);
			}
			
			if(Utils.nameTagEditStatus()) {
				NametagEdit.getApi().setPrefix(p.getPlayer(), ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Prefix")));
				NametagEdit.getApi().setSuffix(p.getPlayer(), ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Suffix")));
			}
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
