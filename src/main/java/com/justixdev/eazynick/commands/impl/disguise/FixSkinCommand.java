package com.justixdev.eazynick.commands.impl.disguise;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.commands.Command;
import com.justixdev.eazynick.commands.CommandResult;
import com.justixdev.eazynick.commands.CustomCommand;
import com.justixdev.eazynick.commands.parameters.ParameterCombination;
import com.justixdev.eazynick.nms.NMSNickManager;
import com.justixdev.eazynick.utilities.Utils;
import com.justixdev.eazynick.utilities.configuration.yaml.LanguageYamlFile;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CustomCommand(name = "fixskin", description = "Reloads your skin")
public class FixSkinCommand extends Command {

    @Override
    public CommandResult execute(CommandSender sender, ParameterCombination args) {
        EazyNick eazyNick = EazyNick.getInstance();
        Utils utils = eazyNick.getUtils();
        LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();

        Player player = (Player) sender;

        if(!player.hasPermission("eazynick.skin.fix"))
            return CommandResult.FAILURE_NO_PERMISSION;

        new NMSNickManager(player).updatePlayer();

        languageYamlFile.sendMessage(
                player,
                languageYamlFile.getConfigString(player, "Messages.FixSkin")
                        .replace("%prefix%", utils.getPrefix())
        );

        return CommandResult.SUCCESS;
    }

}
