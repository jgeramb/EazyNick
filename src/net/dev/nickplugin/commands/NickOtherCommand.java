package net.dev.nickplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import net.dev.nickplugin.main.Main;
import net.dev.nickplugin.utils.FileUtils;
import net.dev.nickplugin.utils.Utils;

public class NickOtherCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(p.hasPermission("nick.other") || Utils.hasLuckPermsPermission(p.getUniqueId(), "nick.other")) {
				if(args.length >= 1) {
					Player t = Bukkit.getPlayer(args[0]);
					
					if(t != null) {
						if(!(Utils.nickedPlayers.contains(t.getUniqueId()))) {
							if(args.length >= 2) {
								if(args[1].length() <= 16) {
									String name = args[1].trim();
									
									p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Messages.Other.SelectedNick")).replace("%playerName%", t.getName()).replace("%nickName%", ChatColor.translateAlternateColorCodes('&', name)));
									
									if(!(t.hasPermission("nick.use"))) {
										PermissionAttachment pa = t.addAttachment(Main.getPlugin(Main.class));
										pa.setPermission("nick.use", true);
										pa.setPermission("nick.customnickname", true);
										t.recalculatePermissions();
										
										t.chat("/nick " + name);
										
										t.removeAttachment(pa);
										t.recalculatePermissions();
									} else {
										t.chat("/nick " + name);
									}
								} else {
									p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Messages.NickTooLong")));
								}
							} else {
								p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Messages.Other.RandomNick")).replace("%playerName%", t.getName()));
								
								if(!(t.hasPermission("nick.use"))) {
									PermissionAttachment pa = t.addAttachment(Main.getPlugin(Main.class));
									pa.setPermission("nick.use", true);
									t.recalculatePermissions();
									
									t.chat("/nick");
									
									t.removeAttachment(pa);
									t.recalculatePermissions();
								} else {
									t.chat("/nick");
								}
							}
						} else {
							p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Messages.Other.Unnick")).replace("%playerName%", t.getName()));
							
							if(!(t.hasPermission("nick.use"))) {
								PermissionAttachment pa = t.addAttachment(Main.getPlugin(Main.class));
								pa.setPermission("nick.use", true);
								t.recalculatePermissions();
								
								t.chat("/unnick");
								
								t.removeAttachment(pa);
								t.recalculatePermissions();
							} else {
								t.chat("/unnick");
							}
						}
					} else {
						p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Messages.PlayerNotFound")));
					}	
				}
			} else {
				p.sendMessage(Utils.NO_PERM);
			}
		} else {
			Utils.sendConsole(Utils.NOT_PLAYER);
		}
		
		return true;
	}

}
