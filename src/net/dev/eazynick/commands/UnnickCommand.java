package net.dev.eazynick.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.PlayerUnnickEvent;
import net.dev.eazynick.sql.MySQLNickManager;
import net.dev.eazynick.sql.MySQLPlayerDataManager;
import net.dev.eazynick.utilities.Utils;
import net.dev.eazynick.utilities.configuration.yaml.LanguageYamlFile;
import net.dev.eazynick.utilities.configuration.yaml.SetupYamlFile;

public class UnnickCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
		SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
		MySQLNickManager mysqlNickManager = eazyNick.getMySQLNickManager();
		MySQLPlayerDataManager mysqlPlayerDataManager = eazyNick.getMySQLPlayerDataManager();
		
		String prefix = utils.getPrefix();
		
		if(sender instanceof Player) {
			Player player = (Player) sender;
			
			if(player.hasPermission("nick.reset")) {
				if(utils.getNickedPlayers().containsKey(player.getUniqueId()))
					Bukkit.getPluginManager().callEvent(new PlayerUnnickEvent(player));
				 else if((mysqlNickManager != null) && mysqlNickManager.isPlayerNicked(player.getUniqueId()) && setupYamlFile.getConfiguration().getBoolean("LobbyMode") && setupYamlFile.getConfiguration().getBoolean("RemoveMySQLNickOnUnnickWhenLobbyModeEnabled")) {
					mysqlNickManager.removePlayer(player.getUniqueId());
					mysqlPlayerDataManager.removeData(player.getUniqueId());
					
					languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.Unnick").replace("%prefix%", prefix));
				} else
					languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.NotNicked").replace("%prefix%", prefix));
			} else
				languageYamlFile.sendMessage(player, utils.getNoPerm());
		} else
			utils.sendConsole(utils.getNotPlayer());
		
		return true;
	}

}
