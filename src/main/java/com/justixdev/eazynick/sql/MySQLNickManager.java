package com.justixdev.eazynick.sql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

@RequiredArgsConstructor
public class MySQLNickManager {

    private static final HashMap<UUID, CachedNickData> CACHE = new HashMap<>();

    private final MySQL mysql;

    public String getNickName(UUID uniqueId) {
        return this.isNicked(uniqueId) ? CACHE.get(uniqueId).getNickName() : "";
    }

    public String getSkinName(UUID uniqueId) {
        return this.isNicked(uniqueId) ? CACHE.get(uniqueId).getSkinName() : "";
    }

    public void addPlayer(UUID uniqueId, String nickName, String skinName) {
        this.removePlayer(uniqueId);

        CACHE.put(uniqueId, new CachedNickData(nickName, skinName));

        this.mysql.update("INSERT INTO nicked_players VALUES (?, ?, ?)", uniqueId, nickName, skinName);
    }

    public void removePlayer(UUID uniqueId) {
        CACHE.remove(uniqueId);

        this.mysql.update("DELETE FROM nicked_players WHERE unique_id = ?", uniqueId);
    }

    public boolean isNicked(UUID uniqueId) {
        if(CACHE.containsKey(uniqueId))
            return true;

        try(ResultSet resultSet = this.mysql.getResult("SELECT * FROM nicked_players WHERE unique_id = ?", uniqueId)) {
            if(resultSet.next()) {
                CACHE.put(uniqueId, CachedNickData.fromResultSet(resultSet));

                return true;
            }
        } catch (SQLException ex) {
            Bukkit.getLogger().log(Level.WARNING, "Could not execute sql query: " + ex.getMessage());
        }

        return false;
    }

    public void clearCachedData(UUID uniqueId) {
        CACHE.remove(uniqueId);
    }

    @Data
    @AllArgsConstructor
    public static class CachedNickData {

        private final String nickName, skinName;

        public static CachedNickData fromResultSet(ResultSet resultSet) throws SQLException {
            return new CachedNickData(
                    resultSet.getString("nickname"),
                    resultSet.getString("skin_name"));
        }

    }

}