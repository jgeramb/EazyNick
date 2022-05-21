package com.justixdev.eazynick.commands;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.api.NickManager;
import com.justixdev.eazynick.utilities.Utils;
import com.justixdev.eazynick.utilities.configuration.yaml.LanguageYamlFile;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ResetSkinOtherCommand implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
		
		String prefix = utils.getPrefix();
		
		if(sender instanceof Player) {
			Player player = (Player) sender;
			
			if(player.hasPermission("eazynick.other.skin.reset")) {
				if(args.length >= 1) {
					Player targetPlayer = Bukkit.getPlayer(args[0]);
					
					if(targetPlayer != null) {
						NickManager api = new NickManager(targetPlayer);
				
						api.changeSkin(api.getRealName());
						
						languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.Other.ResetSkin").replace("%playerName%", targetPlayer.getName()).replace("%playername%", targetPlayer.getName()).replace("%prefix%", utils.getPrefix()));
					} else
						languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.PlayerNotFound").replace("%prefix%", prefix));
				}
			} else
				languageYamlFile.sendMessage(player, utils.getNoPerm());
		} else
			utils.sendConsole(utils.getNotPlayer());
		
		return true;
	}
	
}
