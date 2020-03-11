package net.dev.eazynick.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dev.eazynick.utils.PagesHandler;
import net.dev.eazynick.utils.Utils;

public class NickListCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(p.hasPermission("nick.gui")) {
				if(Utils.nickedPlayers.contains(p.getUniqueId()))
					p.chat("/unnick");
				
				Utils.nickNamesHandler = new PagesHandler(36);

				for (String name : Utils.nickNames)
					Utils.nickNamesHandler.addObject(name);
				
				Utils.nickNamesHandler.createPage(p, 0);
				Utils.nickNameListPage.put(p.getUniqueId(), 0);
			} else
				p.sendMessage(Utils.noPerm);
		} else
			Utils.sendConsole(Utils.notPlayer);
		
		return true;
	}

}
