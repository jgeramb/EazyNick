package com.justixdev.eazynick.commands.impl.disguise;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.api.NickManager;
import com.justixdev.eazynick.commands.Command;
import com.justixdev.eazynick.commands.CommandResult;
import com.justixdev.eazynick.commands.CustomCommand;
import com.justixdev.eazynick.commands.parameters.ParameterCombination;
import com.justixdev.eazynick.sql.MySQLNickManager;
import com.justixdev.eazynick.utilities.Utils;
import com.justixdev.eazynick.utilities.configuration.yaml.LanguageYamlFile;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CustomCommand(name = "resetname", description = "Resets your name")
public class ResetNameCommand extends Command {

    @Override
    public CommandResult execute(CommandSender sender, ParameterCombination args) {
        EazyNick eazyNick = EazyNick.getInstance();
        Utils utils = eazyNick.getUtils();
        LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
        MySQLNickManager mysqlNickManager = eazyNick.getMysqlNickManager();

        Player player = (Player) sender;

        if(!player.hasPermission("eazynick.nick.reset"))
            return CommandResult.FAILURE_NO_PERMISSION;

        NickManager api = new NickManager(player);
        api.setName(api.getRealName());

        if(mysqlNickManager != null)
            mysqlNickManager.removePlayer(player.getUniqueId());

        languageYamlFile.sendMessage(
                player,
                languageYamlFile.getConfigString(player, "Messages.ResetName")
                        .replace("%prefix%", utils.getPrefix())
        );

        return CommandResult.SUCCESS;
    }

}
