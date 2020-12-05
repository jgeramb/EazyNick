package net.dev.eazynick.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.NickManager;
import net.dev.eazynick.utils.LanguageFileUtils;
import net.dev.eazynick.utils.Utils;

public class RealNameCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		LanguageFileUtils languageFileUtils = eazyNick.getLanguageFileUtils();
		
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(p.hasPermission("nick.realname")) {
				if(args.length >= 1) {
					Player t = Bukkit.getPlayer(args[0]);
					
					if(t != null) {
						if(utils.getNickedPlayers().contains(t.getUniqueId())) {
							String realName = new NickManager(t).getRealName();
							
							p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString(p, "Messages.RealName").replace("%realName%", realName).replace("%realname%", realName));
						} else
							p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString(p, "Messages.PlayerNotNicked"));
					} else
						p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString(p, "Messages.PlayerNotFound"));
				}
			} else
				p.sendMessage(utils.getNoPerm());
		} else
			utils.sendConsole(utils.getNotPlayer());
		
		return true;
	}
	
}
