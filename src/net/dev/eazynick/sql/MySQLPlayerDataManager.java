package net.dev.eazynick.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MySQLPlayerDataManager {

	private MySQL mysql;
	
	public MySQLPlayerDataManager(MySQL mysql) {
		this.mysql = mysql;
	}
	
	public String getGroupName(UUID uuid) {
		//Check if connection is open
		if(mysql.isConnected()) {
			//Check if player is in table
			if(isRegistered(uuid)) {
				try {
					ResultSet rs = mysql.getResult("SELECT * FROM NickedPlayerDatas WHERE UUID = '" + uuid.toString() + "'");
					
					if(rs.next()) {
						String s = rs.getString("GroupName");
						
						rs.close();
						
						return s;
					}
					
					rs.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		}
	
		return "";
	}
	
	public String getChatPrefix(UUID uuid) {
		//Check if connection is open
		if(mysql.isConnected()) {
			//Check if player is in table
			if(isRegistered(uuid)) {
				try {
					ResultSet rs = mysql.getResult("SELECT * FROM NickedPlayerDatas WHERE UUID = '" + uuid.toString() + "'");
					
					if(rs.next()) {
						String s = rs.getString("ChatPrefix");
						
						rs.close();
						
						return s;
					}
					
					rs.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		}
		
		return "";
	}
	
	public String getChatSuffix(UUID uuid) {
		//Check if connection is open
		if(mysql.isConnected()) {
			//Check if player is in table
			if(isRegistered(uuid)) {
				try {
					ResultSet rs = mysql.getResult("SELECT * FROM NickedPlayerDatas WHERE UUID = '" + uuid.toString() + "'");
					
					if(rs.next()) {
						String s = rs.getString("ChatSuffix");
						
						rs.close();
						
						return s;
					}
					
					rs.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		}
		
		return "";
	}
	
	public String getTabPrefix(UUID uuid) {
		//Check if connection is open
		if(mysql.isConnected()) {
			//Check if player is in table
			if(isRegistered(uuid)) {
				try {
					ResultSet rs = mysql.getResult("SELECT * FROM NickedPlayerDatas WHERE UUID = '" + uuid.toString() + "'");
					
					if(rs.next()) {
						String s = rs.getString("TabPrefix");
						
						rs.close();
						
						return s;
					}
					
					rs.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		}
		
		return "";
	}
	
	public String getTabSuffix(UUID uuid) {
		//Check if connection is open
		if(mysql.isConnected()) {
			//Check if player is in table
			if(isRegistered(uuid)) {
				try {
					ResultSet rs = mysql.getResult("SELECT * FROM NickedPlayerDatas WHERE UUID = '" + uuid.toString() + "'");
					
					if(rs.next()) {
						String s = rs.getString("TabSuffix");
						
						rs.close();
						
						return s;
					}
					
					rs.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		}
		
		return "";
	}
	
	public String getTagPrefix(UUID uuid) {
		//Check if connection is open
		if(mysql.isConnected()) {
			//Check if player is in table
			if(isRegistered(uuid)) {
				try {
					ResultSet rs = mysql.getResult("SELECT * FROM NickedPlayerDatas WHERE UUID = '" + uuid.toString() + "'");
					
					if(rs.next()) {
						String s = rs.getString("TagPrefix");
						
						rs.close();
						
						return s;
					}
					
					rs.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		}
		
		return "";
	}
	
	public String getTagSuffix(UUID uuid) {
		//Check if connection is open
		if(mysql.isConnected()) {
			//Check if player is in table
			if(isRegistered(uuid)) {
				try {
					ResultSet rs = mysql.getResult("SELECT * FROM NickedPlayerDatas WHERE UUID = '" + uuid.toString() + "'");
					
					if(rs.next()) {
						String s = rs.getString("TagSuffix");
						
						rs.close();
						
						return s;
					}
					
					rs.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		}
		
		return "";
	}
	
	public void insertData(UUID uuid, String groupName, String chatPrefix, String chatSuffix, String tabPrefix, String tabSuffix, String tagPrefix, String tagSuffix) {
		//Check if connection is open
		if(mysql.isConnected()) {
			//Remove player from table
			if(isRegistered(uuid))
				removeData(uuid);
			
			mysql.update("INSERT INTO NickedPlayerDatas (UUID, GroupName, ChatPrefix, ChatSuffix, TabPrefix, TabSuffix, TagPrefix, TagSuffix) VALUES ('" + uuid.toString() + "', '" + groupName + "', " + "'" + chatPrefix + "', '" + chatSuffix + "', '" + tabPrefix + "', '" + tabSuffix + "', '" + tagPrefix + "', '" + tagSuffix + "')");
		}
	}
	
	public void removeData(UUID uuid) {
		//Check if connection is open
		if(mysql.isConnected()) {
			//Check if player is in table
			if(isRegistered(uuid))
				mysql.update("DELETE FROM NickedPlayerDatas WHERE UUID = '" + uuid.toString() + "'");
		}
	}

	public boolean isRegistered(UUID uuid) {
		//Check if connection is open
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