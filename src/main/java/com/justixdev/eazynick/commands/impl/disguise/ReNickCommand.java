package com.justixdev.eazynick.commands.impl.disguise;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.api.events.PlayerUnnickEvent;
import com.justixdev.eazynick.commands.Command;
import com.justixdev.eazynick.commands.CommandResult;
import com.justixdev.eazynick.commands.CustomCommand;
import com.justixdev.eazynick.commands.parameters.ParameterCombination;
import com.justixdev.eazynick.sql.MySQLNickManager;
import com.justixdev.eazynick.utilities.Utils;
import com.justixdev.eazynick.utilities.configuration.yaml.LanguageYamlFile;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CustomCommand(name = "renick", description = "For internal purposes only")
public class ReNickCommand extends Command {

    @Override
    public CommandResult execute(CommandSender sender, ParameterCombination args) {
        EazyNick eazyNick = EazyNick.getInstance();
        Utils utils = eazyNick.getUtils();
        LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
        MySQLNickManager mysqlNickManager = eazyNick.getMysqlNickManager();

        Player player = (Player) sender;

        if(utils.getNickOnWorldChangePlayers().contains(player.getUniqueId())
                || ((mysqlNickManager != null) && mysqlNickManager.isNicked(player.getUniqueId()))) {
            if(utils.getNickedPlayers().containsKey(player.getUniqueId())) {
                Bukkit.getPluginManager().callEvent(new PlayerUnnickEvent(player));
                return CommandResult.SUCCESS;
            }

            if(eazyNick.getSetupYamlFile().getConfiguration().getStringList("DisabledNickWorlds")
                    .contains(player.getWorld().getName())) {
                languageYamlFile.sendMessage(
                        player,
                        languageYamlFile.getConfigString(player, "Messages.DisabledWorld")
                                .replace("%prefix%", utils.getPrefix())
                );
                return CommandResult.SUCCESS;
            }

            utils.performReNick(player);
        }

        return CommandResult.SUCCESS;
    }

}
