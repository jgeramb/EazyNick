package net.dev.nickplugin.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import net.dev.nickplugin.main.Main;
import net.dev.nickplugin.utils.FileUtils;

public class MySQLNickManager {

	public static String getNickName(UUID uuid) {
		if(isPlayerNicked(uuid)) {
			if(Main.mysql.isConnected()) {
				try {
					ResultSet rs = Main.mysql.getResult("SELECT * FROM NickedPlayers WHERE UUID = '" + uuid.toString() + "'");
					
					if(rs.next()) {
						return rs.getString("NAME");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return "";
	}
	
	public static void addPlayer(UUID uuid, String name) {
		if(FileUtils.cfg.getBoolean("BungeeCord")) {
			if(Main.mysql.isConnected()) {
				if(!(isPlayerNicked(uuid))) {
					Main.mysql.update("INSERT INTO NickedPlayers (UUID, NAME) VALUES ('" + uuid.toString() + "', '" + name + "')");
				}
			}
		}
	}
	
	public static void removePlayer(UUID uuid) {
		if(FileUtils.cfg.getBoolean("BungeeCord")) {
			if(Main.mysql.isConnected()) {
				if(isPlayerNicked(uuid)) {
					Main.mysql.update("DELETE FROM NickedPlayers WHERE UUID = '" + uuid.toString() + "'");
				}
			}
		}
	}

	public static boolean isPlayerNicked(UUID uuid) {
		if(FileUtils.cfg.getBoolean("BungeeCord")) {
			if(Main.mysql.isConnected()) {
				try {
					ResultSet rs = Main.mysql.getResult("SELECT * FROM NickedPlayers WHERE UUID = '" + uuid.toString() + "'");
					
					if(rs.next()) {
						if(rs.getString("NAME") != null)
							return true;
					}
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		}
		
		return false;
	}

}