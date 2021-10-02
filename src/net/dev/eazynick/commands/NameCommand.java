package net.dev.eazynick.commands;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.NickManager;
import net.dev.eazynick.utilities.Utils;
import net.dev.eazynick.utilities.configuration.yaml.LanguageYamlFile;

public class NameCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
		
		String prefix = utils.getPrefix();
		
		if(sender instanceof Player) {
			Player player = (Player) sender;
			NickManager api = new NickManager(player);
			
			if(api.isNicked())
				languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.Name").replace("%name%", api.getNickName()).replace("%prefix%", prefix));
		} else
			utils.sendConsole(utils.getNotPlayer());
		
		return true;
	}
	
}
