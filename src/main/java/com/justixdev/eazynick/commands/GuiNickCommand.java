package com.justixdev.eazynick.commands;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.api.NickManager;
import com.justixdev.eazynick.utilities.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GuiNickCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        EazyNick eazyNick = EazyNick.getInstance();
        Utils utils = eazyNick.getUtils();

        if(!(sender instanceof Player)) {
            utils.sendConsole(utils.getNotPlayer());
            return true;
        }

        Player player = (Player) sender;

        if(!(player.hasPermission("eazynick.nick.random")
                || player.hasPermission("eazynick.nick.custom"))) {
            eazyNick.getLanguageYamlFile().sendMessage(player, utils.getNoPerm());
            return true;
        }

        NickManager api = new NickManager(player);

        if(api.isNicked() || (args.length < 3)) return true;

        utils.performRankedNick(player, args[0], args[1], args[2]);

        return true;
    }

}