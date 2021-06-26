package net.dev.eazynick.listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerKickEvent;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.NickManager;
import net.dev.eazynick.api.NickedPlayerData;
import net.dev.eazynick.hooks.LuckPermsHook;
import net.dev.eazynick.hooks.TABHook;
import net.dev.eazynick.sql.MySQLNickManager;
import net.dev.eazynick.sql.MySQLPlayerDataManager;
import net.dev.eazynick.utilities.Utils;
import net.dev.eazynick.utilities.configuration.yaml.SetupYamlFile;

import me.clip.placeholderapi.PlaceholderAPI;

public class PlayerKickListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerKick(PlayerKickEvent event) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
		MySQLNickManager mysqlNickManager = eazyNick.getMySQLNickManager();
		MySQLPlayerDataManager mysqlPlayerDataManager = eazyNick.getMySQLPlayerDataManager();
		
		Player player = event.getPlayer();
		UUID uniqueId = player.getUniqueId();
		NickManager api = new NickManager(player);

		if (api.isNicked()) {
			NickedPlayerData nickedPlayerData = utils.getNickedPlayers().get(uniqueId).clone();
			
			if(setupYamlFile.getConfiguration().getBoolean("NickCommands.OnUnnick")) {
				if(utils.isPluginInstalled("PlaceholderAPI"))
					setupYamlFile.getConfiguration().getStringList("NickCommands.Unnick").forEach(command -> Bukkit.dispatchCommand(setupYamlFile.getConfiguration().getBoolean("NickCommands.SendAsConsole") ? Bukkit.getConsoleSender() : player, PlaceholderAPI.setPlaceholders(player, command)));
				else
					setupYamlFile.getConfiguration().getStringList("NickCommands.Unnick").forEach(command -> Bukkit.dispatchCommand(setupYamlFile.getConfiguration().getBoolean("NickCommands.SendAsConsole") ? Bukkit.getConsoleSender() : player, command));
			}
			
			if(utils.getOldExperienceLevels().containsKey(player.getUniqueId())) {
				player.setLevel(utils.getOldExperienceLevels().get(player.getUniqueId()));
				
				utils.getOldExperienceLevels().remove(player.getUniqueId());
			}
			
			if (setupYamlFile.getConfiguration().getBoolean("DisconnectUnnick") || setupYamlFile.getConfiguration().getBoolean("BungeeCord"))
				api.unnickPlayerWithoutRemovingMySQL(false, false);
			else
				api.unnickPlayerWithoutRemovingMySQL(true, false);
			
			if(utils.isPluginInstalled("LuckPerms"))
				new LuckPermsHook(player).resetNodes();
			
			if(utils.isPluginInstalled("TAB", "NEZNAMY") && setupYamlFile.getConfiguration().getBoolean("ChangeNameAndPrefixAndSuffixInTAB"))
				new TABHook(player).reset();
			
			if(utils.getIncomingPacketInjectors().containsKey(uniqueId)) {
				Object incomingPacketInjector = utils.getIncomingPacketInjectors().get(uniqueId);
				
				try {
					incomingPacketInjector.getClass().getMethod("unregister").invoke(incomingPacketInjector);
				} catch (Exception ignore) {
				}
				
				utils.getIncomingPacketInjectors().remove(uniqueId);
			}
			
			if(setupYamlFile.getConfiguration().getBoolean("OverwriteJoinQuitMessages")) {
				String message = setupYamlFile.getConfigString(player, "OverwrittenMessages.Quit");
				
				if(setupYamlFile.getConfiguration().getBoolean("BungeeCord") && mysqlNickManager.isPlayerNicked(uniqueId))
					message = message.replace("%name%", mysqlNickManager.getNickName(uniqueId)).replace("%displayName%", mysqlPlayerDataManager.getChatPrefix(uniqueId) + mysqlNickManager.getNickName(uniqueId) + mysqlPlayerDataManager.getChatSuffix(uniqueId)).replace("%displayname%", mysqlPlayerDataManager.getChatPrefix(uniqueId) + mysqlNickManager.getNickName(uniqueId) + mysqlPlayerDataManager.getChatSuffix(uniqueId));
				else
					message = message.replace("%name%", nickedPlayerData.getNickName()).replace("%displayName%", nickedPlayerData.getChatPrefix() + nickedPlayerData.getNickName() + nickedPlayerData.getChatSuffix()).replace("%displayname%", nickedPlayerData.getChatPrefix() + nickedPlayerData.getNickName() + nickedPlayerData.getChatSuffix());
				
				event.setLeaveMessage(message);
			} else if ((event.getLeaveMessage() != null) && (event.getLeaveMessage() != "")) {
				if (event.getLeaveMessage().contains("formerly known as"))
					event.setLeaveMessage("§e" + player.getName() + " left the game.");

				event.setLeaveMessage(event.getLeaveMessage().replace(player.getName(), nickedPlayerData.getNickName()));
			}
		}
	}

}
