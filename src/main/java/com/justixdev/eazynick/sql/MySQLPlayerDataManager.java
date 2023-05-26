package com.justixdev.eazynick.sql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

@RequiredArgsConstructor
public class MySQLPlayerDataManager {

    private static final HashMap<UUID, CachedNickedPlayerData> CACHE = new HashMap<>();

    private final MySQL mysql;

    public String getGroupName(UUID uniqueId) {
        return this.isRegistered(uniqueId) ? CACHE.get(uniqueId).getGroupName() : "";
    }

    public String getChatPrefix(UUID uniqueId) {
        return this.isRegistered(uniqueId) ? CACHE.get(uniqueId).getChatPrefix() : "";
    }

    public String getChatSuffix(UUID uniqueId) {
        return this.isRegistered(uniqueId) ? CACHE.get(uniqueId).getChatSuffix() : "";
    }

    public String getTabPrefix(UUID uniqueId) {
        return this.isRegistered(uniqueId) ? CACHE.get(uniqueId).getTabPrefix() : "";
    }

    public String getTabSuffix(UUID uniqueId) {
        return this.isRegistered(uniqueId) ? CACHE.get(uniqueId).getTabSuffix() : "";
    }

    public String getTagPrefix(UUID uniqueId) {
        return this.isRegistered(uniqueId) ? CACHE.get(uniqueId).getTagPrefix() : "";
    }

    public String getTagSuffix(UUID uniqueId) {
        return this.isRegistered(uniqueId) ? CACHE.get(uniqueId).getTagSuffix() : "";
    }

    public void insertData(UUID uniqueId,
                           String groupName,
                           String chatPrefix,
                           String chatSuffix,
                           String tabPrefix,
                           String tabSuffix,
                           String tagPrefix,
                           String tagSuffix) {
        this.removeData(uniqueId);

        CACHE.put(uniqueId, new CachedNickedPlayerData(
                groupName,
                chatPrefix,
                chatSuffix,
                tabPrefix,
                tabSuffix,
                tagPrefix,
                tagSuffix));

        this.mysql.update(
                "INSERT INTO `nicked_player_data` VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                uniqueId,
                groupName,
                chatPrefix,
                chatSuffix,
                tabPrefix,
                tabSuffix,
                tagPrefix,
                tagSuffix);
    }

    public void removeData(UUID uniqueId) {
        CACHE.remove(uniqueId);

        this.mysql.update("DELETE FROM `nicked_player_data` WHERE unique_id = ?", uniqueId);
    }

    public boolean isRegistered(UUID uniqueId) {
        if(CACHE.containsKey(uniqueId))
            return true;

        try(ResultSet resultSet = this.mysql.getResult("SELECT * FROM `nicked_player_data` WHERE unique_id = ?", uniqueId)) {
            if(resultSet.next()) {
                CACHE.put(uniqueId, CachedNickedPlayerData.fromResultSet(resultSet));

                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    public void clearCachedData(UUID uniqueId) {
        CACHE.remove(uniqueId);
    }

    @Data
    @AllArgsConstructor
    public static class CachedNickedPlayerData {

        private final String groupName, chatPrefix, chatSuffix, tabPrefix, tabSuffix, tagPrefix, tagSuffix;

        public static CachedNickedPlayerData fromResultSet(ResultSet resultSet) throws SQLException {
            return new CachedNickedPlayerData(
                    resultSet.getString("group"),
                    resultSet.getString("chat_prefix"),
                    resultSet.getString("chat_suffix"),
                    resultSet.getString("tab_prefix"),
                    resultSet.getString("tab_suffix"),
                    resultSet.getString("tag_prefix"),
                    resultSet.getString("tag_suffix"));
        }

    }

}