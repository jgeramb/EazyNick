package net.dev.nickplugin.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import net.dev.nickplugin.NickPlugin;
import net.dev.nickplugin.utils.FileUtils;

public class MySQLNickManager {

	public static String getNickName(UUID uuid) {
		if(isPlayerNicked(uuid)) {
			if(NickPlugin.mysql.isConnected()) {
				try {
					ResultSet rs = NickPlugin.mysql.getResult("SELECT * FROM NickedPlayers WHERE UUID = '" + uuid.toString() + "'");
					
					if(rs.next())
						return rs.getString("NICKNAME");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return "";
	}
	
	public static String getSkinName(UUID uuid) {
		if(isPlayerNicked(uuid)) {
			if(NickPlugin.mysql.isConnected()) {
				try {
					ResultSet rs = NickPlugin.mysql.getResult("SELECT * FROM NickedPlayers WHERE UUID = '" + uuid.toString() + "'");
					
					if(rs.next())
						return rs.getString("SKINNAME");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return "";
	}
	
	public static void addPlayer(UUID uuid, String nickName, String skinName) {
		if(FileUtils.cfg.getBoolean("BungeeCord")) {
			if(NickPlugin.mysql.isConnected()) {
				if(!(isPlayerNicked(uuid)))
					NickPlugin.mysql.update("INSERT INTO NickedPlayers (UUID, NICKNAME, SKINNAME) VALUES ('" + uuid.toString() + "', '" + nickName + "', '" + skinName + "')");
			}
		}
	}
	
	public static void removePlayer(UUID uuid) {
		if(FileUtils.cfg.getBoolean("BungeeCord")) {
			if(NickPlugin.mysql.isConnected()) {
				if(isPlayerNicked(uuid))
					NickPlugin.mysql.update("DELETE FROM NickedPlayers WHERE UUID = '" + uuid.toString() + "'");
			}
		}
	}

	public static boolean isPlayerNicked(UUID uuid) {
		if(FileUtils.cfg.getBoolean("BungeeCord")) {
			if(NickPlugin.mysql.isConnected()) {
				try {
					ResultSet rs = NickPlugin.mysql.getResult("SELECT * FROM NickedPlayers WHERE UUID = '" + uuid.toString() + "'");
					
					return rs.next();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		}
		
		return false;
	}

}