package net.dev.nickplugin.listeners;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.SkullMeta;

import net.dev.nickplugin.api.NickManager;
import net.dev.nickplugin.api.PlayerNickEvent;
import net.dev.nickplugin.api.PlayerUnnickEvent;
import net.dev.nickplugin.main.Main;
import net.dev.nickplugin.sql.MySQLNickManager;
import net.dev.nickplugin.sql.MySQLPlayerDataManager;
import net.dev.nickplugin.utils.FileUtils;
import net.dev.nickplugin.utils.LanguageFileUtils;
import net.dev.nickplugin.utils.Utils;
import net.dev.nickplugin.utils.scoreboard.ScoreboardTeamManager;

import me.clip.placeholderapi.PlaceholderAPI;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class NickListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onNick(PlayerNickEvent e) {
		if(!(e.isCancelled())) {
			Player p = e.getPlayer();
			NickManager api = new NickManager(p);
			
			api.updateLuckPerms(e.getTagPrefix(), e.getTagSuffix());
			api.nickPlayer(e.getNickName(), e.getSkinName());
			api.updatePrefixSuffix(e.getTagPrefix(), e.getTagSuffix(), e.getChatPrefix(), e.getChatSuffix(), e.getTabPrefix(), e.getTabSuffix(), e.getGroupName());
			
			Utils.canUseNick.put(p.getUniqueId(), false);
			Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
				
				@Override
				public void run() {
					Utils.canUseNick.put(p.getUniqueId(), true);
				}
			}, FileUtils.cfg.getLong("Settings.NickDelay") * 20);
			
			if(FileUtils.cfg.getBoolean("BungeeCord")) {
				String oldPermissionsExRank = "";
				
				if(Utils.permissionsExStatus()) {
					if(e.isJoinNick())
						oldPermissionsExRank = e.getGroupName();
					else if(Utils.oldPermissionsExGroups.containsKey(p.getUniqueId()))
						oldPermissionsExRank = Utils.oldPermissionsExGroups.get(p.getUniqueId()).toString();
				}
				
				MySQLPlayerDataManager.insertData(p.getUniqueId(), oldPermissionsExRank, e.getChatPrefix(), e.getChatSuffix(), e.getTabPrefix(), e.getTabSuffix(), e.getTagPrefix(), e.getTagSuffix());
			}
			
			p.sendMessage(Utils.prefix + ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("Messages." + (e.isJoinNick() ? "ActiveNick" : "Nick")).replace("%name%", e.getNickName())));
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onUnnick(PlayerUnnickEvent e) {
		if(!(e.isCancelled())) {
			Player p = e.getPlayer();
			
			new NickManager(p).unnickPlayer();
			
			p.sendMessage(Utils.prefix + ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("Messages.Unnick")));
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		NickManager api = new NickManager(p);

		Utils.nameCache.put(p.getUniqueId(), p.getName());
		
		if (!(Utils.canUseNick.containsKey(p.getUniqueId())))
			Utils.canUseNick.put(p.getUniqueId(), true);

		if (!(Main.version.equalsIgnoreCase("1_7_R4")))
			p.setCustomName(p.getName());

		if (e.getJoinMessage() != null && e.getJoinMessage() != "") {
			if (MySQLNickManager.isPlayerNicked(p.getUniqueId())) {
				if (e.getJoinMessage().contains("formerly known as"))
					e.setJoinMessage("§e" + p.getName() + " joined the game");

				e.setJoinMessage(e.getJoinMessage().replace(p.getName(), MySQLNickManager.getNickName(p.getUniqueId())));
			} else if (Utils.playerNicknames.containsKey(p.getUniqueId())) {
				if (e.getJoinMessage().contains("formerly known as"))
					e.setJoinMessage("§e" + p.getName() + " joined the game");

				e.setJoinMessage(e.getJoinMessage().replace(p.getName(), Utils.playerNicknames.get(p.getUniqueId())));
			}
		}

		Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {

			@EventHandler
			public void run() {
				if(p.hasPermission("nick.bypass") && Main.mysql.isConnected()) {
					for (Player all : Bukkit.getOnlinePlayers()) {
						NickManager apiAll = new NickManager(all);
						
						if (apiAll.isNicked()) {
							String name = apiAll.getNickName();

							Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {

								@Override
								public void run() {
									apiAll.unnickPlayerWithoutRemovingMySQL();
									
									Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {

										@Override
										public void run() {
											Bukkit.getPluginManager().callEvent(new PlayerNickEvent(all, name, MySQLNickManager.getSkinName(all.getUniqueId()),
													MySQLPlayerDataManager.getChatPrefix(all.getUniqueId()),
													MySQLPlayerDataManager.getChatSuffix(all.getUniqueId()),
													MySQLPlayerDataManager.getTabPrefix(all.getUniqueId()),
													MySQLPlayerDataManager.getTabSuffix(all.getUniqueId()),
													MySQLPlayerDataManager.getTagPrefix(all.getUniqueId()),
													MySQLPlayerDataManager.getTagSuffix(all.getUniqueId()),
													true, "NONE"));
										}
									}, 10 + (FileUtils.cfg.getBoolean("RandomDisguiseDelay") ? (20 * 2) : 0));
								}
							}, 10);
						}
					}
				}
				
				if (FileUtils.cfg.getBoolean("JoinNick")) {
					if (!(api.isNicked())) {
						Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {

							@Override
							public void run() {
								if(p.hasPermission("nick.use"))
									p.chat("/nick");
							}
						}, 10);
					}
				} else if (!(FileUtils.cfg.getBoolean("DiconnectUnnick"))) {
					if (api.isNicked()) {
						Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {

							@Override
							public void run() {
								api.unnickPlayerWithoutRemovingMySQL();
								
								Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {

									@Override
									public void run() {
										Bukkit.getPluginManager().callEvent(new PlayerNickEvent(p, api.getNickName(), MySQLNickManager.getSkinName(p.getUniqueId()),
												MySQLPlayerDataManager.getChatPrefix(p.getUniqueId()),
												MySQLPlayerDataManager.getChatSuffix(p.getUniqueId()),
												MySQLPlayerDataManager.getTabPrefix(p.getUniqueId()),
												MySQLPlayerDataManager.getTabSuffix(p.getUniqueId()),
												MySQLPlayerDataManager.getTagPrefix(p.getUniqueId()),
												MySQLPlayerDataManager.getTagSuffix(p.getUniqueId()),
												true, "NONE"));
									}
								}, 10 + (FileUtils.cfg.getBoolean("RandomDisguiseDelay") ? (20 * 2) : 0));
							}
						}, 10);
						
						return;
					}
				}
				
				if (FileUtils.cfg.getBoolean("BungeeCord")) {
					if (!(FileUtils.cfg.getBoolean("LobbyMode"))) {
						if (MySQLNickManager.isPlayerNicked(p.getUniqueId())) {
							if (!(api.isNicked())) {
								Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {

									@Override
									public void run() {
										p.chat("/renick");
									}
								}, 10);
							}
						}
					} else {
						if (MySQLNickManager.isPlayerNicked(p.getUniqueId())) {
							if (FileUtils.cfg.getBoolean("GetNewNickOnEveryServerSwitch")) {
								String name = Utils.nickNames.get((new Random().nextInt(Utils.nickNames.size())));
								
								MySQLNickManager.removePlayer(p.getUniqueId());
								MySQLNickManager.addPlayer(p.getUniqueId(), name, name);
							}
						}
					}

					if (FileUtils.cfg.getBoolean("NickItem.getOnJoin")) {
						if (p.hasPermission("nick.item")) {
							if (!(MySQLNickManager.isPlayerNicked(p.getUniqueId())))
								p.getInventory().setItem(FileUtils.cfg.getInt("NickItem.Slot") - 1, Utils.createItem(Material.getMaterial(FileUtils.cfg.getString("NickItem.ItemType.Disabled")), FileUtils.cfg.getInt("NickItem.ItemAmount.Disabled"), FileUtils.cfg.getInt("NickItem.MetaData.Disabled"), ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickItem.BungeeCord.DisplayName.Disabled")), ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickItem.ItemLore.Disabled").replace("&n", "\n")), FileUtils.cfg.getBoolean("NickItem.Enchanted.Disabled")));
							else
								p.getInventory().setItem(FileUtils.cfg.getInt("NickItem.Slot") - 1, Utils.createItem(Material.getMaterial(FileUtils.cfg.getString("NickItem.ItemType.Enabled")), FileUtils.cfg.getInt("NickItem.ItemAmount.Enabled"), FileUtils.cfg.getInt("NickItem.MetaData.Enabled"), ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickItem.BungeeCord.DisplayName.Enabled")), ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickItem.ItemLore.Enabled").replace("&n", "\n")), FileUtils.cfg.getBoolean("NickItem.Enchanted.Enabled")));
						}
					}
				} else {
					if (FileUtils.cfg.getBoolean("NickItem.getOnJoin")) {
						if (p.hasPermission("nick.item")) {
							if (FileUtils.cfg.getBoolean("NickOnWorldChange"))
								p.getInventory().setItem(FileUtils.cfg.getInt("NickItem.Slot") - 1, Utils.createItem(Material.getMaterial(FileUtils.cfg.getString("NickItem.ItemType.Disabled")), FileUtils.cfg.getInt("NickItem.ItemAmount.Disabled"), FileUtils.cfg.getInt("NickItem.MetaData.Disabled"), ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickItem.WorldChange.DisplayName.Disabled")), ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickItem.ItemLore.Disabled").replace("&n", "\n")), FileUtils.cfg.getBoolean("NickItem.Enchanted.Disabled")));
							else
								p.getInventory().setItem(FileUtils.cfg.getInt("NickItem.Slot") - 1, Utils.createItem(Material.getMaterial(FileUtils.cfg.getString("NickItem.ItemType.Disabled")), FileUtils.cfg.getInt("NickItem.ItemAmount.Disabled"), FileUtils.cfg.getInt("NickItem.MetaData.Disabled"), ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickItem.DisplayName.Disabled")), ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickItem.ItemLore.Disabled").replace("&n", "\n")), FileUtils.cfg.getBoolean("NickItem.Enchanted.Disabled")));
						}
					}
				}
			}
		}, 5);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		
		if(Utils.nameCache.containsKey(p.getUniqueId()))
			Utils.nameCache.remove(p.getUniqueId());
		
		if (FileUtils.cfg.getBoolean("DisconnectUnnick")) {
			NickManager api = new NickManager(p);

			if (api.isNicked()) {
				api.resetCloudNET();
				api.resetLuckPerms();

				if (Utils.permissionsExStatus()) {
					PermissionUser user = PermissionsEx.getUser(p);

					if (FileUtils.cfg.getBoolean("SwitchPermissionsExGroupByNicking")) {
						if (Utils.oldPermissionsExGroups.containsKey(p.getUniqueId()))
							user.setGroups(Utils.oldPermissionsExGroups.get(p.getUniqueId()));
					} else {
						user.setPrefix(Utils.oldPermissionsExPrefixes.get(p.getUniqueId()), p.getWorld().getName());
						user.setSuffix(Utils.oldPermissionsExSuffixes.get(p.getUniqueId()), p.getWorld().getName());
					}
				}

				Utils.nickedPlayers.remove(p.getUniqueId());
				Utils.playerNicknames.remove(p.getUniqueId());

				p.setDisplayName(Utils.oldDisplayNames.get(p.getUniqueId()));
				api.setPlayerListName(Utils.oldPlayerListNames.get(p.getUniqueId()));

				Utils.oldDisplayNames.remove(p.getUniqueId());
				Utils.oldPlayerListNames.remove(p.getUniqueId());

				if (FileUtils.cfg.getBoolean("BungeeCord")) {
					if (MySQLNickManager.isPlayerNicked(p.getUniqueId()))
						MySQLNickManager.removePlayer(p.getUniqueId());
					
					if(MySQLPlayerDataManager.isRegistered(p.getUniqueId()))
						MySQLPlayerDataManager.removeData(p.getUniqueId());
				}

				if (FileUtils.cfg.getBoolean("Settings.NameChangeOptions.NameTagColored")) {
					if (Utils.scoreboardTeamManagers.containsKey(p.getUniqueId())) {
						if (Utils.scoreboardTeamContents.get(api.getRealName()).contains(api.getRealName())) {
							ScoreboardTeamManager sbtm = Utils.scoreboardTeamManagers.get(p.getUniqueId());

							Utils.scoreboardTeamContents.remove(api.getRealName());

							sbtm.destroyTeam();
							sbtm.createTeam();

							Utils.scoreboardTeamManagers.remove(p.getUniqueId());
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();

		if ((e.getItem() != null) && (e.getItem().getType() != Material.AIR && (e.getItem().getItemMeta() != null) && (e.getItem().getItemMeta().getDisplayName() != null))) {
			if (FileUtils.cfg.getBoolean("NickItem.getOnJoin")) {
				if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
					if (e.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickItem.DisplayName.Disabled")))) {
						e.setCancelled(true);
						p.chat("/nick");

						p.getInventory().setItem(p.getInventory().getHeldItemSlot(), Utils.createItem(Material.getMaterial(FileUtils.cfg.getString("NickItem.ItemType.Enabled")), FileUtils.cfg.getInt("NickItem.ItemAmount.Enabled"), FileUtils.cfg.getInt("NickItem.MetaData.Enabled"), ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickItem.DisplayName.Enabled")), ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickItem.ItemLore.Enabled").replace("&n", "\n")), FileUtils.cfg.getBoolean("NickItem.Enchanted.Enabled")));
					} else if (e.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickItem.DisplayName.Enabled")))) {
						e.setCancelled(true);
						p.chat("/unnick");

						p.getInventory().setItem(p.getInventory().getHeldItemSlot(), Utils.createItem(Material.getMaterial(FileUtils.cfg.getString("NickItem.ItemType.Disabled")), FileUtils.cfg.getInt("NickItem.ItemAmount.Disabled"), FileUtils.cfg.getInt("NickItem.MetaData.Disabled"), ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickItem.DisplayName.Disabled")), ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickItem.ItemLore.Disabled").replace("&n", "\n")), FileUtils.cfg.getBoolean("NickItem.Enchanted.Disabled")));
					} else {
						if (FileUtils.cfg.getBoolean("NickOnWorldChange")) {
							if (e.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickItem.WorldChange.DisplayName.Disabled")))) {
								e.setCancelled(true);

								Utils.nickOnWorldChangePlayers.add(p.getUniqueId());
								p.getInventory().setItem(p.getInventory().getHeldItemSlot(), Utils.createItem(Material.getMaterial(FileUtils.cfg.getString("NickItem.ItemType.Enabled")), FileUtils.cfg.getInt("NickItem.ItemAmount.Enabled"), FileUtils.cfg.getInt("NickItem.MetaData.Enabled"), ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickItem.WorldChange.DisplayName.Enabled")), ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickItem.ItemLore.Enabled").replace("&n", "\n")), FileUtils.cfg.getBoolean("NickItem.Enchanted.Enabled")));

								p.sendMessage(Utils.prefix + ChatColor.translateAlternateColorCodes('&',
										LanguageFileUtils.cfg.getString("Messages.WorldChangeAutoNickEnabled")));
							} else if (e.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickItem.WorldChange.DisplayName.Enabled")))) {
								e.setCancelled(true);

								Utils.nickOnWorldChangePlayers.remove(p.getUniqueId());
								p.getInventory().setItem(p.getInventory().getHeldItemSlot(), Utils.createItem(Material.getMaterial(FileUtils.cfg.getString("NickItem.ItemType.Disabled")), FileUtils.cfg.getInt("NickItem.ItemAmount.Disabled"), FileUtils.cfg.getInt("NickItem.MetaData.Disabled"), ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickItem.WorldChange.DisplayName.Disabled")), ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickItem.ItemLore.Disabled").replace("&n", "\n")), FileUtils.cfg.getBoolean("NickItem.Enchanted.Disabled")));

								p.sendMessage(Utils.prefix + ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("Messages.WorldChangeAutoNickDisabled")));
							}
						}

						if (FileUtils.cfg.getBoolean("BungeeCord")) {
							if (!(Utils.nickedPlayers.contains(p.getUniqueId()))) {
								if (e.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickItem.BungeeCord.DisplayName.Disabled")))) {
									e.setCancelled(true);

									p.chat("/togglenick");
								} else if (e.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickItem.BungeeCord.DisplayName.Enabled")))) {
									e.setCancelled(true);

									p.chat("/togglenick");
								}
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent e) {
		if ((e.getItemDrop() != null) && (e.getItemDrop().getItemStack().getType() != Material.AIR) && (e.getItemDrop().getItemStack().getItemMeta() != null) && (e.getItemDrop().getItemStack().getItemMeta().getDisplayName() != null)) {
			if (e.getItemDrop().getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickItem.DisplayName.Enabled"))) || e.getItemDrop().getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickItem.DisplayName.Disabled"))) || e.getItemDrop().getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickItem.WorldChange.DisplayName.Enabled"))) || e.getItemDrop().getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickItem.WorldChange.DisplayName.Disabled"))) || e.getItemDrop().getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickItem.BungeeCord.DisplayName.Enabled"))) || e.getItemDrop().getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickItem.BungeeCord.DisplayName.Disabled")))) {
				if (FileUtils.cfg.getBoolean("NickItem.InventorySettings.PlayersCanDropItem") == false)
					e.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInventoryClick(InventoryClickEvent e) {
		if (e.getWhoClicked() instanceof Player) {
			Player p = (Player) e.getWhoClicked();

			if ((e.getCurrentItem() != null) && (e.getCurrentItem().getType() != Material.AIR) && (e.getCurrentItem().getItemMeta() != null) && (e.getCurrentItem().getItemMeta().getDisplayName() != null)) {
				if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickItem.DisplayName.Enabled"))) || e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickItem.DisplayName.Disabled"))) || e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickItem.WorldChange.DisplayName.Enabled"))) || e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickItem.WorldChange.DisplayName.Disabled"))) || e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickItem.BungeeCord.DisplayName.Enabled"))) || e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickItem.BungeeCord.DisplayName.Disabled")))) {
					if (FileUtils.cfg.getBoolean("NickItem.InventorySettings.PlayersCanMoveItem") == false)
						e.setCancelled(true);
				}

				if (e.getView().getTitle().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickGUI.InventoryTitle")))) {
					e.setCancelled(true);

					if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickGUI.NickItem.DisplayName")))) {
						p.closeInventory();
						p.chat("/nick");
					} else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickGUI.UnnickItem.DisplayName")))) {
						p.closeInventory();
						p.chat("/unnick");
					}
				} else if (Utils.nickNameListPage.containsKey(p.getUniqueId())) {
					String shownPage = "" + (Utils.nickNameListPage.get(p.getUniqueId()) + 1);
					String inventoryName = ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickNameGUI.InventoryTitle")).replace("%currentPage%", shownPage);

					if (e.getView().getTitle().equalsIgnoreCase(inventoryName)) {
						Integer page = Utils.nickNameListPage.get(p.getUniqueId());
						e.setCancelled(true);

						if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickNameGUI.BackItem.DisplayName")))) {
							if (!(page == 0)) {
								Utils.nickNamesHandler.createPage(p, page - 1);
								Utils.nickNameListPage.put(p.getUniqueId(), page - 1);
							} else
								p.sendMessage(Utils.prefix + ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("Messages.NoMorePages")));
						} else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickNameGUI.NextItem.DisplayName")))) {
							if (page < (Utils.nickNamesHandler.getPages().size() - 1)) {
								if (page != 99) {
									Utils.nickNamesHandler.createPage(p, page + 1);
									Utils.nickNameListPage.put(p.getUniqueId(), page + 1);
								} else
									p.sendMessage(Utils.prefix + ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("Messages.NoMorePagesCanBeLoaded")));
							} else
								p.sendMessage(Utils.prefix + ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("Messages.NoMorePages")));
						} else {
							if (e.getCurrentItem().getType().equals(Material.getMaterial((Main.version.startsWith("1_13") || Main.version.startsWith("1_14")) ? "LEGACY_SKULL_ITEM" : "SKULL_ITEM"))) {
								String nickName = "";
								String skullOwner = ((SkullMeta) e.getCurrentItem().getItemMeta()).getOwner();

								for (String name : Utils.nickNames)
									if (skullOwner.equalsIgnoreCase(name))
										nickName = name;

								p.closeInventory();
								p.chat("/nick " + nickName);
							}
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();

		NickManager api = new NickManager(p);
		String deathMessage = (e.getDeathMessage() != null && e.getDeathMessage() != "") ? e.getDeathMessage() : null;

		if (deathMessage != null) {
			if (api.isNicked()) {
				if (FileUtils.cfg.getBoolean("SeeNickSelf") == false) {
					e.setDeathMessage(null);

					for (Player all : Bukkit.getOnlinePlayers()) {
						if (all != p)
							all.sendMessage(deathMessage);
						else {
							String msg = deathMessage;
							msg = msg.replace(api.getNickFormat(), api.getOldDisplayName());

							all.sendMessage(msg);
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();

		if (FileUtils.cfg.getBoolean("ReplaceNickedChatFormat")) {
			if (!(e.isCancelled())) {
				NickManager api = new NickManager(p);
	
				if (api.isNicked()) {
					e.setCancelled(true);
					
					String format = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.ChatFormat"));
					format = format.replace("%displayName%", p.getDisplayName());
					format = format.replace("%nickName%", api.getNickName());
					format = format.replace("%playerName%", p.getName());
					format = format.replace("%prefix%", api.getChatPrefix());
					format = format.replace("%suffix%", api.getChatSuffix());
					format = format.replace("%message%", e.getMessage());
					
					if(Utils.placeholderAPIStatus())
						format = PlaceholderAPI.setPlaceholders(p, format);
					
					e.setFormat(format);
					
					Bukkit.getConsoleSender().sendMessage(format);
	
					for (Player all : Bukkit.getOnlinePlayers()) {
						if (all.getName().equalsIgnoreCase(p.getName())) {
							if (FileUtils.cfg.getBoolean("SeeNickSelf"))
								all.sendMessage(format);
							else
								all.sendMessage(format.replace(p.getDisplayName(), api.getOldDisplayName()));
						} else if (all.hasPermission("nick.bypass"))
							all.sendMessage(format.replace(p.getDisplayName(), api.getOldDisplayName()));
						else
							all.sendMessage(format);
					}
				}
			}
		}
	}

	@EventHandler
	public void onWorldChanged(PlayerChangedWorldEvent e) {
		Player p = e.getPlayer();
		NickManager api = new NickManager(p);
		
		if (!(Utils.worldBlackList.contains(p.getWorld().getName().toUpperCase()))) {
			if (FileUtils.cfg.getBoolean("NickOnWorldChange")) {
				if (Utils.nickOnWorldChangePlayers.contains(p.getUniqueId())) {
					if (!(api.isNicked()))
						p.chat("/renick");
					else {
						p.chat("/unnick");
						
						Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {

							@Override
							public void run() {
								api.unnickPlayer();
								
								Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {

									@Override
									public void run() {
										p.chat("/renick");
									}
								}, 10 + (FileUtils.cfg.getBoolean("RandomDisguiseDelay") ? (20 * 2) : 0));
							}
						}, 10);
					}
				}
			}
		}
	}

	@EventHandler
	public void onCommandPreprocess(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();
		String msg = e.getMessage();
		
		for (Player all : Bukkit.getOnlinePlayers()) {
			NickManager api = new NickManager(all);
			
			if(api.isNicked()) {
				String realName = api.getRealName();
				
				if(msg.contains(realName))
					msg = msg.replaceAll(realName, realName + "§r");
			}
		}
		
		e.setMessage(msg);
		
		if (e.getMessage().toLowerCase().startsWith("/help nick") || e.getMessage().toLowerCase().startsWith("/help eazynick") || e.getMessage().toLowerCase().startsWith("/? nick") || e.getMessage().toLowerCase().startsWith("/? eazynick")) {
			if (p.hasPermission("bukkit.command.help") || Utils.hasLuckPermsPermission(p.getUniqueId(), "bukkit.command.help")) {
				e.setCancelled(true);

				p.sendMessage("§e--------- §fHelp: " + Main.getInstance().getDescription().getName() + " §e----------------------");
				p.sendMessage("§7Below is a list of all " + Main.getInstance().getDescription().getName() + " commands:");
				p.sendMessage("§6/eazynick: §r" + Main.getInstance().getCommand("eazynick").getDescription());
			}
		}
	}

}
