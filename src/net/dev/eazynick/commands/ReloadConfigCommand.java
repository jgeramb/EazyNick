package net.dev.eazynick.commands;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.utilities.Utils;
import net.dev.eazynick.utilities.configuration.yaml.LanguageYamlFile;

public class ReloadConfigCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
		
		if(sender instanceof Player) {
			Player player = (Player) sender;
			
			if(player.hasPermission("nick.reload")) {
				utils.reloadConfigs();
				
				languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.ReloadConfig").replace("%prefix%", utils.getPrefix()));
			} else
				languageYamlFile.sendMessage(player, utils.getNoPerm());
		} else
			utils.sendConsole(utils.getNotPlayer());
		
		return true;
	}
	
}
