package net.dev.eazynick.commands;

import java.util.Random;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.NickManager;
import net.dev.eazynick.utilities.Utils;
import net.dev.eazynick.utilities.configuration.yaml.LanguageYamlFile;

public class ChangeSkinCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
		
		String prefix = utils.getPrefix();
		
		if(sender instanceof Player) {
			Player player = (Player) sender;
			
			if(player.hasPermission("nick.skin")) {
				if(utils.getCanUseNick().get(player.getUniqueId())) {
					NickManager api = new NickManager(player);
					
					if(args.length >= 1) {
						String name = args[0];
						
						api.changeSkin(name);
						
						languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.SkinChanged").replace("%skinName%", name).replace("%skinname%", name).replace("%prefix%", prefix));
					} else {
						String name = eazyNick.getSetupYamlFile().getConfiguration().getBoolean("UseMineSkinAPI") ? "MineSkin" : utils.getNickNames().get((new Random().nextInt(utils.getNickNames().size())));
						
						api.changeSkin(name);
						
						languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.SkinChanged").replace("%skinName%", name).replace("%skinname%", name).replace("%prefix%", prefix));
					}
				} else
					languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.NickDelay").replace("%prefix%", prefix));
			} else
				languageYamlFile.sendMessage(player, utils.getNoPerm());
		} else
			utils.sendConsole(utils.getNotPlayer());
		
		return true;
	}
	
}
