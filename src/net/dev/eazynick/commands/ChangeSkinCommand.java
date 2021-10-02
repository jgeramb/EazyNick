package net.dev.eazynick.commands;

import java.util.Random;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.NickManager;
import net.dev.eazynick.utilities.Utils;
import net.dev.eazynick.utilities.configuration.yaml.LanguageYamlFile;
import net.dev.eazynick.utilities.configuration.yaml.SetupYamlFile;

public class ChangeSkinCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
		SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
		
		String prefix = utils.getPrefix();
		
		if(sender instanceof Player) {
			Player player = (Player) sender;
			
			if(utils.getCanUseNick().get(player.getUniqueId())) {
				NickManager api = new NickManager(player);
				
				if(args.length >= 1) {
					if(player.hasPermission("eazynick.skin.custom")) {
						String name = args[0];
						
						api.changeSkin(name);
						
						languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.SkinChanged").replace("%skinName%", name).replace("%skinname%", name).replace("%prefix%", prefix));
					} else
						languageYamlFile.sendMessage(player, utils.getNoPerm());
				} else if(player.hasPermission("eazynick.skin.random")) {
					String name = setupYamlFile.getConfiguration().getBoolean("UseMineSkinAPI") ? ("MINESKIN:" + utils.getRandomStringFromList(setupYamlFile.getConfiguration().getStringList("MineSkinIds"))) : utils.getNickNames().get((new Random().nextInt(utils.getNickNames().size())));
					
					api.changeSkin(name);
					
					languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.SkinChanged").replace("%skinName%", name).replace("%skinname%", name).replace("%prefix%", prefix));
				} else
					languageYamlFile.sendMessage(player, utils.getNoPerm());
			} else
				languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.NickDelay").replace("%prefix%", prefix));
		} else
			utils.sendConsole(utils.getNotPlayer());
		
		return true;
	}
	
}
