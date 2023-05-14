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

@CustomCommand(name = "realname", description = "Shows the real username of a nicked player")
public class RealNameCommand extends Command {

    @Override
    protected void initAliases() {
        super.initAliases();

        this.aliases.add("rd");
        this.aliases.add("realdisguise");
    }

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

        if(!player.hasPermission("eazynick.real"))
            return CommandResult.FAILURE_NO_PERMISSION;

        String prefix = utils.getPrefix();
        NickManager api = new NickManager(args.withName("player")
                .map(CommandParameter::asPlayer)
                .orElse(null)
        );

        if(!api.isNicked()) {
            languageYamlFile.sendMessage(
                    player,
                    languageYamlFile.getConfigString(player, "Messages.PlayerNotNicked")
                            .replace("%prefix%", prefix)
            );
            return CommandResult.SUCCESS;
        }

        String realName = api.getRealName();

        languageYamlFile.sendMessage(
                player,
                languageYamlFile.getConfigString(player, "Messages.RealName")
                        .replace("%realName%", realName)
                        .replace("%realname%", realName)
                        .replace("%prefix%", prefix)
        );

        return CommandResult.SUCCESS;
    }

}
