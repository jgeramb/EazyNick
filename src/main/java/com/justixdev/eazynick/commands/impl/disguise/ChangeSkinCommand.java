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

@CustomCommand(name = "changeskin", description = "Changes your skin")
public class ChangeSkinCommand extends Command {

    @Override
    public List<ParameterCombination> getCombinations() {
        return Arrays.asList(
                // random
                new ParameterCombination(),
                // custom
                new ParameterCombination(
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

        if(!(utils.getCanUseNick().getOrDefault(player.getUniqueId(), System.currentTimeMillis()) <= System.currentTimeMillis())) {
            languageYamlFile.sendMessage(
                    player,
                    languageYamlFile.getConfigString(player, "Messages.NickDelay")
                            .replace("%prefix%", prefix)
            );
            return CommandResult.SUCCESS;
        }

        NickManager api = new NickManager(player);
        String name = args.withName("name")
                .map(CommandParameter::asText)
                .orElse(null);

        if(name != null) {
            if(!player.hasPermission("eazynick.skin.custom"))
                return CommandResult.FAILURE_NO_PERMISSION;
        } else if(player.hasPermission("eazynick.skin.random")) {
            name = setupYamlFile.getConfiguration().getBoolean("UseMineSkinAPI")
                    ? "MINESKIN:" + utils.getRandomStringFromList(utils.getMineSkinUUIDs())
                    : api.getRandomName();
        } else
            return CommandResult.FAILURE_NO_PERMISSION;

        api.changeSkin(name);

        languageYamlFile.sendMessage(
                player,
                languageYamlFile.getConfigString(player, "Messages.SkinChanged")
                        .replace("%skinName%", name)
                        .replace("%skinname%", name)
                        .replace("%prefix%", prefix)
        );

        return CommandResult.SUCCESS;
    }

}
