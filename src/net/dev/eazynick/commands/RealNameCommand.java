package net.dev.eazynick.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dev.eazynick.api.NickManager;
import net.dev.eazynick.utils.LanguageFileUtils;
import net.dev.eazynick.utils.Utils;

public class RealNameCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(p.hasPermission("nick.realname")) {
				if(args.length >= 1) {
					Player t = Bukkit.getPlayer(args[0]);
					
					if(t != null) {
						if(Utils.nickedPlayers.contains(t.getUniqueId()))
							p.sendMessage(Utils.prefix + LanguageFileUtils.getConfigString("Messages.RealName").replace("%realName%", new NickManager(t).getRealName()));
						else
							p.sendMessage(Utils.prefix + LanguageFileUtils.getConfigString("Messages.PlayerNotNicked"));
					} else
						p.sendMessage(Utils.prefix + LanguageFileUtils.getConfigString("Messages.PlayerNotFound"));
				}
			} else
				p.sendMessage(Utils.noPerm);
		} else
			Utils.sendConsole(Utils.notPlayer);
		
		return true;
	}
	
}
