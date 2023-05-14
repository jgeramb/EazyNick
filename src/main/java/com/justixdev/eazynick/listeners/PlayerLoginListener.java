package com.justixdev.eazynick.listeners;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.utilities.Utils;
import com.justixdev.eazynick.utilities.configuration.yaml.SetupYamlFile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.UUID;

public class PlayerLoginListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLastPlayerLogin(PlayerLoginEvent event) {
        EazyNick eazyNick = EazyNick.getInstance();
        Utils utils = eazyNick.getUtils();
        SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();

        Player player = event.getPlayer();
        UUID uniqueId = player.getUniqueId();

        if (
                (setupYamlFile.getConfiguration().getBoolean("BungeeCord")
                        && (!setupYamlFile.getConfiguration().getBoolean("LobbyMode")
                                || (player.hasPermission("eazynick.bypasslobbymode")
                                        && setupYamlFile.getConfiguration().getBoolean("EnableBypassLobbyModePermission")))
                        && eazyNick.getMysqlNickManager().isNicked(uniqueId))
                || utils.getLastNickData().containsKey(uniqueId)
                || setupYamlFile.getConfiguration().getBoolean("JoinNick")
                || (setupYamlFile.getConfiguration().getBoolean("SaveLocalNickData")
                        && eazyNick.getSavedNickDataYamlFile().getConfiguration()
                                .contains(player.getUniqueId().toString().replace("-", ""))))
            utils.getSoonNickedPlayers().add(uniqueId);
    }

}
