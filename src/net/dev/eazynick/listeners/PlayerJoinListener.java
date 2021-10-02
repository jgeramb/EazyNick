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
import net.dev.eazynick.utilities.AsyncTask.AsyncRunnable;
import net.dev.eazynick.utilities.configuration.yaml.*;

public class PlayerJoinListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
		SavedNickDatasYamlFile savedNickDatasYamlFile = eazyNick.getSavedNickDatasYamlFile();
		LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
		MySQLNickManager mysqlNickManager = eazyNick.getMySQLNickManager();
		MySQLPlayerDataManager mysqlPlayerDataManager = eazyNick.getMySQLPlayerDataManager();
		
		Player player = event.getPlayer();
		UUID uniqueId = player.getUniqueId();
		String uniqueIdString = uniqueId.toString().replace("-", "");
		boolean isAPIMode = setupYamlFile.getConfiguration().getBoolean("APIMode");
		NickManager api = new NickManager(player);
		
		if (!(utils.getCanUseNick().containsKey(uniqueId)))
			utils.getCanUseNick().put(uniqueId, true);

		if (!(eazyNick.getVersion().equalsIgnoreCase("1_7_R4")))
			player.setCustomName(player.getName());
		
		if(!(isAPIMode)) {
			if(setupYamlFile.getConfiguration().getBoolean("OverwriteJoinQuitMessages") && ((setupYamlFile.getConfiguration().getBoolean("BungeeCord") && mysqlNickManager.isPlayerNicked(uniqueId)) || utils.getLastNickDatas().containsKey(uniqueId))) {
				String message = setupYamlFile.getConfigString(player, "OverwrittenMessages.Join");
				
				if(setupYamlFile.getConfiguration().getBoolean("BungeeCord") && (!(setupYamlFile.getConfiguration().getBoolean("LobbyMode")) || (player.hasPermission("eazynick.bypasslobbymode") && setupYamlFile.getConfiguration().getBoolean("EnableBypassLobbyModePermission"))) && mysqlNickManager.isPlayerNicked(uniqueId))
					message = message.replace("%name%", mysqlNickManager.getNickName(uniqueId)).replace("%displayName%", mysqlPlayerDataManager.getChatPrefix(uniqueId) + mysqlNickManager.getNickName(uniqueId) + mysqlPlayerDataManager.getChatSuffix(uniqueId));
				else if(utils.getLastNickDatas().containsKey(uniqueId)) {
					NickedPlayerData nickedPlayerData = utils.getLastNickDatas().get(uniqueId);
					
					message = message.replace("%name%", nickedPlayerData.getNickName()).replace("%displayName%", nickedPlayerData.getChatPrefix() + nickedPlayerData.getNickName() + nickedPlayerData.getChatSuffix());
				} else if(setupYamlFile.getConfiguration().getBoolean("SaveLocalNickDatas") && savedNickDatasYamlFile.getConfiguration().contains(uniqueIdString))
					message = message.replace("%name%", savedNickDatasYamlFile.getConfigString(uniqueIdString + ".nick_name")).replace("%displayName%", savedNickDatasYamlFile.getConfigString(uniqueIdString + ".chat_prefix") + savedNickDatasYamlFile.getConfigString(player, uniqueIdString + ".nick_name") + savedNickDatasYamlFile.getConfigString(player, uniqueIdString + ".chat_suffix"));
					
				event.setJoinMessage(message);
			} else if ((event.getJoinMessage() != null) && (event.getJoinMessage() != "")) {
				if (setupYamlFile.getConfiguration().getBoolean("BungeeCord") && (!(setupYamlFile.getConfiguration().getBoolean("LobbyMode")) || (player.hasPermission("eazynick.bypasslobbymode") && setupYamlFile.getConfiguration().getBoolean("EnableBypassLobbyModePermission"))) && mysqlNickManager.isPlayerNicked(uniqueId)) {
					if (event.getJoinMessage().contains("formerly known as"))
						event.setJoinMessage("§e" + player.getName() + " joined the game");
	
					event.setJoinMessage(event.getJoinMessage().replace(player.getName(), mysqlNickManager.getNickName(uniqueId)));
				} else if (utils.getLastNickDatas().containsKey(uniqueId)) {
					if (event.getJoinMessage().contains("formerly known as"))
						event.setJoinMessage("§e" + player.getName() + " joined the game");
	
					event.setJoinMessage(event.getJoinMessage().replace(player.getName(), utils.getLastNickDatas().get(uniqueId).getNickName()));
				} else if (setupYamlFile.getConfiguration().getBoolean("SaveLocalNickDatas") && savedNickDatasYamlFile.getConfiguration().contains(uniqueIdString)) {
					if (event.getJoinMessage().contains("formerly known as"))
						event.setJoinMessage("§e" + player.getName() + " joined the game");
	
					event.setJoinMessage(event.getJoinMessage().replace(player.getName(), savedNickDatasYamlFile.getConfigString(uniqueIdString + ".nick_name")));
				}
			}
		}
		
		if(eazyNick.getVersion().equals("1_7_R4")) {
			((OutgoingPacketInjector_1_7) eazyNick.getOutgoingPacketInjector()).unregister();
			
			OutgoingPacketInjector_1_7 outgoingPacketInjector = new OutgoingPacketInjector_1_7();
			outgoingPacketInjector.init();
			
			eazyNick.setOutgoingPacketInjector(outgoingPacketInjector);
			
			utils.getIncomingPacketInjectors().put(uniqueId, new IncomingPacketInjector_1_7(player));
		} else {
			((OutgoingPacketInjector) eazyNick.getOutgoingPacketInjector()).unregister();

			OutgoingPacketInjector outgoingPacketInjector = new OutgoingPacketInjector();
			outgoingPacketInjector.init();
			
			eazyNick.setOutgoingPacketInjector(outgoingPacketInjector);
			
			utils.getIncomingPacketInjectors().put(uniqueId, new IncomingPacketInjector(player));
		}
		
		new AsyncTask(new AsyncRunnable() {
			
			@Override
			public void run() {
				if(!(isAPIMode)) {
					Bukkit.getScheduler().runTask(eazyNick, () -> {
						if (setupYamlFile.getConfiguration().getBoolean("BungeeCord")) {
							if (!(setupYamlFile.getConfiguration().getBoolean("LobbyMode")) || (player.hasPermission("eazynick.bypasslobbymode") && setupYamlFile.getConfiguration().getBoolean("EnableBypassLobbyModePermission"))) {
								if (mysqlNickManager.isPlayerNicked(uniqueId))
									utils.performReNick(player);
							} else if (mysqlNickManager.isPlayerNicked(uniqueId) && setupYamlFile.getConfiguration().getBoolean("GetNewNickOnEveryServerSwitch")) {
								String name = api.getRandomName();
								
								mysqlNickManager.removePlayer(uniqueId);
								mysqlNickManager.addPlayer(uniqueId, name, name);
							}
			
							if (setupYamlFile.getConfiguration().getBoolean("NickItem.getOnJoin")) {
								if (player.hasPermission("eazynick.item")) {
									if (!(mysqlNickManager.isPlayerNicked(uniqueId)))
										player.getInventory().setItem(setupYamlFile.getConfiguration().getInt("NickItem.Slot") - 1, new ItemBuilder(Material.getMaterial(setupYamlFile.getConfiguration().getString("NickItem.ItemType.Disabled")), setupYamlFile.getConfiguration().getInt("NickItem.ItemAmount.Disabled"), setupYamlFile.getConfiguration().getInt("NickItem.MetaData.Disabled")).setDisplayName(languageYamlFile.getConfigString(player, "NickItem.BungeeCord.DisplayName.Disabled")).setLore(languageYamlFile.getConfigString(player, "NickItem.ItemLore.Disabled").split("&n")).setEnchanted(setupYamlFile.getConfiguration().getBoolean("NickItem.Enchanted.Disabled")).build());
									else
										player.getInventory().setItem(setupYamlFile.getConfiguration().getInt("NickItem.Slot") - 1, new ItemBuilder(Material.getMaterial(setupYamlFile.getConfiguration().getString("NickItem.ItemType.Enabled")), setupYamlFile.getConfiguration().getInt("NickItem.ItemAmount.Enabled"), setupYamlFile.getConfiguration().getInt("NickItem.MetaData.Enabled")).setDisplayName(languageYamlFile.getConfigString(player, "NickItem.BungeeCord.DisplayName.Enabled")).setLore(languageYamlFile.getConfigString(player, "NickItem.ItemLore.Enabled").split("&n")).setEnchanted(setupYamlFile.getConfiguration().getBoolean("NickItem.Enchanted.Enabled")).build());
								}
							}
						} else {
							if (setupYamlFile.getConfiguration().getBoolean("NickItem.getOnJoin")) {
								if (player.hasPermission("eazynick.item")) {
									if (setupYamlFile.getConfiguration().getBoolean("NickOnWorldChange"))
										player.getInventory().setItem(setupYamlFile.getConfiguration().getInt("NickItem.Slot") - 1, new ItemBuilder(Material.getMaterial(setupYamlFile.getConfiguration().getString("NickItem.ItemType.Disabled")), setupYamlFile.getConfiguration().getInt("NickItem.ItemAmount.Disabled"), setupYamlFile.getConfiguration().getInt("NickItem.MetaData.Disabled")).setDisplayName(languageYamlFile.getConfigString(player, "NickItem.WorldChange.DisplayName.Disabled")).setLore(languageYamlFile.getConfigString(player, "NickItem.ItemLore.Disabled").split("&n")).setEnchanted(setupYamlFile.getConfiguration().getBoolean("NickItem.Enchanted.Disabled")).build());
									else
										player.getInventory().setItem(setupYamlFile.getConfiguration().getInt("NickItem.Slot") - 1, new ItemBuilder(Material.getMaterial(setupYamlFile.getConfiguration().getString("NickItem.ItemType.Disabled")), setupYamlFile.getConfiguration().getInt("NickItem.ItemAmount.Disabled"), setupYamlFile.getConfiguration().getInt("NickItem.MetaData.Disabled")).setDisplayName(languageYamlFile.getConfigString(player, "NickItem.DisplayName.Disabled")).setLore(languageYamlFile.getConfigString(player, "NickItem.ItemLore.Disabled").split("&n")).setEnchanted(setupYamlFile.getConfiguration().getBoolean("NickItem.Enchanted.Disabled")).build());
								}
							}
								
							if (!(setupYamlFile.getConfiguration().getBoolean("DisconnectUnnick"))) {
								if(utils.getLastNickDatas().containsKey(uniqueId)) {
									NickedPlayerData nickedPlayerData = utils.getLastNickDatas().get(uniqueId);
									
									Bukkit.getPluginManager().callEvent(new PlayerNickEvent(player, nickedPlayerData.getNickName(), nickedPlayerData.getSkinName(), nickedPlayerData.getSpoofedUniqueId(), nickedPlayerData.getChatPrefix(), nickedPlayerData.getChatSuffix(), nickedPlayerData.getTabPrefix(), nickedPlayerData.getTabSuffix(), nickedPlayerData.getTagPrefix(), nickedPlayerData.getTagSuffix(), false, true, nickedPlayerData.getSortID(), nickedPlayerData.getGroupName()));
								} else if(setupYamlFile.getConfiguration().getBoolean("SaveLocalNickDatas") && savedNickDatasYamlFile.getConfiguration().contains(uniqueIdString))
									Bukkit.getPluginManager().callEvent(new PlayerNickEvent(player, savedNickDatasYamlFile.getConfigString(uniqueIdString + ".nick_name"), savedNickDatasYamlFile.getConfigString(uniqueIdString + ".skin_name"), UUID.fromString(savedNickDatasYamlFile.getConfigString(uniqueIdString + ".spoofed_uniqueid")), savedNickDatasYamlFile.getConfigString(uniqueIdString + ".chat_prefix"), savedNickDatasYamlFile.getConfigString(uniqueIdString + ".chat_suffix"), savedNickDatasYamlFile.getConfigString(uniqueIdString + ".tablist_prefix"), savedNickDatasYamlFile.getConfigString(uniqueIdString + ".tablist_suffix"), savedNickDatasYamlFile.getConfigString(uniqueIdString + ".nametag_prefix"), savedNickDatasYamlFile.getConfigString(uniqueIdString + ".nametag_suffix"), false, true, savedNickDatasYamlFile.getConfiguration().getInt(uniqueIdString + ".sort_id"), savedNickDatasYamlFile.getConfigString(uniqueIdString + ".group_name")));
							}
						}
						
						if (setupYamlFile.getConfiguration().getBoolean("JoinNick")) {
							if (!(api.isNicked()) && player.hasPermission("eazynick.nick.random"))
								utils.performNick(player, "RANDOM");
						}
					});
				}
			}
		}, 50L * 7).run();
	}

}
