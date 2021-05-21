package net.dev.eazynick.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerJoinEvent;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.*;
import net.dev.eazynick.nms.netty.PacketInjector;
import net.dev.eazynick.nms.netty.PacketInjector_1_7;
import net.dev.eazynick.sql.MySQLNickManager;
import net.dev.eazynick.sql.MySQLPlayerDataManager;
import net.dev.eazynick.utilities.ItemBuilder;
import net.dev.eazynick.utilities.Utils;
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
		NickManager api = new NickManager(player);
		
		utils.getNameCache().put(player.getUniqueId(), player.getName());
		
		if (!(utils.getCanUseNick().containsKey(player.getUniqueId())))
			utils.getCanUseNick().put(player.getUniqueId(), true);

		if (!(eazyNick.getVersion().equalsIgnoreCase("1_7_R4")))
			player.setCustomName(player.getName());

		if(setupYamlFile.getConfiguration().getBoolean("OverwriteJoinQuitMessages") && ((setupYamlFile.getConfiguration().getBoolean("BungeeCord") && mysqlNickManager.isPlayerNicked(player.getUniqueId())) || utils.getLastNickDatas().containsKey(player.getUniqueId()))) {
			String message = setupYamlFile.getConfigString(player, "OverwrittenMessages.Join");
			
			if(setupYamlFile.getConfiguration().getBoolean("BungeeCord") && mysqlNickManager.isPlayerNicked(player.getUniqueId()))
				message = message.replace("%name%", mysqlNickManager.getNickName(player.getUniqueId())).replace("%displayName%", mysqlPlayerDataManager.getChatPrefix(player.getUniqueId()) + mysqlNickManager.getNickName(player.getUniqueId()) + mysqlPlayerDataManager.getChatSuffix(player.getUniqueId()));
			else if(utils.getLastNickDatas().containsKey(player.getUniqueId())) {
				NickedPlayerData nickedPlayerData = utils.getLastNickDatas().get(player.getUniqueId());
				
				message = message.replace("%name%", nickedPlayerData.getNickName()).replace("%displayName%", nickedPlayerData.getChatPrefix() + nickedPlayerData.getNickName() + nickedPlayerData.getChatSuffix());
			}
				
			event.setJoinMessage(message);
		} else if ((event.getJoinMessage() != null) && (event.getJoinMessage() != "")) {
			if (setupYamlFile.getConfiguration().getBoolean("BungeeCord") && !(setupYamlFile.getConfiguration().getBoolean("LobbyMode")) && mysqlNickManager.isPlayerNicked(player.getUniqueId())) {
				if (event.getJoinMessage().contains("formerly known as"))
					event.setJoinMessage("§e" + player.getName() + " joined the game");

				event.setJoinMessage(event.getJoinMessage().replace(player.getName(), mysqlNickManager.getNickName(player.getUniqueId())));
			} else if (utils.getLastNickDatas().containsKey(player.getUniqueId())) {
				if (event.getJoinMessage().contains("formerly known as"))
					event.setJoinMessage("§e" + player.getName() + " joined the game");

				event.setJoinMessage(event.getJoinMessage().replace(player.getName(), utils.getLastNickDatas().get(player.getUniqueId()).getNickName()));
			}
		}
		
		Bukkit.getScheduler().runTaskLater(eazyNick, () -> {
			if(eazyNick.getVersion().equals("1_7_R4"))
				new PacketInjector_1_7().init();
			else
				new PacketInjector().init();
			
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
					if (mysqlNickManager.isPlayerNicked(player.getUniqueId()))
						utils.performReNick(player);
				} else if (mysqlNickManager.isPlayerNicked(player.getUniqueId()) && setupYamlFile.getConfiguration().getBoolean("GetNewNickOnEveryServerSwitch")) {
					String name = api.getRandomName();
					
					mysqlNickManager.removePlayer(player.getUniqueId());
					mysqlNickManager.addPlayer(player.getUniqueId(), name, name);
				}

				if (setupYamlFile.getConfiguration().getBoolean("NickItem.getOnJoin")) {
					if (player.hasPermission("nick.item")) {
						if (!(mysqlNickManager.isPlayerNicked(player.getUniqueId())))
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
			} else if (!(setupYamlFile.getConfiguration().getBoolean("DisconnectUnnick")) && utils.getLastNickDatas().containsKey(player.getUniqueId())) {
				if((eazyNick.getMySQL() != null) && eazyNick.getMySQL().isConnected())
					Bukkit.getPluginManager().callEvent(new PlayerNickEvent(player, mysqlNickManager.getNickName(player.getUniqueId()), mysqlNickManager.getSkinName(player.getUniqueId()), mysqlPlayerDataManager.getChatPrefix(player.getUniqueId()), mysqlPlayerDataManager.getChatSuffix(player.getUniqueId()), mysqlPlayerDataManager.getTabPrefix(player.getUniqueId()), mysqlPlayerDataManager.getTabSuffix(player.getUniqueId()), mysqlPlayerDataManager.getTagPrefix(player.getUniqueId()), mysqlPlayerDataManager.getTagSuffix(player.getUniqueId()), false, false, 9999, "NONE"));
				else if(utils.getLastNickDatas().containsKey(player.getUniqueId())) {
					NickedPlayerData nickedPlayerData = utils.getLastNickDatas().get(player.getUniqueId());
					
					Bukkit.getPluginManager().callEvent(new PlayerNickEvent(player, nickedPlayerData.getNickName(), nickedPlayerData.getSkinName(), nickedPlayerData.getChatPrefix(), nickedPlayerData.getChatSuffix(), nickedPlayerData.getTabPrefix(), nickedPlayerData.getTabSuffix(), nickedPlayerData.getTagPrefix(), nickedPlayerData.getTagSuffix(), false, true, nickedPlayerData.getSortID(), nickedPlayerData.getGroupName()));
				}
			}
		}, 7);
	}

}
