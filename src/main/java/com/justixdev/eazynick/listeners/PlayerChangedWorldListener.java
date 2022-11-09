package com.justixdev.eazynick.listeners;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.api.NickManager;
import com.justixdev.eazynick.utilities.Utils;
import com.justixdev.eazynick.utilities.configuration.yaml.SetupYamlFile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class PlayerChangedWorldListener implements Listener {

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        EazyNick eazyNick = EazyNick.getInstance();
        Utils utils = eazyNick.getUtils();
        SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();

        Player player = event.getPlayer();
        NickManager api = new NickManager(player);

        if(utils.getWorldBlackList().stream().noneMatch(world -> world.equalsIgnoreCase(player.getWorld().getName()))) {
            if (setupYamlFile.getConfiguration().getBoolean("NickOnWorldChange")
                    && utils.getNickOnWorldChangePlayers().contains(player.getUniqueId())
                    && !(api.isNicked()))
                utils.performReNick(player);
            else if(!(setupYamlFile.getConfiguration().getBoolean("KeepNickOnWorldChange")))
                api.unnickPlayerWithoutRemovingMySQL(false, true);
        } else if(api.isNicked())
            api.unnickPlayerWithoutRemovingMySQL(false, true);
    }

}
