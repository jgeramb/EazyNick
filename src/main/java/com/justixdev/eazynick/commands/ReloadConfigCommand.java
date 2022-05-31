package com.justixdev.eazynick.commands;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.utilities.Utils;
import com.justixdev.eazynick.utilities.configuration.yaml.LanguageYamlFile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReloadConfigCommand implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();

		if(!(sender instanceof Player)) {
			utils.sendConsole(utils.getNotPlayer());
			return true;
		}

		Player player = (Player) sender;

		if(!(player.hasPermission("eazynick.reload"))) {
			languageYamlFile.sendMessage(player, utils.getNoPerm());
			return true;
		}

		utils.reloadConfigs();

		languageYamlFile.sendMessage(
				player,
				languageYamlFile.getConfigString(player, "Messages.ReloadConfig")
						.replace("%prefix%", utils.getPrefix())
		);
		
		return true;
	}
	
}
