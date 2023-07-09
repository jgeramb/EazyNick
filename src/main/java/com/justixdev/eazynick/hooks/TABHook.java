package com.justixdev.eazynick.hooks;

import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.nametag.NameTagManager;
import me.neznamy.tab.api.tablist.TabListFormatManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class TABHook {

    private static TabAPI api;

    private final TabPlayer tabPlayer;
    private final TabListFormatManager tablistFormatManager;
    private final NameTagManager nametagManager;

    public TABHook(Player player) {
        if(api == null)
            api = TabAPI.getInstance();

        this.tabPlayer = api.getPlayer(player.getUniqueId());
        this.nametagManager = api.getNameTagManager();
        this.tablistFormatManager = api.getTabListFormatManager();

        if((this.tabPlayer == null) || (this.tablistFormatManager == null) || (this.nametagManager == null))
            Bukkit.getLogger().log(Level.SEVERE, "Could not load TabPlayer for '" + player.getName() + "'");
    }

    public void update(String tabPrefix, String tabSuffix, String tagPrefix, String tagSuffix, String groupName) {
        if((this.tabPlayer == null) || (this.tablistFormatManager == null) || (this.nametagManager == null))
            return;

        // Set temporarily nametag values
        this.nametagManager.setPrefix(this.tabPlayer, tagPrefix);
        this.nametagManager.setSuffix(this.tabPlayer, tagSuffix);

        // Set temporarily list values
        this.tablistFormatManager.setPrefix(this.tabPlayer, tabPrefix);
        this.tablistFormatManager.setSuffix(this.tabPlayer, tabSuffix);

        // Change group name
        this.tabPlayer.setTemporaryGroup(groupName);
    }

    public void reset() {
        if((this.tabPlayer == null) || (this.tablistFormatManager == null) || (this.nametagManager == null))
            return;

        // Unset temporarily nametag values
        this.nametagManager.setPrefix(this.tabPlayer, this.nametagManager.getOriginalPrefix(this.tabPlayer));
        this.nametagManager.setSuffix(this.tabPlayer, this.nametagManager.getOriginalSuffix(this.tabPlayer));

        // Unset temporarily list values
        this.tablistFormatManager.setPrefix(this.tabPlayer, this.tablistFormatManager.getOriginalPrefix(this.tabPlayer));
        this.tablistFormatManager.setSuffix(this.tabPlayer, this.tablistFormatManager.getOriginalSuffix(this.tabPlayer));

        // Reset group name
        this.tabPlayer.setTemporaryGroup(null);
    }

}
