package net.dev.eazynick.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MySQLPlayerDataManager {

	private MySQL mysql;
	
	public MySQLPlayerDataManager(MySQL mysql) {
		this.mysql = mysql;
	}
	
	public String getOldPermissionsExRank(UUID uuid) {
		if(mysql.isConnected()) {
			if(isRegistered(uuid)) {
				try {
					ResultSet rs = mysql.getResult("SELECT * FROM NickedPlayerDatas WHERE UUID = '" + uuid.toString() + "'");
					
					if(rs.next())
						return rs.getString("OldPermissionsExRank");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	
		return "";
	}
	
	public String getChatPrefix(UUID uuid) {
		if(mysql.isConnected()) {
			if(isRegistered(uuid)) {
				try {
					ResultSet rs = mysql.getResult("SELECT * FROM NickedPlayerDatas WHERE UUID = '" + uuid.toString() + "'");
					
					if(rs.next())
						return rs.getString("ChatPrefix");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return "";
	}
	
	public String getChatSuffix(UUID uuid) {
		if(mysql.isConnected()) {
			if(isRegistered(uuid)) {
				try {
					ResultSet rs = mysql.getResult("SELECT * FROM NickedPlayerDatas WHERE UUID = '" + uuid.toString() + "'");
					
					if(rs.next())
						return rs.getString("ChatSuffix");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return "";
	}
	
	public String getTabPrefix(UUID uuid) {
		if(mysql.isConnected()) {
			if(isRegistered(uuid)) {
				try {
					ResultSet rs = mysql.getResult("SELECT * FROM NickedPlayerDatas WHERE UUID = '" + uuid.toString() + "'");
					
					if(rs.next())
						return rs.getString("TabPrefix");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return "";
	}
	
	public String getTabSuffix(UUID uuid) {
		if(mysql.isConnected()) {
			if(isRegistered(uuid)) {
				try {
					ResultSet rs = mysql.getResult("SELECT * FROM NickedPlayerDatas WHERE UUID = '" + uuid.toString() + "'");
					
					if(rs.next())
						return rs.getString("TabSuffix");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return "";
	}
	
	public String getTagPrefix(UUID uuid) {
		if(mysql.isConnected()) {
			if(isRegistered(uuid)) {
				try {
					ResultSet rs = mysql.getResult("SELECT * FROM NickedPlayerDatas WHERE UUID = '" + uuid.toString() + "'");
					
					if(rs.next())
						return rs.getString("TagPrefix");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return "";
	}
	
	public String getTagSuffix(UUID uuid) {
		if(mysql.isConnected()) {
			if(isRegistered(uuid)) {
				try {
					ResultSet rs = mysql.getResult("SELECT * FROM NickedPlayerDatas WHERE UUID = '" + uuid.toString() + "'");
					
					if(rs.next())
						return rs.getString("TagSuffix");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return "";
	}
	
	public void insertData(UUID uuid, String oldPermissionsExRank, String chatPrefix, String chatSuffix, String tabPrefix, String tabSuffix, String tagPrefix, String tagSuffix) {
		if(mysql.isConnected()) {
			if(isRegistered(uuid))
				removeData(uuid);
			
			mysql.update("INSERT INTO NickedPlayerDatas (UUID, OldPermissionsExRank, ChatPrefix, ChatSuffix, TabPrefix, TabSuffix, TagPrefix, TagSuffix) VALUES ('" + uuid.toString() + "', '" + oldPermissionsExRank + "', " + "'" + chatPrefix + "', '" + chatSuffix + "', '" + tabPrefix + "', '" + tabSuffix + "', '" + tagPrefix + "', '" + tagSuffix + "')");
		}
	}
	
	public void removeData(UUID uuid) {
		if(mysql.isConnected()) {
			if(isRegistered(uuid))
				mysql.update("DELETE FROM NickedPlayerDatas WHERE UUID = '" + uuid.toString() + "'");
		}
	}

	public boolean isRegistered(UUID uuid) {
		if(mysql.isConnected()) {
			try {
				ResultSet rs = mysql.getResult("SELECT * FROM NickedPlayerDatas WHERE UUID = '" + uuid.toString() + "'");
				
				if(rs.next()) {
					if(rs.getString("OldPermissionsExRank") != null)
						return true;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		
		return false;
	}

}