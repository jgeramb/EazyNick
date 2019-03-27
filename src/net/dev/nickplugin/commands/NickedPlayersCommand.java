package net.dev.nickplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dev.nickplugin.utils.LanguageFileUtils;
import net.dev.nickplugin.utils.NickManager;
import net.dev.nickplugin.utils.Utils;

public class NickedPlayersCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(p.hasPermission("nick.nickedplayers") || Utils.hasLuckPermsPermission(p.getUniqueId(), "nick.nickedplayers")) {
				boolean playerIsNicked = false;
				
				for (Player all : Bukkit.getOnlinePlayers()) {
					NickManager api = new NickManager(all);
					
					if(api.isNicked()) {
						playerIsNicked = true;
					}
				}
				
				if(playerIsNicked) {
					p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("Messages.NickedPlayers.CurrentNickedPlayers")));
					
					for (Player all : Bukkit.getOnlinePlayers()) {
						NickManager api = new NickManager(all);
						
						if(api.isNicked()) {
							p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("Messages.NickedPlayers.PlayerINFO")).replace("%realName%", api.getRealName()).replace("%nickName%", api.getNickName()));
						}
					}
				} else {
					p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("Messages.NickedPlayers.NoPlayerIsNicked")));
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
