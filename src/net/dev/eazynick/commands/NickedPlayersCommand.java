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

public class NickedPlayersCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		LanguageFileUtils languageFileUtils = eazyNick.getLanguageFileUtils();
		
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(p.hasPermission("nick.nickedplayers")) {
				boolean playerIsNicked = false;
				
				for (Player all : Bukkit.getOnlinePlayers()) {
					NickManager api = new NickManager(all);
					
					if(api.isNicked())
						playerIsNicked = true;
				}
				
				if(playerIsNicked) {
					p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString("Messages.NickedPlayers.CurrentNickedPlayers"));
					
					for (Player all : Bukkit.getOnlinePlayers()) {
						NickManager api = new NickManager(all);
						
						if(api.isNicked())
							p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString("Messages.NickedPlayers.PlayerINFO").replace("%realName%", api.getRealName()).replace("%nickName%", api.getNickName()));
					}
				} else
					p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString("Messages.NickedPlayers.NoPlayerIsNicked"));
			} else
				p.sendMessage(utils.getNoPerm());
		} else
			utils.sendConsole(utils.getNotPlayer());
		
		return true;
	}

}
