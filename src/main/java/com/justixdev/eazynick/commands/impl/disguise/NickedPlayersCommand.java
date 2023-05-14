package com.justixdev.eazynick.commands.impl.disguise;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.api.NickManager;
import com.justixdev.eazynick.commands.Command;
import com.justixdev.eazynick.commands.CommandResult;
import com.justixdev.eazynick.commands.CustomCommand;
import com.justixdev.eazynick.commands.parameters.ParameterCombination;
import com.justixdev.eazynick.utilities.Utils;
import com.justixdev.eazynick.utilities.configuration.yaml.LanguageYamlFile;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

@CustomCommand(name = "nickedplayers", description = "Shows a list of all nicked players")
public class NickedPlayersCommand extends Command {

    @Override
    protected void initAliases() {
        super.initAliases();

        this.aliases.add("disguisedplayers");
    }

    @Override
    public CommandResult execute(CommandSender sender, ParameterCombination args) {
        EazyNick eazyNick = EazyNick.getInstance();
        Utils utils = eazyNick.getUtils();
        LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();

        String prefix = utils.getPrefix();
        Player player = (Player) sender;

        if(!player.hasPermission("eazynick.nickedplayers"))
            return CommandResult.FAILURE_NO_PERMISSION;

        List<? extends Player> nickedPlayers = Bukkit.getOnlinePlayers()
                .stream()
                .filter(currentPlayer -> utils.getNickedPlayers().containsKey(currentPlayer.getUniqueId()))
                .collect(Collectors.toList());

        if(!nickedPlayers.isEmpty()) {
            languageYamlFile.sendMessage(
                    player,
                    languageYamlFile.getConfigString(player, "Messages.NickedPlayers.CurrentNickedPlayers")
                            .replace("%prefix%", prefix)
            );

            nickedPlayers.forEach(currentNickedPlayer -> {
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
            });
        } else
            languageYamlFile.sendMessage(
                    player,
                    languageYamlFile.getConfigString(player, "Messages.NickedPlayers.NoPlayerIsNicked")
                            .replace("%prefix%", prefix)
            );

        return CommandResult.SUCCESS;
    }

}
