package net.dev.eazynick.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MySQLNickManager {

	private MySQL mysql;
	
	public MySQLNickManager(MySQL mysql) {
		this.mysql = mysql;
	}

	public String getNickName(UUID uuid) {
		//Check if player is in table
		if(isPlayerNicked(uuid)) {
			//Check if connection is open
			if(mysql.isConnected()) {
				try {
					ResultSet rs = mysql.getResult("SELECT * FROM NickedPlayers WHERE UUID = '" + uuid.toString() + "'");

					if(rs.next()) {
						String s = rs.getString("NickName");
						
						rs.close();
						
						return s;
					}
					
					rs.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		}
		
		return "NaN";
	}
	
	public String getSkinName(UUID uuid) {
		//Check if player is in table
		if(isPlayerNicked(uuid)) {
			//Check if connection is open
			if(mysql.isConnected()) {
				try {
					ResultSet rs = mysql.getResult("SELECT * FROM NickedPlayers WHERE UUID = '" + uuid.toString() + "'");
					
					if(rs.next()) {
						String s = rs.getString("SkinName");
						
						rs.close();
						
						return s;
					}
					
					rs.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		}
		
		return "Error";
	}
	
	public void addPlayer(UUID uuid, String nickName, String skinName) {
		//Check if connection is open
		if(mysql.isConnected()) {
			//Check if player is not in table
			if(!(isPlayerNicked(uuid)))
				mysql.update("INSERT INTO NickedPlayers (UUID, NickName, SkinName) VALUES ('" + uuid.toString() + "', '" + nickName + "', '" + skinName + "')");
		}
	}
	
	public void removePlayer(UUID uuid) {
		//Check if connection is open
		if(mysql.isConnected()) {
			//Check if player is in table
			if(isPlayerNicked(uuid))
				mysql.update("DELETE FROM NickedPlayers WHERE UUID = '" + uuid.toString() + "'");
		}
	}

	public boolean isPlayerNicked(UUID uuid) {
		//Check if connection is open
		if(mysql.isConnected()) {
			try {
				ResultSet rs = mysql.getResult("SELECT * FROM NickedPlayers WHERE UUID = '" + uuid.toString() + "'");
				
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