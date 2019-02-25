package net.dev.nickplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dev.nickplugin.utils.FileUtils;
import net.dev.nickplugin.utils.Utils;
import net.dev.nickplugin.utils.nickutils.UUIDFetcher_1_8_R1;

public class RealNameCommand_1_8_R1 implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(p.hasPermission("nick.real") || Utils.hasLuckPermsPermission(p.getUniqueId(), "nick.real")) {
				if(args.length >= 1) {
					Player t = Bukkit.getPlayer(args[0]);
					
					if(t != null) {
						if(Utils.nickedPlayers.contains(p.getUniqueId())) {
							p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Messages.RealName").replace("%realName%", UUIDFetcher_1_8_R1.getName(t.getUniqueId()))));
						} else {
							p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Messages.PlayerNotNicked")));
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
