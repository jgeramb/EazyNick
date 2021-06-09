package net.dev.eazynick.listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerJoinEvent;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.*;
import net.dev.eazynick.nms.netty.client.IncomingPacketInjector;
import net.dev.eazynick.nms.netty.client.IncomingPacketInjector_1_7;
import net.dev.eazynick.nms.netty.server.OutgoingPacketInjector;
import net.dev.eazynick.nms.netty.server.OutgoingPacketInjector_1_7;
import net.dev.eazynick.sql.MySQLNickManager;
import net.dev.eazynick.sql.MySQLPlayerDataManager;
import net.dev.eazynick.utilities.*;
import net.dev.eazynick.utilities.configuration.yaml.LanguageYamlFile;
import net.dev.eazynick.utilities.configuration.yaml.SetupYamlFile;

public class PlayerJoinListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
		LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
		MySQLNickManager mysqlNickManager = eazyNick.getMySQLNickManager();
		MySQLPlayerDataManager mysqlPlayerDataManager = eazyNick.getMySQLPlayerDataManager();
		
		Player player = event.getPlayer();
		UUID uniqueId = player.getUniqueId();
		NickManager api = new NickManager(player);
		
		if (!(utils.getCanUseNick().containsKey(uniqueId)))
			utils.getCanUseNick().put(uniqueId, true);

		if (!(eazyNick.getVersion().equalsIgnoreCase("1_7_R4")))
			player.setCustomName(player.getName());

		if(setupYamlFile.getConfiguration().getBoolean("OverwriteJoinQuitMessages") && ((setupYamlFile.getConfiguration().getBoolean("BungeeCord") && mysqlNickManager.isPlayerNicked(uniqueId)) || utils.getLastNickDatas().containsKey(uniqueId))) {
			String message = setupYamlFile.getConfigString(player, "OverwrittenMessages.Join");
			
			if(setupYamlFile.getConfiguration().getBoolean("BungeeCord") && (!(setupYamlFile.getConfiguration().getBoolean("LobbyMode")) || (player.hasPermission("nick.bypasslobbymode") && setupYamlFile.getConfiguration().getBoolean("EnableBypassLobbyModePermission"))) && mysqlNickManager.isPlayerNicked(uniqueId))
				message = message.replace("%name%", mysqlNickManager.getNickName(uniqueId)).replace("%displayName%", mysqlPlayerDataManager.getChatPrefix(uniqueId) + mysqlNickManager.getNickName(uniqueId) + mysqlPlayerDataManager.getChatSuffix(uniqueId));
			else if(utils.getLastNickDatas().containsKey(uniqueId)) {
				NickedPlayerData nickedPlayerData = utils.getLastNickDatas().get(uniqueId);
				
				message = message.replace("%name%", nickedPlayerData.getNickName()).replace("%displayName%", nickedPlayerData.getChatPrefix() + nickedPlayerData.getNickName() + nickedPlayerData.getChatSuffix());
			}
				
			event.setJoinMessage(message);
		} else if ((event.getJoinMessage() != null) && (event.getJoinMessage() != "")) {
			if (setupYamlFile.getConfiguration().getBoolean("BungeeCord") && (!(setupYamlFile.getConfiguration().getBoolean("LobbyMode")) || (player.hasPermission("nick.bypasslobbymode") && setupYamlFile.getConfiguration().getBoolean("EnableBypassLobbyModePermission"))) && mysqlNickManager.isPlayerNicked(uniqueId)) {
				if (event.getJoinMessage().contains("formerly known as"))
					event.setJoinMessage("§e" + player.getName() + " joined the game");

				event.setJoinMessage(event.getJoinMessage().replace(player.getName(), mysqlNickManager.getNickName(uniqueId)));
			} else if (utils.getLastNickDatas().containsKey(uniqueId)) {
				if (event.getJoinMessage().contains("formerly known as"))
					event.setJoinMessage("§e" + player.getName() + " joined the game");

				event.setJoinMessage(event.getJoinMessage().replace(player.getName(), utils.getLastNickDatas().get(uniqueId).getNickName()));
			}
		}
		
		Bukkit.getScheduler().runTaskLater(eazyNick, () -> {
			if(eazyNick.getVersion().equals("1_7_R4")) {
				new OutgoingPacketInjector_1_7().init();
				
				utils.getIncomingPacketInjectors().put(uniqueId, new IncomingPacketInjector_1_7(player));
			} else {
				new OutgoingPacketInjector().init();
				
				utils.getIncomingPacketInjectors().put(uniqueId, new IncomingPacketInjector(player));
			}
			
			if(!(player.hasPermission("nick.bypass") && setupYamlFile.getConfiguration().getBoolean("EnableBypassPermission")) && setupYamlFile.getConfiguration().getBoolean("ReNickAllOnNewPlayerJoinServer")) {
				for (Player currentPlayer : Bukkit.getOnlinePlayers()) {
					NickManager apiCurrentPlayer = new NickManager(currentPlayer);
					
					if (apiCurrentPlayer.isNicked()) {
						NickedPlayerData nickedPlayerData = utils.getNickedPlayers().get(currentPlayer.getUniqueId()).clone();
						
						apiCurrentPlayer.unnickPlayerWithoutRemovingMySQL(false, false);
						
						Bukkit.getScheduler().runTaskLater(eazyNick, () -> {
							if(currentPlayer.isOnline())
								Bukkit.getPluginManager().callEvent(new PlayerNickEvent(currentPlayer, nickedPlayerData.getNickName(), nickedPlayerData.getSkinName(), nickedPlayerData.getChatPrefix(), nickedPlayerData.getChatSuffix(), nickedPlayerData.getTabPrefix(), nickedPlayerData.getTabSuffix(), nickedPlayerData.getTagPrefix(), nickedPlayerData.getTagSuffix(), false, true, nickedPlayerData.getSortID(), nickedPlayerData.getGroupName()));
						}, 21 + (setupYamlFile.getConfiguration().getBoolean("RandomDisguiseDelay") ? (20 * 2) : 0));
					}
				}
			}
			
			if (setupYamlFile.getConfiguration().getBoolean("BungeeCord")) {
				if (!(setupYamlFile.getConfiguration().getBoolean("LobbyMode")) || (player.hasPermission("nick.bypasslobbymode") && setupYamlFile.getConfiguration().getBoolean("EnableBypassLobbyModePermission"))) {
					if (mysqlNickManager.isPlayerNicked(uniqueId))
						utils.performReNick(player);
				} else if (mysqlNickManager.isPlayerNicked(uniqueId) && setupYamlFile.getConfiguration().getBoolean("GetNewNickOnEveryServerSwitch")) {
					String name = api.getRandomName();
					
					mysqlNickManager.removePlayer(uniqueId);
					mysqlNickManager.addPlayer(uniqueId, name, name);
				}

				if (setupYamlFile.getConfiguration().getBoolean("NickItem.getOnJoin")) {
					if (player.hasPermission("nick.item")) {
						if (!(mysqlNickManager.isPlayerNicked(uniqueId)))
							player.getInventory().setItem(setupYamlFile.getConfiguration().getInt("NickItem.Slot") - 1, new ItemBuilder(Material.getMaterial(setupYamlFile.getConfiguration().getString("NickItem.ItemType.Disabled")), setupYamlFile.getConfiguration().getInt("NickItem.ItemAmount.Disabled"), setupYamlFile.getConfiguration().getInt("NickItem.MetaData.Disabled")).setDisplayName(languageYamlFile.getConfigString(player, "NickItem.BungeeCord.DisplayName.Disabled")).setLore(languageYamlFile.getConfigString(player, "NickItem.ItemLore.Disabled").split("&n")).setEnchanted(setupYamlFile.getConfiguration().getBoolean("NickItem.Enchanted.Disabled")).build());
						else
							player.getInventory().setItem(setupYamlFile.getConfiguration().getInt("NickItem.Slot") - 1, new ItemBuilder(Material.getMaterial(setupYamlFile.getConfiguration().getString("NickItem.ItemType.Enabled")), setupYamlFile.getConfiguration().getInt("NickItem.ItemAmount.Enabled"), setupYamlFile.getConfiguration().getInt("NickItem.MetaData.Enabled")).setDisplayName(languageYamlFile.getConfigString(player, "NickItem.BungeeCord.DisplayName.Enabled")).setLore(languageYamlFile.getConfigString(player, "NickItem.ItemLore.Enabled").split("&n")).setEnchanted(setupYamlFile.getConfiguration().getBoolean("NickItem.Enchanted.Enabled")).build());
					}
				}
			} else if (setupYamlFile.getConfiguration().getBoolean("NickItem.getOnJoin")) {
				if (player.hasPermission("nick.item")) {
					if (setupYamlFile.getConfiguration().getBoolean("NickOnWorldChange"))
						player.getInventory().setItem(setupYamlFile.getConfiguration().getInt("NickItem.Slot") - 1, new ItemBuilder(Material.getMaterial(setupYamlFile.getConfiguration().getString("NickItem.ItemType.Disabled")), setupYamlFile.getConfiguration().getInt("NickItem.ItemAmount.Disabled"), setupYamlFile.getConfiguration().getInt("NickItem.MetaData.Disabled")).setDisplayName(languageYamlFile.getConfigString(player, "NickItem.WorldChange.DisplayName.Disabled")).setLore(languageYamlFile.getConfigString(player, "NickItem.ItemLore.Disabled").split("&n")).setEnchanted(setupYamlFile.getConfiguration().getBoolean("NickItem.Enchanted.Disabled")).build());
					else
						player.getInventory().setItem(setupYamlFile.getConfiguration().getInt("NickItem.Slot") - 1, new ItemBuilder(Material.getMaterial(setupYamlFile.getConfiguration().getString("NickItem.ItemType.Disabled")), setupYamlFile.getConfiguration().getInt("NickItem.ItemAmount.Disabled"), setupYamlFile.getConfiguration().getInt("NickItem.MetaData.Disabled")).setDisplayName(languageYamlFile.getConfigString(player, "NickItem.DisplayName.Disabled")).setLore(languageYamlFile.getConfigString(player, "NickItem.ItemLore.Disabled").split("&n")).setEnchanted(setupYamlFile.getConfiguration().getBoolean("NickItem.Enchanted.Disabled")).build());
				}
			}
			
			if (setupYamlFile.getConfiguration().getBoolean("JoinNick")) {
				if (!(api.isNicked()) && player.hasPermission("nick.use"))
					utils.performNick(player, "RANDOM");
			} else if (!(setupYamlFile.getConfiguration().getBoolean("DisconnectUnnick")) && utils.getLastNickDatas().containsKey(uniqueId)) {
				if((eazyNick.getMySQL() != null) && eazyNick.getMySQL().isConnected())
					Bukkit.getPluginManager().callEvent(new PlayerNickEvent(player, mysqlNickManager.getNickName(uniqueId), mysqlNickManager.getSkinName(uniqueId), mysqlPlayerDataManager.getChatPrefix(uniqueId), mysqlPlayerDataManager.getChatSuffix(uniqueId), mysqlPlayerDataManager.getTabPrefix(uniqueId), mysqlPlayerDataManager.getTabSuffix(uniqueId), mysqlPlayerDataManager.getTagPrefix(uniqueId), mysqlPlayerDataManager.getTagSuffix(uniqueId), false, false, 9999, "NONE"));
				else if(utils.getLastNickDatas().containsKey(uniqueId)) {
					NickedPlayerData nickedPlayerData = utils.getLastNickDatas().get(uniqueId);
					
					Bukkit.getPluginManager().callEvent(new PlayerNickEvent(player, nickedPlayerData.getNickName(), nickedPlayerData.getSkinName(), nickedPlayerData.getChatPrefix(), nickedPlayerData.getChatSuffix(), nickedPlayerData.getTabPrefix(), nickedPlayerData.getTabSuffix(), nickedPlayerData.getTagPrefix(), nickedPlayerData.getTagSuffix(), false, true, nickedPlayerData.getSortID(), nickedPlayerData.getGroupName()));
				}
			}
		}, 7);
	}

}
