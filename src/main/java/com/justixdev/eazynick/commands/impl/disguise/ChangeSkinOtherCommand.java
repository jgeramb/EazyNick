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
import com.justixdev.eazynick.utilities.configuration.yaml.SetupYamlFile;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

@CustomCommand(name = "changeskinother", description = "Changes the skin of another player")
public class ChangeSkinOtherCommand extends Command {

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
        LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
        SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();

        String prefix = utils.getPrefix();
        Player player = (Player) sender;
        Player targetPlayer = args.withName("player")
                .map(CommandParameter::asPlayer)
                .orElse(null);
        NickManager api = new NickManager(targetPlayer);
        String name = args.withName("name")
                .map(CommandParameter::asText)
                .orElse(null);

        assert targetPlayer != null;

        if(name != null) {
            if(!player.hasPermission("eazynick.other.skin.custom"))
                return CommandResult.FAILURE_NO_PERMISSION;

            languageYamlFile.sendMessage(
                    player,
                    languageYamlFile.getConfigString(player, "Messages.Other.SelectedSkin")
                            .replace("%playerName%", targetPlayer.getName())
                            .replace("%playername%", targetPlayer.getName())
                            .replace("%skinName%", name)
                            .replace("%skinname%", name)
                            .replace("%prefix%", prefix)
            );
        } else if(player.hasPermission("eazynick.other.skin.random")) {
            name = setupYamlFile.getConfiguration().getBoolean("UseMineSkinAPI")
                    ? "MINESKIN:" + utils.getRandomStringFromList(utils.getMineSkinUUIDs())
                    : api.getRandomName();

            languageYamlFile.sendMessage(
                    player,
                    languageYamlFile.getConfigString(player, "Messages.Other.RandomSkin")
                            .replace("%player%", targetPlayer.getName())
                            .replace("%prefix%", prefix)
            );
        } else
            return CommandResult.FAILURE_NO_PERMISSION;

        api.changeSkin(name);

        return CommandResult.SUCCESS;
    }

}
