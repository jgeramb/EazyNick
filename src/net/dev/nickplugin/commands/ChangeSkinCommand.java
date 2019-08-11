package net.dev.nickplugin.commands;

import java.util.Random;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dev.nickplugin.api.NickManager;
import net.dev.nickplugin.utils.LanguageFileUtils;
import net.dev.nickplugin.utils.Utils;

public class ChangeSkinCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(p.hasPermission("nick.skin")) {
				if((Utils.canUseNick.get(p.getUniqueId()))) {
					NickManager api = new NickManager(p);
					
					if(args.length >= 1) {
						String name = args[0];
						
						api.changeSkin(name);
						api.updatePlayer();
						
						p.sendMessage(Utils.prefix + LanguageFileUtils.getConfigString("Messages.SkinChanged").replace("%skinName%", name));
					} else {
						String name = Utils.nickNames.get((new Random().nextInt(Utils.nickNames.size())));
						
						api.changeSkin(name);		
						
						p.sendMessage(Utils.prefix + LanguageFileUtils.getConfigString("Messages.SkinChanged").replace("%skinName%", name));
					}
				} else
					p.sendMessage(Utils.prefix + LanguageFileUtils.getConfigString("Messages.NickDelay"));
			} else
				p.sendMessage(Utils.noPerm);
		} else
			Utils.sendConsole(Utils.notPlayer);
		
		return true;
	}
	
}
