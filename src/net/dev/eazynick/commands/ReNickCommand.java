package net.dev.eazynick.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.PlayerUnnickEvent;
import net.dev.eazynick.sql.MySQLNickManager;
import net.dev.eazynick.utilities.Utils;
import net.dev.eazynick.utilities.configuration.yaml.LanguageYamlFile;

public class ReNickCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
		MySQLNickManager mysqlNickManager = eazyNick.getMySQLNickManager();
		
		if(sender instanceof Player) {
			Player player = (Player) sender;
			
			if(utils.getNickOnWorldChangePlayers().contains(player.getUniqueId()) || ((mysqlNickManager != null) && mysqlNickManager.isPlayerNicked(player.getUniqueId()))) {
				if(utils.getNickedPlayers().containsKey(player.getUniqueId()))
					Bukkit.getPluginManager().callEvent(new PlayerUnnickEvent(player));
				else if(!(eazyNick.getSetupYamlFile().getConfiguration().getStringList("DisabledNickWorlds").contains(player.getWorld().getName())))
					utils.performReNick(player);
				else
					languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.DisabledWorld").replace("%prefix%", utils.getPrefix()));
			}
		} else
			utils.sendConsole(utils.getNotPlayer());
		
		return true;
	}
	
}
