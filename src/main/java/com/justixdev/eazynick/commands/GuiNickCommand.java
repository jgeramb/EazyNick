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
		
		if(sender instanceof Player) {
			Player player = (Player) sender;
			
			if(player.hasPermission("eazynick.nick.random") || player.hasPermission("eazynick.nick.custom")) {
				NickManager api = new NickManager(player);
				
				if(!(api.isNicked())) {
					if(args.length >= 3)
						utils.performRankedNick(player, args[0], args[1], args[2]);
				}
			} else
				eazyNick.getLanguageYamlFile().sendMessage(player, utils.getNoPerm());
		} else
			utils.sendConsole(utils.getNotPlayer());
		
		return true;
	}

}