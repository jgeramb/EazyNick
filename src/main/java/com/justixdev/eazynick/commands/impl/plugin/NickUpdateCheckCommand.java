package com.justixdev.eazynick.commands.impl.plugin;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.commands.Command;
import com.justixdev.eazynick.commands.CommandResult;
import com.justixdev.eazynick.commands.CustomCommand;
import com.justixdev.eazynick.commands.parameters.ParameterCombination;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CustomCommand(name = "eazynickupdatecheck", description = "Checks for updates for the plugin")
public class NickUpdateCheckCommand extends Command {

    @Override
    public CommandResult execute(CommandSender sender, ParameterCombination args) {
        Player player = (Player) sender;

        if(!player.hasPermission("eazynick.updatecheck"))
            return CommandResult.FAILURE_NO_PERMISSION;

        EazyNick.getInstance().getUpdater().checkForUpdates(player);

        return CommandResult.SUCCESS;
    }

}