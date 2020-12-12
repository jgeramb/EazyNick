package net.dev.eazynick.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.PlayerUnnickEvent;
import net.dev.eazynick.utils.*;

public class NickOtherCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		FileUtils fileUtils = eazyNick.getFileUtils();
		LanguageFileUtils languageFileUtils = eazyNick.getLanguageFileUtils();
		
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(p.hasPermission("nick.other")) {
				if(args.length >= 1) {
					Player t = Bukkit.getPlayer(args[0]);
					
					if(t != null) {
						if(!(utils.getNickedPlayers().contains(t.getUniqueId()))) {
							if(args.length >= 2) {
								if(args[1].length() <= 16) {
									if(!(fileUtils.getConfig().getStringList("DisabledNickWorlds").contains(t.getWorld().getName()))) {
										String name = args[1].trim(), formattedName = ChatColor.translateAlternateColorCodes('&', name);
										
										p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString(t, "Messages.Other.SelectedNick").replace("%playerName%", t.getName()).replace("%playername%", t.getName()).replace("%nickName%", formattedName).replace("%nickname%", formattedName));
										
										utils.performNick(t, name);
									} else
										p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString(p, "Messages.Other.DisabledWorld").replace("%playerName%", t.getName()).replace("%playername%", t.getName()));
								} else
									p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString(p, "Messages.NickTooLong"));
							} else {
								p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString(t, "Messages.Other.RandomNick").replace("%playerName%", t.getName()).replace("%playername%", t.getName()));
								
								if(!(fileUtils.getConfig().getStringList("DisabledNickWorlds").contains(t.getWorld().getName())))
									utils.performNick(t, "RANDOM");
								else
									p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString(p, "Messages.Other.DisabledWorld").replace("%playerName%", t.getName()).replace("%playername%", t.getName()));
							}
						} else { 
							p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString(t, "Messages.Other.Unnick").replace("%playerName%", t.getName()).replace("%playername%", t.getName()));
							
							Bukkit.getPluginManager().callEvent(new PlayerUnnickEvent(t));
						}
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
