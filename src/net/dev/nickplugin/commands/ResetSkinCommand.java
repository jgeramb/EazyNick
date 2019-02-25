package net.dev.nickplugin.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dev.nickplugin.utils.FileUtils;
import net.dev.nickplugin.utils.NickManager;
import net.dev.nickplugin.utils.Utils;

public class ResetSkinCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(p.hasPermission("nick.skin") || Utils.hasLuckPermsPermission(p.getUniqueId(), "nick.skin")) {
				NickManager api = new NickManager(p);
				
				api.changeSkin(api.getRealName());
				
				p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Messages.ResetSkin")));
			} else {
				p.sendMessage(Utils.NO_PERM);
			}
		} else {
			Utils.sendConsole(Utils.NOT_PLAYER);
		}
		
		return true;
	}
	
}
