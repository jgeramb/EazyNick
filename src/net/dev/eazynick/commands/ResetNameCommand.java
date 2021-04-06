package net.dev.eazynick.commands;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.NickManager;
import net.dev.eazynick.sql.MySQLNickManager;
import net.dev.eazynick.utilities.Utils;
import net.dev.eazynick.utilities.configuration.yaml.LanguageYamlFile;

public class ResetNameCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
		MySQLNickManager mysqlNickManager = eazyNick.getMySQLNickManager();
		
		if(sender instanceof Player) {
			Player player = (Player) sender;
			
			if(player.hasPermission("nick.name")) {
				NickManager api = new NickManager(player);
				api.setName(api.getRealName());
				
				if(mysqlNickManager != null)
					mysqlNickManager.removePlayer(player.getUniqueId());
				
				languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.ResetName").replace("%prefix%", utils.getPrefix()));
			} else
				languageYamlFile.sendMessage(player, utils.getNoPerm());
		} else
			utils.sendConsole(utils.getNotPlayer());
		
		return true;
	}
	
}
