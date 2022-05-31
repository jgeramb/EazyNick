package com.justixdev.eazynick.commands;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.utilities.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ToggleBungeeNickCommand implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();

		if(!(sender instanceof Player)) {
			utils.sendConsole(utils.getNotPlayer());
			return true;
		}

		Player player = (Player) sender;

		if(!(player.hasPermission("eazynick.nick.random") && player.hasPermission("eazynick.item"))) {
			eazyNick.getLanguageYamlFile().sendMessage(player, utils.getNoPerm());
			return true;
		}

		if (eazyNick.getSetupYamlFile().getConfiguration().getBoolean("BungeeCord"))
			utils.toggleBungeeNick(player);
		
		return true;
	}
	
}
