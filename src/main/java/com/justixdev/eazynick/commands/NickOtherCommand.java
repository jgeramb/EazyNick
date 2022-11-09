package com.justixdev.eazynick.commands;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.api.PlayerUnnickEvent;
import com.justixdev.eazynick.sql.MySQLNickManager;
import com.justixdev.eazynick.sql.MySQLPlayerDataManager;
import com.justixdev.eazynick.utilities.Utils;
import com.justixdev.eazynick.utilities.configuration.yaml.LanguageYamlFile;
import com.justixdev.eazynick.utilities.configuration.yaml.SetupYamlFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NickOtherCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        EazyNick eazyNick = EazyNick.getInstance();
        Utils utils = eazyNick.getUtils();
        SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
        LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
        MySQLNickManager mysqlNickManager = eazyNick.getMySQLNickManager();
        MySQLPlayerDataManager mysqlPlayerDataManager = eazyNick.getMySQLPlayerDataManager();

        if(!(sender instanceof Player)) {
            utils.sendConsole(utils.getNotPlayer());
            return true;
        }

        Player player = (Player) sender;

        if(!(player.hasPermission("eazynick.other.nick.random")
                || player.hasPermission("eazynick.other.nick.custom"))) {
            languageYamlFile.sendMessage(player, utils.getNoPerm());
            return true;
        }

        String prefix = utils.getPrefix();

        if(args.length >= 1) {
            Player targetPlayer = Bukkit.getPlayer(args[0]);

            if(targetPlayer == null) {
                languageYamlFile.sendMessage(
                        player,
                        languageYamlFile.getConfigString(player, "Messages.PlayerNotFound")
                                .replace("%prefix%", prefix)
                );
                return true;
            }

            if(!(utils.getNickedPlayers().containsKey(targetPlayer.getUniqueId()))) {
                if(args.length >= 2) {
                    if(!(player.hasPermission("eazynick.other.nick.custom"))) {
                        languageYamlFile.sendMessage(player, utils.getNoPerm());
                        return true;
                    }

                    if(args[1].length() > 16) {
                        languageYamlFile.sendMessage(
                                player,
                                languageYamlFile.getConfigString(player, "Messages.NickTooLong")
                                        .replace("%prefix%", prefix)
                        );
                        return true;
                    }
                    if(setupYamlFile.getConfiguration().getStringList("DisabledNickWorlds")
                            .contains(targetPlayer.getWorld().getName())) {
                        languageYamlFile.sendMessage(
                                player,
                                languageYamlFile.getConfigString(player, "Messages.Other.DisabledWorld")
                                        .replace("%playerName%", targetPlayer.getName())
                                        .replace("%playername%", targetPlayer.getName())
                                        .replace("%prefix%", prefix)
                        );
                        return true;
                    }

                    String name = args[1].trim(),
                            formattedName = ChatColor.translateAlternateColorCodes('&', name);

                    languageYamlFile.sendMessage(
                            player,
                            languageYamlFile.getConfigString(player, "Messages.Other.SelectedNick")
                                    .replace("%playerName%", targetPlayer.getName())
                                    .replace("%playername%", targetPlayer.getName())
                                    .replace("%nickName%", formattedName)
                                    .replace("%nickname%", formattedName)
                                    .replace("%prefix%", prefix)
                    );

                    utils.performNick(targetPlayer, formattedName);
                } else {
                    if(player.hasPermission("eazynick.other.nick.random")) {
                        languageYamlFile.sendMessage(
                                player,
                                languageYamlFile.getConfigString(player, "Messages.Other.RandomNick")
                                        .replace("%playerName%", targetPlayer.getName())
                                        .replace("%playername%", targetPlayer.getName())
                                        .replace("%prefix%", prefix)
                        );

                        if(!(setupYamlFile.getConfiguration().getStringList("DisabledNickWorlds")
                                .contains(targetPlayer.getWorld().getName())))
                            utils.performNick(targetPlayer, "RANDOM");
                        else
                            languageYamlFile.sendMessage(
                                    player,
                                    languageYamlFile.getConfigString(player, "Messages.Other.DisabledWorld")
                                            .replace("%playerName%", targetPlayer.getName())
                                            .replace("%playername%", targetPlayer.getName())
                                            .replace("%prefix%", prefix)
                            );
                    } else
                        languageYamlFile.sendMessage(player, utils.getNoPerm());
                }
            } else if(player.hasPermission("eazynick.other.nick.reset")) {
                if((mysqlNickManager != null)
                        && mysqlNickManager.isPlayerNicked(targetPlayer.getUniqueId())
                        && setupYamlFile.getConfiguration().getBoolean("LobbyMode")
                        && setupYamlFile.getConfiguration().getBoolean("RemoveMySQLNickOnUnnickWhenLobbyModeEnabled")) {
                    mysqlNickManager.removePlayer(targetPlayer.getUniqueId());
                    mysqlPlayerDataManager.removeData(targetPlayer.getUniqueId());

                    languageYamlFile.sendMessage(
                            targetPlayer,
                            eazyNick.getLanguageYamlFile().getConfigString(targetPlayer, "Messages.Unnick")
                                    .replace("%prefix%", prefix)
                    );
                    languageYamlFile.sendMessage(
                            player,
                            languageYamlFile.getConfigString(player, "Messages.Other.Unnick")
                                    .replace("%playerName%", targetPlayer.getName())
                                    .replace("%playername%", targetPlayer.getName())
                                    .replace("%prefix%", prefix)
                    );
                } else {
                    languageYamlFile.sendMessage(
                            player,
                            languageYamlFile.getConfigString(player, "Messages.Other.Unnick")
                                    .replace("%playerName%", targetPlayer.getName())
                                    .replace("%playername%", targetPlayer.getName())
                                    .replace("%prefix%", prefix)
                    );

                    Bukkit.getPluginManager().callEvent(new PlayerUnnickEvent(targetPlayer));
                }
            } else
                languageYamlFile.sendMessage(player, utils.getNoPerm());
        }

        return true;
    }

}
