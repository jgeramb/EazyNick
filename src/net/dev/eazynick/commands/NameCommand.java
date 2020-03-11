package net.dev.eazynick.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dev.eazynick.api.NickManager;
import net.dev.eazynick.utils.LanguageFileUtils;
import net.dev.eazynick.utils.Utils;

public class NameCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(p.hasPermission("nick.use")) {
				NickManager api = new NickManager(p);
				
				if(api.isNicked()) {
					p.sendMessage(Utils.prefix + LanguageFileUtils.getConfigString("Messages.Name").replace("%name%", api.getNickName()));
				} else
					p.sendMessage(Utils.prefix + LanguageFileUtils.getConfigString("Messages.NotNicked"));
			} else
				p.sendMessage(Utils.noPerm);
		} else
			Utils.sendConsole(Utils.notPlayer);
		
		return true;
	}
	
}
