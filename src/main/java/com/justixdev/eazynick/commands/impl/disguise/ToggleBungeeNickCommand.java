package com.justixdev.eazynick.commands.impl.disguise;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.commands.Command;
import com.justixdev.eazynick.commands.CommandResult;
import com.justixdev.eazynick.commands.CustomCommand;
import com.justixdev.eazynick.commands.parameters.ParameterCombination;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CustomCommand(name = "togglebungeenick", description = "Toggles the automatic nickname on server switch")
public class ToggleBungeeNickCommand extends Command {

    @Override
    protected void initAliases() {
        super.initAliases();

        this.aliases.add("togglenick");
        this.aliases.add("togglebungeedisguise");
        this.aliases.add("toggledisguise");
    }

    @Override
    public CommandResult execute(CommandSender sender, ParameterCombination args) {
        EazyNick eazyNick = EazyNick.getInstance();

        Player player = (Player) sender;

        if(!(player.hasPermission("eazynick.nick.random") && player.hasPermission("eazynick.item")))
            return CommandResult.FAILURE_NO_PERMISSION;

        if (eazyNick.getSetupYamlFile().getConfiguration().getBoolean("BungeeCord"))
            eazyNick.getUtils().toggleBungeeNick(player);

        return CommandResult.SUCCESS;
    }

}
