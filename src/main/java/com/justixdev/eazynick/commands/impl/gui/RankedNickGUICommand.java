package com.justixdev.eazynick.commands.impl.gui;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.api.NickManager;
import com.justixdev.eazynick.api.events.PlayerUnnickEvent;
import com.justixdev.eazynick.commands.Command;
import com.justixdev.eazynick.commands.CommandResult;
import com.justixdev.eazynick.commands.CustomCommand;
import com.justixdev.eazynick.commands.parameters.ParameterCombination;
import com.justixdev.eazynick.sql.MySQLNickManager;
import com.justixdev.eazynick.sql.MySQLPlayerDataManager;
import com.justixdev.eazynick.utilities.Utils;
import com.justixdev.eazynick.utilities.configuration.yaml.LanguageYamlFile;
import com.justixdev.eazynick.utilities.configuration.yaml.SetupYamlFile;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CustomCommand(name = "rankednickgui", description = "Alternative GUI for legacy server versions")
public class RankedNickGUICommand extends Command {

    @Override
    public CommandResult execute(CommandSender sender, ParameterCombination args) {
        EazyNick eazyNick = EazyNick.getInstance();
        Utils utils = eazyNick.getUtils();
        SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
        LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
        MySQLNickManager mysqlNickManager = eazyNick.getMysqlNickManager();
        MySQLPlayerDataManager mysqlPlayerDataManager = eazyNick.getMysqlPlayerDataManager();

        String prefix = utils.getPrefix();
        Player player = (Player) sender;

        if(new NickManager(player).isNicked()) {
            if(player.hasPermission("eazynick.nick.reset"))
                Bukkit.getPluginManager().callEvent(new PlayerUnnickEvent(player));
        } else if((mysqlNickManager != null)
                && mysqlNickManager.isNicked(player.getUniqueId())
                && setupYamlFile.getConfiguration().getBoolean("LobbyMode")
                && setupYamlFile.getConfiguration().getBoolean("RemoveMySQLNickOnUnnickWhenLobbyModeEnabled")) {
            mysqlNickManager.removePlayer(player.getUniqueId());
            mysqlPlayerDataManager.removeData(player.getUniqueId());

            languageYamlFile.sendMessage(
                    player,
                    languageYamlFile.getConfigString(player, "Messages.Unnick")
                            .replace("%prefix%", prefix)
            );
        } else if(player.hasPermission("eazynick.gui.list")) {
            if(!setupYamlFile.getConfiguration().getStringList("DisabledNickWorlds")
                    .contains(player.getWorld().getName()))
                eazyNick.getGuiManager().openRankedNickGUI(player, "");
            else
                languageYamlFile.sendMessage(
                        player,
                        languageYamlFile.getConfigString(player, "Messages.DisabledWorld")
                                .replace("%prefix%", prefix)
                );
        } else
            return CommandResult.FAILURE_NO_PERMISSION;

        return CommandResult.SUCCESS;
    }

}
