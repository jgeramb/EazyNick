package net.dev.eazynick.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MySQLPlayerDataManager {

	private MySQL mysql;
	
	public MySQLPlayerDataManager(MySQL mysql) {
		this.mysql = mysql;
	}
	
	public String getOldRank(UUID uuid) {
		if(mysql.isConnected()) {
			if(isRegistered(uuid)) {
				try {
					ResultSet rs = mysql.getResult("SELECT * FROM NickedPlayerDatas WHERE UUID = '" + uuid.toString() + "'");
					
					if(rs.next()) {
						String s = rs.getString("OldRank");
						
						rs.close();
						
						return s;
					}
					
					rs.close();
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
					
					if(rs.next()) {
						String s = rs.getString("ChatPrefix");
						
						rs.close();
						
						return s;
					}
					
					rs.close();
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
					
					if(rs.next()) {
						String s = rs.getString("ChatSuffix");
						
						rs.close();
						
						return s;
					}
					
					rs.close();
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
					
					if(rs.next()) {
						String s = rs.getString("TabPrefix");
						
						rs.close();
						
						return s;
					}
					
					rs.close();
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
					
					if(rs.next()) {
						String s = rs.getString("TabSuffix");
						
						rs.close();
						
						return s;
					}
					
					rs.close();
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
					
					if(rs.next()) {
						String s = rs.getString("TagPrefix");
						
						rs.close();
						
						return s;
					}
					
					rs.close();
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
					
					if(rs.next()) {
						String s = rs.getString("TagSuffix");
						
						rs.close();
						
						return s;
					}
					
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return "";
	}
	
	public void insertData(UUID uuid, String oldRank, String chatPrefix, String chatSuffix, String tabPrefix, String tabSuffix, String tagPrefix, String tagSuffix) {
		if(mysql.isConnected()) {
			if(isRegistered(uuid))
				removeData(uuid);
			
			mysql.update("INSERT INTO NickedPlayerDatas (UUID, OldRank, ChatPrefix, ChatSuffix, TabPrefix, TabSuffix, TagPrefix, TagSuffix) VALUES ('" + uuid.toString() + "', '" + oldRank + "', " + "'" + chatPrefix + "', '" + chatSuffix + "', '" + tabPrefix + "', '" + tabSuffix + "', '" + tagPrefix + "', '" + tagSuffix + "')");
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
					rs.close();
					
					return true;
				}
				
				rs.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		
		return false;
	}

}