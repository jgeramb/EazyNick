package net.dev.eazynick.commands;

import java.util.Random;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.NickManager;
import net.dev.eazynick.utils.LanguageFileUtils;
import net.dev.eazynick.utils.Utils;

public class ChangeSkinCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		LanguageFileUtils languageFileUtils = eazyNick.getLanguageFileUtils();
		
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(p.hasPermission("nick.skin")) {
				if(utils.getCanUseNick().get(p.getUniqueId())) {
					NickManager api = new NickManager(p);
					
					if(args.length >= 1) {
						String name = args[0];
						
						api.changeSkin(name);
						api.updatePlayer();
						
						p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString("Messages.SkinChanged").replace("%skinName%", name));
					} else {
						String name = eazyNick.getFileUtils().getConfig().getBoolean("UseMineSkinAPI") ? "MineSkin" : utils.getNickNames().get((new Random().nextInt(utils.getNickNames().size())));
						
						api.changeSkin(name);
						api.updatePlayer();
						
						p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString("Messages.SkinChanged").replace("%skinName%", name));
					}
				} else
					p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString("Messages.NickDelay"));
			} else
				p.sendMessage(utils.getNoPerm());
		} else
			utils.sendConsole(utils.getNotPlayer());
		
		return true;
	}
	
}
