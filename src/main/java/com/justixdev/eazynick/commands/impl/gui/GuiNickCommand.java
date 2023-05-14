package com.justixdev.eazynick.commands.impl.gui;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.commands.Command;
import com.justixdev.eazynick.commands.CommandResult;
import com.justixdev.eazynick.commands.CustomCommand;
import com.justixdev.eazynick.commands.parameters.CommandParameter;
import com.justixdev.eazynick.commands.parameters.ParameterCombination;
import com.justixdev.eazynick.commands.parameters.ParameterType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

@CustomCommand(name = "guinick", description = "For internal purposes only")
public class GuiNickCommand extends Command {

    @Override
    public List<ParameterCombination> getCombinations() {
        return Collections.singletonList(
                new ParameterCombination(
                        new CommandParameter("rank", ParameterType.TEXT),
                        new CommandParameter("skin", ParameterType.TEXT),
                        new CommandParameter("nickname", ParameterType.TEXT)));
    }

    @Override
    public CommandResult execute(CommandSender sender, ParameterCombination args) {
        Player player = (Player) sender;

        if(!(player.hasPermission("eazynick.nick.random")
                || player.hasPermission("eazynick.nick.custom")))
           return CommandResult.FAILURE_NO_PERMISSION;

        EazyNick.getInstance().getUtils().performRankedNick(
                player,
                args.withName("rank")
                        .map(CommandParameter::asText)
                        .orElse(null),
                args.withName("skin")
                        .map(CommandParameter::asText)
                        .orElse(null),
                args.withName("nickname")
                        .map(CommandParameter::asText)
                        .orElse(null)
        );

        return CommandResult.SUCCESS;
    }

}