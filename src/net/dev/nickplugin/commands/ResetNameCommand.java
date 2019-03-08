package net.dev.nickplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dev.nickplugin.sql.MySQLNickManager;
import net.dev.nickplugin.sql.MySQLPlayerDataManager;
import net.dev.nickplugin.utils.FileUtils;
import net.dev.nickplugin.utils.NickManager;
import net.dev.nickplugin.utils.Utils;

public class ResetNameCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(p.hasPermission("nick.name") || Utils.hasLuckPermsPermission(p.getUniqueId(), "nick.name")) {
				NickManager api = new NickManager(p);
				
				api.setName(api.getRealName());
				api.refreshPlayer();
				
				if(Utils.luckPermsStatus()) {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " permission unset prefix.99." + Utils.luckPermsPrefixes.get(p.getUniqueId()));
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " permission unset suffix.99." + Utils.luckPermsSuffixes.get(p.getUniqueId()));
				
					Utils.luckPermsPrefixes.remove(p.getUniqueId());
					Utils.luckPermsSuffixes.remove(p.getUniqueId());
				}
				
				MySQLPlayerDataManager.removeData(p.getUniqueId());
				MySQLNickManager.removePlayer(p.getUniqueId());
				
				p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Messages.ResetName")));
			} else {
				p.sendMessage(Utils.NO_PERM);
			}
		} else {
			Utils.sendConsole(Utils.NOT_PLAYER);
		}
		
		return true;
	}
	
}
