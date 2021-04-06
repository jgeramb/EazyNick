package net.dev.eazynick.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerQuitEvent;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.NickManager;
import net.dev.eazynick.api.NickedPlayerData;
import net.dev.eazynick.sql.MySQLNickManager;
import net.dev.eazynick.sql.MySQLPlayerDataManager;
import net.dev.eazynick.utilities.Utils;
import net.dev.eazynick.utilities.configuration.yaml.SetupYamlFile;

public class PlayerQuitListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
		MySQLNickManager mysqlNickManager = eazyNick.getMySQLNickManager();
		MySQLPlayerDataManager mysqlPlayerDataManager = eazyNick.getMySQLPlayerDataManager();
		
		Player player = event.getPlayer();
		NickManager api = new NickManager(player);

		if(utils.getNameCache().containsKey(player.getUniqueId()))
			utils.getNameCache().remove(player.getUniqueId());
		
		if (api.isNicked()) {
			NickedPlayerData nickedPlayerData = utils.getNickedPlayers().get(player.getUniqueId()).clone();
			
			if (setupYamlFile.getConfiguration().getBoolean("DisconnectUnnick") || setupYamlFile.getConfiguration().getBoolean("BungeeCord"))
				api.unnickPlayerWithoutRemovingMySQL(false);
			else
				api.unnickPlayerWithoutRemovingMySQL(true);
			
			if(setupYamlFile.getConfiguration().getBoolean("OverwriteJoinQuitMessages")) {
				String message = setupYamlFile.getConfigString(player, "OverwrittenMessages.Quit");
				
				if(setupYamlFile.getConfiguration().getBoolean("BungeeCord") && mysqlNickManager.isPlayerNicked(player.getUniqueId()))
					message = message.replace("%name%", mysqlNickManager.getNickName(player.getUniqueId())).replace("%displayName%", mysqlPlayerDataManager.getChatPrefix(player.getUniqueId()) + mysqlNickManager.getNickName(player.getUniqueId()) + mysqlPlayerDataManager.getChatSuffix(player.getUniqueId())).replace("%displayname%", mysqlPlayerDataManager.getChatPrefix(player.getUniqueId()) + mysqlNickManager.getNickName(player.getUniqueId()) + mysqlPlayerDataManager.getChatSuffix(player.getUniqueId()));
				else
					message = message.replace("%name%", nickedPlayerData.getNickName()).replace("%displayName%", nickedPlayerData.getChatPrefix() + nickedPlayerData.getNickName() + nickedPlayerData.getChatSuffix()).replace("%displayname%", nickedPlayerData.getChatPrefix() + nickedPlayerData.getNickName() + nickedPlayerData.getChatSuffix());
				
				event.setQuitMessage(message);
			} else if ((event.getQuitMessage() != null) && (event.getQuitMessage() != "")) {
				if (event.getQuitMessage().contains("formerly known as"))
					event.setQuitMessage("§e" + player.getName() + " left the game.");

				event.setQuitMessage(event.getQuitMessage().replace(player.getName(), nickedPlayerData.getNickName()));
			}
		}
	}

}
