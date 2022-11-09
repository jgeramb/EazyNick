package com.justixdev.eazynick.commands;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.api.PlayerUnnickEvent;
import com.justixdev.eazynick.sql.MySQLNickManager;
import com.justixdev.eazynick.utilities.Utils;
import com.justixdev.eazynick.utilities.configuration.yaml.LanguageYamlFile;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReNickCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        EazyNick eazyNick = EazyNick.getInstance();
        Utils utils = eazyNick.getUtils();
        LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
        MySQLNickManager mysqlNickManager = eazyNick.getMySQLNickManager();

        if(!(sender instanceof Player)) {
            utils.sendConsole(utils.getNotPlayer());
            return true;
        }

        Player player = (Player) sender;

        if(utils.getNickOnWorldChangePlayers().contains(player.getUniqueId())
                || ((mysqlNickManager != null) && mysqlNickManager.isPlayerNicked(player.getUniqueId()))) {
            if(utils.getNickedPlayers().containsKey(player.getUniqueId()))
                Bukkit.getPluginManager().callEvent(new PlayerUnnickEvent(player));
            else if(!(eazyNick.getSetupYamlFile().getConfiguration().getStringList("DisabledNickWorlds")
                    .contains(player.getWorld().getName())))
                utils.performReNick(player);
            else
                languageYamlFile.sendMessage(
                        player,
                        languageYamlFile.getConfigString(player, "Messages.DisabledWorld")
                                .replace("%prefix%", utils.getPrefix())
                );
        }

        return true;
    }

}
