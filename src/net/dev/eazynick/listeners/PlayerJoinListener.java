package net.dev.eazynick.listeners;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.NickManager;
import net.dev.eazynick.api.PlayerNickEvent;
import net.dev.eazynick.sql.MySQLNickManager;
import net.dev.eazynick.sql.MySQLPlayerDataManager;
import net.dev.eazynick.utils.BookGUIFileUtils;
import net.dev.eazynick.utils.FileUtils;
import net.dev.eazynick.utils.LanguageFileUtils;
import net.dev.eazynick.utils.Utils;

public class PlayerJoinListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent e) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		FileUtils fileUtils = eazyNick.getFileUtils();
		LanguageFileUtils languageFileUtils = eazyNick.getLanguageFileUtils();
		BookGUIFileUtils bookGUIFileUtils = eazyNick.getBookGUIFileUtils();
		MySQLNickManager mysqlNickManager = eazyNick.getMySQLNickManager();
		MySQLPlayerDataManager mysqlPlayerDataManager = eazyNick.getMySQLPlayerDataManager();
		
		Player p = e.getPlayer();
		NickManager api = new NickManager(p);
		
		utils.getNameCache().put(p.getUniqueId(), p.getName());
		
		if (!(utils.getCanUseNick().containsKey(p.getUniqueId())))
			utils.getCanUseNick().put(p.getUniqueId(), true);

		if (!(eazyNick.getVersion().equalsIgnoreCase("1_7_R4")))
			p.setCustomName(p.getName());

		if(fileUtils.getConfig().getBoolean("OverwriteJoinQuitMessages") && new NickManager(p).isNicked()) {
			String message = fileUtils.getConfigString("OverwrittenMessages.Join");
			
			if(fileUtils.getConfig().getBoolean("BungeeCord") && mysqlNickManager.isPlayerNicked(p.getUniqueId()))
				message = message.replace("%name%", mysqlNickManager.getNickName(p.getUniqueId())).replace("%displayName%", mysqlPlayerDataManager.getChatPrefix(p.getUniqueId()) + mysqlNickManager.getNickName(p.getUniqueId()) + mysqlPlayerDataManager.getChatSuffix(p.getUniqueId()));
			else if(utils.getPlayerNicknames().containsKey(p.getUniqueId()))
				message = message.replace("%name%", utils.getPlayerNicknames().get(p.getUniqueId()).replace("%displayName%", utils.getChatPrefixes().get(p.getUniqueId()) + utils.getPlayerNicknames().get(p.getUniqueId()) + utils.getChatSuffixes().get(p.getUniqueId())));
			else
				message = message.replace("%name%", p.getName()).replace("%displayName%", p.getDisplayName());
			
			e.setJoinMessage(message);
		} else if ((e.getJoinMessage() != null) && (e.getJoinMessage() != "")) {
			if (fileUtils.getConfig().getBoolean("BungeeCord") && !(fileUtils.getConfig().getBoolean("LobbyMode")) && mysqlNickManager.isPlayerNicked(p.getUniqueId())) {
				if (e.getJoinMessage().contains("formerly known as"))
					e.setJoinMessage("§e" + p.getName() + " joined the game");

				e.setJoinMessage(e.getJoinMessage().replace(p.getName(), mysqlNickManager.getNickName(p.getUniqueId())));
			} else if (utils.getPlayerNicknames().containsKey(p.getUniqueId())) {
				if (e.getJoinMessage().contains("formerly known as"))
					e.setJoinMessage("§e" + p.getName() + " joined the game");

				e.setJoinMessage(e.getJoinMessage().replace(p.getName(), utils.getPlayerNicknames().get(p.getUniqueId())));
			}
		}
		
		Bukkit.getScheduler().runTaskLater(eazyNick, new Runnable() {

			@EventHandler
			public void run() {
				if(p.hasPermission("nick.bypass")) {
					if((eazyNick.getMySQL() != null) && eazyNick.getMySQL().isConnected()) {
						for (Player all : Bukkit.getOnlinePlayers()) {
							NickManager apiAll = new NickManager(all);
							
							if (apiAll.isNicked()) {
								String name = apiAll.getNickName();

								apiAll.unnickPlayerWithoutRemovingMySQL(false);
								
								Bukkit.getScheduler().runTaskLater(eazyNick, new Runnable() {

									@Override
									public void run() {
										Bukkit.getPluginManager().callEvent(new PlayerNickEvent(all, name, mysqlNickManager.getSkinName(all.getUniqueId()),
												mysqlPlayerDataManager.getChatPrefix(all.getUniqueId()),
												mysqlPlayerDataManager.getChatSuffix(all.getUniqueId()),
												mysqlPlayerDataManager.getTabPrefix(all.getUniqueId()),
												mysqlPlayerDataManager.getTabSuffix(all.getUniqueId()),
												mysqlPlayerDataManager.getTagPrefix(all.getUniqueId()),
												mysqlPlayerDataManager.getTagSuffix(all.getUniqueId()),
												false,
												true,
												"NONE"));
									}
								}, 5);
							}
						}
					}
				}
				
				if (fileUtils.getConfig().getBoolean("BungeeCord")) {
					if (!(fileUtils.getConfig().getBoolean("LobbyMode"))) {
						if (mysqlNickManager.isPlayerNicked(p.getUniqueId())) {
							if (!(api.isNicked())) {
								Bukkit.getScheduler().runTaskLater(eazyNick, new Runnable() {

									@Override
									public void run() {
										utils.performReNick(p);
									}
								}, 15);
							}
						}
					} else {
						if (mysqlNickManager.isPlayerNicked(p.getUniqueId())) {
							if (fileUtils.getConfig().getBoolean("GetNewNickOnEveryServerSwitch")) {
								String name = api.getRandomName();
								
								mysqlNickManager.removePlayer(p.getUniqueId());
								mysqlNickManager.addPlayer(p.getUniqueId(), name, name);
							}
						}
					}

					if (fileUtils.getConfig().getBoolean("NickItem.getOnJoin")) {
						if (p.hasPermission("nick.item")) {
							if (!(mysqlNickManager.isPlayerNicked(p.getUniqueId())))
								p.getInventory().setItem(fileUtils.getConfig().getInt("NickItem.Slot") - 1, utils.createItem(Material.getMaterial(fileUtils.getConfig().getString("NickItem.ItemType.Disabled")), fileUtils.getConfig().getInt("NickItem.ItemAmount.Disabled"), fileUtils.getConfig().getInt("NickItem.MetaData.Disabled"), languageFileUtils.getConfigString("NickItem.BungeeCord.DisplayName.Disabled"), languageFileUtils.getConfigString("NickItem.ItemLore.Disabled").replace("&n", "\n"), fileUtils.getConfig().getBoolean("NickItem.Enchanted.Disabled")));
							else
								p.getInventory().setItem(fileUtils.getConfig().getInt("NickItem.Slot") - 1, utils.createItem(Material.getMaterial(fileUtils.getConfig().getString("NickItem.ItemType.Enabled")), fileUtils.getConfig().getInt("NickItem.ItemAmount.Enabled"), fileUtils.getConfig().getInt("NickItem.MetaData.Enabled"), languageFileUtils.getConfigString("NickItem.BungeeCord.DisplayName.Enabled"), languageFileUtils.getConfigString("NickItem.ItemLore.Enabled").replace("&n", "\n"), fileUtils.getConfig().getBoolean("NickItem.Enchanted.Enabled")));
						}
					}
				} else {
					if (fileUtils.getConfig().getBoolean("NickItem.getOnJoin")) {
						if (p.hasPermission("nick.item")) {
							if (fileUtils.getConfig().getBoolean("NickOnWorldChange"))
								p.getInventory().setItem(fileUtils.getConfig().getInt("NickItem.Slot") - 1, utils.createItem(Material.getMaterial(fileUtils.getConfig().getString("NickItem.ItemType.Disabled")), fileUtils.getConfig().getInt("NickItem.ItemAmount.Disabled"), fileUtils.getConfig().getInt("NickItem.MetaData.Disabled"), languageFileUtils.getConfigString("NickItem.WorldChange.DisplayName.Disabled"), languageFileUtils.getConfigString("NickItem.ItemLore.Disabled").replace("&n", "\n"), fileUtils.getConfig().getBoolean("NickItem.Enchanted.Disabled")));
							else
								p.getInventory().setItem(fileUtils.getConfig().getInt("NickItem.Slot") - 1, utils.createItem(Material.getMaterial(fileUtils.getConfig().getString("NickItem.ItemType.Disabled")), fileUtils.getConfig().getInt("NickItem.ItemAmount.Disabled"), fileUtils.getConfig().getInt("NickItem.MetaData.Disabled"), languageFileUtils.getConfigString("NickItem.DisplayName.Disabled"), languageFileUtils.getConfigString("NickItem.ItemLore.Disabled").replace("&n", "\n"), fileUtils.getConfig().getBoolean("NickItem.Enchanted.Disabled")));
						}
					}
				}
				
				if (fileUtils.getConfig().getBoolean("JoinNick")) {
					if (!(api.isNicked())) {
						Bukkit.getScheduler().runTaskLater(eazyNick, new Runnable() {

							@Override
							public void run() {
								utils.performNick(p, "RANDOM");
							}
						}, 10);
					}
				} else if (!(fileUtils.getConfig().getBoolean("DiconnectUnnick"))) {
					if (api.isNicked()) {
						if((eazyNick.getMySQL() != null) && eazyNick.getMySQL().isConnected()) {
							api.unnickPlayerWithoutRemovingMySQL(false);
							
							Bukkit.getScheduler().runTaskLater(eazyNick, new Runnable() {

								@Override
								public void run() {
									Bukkit.getPluginManager().callEvent(new PlayerNickEvent(p, api.getNickName(), mysqlNickManager.getSkinName(p.getUniqueId()),
											mysqlPlayerDataManager.getChatPrefix(p.getUniqueId()),
											mysqlPlayerDataManager.getChatSuffix(p.getUniqueId()),
											mysqlPlayerDataManager.getTabPrefix(p.getUniqueId()),
											mysqlPlayerDataManager.getTabSuffix(p.getUniqueId()),
											mysqlPlayerDataManager.getTagPrefix(p.getUniqueId()),
											mysqlPlayerDataManager.getTagSuffix(p.getUniqueId()),
											false,
											false,
											"NONE"));
								}
							}, 10);
						} else if(utils.getPlayerNicknames().containsKey(p.getUniqueId())) {
							String nickName = utils.getPlayerNicknames().get(p.getUniqueId()), skinName = utils.getLastSkinNames().get(p.getUniqueId()), rankName = api.getGroupName(), chatPrefix = "", chatSuffix = "", tabPrefix = "", tabSuffix = "", tagPrefix = "", tagSuffix = "";
							
							for (int i = 1; i <= 18; i++) {
								if(rankName.equals(bookGUIFileUtils.getConfig().getString("BookGUI.Rank" + i + ".RankName"))) {
									chatPrefix = bookGUIFileUtils.getConfigString("Settings.NickFormat.Rank" + i + ".ChatPrefix");
									chatSuffix = bookGUIFileUtils.getConfigString("Settings.NickFormat.Rank" + i + ".ChatSuffix");
									tabPrefix = bookGUIFileUtils.getConfigString("Settings.NickFormat.Rank" + i + ".TabPrefix");
									tabSuffix = bookGUIFileUtils.getConfigString("Settings.NickFormat.Rank" + i + ".TabSuffix");
									tagPrefix = bookGUIFileUtils.getConfigString("Settings.NickFormat.Rank" + i + ".TagPrefix");
									tagSuffix = bookGUIFileUtils.getConfigString("Settings.NickFormat.Rank" + i + ".TagSuffix");
								}
							}
							
							String randomColor = "§" + ("0123456789abcdef".charAt(new Random().nextInt(16)));
							
							chatPrefix = chatPrefix.replaceAll("%randomColor%", randomColor);
							chatSuffix = chatSuffix.replaceAll("%randomColor%", randomColor);
							tabPrefix = tabPrefix.replaceAll("%randomColor%", randomColor);
							tabSuffix = tabSuffix.replaceAll("%randomColor%", randomColor);
							tagPrefix = tagPrefix.replaceAll("%randomColor%", randomColor);
							tagSuffix = tagSuffix.replaceAll("%randomColor%", randomColor);
							
							api.unnickPlayerWithoutRemovingMySQL(false);

							if(utils.getLastSkinNames().containsKey(p.getUniqueId()))
								utils.getLastSkinNames().remove(p.getUniqueId());
							
							if(utils.getLastNickNames().containsKey(p.getUniqueId()))
								utils.getLastNickNames().remove(p.getUniqueId());
							
							utils.getLastSkinNames().put(p.getUniqueId(), skinName);
							utils.getLastNickNames().put(p.getUniqueId(), nickName);
							
							new NickManager(p).setGroupName(rankName);
							
							Bukkit.getPluginManager().callEvent(new PlayerNickEvent(p, nickName, skinName, chatPrefix, chatSuffix, tabPrefix, tabSuffix, tagPrefix, tagSuffix, false, true, rankName));
						}
					}
				}
			}
		}, 5);
	}

}
