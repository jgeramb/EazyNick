package com.justixdev.eazynick.commands.impl.disguise;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.api.NickManager;
import com.justixdev.eazynick.commands.Command;
import com.justixdev.eazynick.commands.CommandResult;
import com.justixdev.eazynick.commands.CustomCommand;
import com.justixdev.eazynick.commands.parameters.ParameterCombination;
import com.justixdev.eazynick.utilities.Utils;
import com.justixdev.eazynick.utilities.configuration.yaml.LanguageYamlFile;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CustomCommand(name = "name", description = "Shows your active nickname")
public class NameCommand extends Command {

    @Override
    public CommandResult execute(CommandSender sender, ParameterCombination args) {
        EazyNick eazyNick = EazyNick.getInstance();
        Utils utils = eazyNick.getUtils();
        LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();

        String prefix = utils.getPrefix();
        Player player = (Player) sender;
        NickManager api = new NickManager(player);

        if(!api.isNicked()) {
            languageYamlFile.sendMessage(
                    player,
                    languageYamlFile.getConfigString(player, "Messages.NotNicked")
                            .replace("%prefix%", prefix)
            );
            return CommandResult.SUCCESS;
        }

        languageYamlFile.sendMessage(
                player,
                languageYamlFile.getConfigString(player, "Messages.Name")
                        .replace("%name%", api.getNickName())
                        .replace("%prefix%", prefix)
        );

        return CommandResult.SUCCESS;
    }

}
