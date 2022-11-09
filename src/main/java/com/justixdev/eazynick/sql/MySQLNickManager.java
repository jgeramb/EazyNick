package com.justixdev.eazynick.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class MySQLNickManager {

    private static final HashMap<UUID, CachedNickData> CACHE = new HashMap<>();
    private final MySQL mysql;

    public MySQLNickManager(MySQL mysql) {
        this.mysql = mysql;
    }

    public String getNickName(UUID uniqueId) {
        if(CACHE.containsKey(uniqueId))
            return CACHE.get(uniqueId).getNickName();

        //Check if player is in table
        if(isPlayerNicked(uniqueId)) {
            //Check if connection is open
            if(mysql.isConnected()) {
                try(ResultSet resultSet = mysql.getResult("SELECT * FROM NickedPlayers WHERE UUID = '" + uniqueId.toString() + "'")) {
                    if(resultSet.next()) {
                        String nickName = resultSet.getString("NickName");

                        CACHE.put(uniqueId, new CachedNickData(nickName, resultSet.getString("SkinName")));

                        return nickName;
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return "NaN";
    }

    public String getSkinName(UUID uniqueId) {
        if(CACHE.containsKey(uniqueId))
            return CACHE.get(uniqueId).getSkinName();

        //Check if player is in table
        if(isPlayerNicked(uniqueId)) {
            //Check if connection is open
            if(mysql.isConnected()) {
                try(ResultSet resultSet = mysql.getResult("SELECT * FROM NickedPlayers WHERE UUID = '" + uniqueId.toString() + "'")) {
                    if(resultSet.next()) {
                        String skinName = resultSet.getString("SkinName");

                        CACHE.put(uniqueId, new CachedNickData(resultSet.getString("NickName"), skinName));

                        return skinName;
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return "Error";
    }

    public void addPlayer(UUID uniqueId, String nickName, String skinName) {
        //Check if connection is open
        if(mysql.isConnected()) {
            //Check if player is not in table
            if(!(isPlayerNicked(uniqueId))) {
                CACHE.put(uniqueId, new CachedNickData(nickName, skinName));

                mysql.update("INSERT INTO NickedPlayers (UUID, NickName, SkinName) VALUES ('" + uniqueId.toString() + "', '" + nickName + "', '" + skinName + "')");
            }
        }
    }

    public void removePlayer(UUID uniqueId) {
        //Check if connection is open
        if(mysql.isConnected()) {
            //Check if player is in table
            if(isPlayerNicked(uniqueId)) {
                CACHE.remove(uniqueId);

                mysql.update("DELETE FROM NickedPlayers WHERE UUID = '" + uniqueId.toString() + "'");
            }
        }
    }

    public boolean isPlayerNicked(UUID uniqueId) {
        if(CACHE.containsKey(uniqueId))
            return true;

        //Check if connection is open
        if(mysql.isConnected()) {
            try(ResultSet resultSet = mysql.getResult("SELECT * FROM NickedPlayers WHERE UUID = '" + uniqueId.toString() + "'")) {
                if(resultSet.next()) {
                    CACHE.put(uniqueId, new CachedNickData(resultSet.getString("NickName"), resultSet.getString("SkinName")));

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

    public static class CachedNickData {

        private final String nickName, skinName;

        public CachedNickData(String nickName, String skinName) {
            this.nickName = nickName;
            this.skinName = skinName;
        }

        public String getNickName() {
            return nickName;
        }

        public String getSkinName() {
            return skinName;
        }

    }

}