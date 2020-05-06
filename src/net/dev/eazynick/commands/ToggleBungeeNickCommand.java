package net.dev.eazynick.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.utils.Utils;

public class ToggleBungeeNickCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(p.hasPermission("nick.use") && p.hasPermission("nick.item")) {
				if (eazyNick.getFileUtils().cfg.getBoolean("BungeeCord"))
					utils.toggleBungeeNick(p);
			} else
				p.sendMessage(utils.getNoPerm());
		} else
			utils.sendConsole(utils.getNotPlayer());
		
		return true;
	}
	
}
