package net.dev.eazynick.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.PlayerUnnickEvent;
import net.dev.eazynick.sql.MySQLNickManager;
import net.dev.eazynick.sql.MySQLPlayerDataManager;
import net.dev.eazynick.utilities.Utils;
import net.dev.eazynick.utilities.configuration.yaml.LanguageYamlFile;
import net.dev.eazynick.utilities.configuration.yaml.SetupYamlFile;

public class NickOtherCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
		LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
		MySQLNickManager mysqlNickManager = eazyNick.getMySQLNickManager();
		MySQLPlayerDataManager mysqlPlayerDataManager = eazyNick.getMySQLPlayerDataManager();
		
		String prefix = utils.getPrefix();
		
		if(sender instanceof Player) {
			Player player = (Player) sender;
			
			if(player.hasPermission("eazynick.other.nick.random") || player.hasPermission("eazynick.other.nick.custom")) {
				if(args.length >= 1) {
					Player targetPlayer = Bukkit.getPlayer(args[0]);
					
					if(targetPlayer != null) {
						if(!(utils.getNickedPlayers().containsKey(targetPlayer.getUniqueId()))) {
							if(args.length >= 2) {
								if(player.hasPermission("eazynick.other.nick.custom")) {
									if(args[1].length() <= 16) {
										if(!(setupYamlFile.getConfiguration().getStringList("DisabledNickWorlds").contains(targetPlayer.getWorld().getName()))) {
											String name = args[1].trim(), formattedName = ChatColor.translateAlternateColorCodes('&', name);
											
											languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.Other.SelectedNick").replace("%playerName%", targetPlayer.getName()).replace("%playername%", targetPlayer.getName()).replace("%nickName%", formattedName).replace("%nickname%", formattedName).replace("%prefix%", prefix));
											
											utils.performNick(targetPlayer, formattedName);
										} else
											languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.Other.DisabledWorld").replace("%playerName%", targetPlayer.getName()).replace("%playername%", targetPlayer.getName()).replace("%prefix%", prefix));
									} else
										languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.NickTooLong").replace("%prefix%", prefix));
								} else
									languageYamlFile.sendMessage(player, utils.getNoPerm());
							} else {
								if(player.hasPermission("eazynick.other.nick.random")) {
									languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.Other.RandomNick").replace("%playerName%", targetPlayer.getName()).replace("%playername%", targetPlayer.getName()).replace("%prefix%", prefix));
									
									if(!(setupYamlFile.getConfiguration().getStringList("DisabledNickWorlds").contains(targetPlayer.getWorld().getName())))
										utils.performNick(targetPlayer, "RANDOM");
									else
										languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.Other.DisabledWorld").replace("%playerName%", targetPlayer.getName()).replace("%playername%", targetPlayer.getName()).replace("%prefix%", prefix));
								} else
									languageYamlFile.sendMessage(player, utils.getNoPerm());
							}
						} else if(player.hasPermission("eazynick.other.nick.reset")) {
							if((mysqlNickManager != null) && mysqlNickManager.isPlayerNicked(targetPlayer.getUniqueId()) && setupYamlFile.getConfiguration().getBoolean("LobbyMode") && setupYamlFile.getConfiguration().getBoolean("RemoveMySQLNickOnUnnickWhenLobbyModeEnabled")) {
								mysqlNickManager.removePlayer(targetPlayer.getUniqueId());
								mysqlPlayerDataManager.removeData(targetPlayer.getUniqueId());
								
								languageYamlFile.sendMessage(targetPlayer, eazyNick.getLanguageYamlFile().getConfigString(targetPlayer, "Messages.Unnick").replace("%prefix%", prefix));
								languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.Other.Unnick").replace("%playerName%", targetPlayer.getName()).replace("%playername%", targetPlayer.getName()).replace("%prefix%", prefix));
							} else { 
								languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.Other.Unnick").replace("%playerName%", targetPlayer.getName()).replace("%playername%", targetPlayer.getName()).replace("%prefix%", prefix));
								
								Bukkit.getPluginManager().callEvent(new PlayerUnnickEvent(targetPlayer));
							}
						} else
							languageYamlFile.sendMessage(player, utils.getNoPerm());
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
