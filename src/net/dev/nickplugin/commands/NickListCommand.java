package net.dev.nickplugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dev.nickplugin.utils.PagesHandler;
import net.dev.nickplugin.utils.Utils;

public class NickListCommand implements CommandExecutor {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(p.hasPermission("nick.gui") || Utils.hasLuckPermsPermission(p.getUniqueId(), "nick.gui")) {
				if(Utils.nickedPlayers.contains(p.getUniqueId())) {
					p.chat("/nick");
				}
				
				Utils.nickNamesHandler = new PagesHandler(36);

				for (String name : Utils.nickNames) {
					Utils.nickNamesHandler.addObject(name);
				}
				
				Utils.nickNamesHandler.createPage(p, 0);
				Utils.nickNameListPage.put(p.getUniqueId(), 0);
			} else {
				p.sendMessage(Utils.NO_PERM);
			}
		} else {
			Utils.sendConsole(Utils.NOT_PLAYER);
		}
		
		return true;
	}

}
