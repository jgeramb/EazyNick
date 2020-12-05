package net.dev.eazynick.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.PlayerUnnickEvent;
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
									String name = args[1].trim(), formattedName = ChatColor.translateAlternateColorCodes('&', name);
									
									p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString(t, "Messages.Other.SelectedNick").replace("%playerName%", t.getName()).replace("%playername%", t.getName()).replace("%nickName%", formattedName).replace("%nickname%", formattedName));
									
									if((!(t.hasPermission("nick.use"))) || !(t.hasPermission("nick.customnickname"))) {
										PermissionAttachment pa = t.addAttachment(eazyNick);
										pa.setPermission("nick.use", true);
										pa.setPermission("nick.customnickname", true);
										t.recalculatePermissions();
										
										utils.performNick(t, name);
										
										t.removeAttachment(pa);
										t.recalculatePermissions();
									} else
										utils.performNick(t, name);
								} else
									p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString(p, "Messages.NickTooLong"));
							} else {
								p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString(t, "Messages.Other.RandomNick").replace("%playerName%", t.getName()).replace("%playername%", t.getName()));
								
								if(!(t.hasPermission("nick.use"))) {
									PermissionAttachment pa = t.addAttachment(eazyNick);
									pa.setPermission("nick.use", true);
									t.recalculatePermissions();
									
									utils.performNick(t, "RANDOM");
									
									t.removeAttachment(pa);
									t.recalculatePermissions();
								} else
									utils.performNick(t, "RANDOM");
							}
						} else {
							p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString(t, "Messages.Other.Unnick").replace("%playerName%", t.getName()).replace("%playername%", t.getName()));
							
							Bukkit.getPluginManager().callEvent(new PlayerUnnickEvent(t));
							
							if(!(t.hasPermission("nick.use"))) {
								PermissionAttachment pa = t.addAttachment(eazyNick);
								pa.setPermission("nick.use", true);
								t.recalculatePermissions();
								
								t.removeAttachment(pa);
								t.recalculatePermissions();
							}
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
