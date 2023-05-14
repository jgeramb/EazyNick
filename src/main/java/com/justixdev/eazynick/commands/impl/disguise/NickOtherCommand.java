package com.justixdev.eazynick.commands.impl.disguise;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.api.NickManager;
import com.justixdev.eazynick.api.events.PlayerUnnickEvent;
import com.justixdev.eazynick.commands.Command;
import com.justixdev.eazynick.commands.CommandResult;
import com.justixdev.eazynick.commands.CustomCommand;
import com.justixdev.eazynick.commands.parameters.CommandParameter;
import com.justixdev.eazynick.commands.parameters.ParameterCombination;
import com.justixdev.eazynick.commands.parameters.ParameterType;
import com.justixdev.eazynick.sql.MySQLNickManager;
import com.justixdev.eazynick.sql.MySQLPlayerDataManager;
import com.justixdev.eazynick.utilities.Utils;
import com.justixdev.eazynick.utilities.configuration.yaml.LanguageYamlFile;
import com.justixdev.eazynick.utilities.configuration.yaml.SetupYamlFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

@CustomCommand(name = "nickother", description = "Changes/Resets the identity of another player")
public class NickOtherCommand extends Command {

    @Override
    protected void initAliases() {
        super.initAliases();

        this.aliases.add("do");
        this.aliases.add("disguiseother");
    }

    @Override
    public List<ParameterCombination> getCombinations() {
        return Arrays.asList(
                // random
                new ParameterCombination(
                        new CommandParameter("player", ParameterType.PLAYER)),
                // custom
                new ParameterCombination(
                        new CommandParameter("player", ParameterType.PLAYER),
                        new CommandParameter("name", ParameterType.TEXT)));
    }

    @Override
    public CommandResult execute(CommandSender sender, ParameterCombination args) {
        EazyNick eazyNick = EazyNick.getInstance();
        Utils utils = eazyNick.getUtils();
        SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
        LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
        MySQLNickManager mysqlNickManager = eazyNick.getMysqlNickManager();
        MySQLPlayerDataManager mysqlPlayerDataManager = eazyNick.getMysqlPlayerDataManager();

        Player player = (Player) sender;

        if(!(player.hasPermission("eazynick.other.nick.random")
                || player.hasPermission("eazynick.other.nick.custom")))
            return CommandResult.FAILURE_NO_PERMISSION;

        String prefix = utils.getPrefix();
        Player targetPlayer = args.withName("player")
                .map(CommandParameter::asPlayer)
                .orElse(null);

        assert targetPlayer != null;

        NickManager api = new NickManager(targetPlayer);

        if(api.isNicked()) {
            if(!player.hasPermission("eazynick.other.nick.reset"))
                return CommandResult.FAILURE_NO_PERMISSION;

            Bukkit.getPluginManager().callEvent(new PlayerUnnickEvent(targetPlayer));

            languageYamlFile.sendMessage(
                    player,
                    languageYamlFile.getConfigString(player, "Messages.Other.Unnick")
                            .replace("%playerName%", targetPlayer.getName())
                            .replace("%playername%", targetPlayer.getName())
                            .replace("%prefix%", prefix)
            );

            return CommandResult.SUCCESS;
        } else if((mysqlNickManager != null)
                && mysqlNickManager.isNicked(targetPlayer.getUniqueId())
                && setupYamlFile.getConfiguration().getBoolean("LobbyMode")
                && setupYamlFile.getConfiguration().getBoolean("RemoveMySQLNickOnUnnickWhenLobbyModeEnabled")) {
            if(!player.hasPermission("eazynick.other.nick.reset"))
                return CommandResult.FAILURE_NO_PERMISSION;

            mysqlNickManager.removePlayer(targetPlayer.getUniqueId());
            mysqlPlayerDataManager.removeData(targetPlayer.getUniqueId());

            languageYamlFile.sendMessage(
                    targetPlayer,
                    languageYamlFile.getConfigString(player, "Messages.Unnick")
                            .replace("%prefix%", prefix)
            );
            languageYamlFile.sendMessage(
                    player,
                    languageYamlFile.getConfigString(player, "Messages.Other.Unnick")
                            .replace("%playerName%", targetPlayer.getName())
                            .replace("%playername%", targetPlayer.getName())
                            .replace("%prefix%", prefix)
            );

            return CommandResult.SUCCESS;
        }

        if(!utils.getNickedPlayers().containsKey(targetPlayer.getUniqueId())) {
            String name = args.withName("name")
                    .map(CommandParameter::asText)
                    .orElse(null);

            if(name != null) {
                if(!player.hasPermission("eazynick.other.nick.custom"))
                    return CommandResult.FAILURE_NO_PERMISSION;

                if(name.length() > 16) {
                    languageYamlFile.sendMessage(
                            player,
                            languageYamlFile.getConfigString(player, "Messages.NickTooLong")
                                    .replace("%prefix%", prefix)
                    );
                    return CommandResult.SUCCESS;
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
                    return CommandResult.SUCCESS;
                }

                String formattedName = ChatColor.translateAlternateColorCodes('&', name.trim());

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
                if(!player.hasPermission("eazynick.other.nick.random"))
                    return CommandResult.FAILURE_NO_PERMISSION;

                languageYamlFile.sendMessage(
                        player,
                        languageYamlFile.getConfigString(player, "Messages.Other.RandomNick")
                                .replace("%playerName%", targetPlayer.getName())
                                .replace("%playername%", targetPlayer.getName())
                                .replace("%prefix%", prefix)
                );

                if(setupYamlFile.getConfiguration().getStringList("DisabledNickWorlds")
                        .contains(targetPlayer.getWorld().getName())) {
                    languageYamlFile.sendMessage(
                            player,
                            languageYamlFile.getConfigString(player, "Messages.Other.DisabledWorld")
                                    .replace("%playerName%", targetPlayer.getName())
                                    .replace("%playername%", targetPlayer.getName())
                                    .replace("%prefix%", prefix)
                    );
                    return CommandResult.SUCCESS;
                }

                utils.performNick(targetPlayer, "RANDOM");
            }
        }

        return CommandResult.SUCCESS;
    }

}
