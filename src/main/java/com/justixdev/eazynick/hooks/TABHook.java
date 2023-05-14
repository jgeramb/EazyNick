package com.justixdev.eazynick.hooks;

import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.TablistFormatManager;
import me.neznamy.tab.api.team.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class TABHook {

    private static TabAPI api;

    private final TeamManager teamManager;
    private final TablistFormatManager tablistFormatManager;
    private final TabPlayer tabPlayer;

    public TABHook(Player player) {
        if(api == null)
            api = TabAPI.getInstance();

        this.teamManager = api.getTeamManager();
        this.tablistFormatManager = api.getTablistFormatManager();
        this.tabPlayer = api.getPlayer(player.getUniqueId());

        if((this.teamManager == null) || (this.tablistFormatManager == null) || (this.tabPlayer == null))
            Bukkit.getLogger().log(Level.SEVERE, "Could not load TabPlayer for '" + player.getName() + "'");
    }

    public void update(String tabPrefix, String tabSuffix, String tagPrefix, String tagSuffix, String groupName) {
        if((this.teamManager == null) || (this.tablistFormatManager == null) || (this.tabPlayer == null))
            return;

        // Set temporarily nametag values
        this.teamManager.setPrefix(this.tabPlayer, tagPrefix);
        this.teamManager.setSuffix(this.tabPlayer, tagSuffix);

        // Set temporarily list values
        this.tablistFormatManager.setPrefix(this.tabPlayer, tabPrefix);
        this.tablistFormatManager.setSuffix(this.tabPlayer, tabSuffix);

        // Change group name
        this.tabPlayer.setTemporaryGroup(groupName);
    }

    public void reset() {
        if((this.teamManager == null) || (this.tablistFormatManager == null) || (this.tabPlayer == null))
            return;

        // Unset temporarily nametag values
        this.teamManager.resetPrefix(this.tabPlayer);
        this.teamManager.resetSuffix(this.tabPlayer);

        // Unset temporarily list values
        this.tablistFormatManager.resetPrefix(this.tabPlayer);
        this.tablistFormatManager.resetSuffix(this.tabPlayer);

        // Reset group name
        this.tabPlayer.resetTemporaryGroup();
    }

}
