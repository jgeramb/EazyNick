package com.justixdev.eazynick.commands.impl.disguise;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.api.NickManager;
import com.justixdev.eazynick.commands.Command;
import com.justixdev.eazynick.commands.CommandResult;
import com.justixdev.eazynick.commands.CustomCommand;
import com.justixdev.eazynick.commands.parameters.CommandParameter;
import com.justixdev.eazynick.commands.parameters.ParameterCombination;
import com.justixdev.eazynick.commands.parameters.ParameterType;
import com.justixdev.eazynick.utilities.Utils;
import com.justixdev.eazynick.utilities.configuration.yaml.LanguageYamlFile;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

@CustomCommand(name = "resetskinother", description = "Resets the skin of another player")
public class ResetSkinOtherCommand extends Command {

    @Override
    public List<ParameterCombination> getCombinations() {
        return Collections.singletonList(
                new ParameterCombination(
                        new CommandParameter("player", ParameterType.PLAYER)));
    }

    @Override
    public CommandResult execute(CommandSender sender, ParameterCombination args) {
        EazyNick eazyNick = EazyNick.getInstance();
        Utils utils = eazyNick.getUtils();
        LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();

        Player player = (Player) sender;

        if(!player.hasPermission("eazynick.other.skin.reset"))
            return CommandResult.FAILURE_NO_PERMISSION;

        Player targetPlayer = args.withName("player")
                .map(CommandParameter::asPlayer)
                .orElse(null);

        assert targetPlayer != null;

        NickManager api = new NickManager(targetPlayer);

        api.changeSkin(api.getRealName());

        languageYamlFile.sendMessage(
                player,
                languageYamlFile.getConfigString(player, "Messages.Other.ResetSkin")
                        .replace("%playerName%", targetPlayer.getName())
                        .replace("%playername%", targetPlayer.getName())
                        .replace("%prefix%", utils.getPrefix())
        );

        return CommandResult.SUCCESS;
    }

}
