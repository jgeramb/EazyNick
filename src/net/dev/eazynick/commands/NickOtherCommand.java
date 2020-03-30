package net.dev.eazynick.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.utils.LanguageFileUtils;
import net.dev.eazynick.utils.Utils;

public class NickOtherCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
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
									String name = args[1].trim();
									
									p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString("Messages.Other.SelectedNick").replace("%playerName%", t.getName()).replace("%nickName%", ChatColor.translateAlternateColorCodes('&', name)));
									
									if((!(t.hasPermission("nick.use"))) || !(t.hasPermission("nick.customnickname"))) {
										PermissionAttachment pa = t.addAttachment(EazyNick.getInstance());
										pa.setPermission("nick.use", true);
										pa.setPermission("nick.customnickname", true);
										t.recalculatePermissions();
										
										t.chat("/nick " + name);
										
										t.removeAttachment(pa);
										t.recalculatePermissions();
									} else
										t.chat("/nick " + name);
								} else {
									p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString("Messages.NickTooLong"));
								}
							} else {
								p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString("Messages.Other.RandomNick").replace("%playerName%", t.getName()));
								
								if(!(t.hasPermission("nick.use"))) {
									PermissionAttachment pa = t.addAttachment(EazyNick.getInstance());
									pa.setPermission("nick.use", true);
									t.recalculatePermissions();
									
									t.chat("/nick");
									
									t.removeAttachment(pa);
									t.recalculatePermissions();
								} else
									t.chat("/nick");
							}
						} else {
							p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString("Messages.Other.Unnick").replace("%playerName%", t.getName()));
							
							if(!(t.hasPermission("nick.use"))) {
								PermissionAttachment pa = t.addAttachment(EazyNick.getInstance());
								pa.setPermission("nick.use", true);
								t.recalculatePermissions();
								
								t.chat("/unnick");
								
								t.removeAttachment(pa);
								t.recalculatePermissions();
							} else
								t.chat("/unnick");
						}
					} else
						p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString("Messages.PlayerNotFound"));
				}
			} else
				p.sendMessage(utils.getNoPerm());
		} else
			utils.sendConsole(utils.getNotPlayer());
		
		return true;
	}

}
