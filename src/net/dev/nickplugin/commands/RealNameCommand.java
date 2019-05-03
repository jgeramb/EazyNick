package net.dev.nickplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dev.nickplugin.api.NickManager;
import net.dev.nickplugin.utils.LanguageFileUtils;
import net.dev.nickplugin.utils.Utils;

public class RealNameCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(p.hasPermission("nick.real") || Utils.hasLuckPermsPermission(p.getUniqueId(), "nick.real")) {
				if(args.length >= 1) {
					Player t = Bukkit.getPlayer(args[0]);
					
					if(t != null) {
						if(Utils.nickedPlayers.contains(t.getUniqueId())) {
							p.sendMessage(Utils.prefix + ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("Messages.RealName").replace("%realName%", new NickManager(t).getRealName())));
						} else {
							p.sendMessage(Utils.prefix + ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("Messages.PlayerNotNicked")));
						}
					} else {
						p.sendMessage(Utils.prefix + ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("Messages.PlayerNotFound")));
					}					
				}
			} else {
				p.sendMessage(Utils.noPerm);
			}
		} else {
			Utils.sendConsole(Utils.notPlayer);
		}
		
		return true;
	}
	
}
