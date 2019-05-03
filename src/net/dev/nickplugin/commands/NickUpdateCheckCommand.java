package net.dev.nickplugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dev.nickplugin.updater.SpigotUpdater;
import net.dev.nickplugin.utils.Utils;

public class NickUpdateCheckCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(p.hasPermission("nick.checkforupdates") || Utils.hasLuckPermsPermission(p.getUniqueId(), "nick.checkforupdates")) {
				SpigotUpdater.checkForUpdates(p);
			} else {
				p.sendMessage(Utils.noPerm);
			}
		} else {
			Utils.sendConsole(Utils.notPlayer);
		}
		
		return true;
	}

}