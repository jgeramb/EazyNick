package net.dev.eazynick.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.NickManager;
import net.dev.eazynick.utilities.Utils;
import net.dev.eazynick.utilities.configuration.yaml.LanguageYamlFile;

public class RealNameCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
		
		String prefix = utils.getPrefix();
		
		if(sender instanceof Player) {
			Player player = (Player) sender;
			
			if(player.hasPermission("nick.realname")) {
				if(args.length >= 1) {
					Player targetPlayer = Bukkit.getPlayer(args[0]);
					
					if(targetPlayer != null) {
						if(utils.getNickedPlayers().containsKey(targetPlayer.getUniqueId())) {
							String realName = new NickManager(targetPlayer).getRealName();
							
							languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.RealName").replace("%realName%", realName).replace("%realname%", realName).replace("%prefix%", prefix));
						} else
							languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.PlayerNotNicked").replace("%prefix%", prefix));
					} else
						languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.PlayerNotFound").replace("%prefix%", prefix));
				}
			} else
				languageYamlFile.sendMessage(player, utils.getNoPerm());
		} else
			utils.sendConsole(utils.getNotPlayer());
		
		return true;
	}
	
}
