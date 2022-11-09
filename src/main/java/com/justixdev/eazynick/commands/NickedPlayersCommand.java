package com.justixdev.eazynick.commands;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.api.NickManager;
import com.justixdev.eazynick.utilities.Utils;
import com.justixdev.eazynick.utilities.configuration.yaml.LanguageYamlFile;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class NickedPlayersCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        EazyNick eazyNick = EazyNick.getInstance();
        Utils utils = eazyNick.getUtils();
        LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();

        if(!(sender instanceof Player)) {
            utils.sendConsole(utils.getNotPlayer());
            return true;
        }

        String prefix = utils.getPrefix();
        Player player = (Player) sender;

        if(player.hasPermission("eazynick.nickedplayers")) {
            List<? extends Player> nickedPlayers = Bukkit.getOnlinePlayers()
                    .stream()
                    .filter(currentPlayer -> utils.getNickedPlayers().containsKey(currentPlayer.getUniqueId()))
                    .collect(Collectors.toList());

            if(!(nickedPlayers.isEmpty())) {
                languageYamlFile.sendMessage(
                        player,
                        languageYamlFile.getConfigString(player, "Messages.NickedPlayers.CurrentNickedPlayers")
                                .replace("%prefix%", prefix)
                );

                for (Player currentNickedPlayer : nickedPlayers) {
                    NickManager api = new NickManager(currentNickedPlayer);

                    languageYamlFile.sendMessage(
                            player,
                            languageYamlFile.getConfigString(player, "Messages.NickedPlayers.PlayerInfo")
                                    .replace("%realName%", api.getRealName())
                                    .replace("%realname%", api.getRealName())
                                    .replace("%nickName%", api.getNickName())
                                    .replace("%nickname%", api.getNickName())
                                    .replace("%prefix%", prefix)
                    );
                }
            } else
                languageYamlFile.sendMessage(
                        player,
                        languageYamlFile.getConfigString(player, "Messages.NickedPlayers.NoPlayerIsNicked")
                                .replace("%prefix%", prefix)
                );
        } else
            languageYamlFile.sendMessage(player, utils.getNoPerm());

        return true;
    }

}
