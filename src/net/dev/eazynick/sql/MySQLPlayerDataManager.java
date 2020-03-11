package net.dev.eazynick.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.utils.FileUtils;

public class MySQLPlayerDataManager {

	public static String getOldPermissionsExRank(UUID uuid) {
		if(EazyNick.mysql.isConnected()) {
			if(isRegistered(uuid)) {
				try {
					ResultSet rs = EazyNick.mysql.getResult("SELECT * FROM NickedPlayerDatas WHERE UUID = '" + uuid.toString() + "'");
					
					if(rs.next())
						return rs.getString("OldPermissionsExRank");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	
		return "";
	}
	
	public static String getChatPrefix(UUID uuid) {
		if(EazyNick.mysql.isConnected()) {
			if(isRegistered(uuid)) {
				try {
					ResultSet rs = EazyNick.mysql.getResult("SELECT * FROM NickedPlayerDatas WHERE UUID = '" + uuid.toString() + "'");
					
					if(rs.next())
						return rs.getString("ChatPrefix");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return "";
	}
	
	public static String getChatSuffix(UUID uuid) {
		if(EazyNick.mysql.isConnected()) {
			if(isRegistered(uuid)) {
				try {
					ResultSet rs = EazyNick.mysql.getResult("SELECT * FROM NickedPlayerDatas WHERE UUID = '" + uuid.toString() + "'");
					
					if(rs.next())
						return rs.getString("ChatSuffix");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return "";
	}
	
	public static String getTabPrefix(UUID uuid) {
		if(EazyNick.mysql.isConnected()) {
			if(isRegistered(uuid)) {
				try {
					ResultSet rs = EazyNick.mysql.getResult("SELECT * FROM NickedPlayerDatas WHERE UUID = '" + uuid.toString() + "'");
					
					if(rs.next())
						return rs.getString("TabPrefix");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return "";
	}
	
	public static String getTabSuffix(UUID uuid) {
		if(EazyNick.mysql.isConnected()) {
			if(isRegistered(uuid)) {
				try {
					ResultSet rs = EazyNick.mysql.getResult("SELECT * FROM NickedPlayerDatas WHERE UUID = '" + uuid.toString() + "'");
					
					if(rs.next())
						return rs.getString("TabSuffix");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return "";
	}
	
	public static String getTagPrefix(UUID uuid) {
		if(EazyNick.mysql.isConnected()) {
			if(isRegistered(uuid)) {
				try {
					ResultSet rs = EazyNick.mysql.getResult("SELECT * FROM NickedPlayerDatas WHERE UUID = '" + uuid.toString() + "'");
					
					if(rs.next())
						return rs.getString("TagPrefix");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return "";
	}
	
	public static String getTagSuffix(UUID uuid) {
		if(EazyNick.mysql.isConnected()) {
			if(isRegistered(uuid)) {
				try {
					ResultSet rs = EazyNick.mysql.getResult("SELECT * FROM NickedPlayerDatas WHERE UUID = '" + uuid.toString() + "'");
					
					if(rs.next())
						return rs.getString("TagSuffix");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return "";
	}
	
	public static void insertData(UUID uuid, String oldPermissionsExRank, String chatPrefix, String chatSuffix, String tabPrefix, String tabSuffix, String tagPrefix, String tagSuffix) {
		if(FileUtils.cfg.getBoolean("BungeeCord")) {
			if(EazyNick.mysql.isConnected()) {
				if(isRegistered(uuid))
					removeData(uuid);
				
				EazyNick.mysql.update("INSERT INTO NickedPlayerDatas (UUID, OldPermissionsExRank, ChatPrefix, ChatSuffix, TabPrefix, TabSuffix, TagPrefix, TagSuffix) VALUES ('" + uuid.toString() + "', '" + oldPermissionsExRank + "', " + "'" + chatPrefix + "', '" + chatSuffix + "', '" + tabPrefix + "', '" + tabSuffix + "', '" + tagPrefix + "', '" + tagSuffix + "')");
			}
		}
	}
	
	public static void removeData(UUID uuid) {
		if(FileUtils.cfg.getBoolean("BungeeCord")) {
			if(EazyNick.mysql.isConnected()) {
				if(isRegistered(uuid))
					EazyNick.mysql.update("DELETE FROM NickedPlayerDatas WHERE UUID = '" + uuid.toString() + "'");
			}
		}
	}

	public static boolean isRegistered(UUID uuid) {
		if(FileUtils.cfg.getBoolean("BungeeCord")) {
			if(EazyNick.mysql.isConnected()) {
				try {
					ResultSet rs = EazyNick.mysql.getResult("SELECT * FROM NickedPlayerDatas WHERE UUID = '" + uuid.toString() + "'");
					
					if(rs.next()) {
						if(rs.getString("OldPermissionsExRank") != null)
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