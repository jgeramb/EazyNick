package com.justixdev.eazynick.listeners;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.api.NickManager;
import com.justixdev.eazynick.utilities.configuration.yaml.SetupYamlFile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        EazyNick eazyNick = EazyNick.getInstance();
        SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();

        Player player = event.getEntity();
        String deathMessage = ((event.getDeathMessage() == null) || event.getDeathMessage().isEmpty())
                ? null
                : event.getDeathMessage();
        NickManager api = new NickManager(player);

        if(api.isNicked()
                && (deathMessage != null)
                && !(setupYamlFile.getConfiguration().getBoolean("SeeNickSelf"))) {
            event.setDeathMessage(null);

            for (Player currentPlayer : Bukkit.getOnlinePlayers()) {
                if (currentPlayer != player)
                    currentPlayer.sendMessage(deathMessage);
                else
                    currentPlayer.sendMessage(deathMessage.replace(api.getNickFormat(), api.getOldDisplayName()));
            }
        }
    }

}
