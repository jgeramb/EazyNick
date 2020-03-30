package net.dev.eazynick.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.utils.PagesHandler;
import net.dev.eazynick.utils.Utils;

public class NickListCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Utils utils = EazyNick.getInstance().getUtils();
		
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(p.hasPermission("nick.gui")) {
				if(utils.getNickedPlayers().contains(p.getUniqueId()))
					p.chat("/unnick");
				
				utils.setNickNamesHandler(new PagesHandler(36));

				for (String name : utils.getNickNames())
					utils.getNickNamesHandler().addObject(name);
				
				utils.getNickNamesHandler().createPage(p, 0);
				utils.getNickNameListPage().put(p.getUniqueId(), 0);
			} else
				p.sendMessage(utils.getNoPerm());
		} else
			utils.sendConsole(utils.getNotPlayer());
		
		return true;
	}

}
