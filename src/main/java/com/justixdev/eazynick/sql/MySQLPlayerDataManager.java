package com.justixdev.eazynick.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class MySQLPlayerDataManager {

	private static final HashMap<UUID, CachedNickedPlayerData> CACHE = new HashMap<>();
	private final MySQL mysql;
	
	public MySQLPlayerDataManager(MySQL mysql) {
		this.mysql = mysql;
	}
	
	public String getGroupName(UUID uniqueId) {
		if(CACHE.containsKey(uniqueId))
			return CACHE.get(uniqueId).getGroupName();
		
		//Check if connection is open
		if(mysql.isConnected()) {
			//Check if player is in table
			if(isRegistered(uniqueId)) {
				try(ResultSet resultSet = mysql.getResult("SELECT * FROM NickedPlayerDatas WHERE UUID = '" + uniqueId.toString() + "'")) {
					if(resultSet.next()) {
						String groupName = resultSet.getString("GroupName");
						
						CACHE.put(uniqueId, new CachedNickedPlayerData(groupName, resultSet.getString("ChatPrefix"), resultSet.getString("ChatSuffix"), resultSet.getString("TabPrefix"), resultSet.getString("TabSuffix"), resultSet.getString("TagPrefix"), resultSet.getString("TagSuffix")));
						
						return groupName;
					}
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		}
	
		return "";
	}
	
	public String getChatPrefix(UUID uniqueId) {
		if(CACHE.containsKey(uniqueId))
			return CACHE.get(uniqueId).getChatPrefix();
		
		//Check if connection is open
		if(mysql.isConnected()) {
			//Check if player is in table
			if(isRegistered(uniqueId)) {
				try(ResultSet resultSet = mysql.getResult("SELECT * FROM NickedPlayerDatas WHERE UUID = '" + uniqueId.toString() + "'")) {
					if(resultSet.next()) {
						String chatPrefix = resultSet.getString("ChatPrefix");
						
						CACHE.put(uniqueId, new CachedNickedPlayerData(resultSet.getString("GroupName"), chatPrefix, resultSet.getString("ChatSuffix"), resultSet.getString("TabPrefix"), resultSet.getString("TabSuffix"), resultSet.getString("TagPrefix"), resultSet.getString("TagSuffix")));

						return chatPrefix;
					}
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		}
		
		return "";
	}
	
	public String getChatSuffix(UUID uniqueId) {
		if(CACHE.containsKey(uniqueId))
			return CACHE.get(uniqueId).getChatSuffix();
		
		//Check if connection is open
		if(mysql.isConnected()) {
			//Check if player is in table
			if(isRegistered(uniqueId)) {
				try(ResultSet resultSet = mysql.getResult("SELECT * FROM NickedPlayerDatas WHERE UUID = '" + uniqueId.toString() + "'")) {
					if(resultSet.next()) {
						String chatSuffix = resultSet.getString("ChatSuffix");
						
						CACHE.put(uniqueId, new CachedNickedPlayerData(resultSet.getString("GroupName"), resultSet.getString("ChatPrefix"), chatSuffix, resultSet.getString("TabPrefix"), resultSet.getString("TabSuffix"), resultSet.getString("TagPrefix"), resultSet.getString("TagSuffix")));
						
						return chatSuffix;
					}
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		}
		
		return "";
	}
	
	public String getTabPrefix(UUID uniqueId) {
		if(CACHE.containsKey(uniqueId))
			return CACHE.get(uniqueId).getTabPrefix();
		
		//Check if connection is open
		if(mysql.isConnected()) {
			//Check if player is in table
			if(isRegistered(uniqueId)) {
				try(ResultSet resultSet = mysql.getResult("SELECT * FROM NickedPlayerDatas WHERE UUID = '" + uniqueId.toString() + "'")) {
					if(resultSet.next()) {
						String tabPrefix = resultSet.getString("TabPrefix");
						
						CACHE.put(uniqueId, new CachedNickedPlayerData(resultSet.getString("GroupName"), resultSet.getString("ChatPrefix"), resultSet.getString("ChatSuffix"), tabPrefix, resultSet.getString("TabSuffix"), resultSet.getString("TagPrefix"), resultSet.getString("TagSuffix")));
						
						return tabPrefix;
					}
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		}
		
		return "";
	}
	
	public String getTabSuffix(UUID uniqueId) {
		if(CACHE.containsKey(uniqueId))
			return CACHE.get(uniqueId).getTabSuffix();
		
		//Check if connection is open
		if(mysql.isConnected()) {
			//Check if player is in table
			if(isRegistered(uniqueId)) {
				try(ResultSet resultSet = mysql.getResult("SELECT * FROM NickedPlayerDatas WHERE UUID = '" + uniqueId.toString() + "'")) {
					if(resultSet.next()) {
						String tabSuffix = resultSet.getString("TabSuffix");
						
						CACHE.put(uniqueId, new CachedNickedPlayerData(resultSet.getString("GroupName"), resultSet.getString("ChatPrefix"), resultSet.getString("ChatSuffix"), resultSet.getString("TabPrefix"), tabSuffix, resultSet.getString("TagPrefix"), resultSet.getString("TagSuffix")));
						
						return tabSuffix;
					}
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		}
		
		return "";
	}
	
	public String getTagPrefix(UUID uniqueId) {
		if(CACHE.containsKey(uniqueId))
			return CACHE.get(uniqueId).getTagPrefix();
		
		//Check if connection is open
		if(mysql.isConnected()) {
			//Check if player is in table
			if(isRegistered(uniqueId)) {
				try(ResultSet resultSet = mysql.getResult("SELECT * FROM NickedPlayerDatas WHERE UUID = '" + uniqueId.toString() + "'")) {
					if(resultSet.next()) {
						String tagPrefix = resultSet.getString("TagPrefix");
						
						CACHE.put(uniqueId, new CachedNickedPlayerData(resultSet.getString("GroupName"), resultSet.getString("ChatPrefix"), resultSet.getString("ChatSuffix"), resultSet.getString("TabPrefix"), resultSet.getString("TabSuffix"), tagPrefix, resultSet.getString("TagSuffix")));
						
						return tagPrefix;
					}
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		}
		
		return "";
	}
	
	public String getTagSuffix(UUID uniqueId) {
		if(CACHE.containsKey(uniqueId))
			return CACHE.get(uniqueId).getTagSuffix();
		
		//Check if connection is open
		if(mysql.isConnected()) {
			//Check if player is in table
			if(isRegistered(uniqueId)) {
				try(ResultSet resultSet = mysql.getResult("SELECT * FROM NickedPlayerDatas WHERE UUID = '" + uniqueId.toString() + "'")) {
					if(resultSet.next()) {
						String tagSuffix = resultSet.getString("TagSuffix");
						
						CACHE.put(uniqueId, new CachedNickedPlayerData(resultSet.getString("GroupName"), resultSet.getString("ChatPrefix"), resultSet.getString("ChatSuffix"), resultSet.getString("TabPrefix"), resultSet.getString("TabSuffix"), resultSet.getString("TagPrefix"), tagSuffix));
						
						return tagSuffix;
					}
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		}
		
		return "";
	}
	
	public void insertData(UUID uniqueId, String groupName, String chatPrefix, String chatSuffix, String tabPrefix, String tabSuffix, String tagPrefix, String tagSuffix) {
		//Check if connection is open
		if(mysql.isConnected()) {
			//Remove player from table
			if(isRegistered(uniqueId))
				removeData(uniqueId);
			
			CACHE.put(uniqueId, new CachedNickedPlayerData(groupName, chatPrefix, chatSuffix, tabPrefix, tabSuffix, tagPrefix, tagSuffix));
			
			mysql.update("INSERT INTO NickedPlayerDatas (UUID, GroupName, ChatPrefix, ChatSuffix, TabPrefix, TabSuffix, TagPrefix, TagSuffix) VALUES ('" + uniqueId.toString() + "', '" + groupName + "', " + "'" + chatPrefix + "', '" + chatSuffix + "', '" + tabPrefix + "', '" + tabSuffix + "', '" + tagPrefix + "', '" + tagSuffix + "')");
		}
	}
	
	public void removeData(UUID uniqueId) {
		//Check if connection is open
		if(mysql.isConnected()) {
			//Check if player is in table
			if(isRegistered(uniqueId)) {
				CACHE.remove(uniqueId);
				
				mysql.update("DELETE FROM NickedPlayerDatas WHERE UUID = '" + uniqueId.toString() + "'");
			}
		}
	}

	public boolean isRegistered(UUID uniqueId) {
		if(CACHE.containsKey(uniqueId))
			return true;
		
		//Check if connection is open
		if(mysql.isConnected()) {
			try(ResultSet resultSet = mysql.getResult("SELECT * FROM NickedPlayerDatas WHERE UUID = '" + uniqueId.toString() + "'")) {
				if(resultSet.next()) {
					CACHE.put(uniqueId, new CachedNickedPlayerData(resultSet.getString("GroupName"), resultSet.getString("ChatPrefix"), resultSet.getString("ChatSuffix"), resultSet.getString("TabPrefix"), resultSet.getString("TabSuffix"), resultSet.getString("TagPrefix"), resultSet.getString("TagSuffix")));
					
					return true;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		
		return false;
	}
	
	public void clearCachedData(UUID uniqueId) {
		CACHE.remove(uniqueId);
	}
	
	public static class CachedNickedPlayerData {
		
		private final String groupName, chatPrefix, chatSuffix, tabPrefix, tabSuffix, tagPrefix, tagSuffix;

		public CachedNickedPlayerData(String groupName, String chatPrefix, String chatSuffix, String tabPrefix, String tabSuffix, String tagPrefix, String tagSuffix) {
			this.groupName = groupName;
			this.chatPrefix = chatPrefix;
			this.chatSuffix = chatSuffix;
			this.tabPrefix = tabPrefix;
			this.tabSuffix = tabSuffix;
			this.tagPrefix = tagPrefix;
			this.tagSuffix = tagSuffix;
		}

		public String getGroupName() {
			return groupName;
		}

		public String getChatPrefix() {
			return chatPrefix;
		}

		public String getChatSuffix() {
			return chatSuffix;
		}

		public String getTabPrefix() {
			return tabPrefix;
		}

		public String getTabSuffix() {
			return tabSuffix;
		}

		public String getTagPrefix() {
			return tagPrefix;
		}

		public String getTagSuffix() {
			return tagSuffix;
		}
		
	}

}