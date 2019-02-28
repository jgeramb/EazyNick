package net.dev.nickplugin.listeners;

import java.util.Random;
import java.util.UUID;

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

import com.nametagedit.plugin.NametagEdit;

import net.dev.nickplugin.main.Main;
import net.dev.nickplugin.sql.MySQLNickManager;
import net.dev.nickplugin.sql.MySQLPlayerDataManager;
import net.dev.nickplugin.utils.FileUtils;
import net.dev.nickplugin.utils.NickManager;
import net.dev.nickplugin.utils.Utils;
import net.dev.nickplugin.utils.scoreboard.ScoreboardTeamManager;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.CloudServer;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class NickListener_1_13 implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		NickManager api = new NickManager(p);

		if (!(Utils.canUseNick.containsKey(p.getUniqueId()))) {
			Utils.canUseNick.put(p.getUniqueId(), true);
		}

		if (!(Main.version.equalsIgnoreCase("1_7_R4"))) {
			p.setCustomName(p.getName());
		}

		String joinNickName = NickManager.getRandomName();

		if (e.getJoinMessage() != null && e.getJoinMessage() != "") {
			if (MySQLNickManager.isPlayerNicked(p.getUniqueId())) {
				if (e.getJoinMessage().contains("formerly known as")) {
					e.setJoinMessage("§e" + p.getName() + " joined the game");
				}

				e.setJoinMessage(
						e.getJoinMessage().replace(p.getName(), MySQLNickManager.getNickName(p.getUniqueId())));
			} else if (Utils.playerNicknames.containsKey(p.getUniqueId())) {
				if (e.getJoinMessage().contains("formerly known as")) {
					e.setJoinMessage("§e" + p.getName() + " joined the game");
				}

				e.setJoinMessage(e.getJoinMessage().replace(p.getName(), Utils.playerNicknames.get(p.getUniqueId())));
			}
		}

		Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), new Runnable() {

			@Override
			public void run() {
				if(p.hasPermission("nick.bypass")) {
					for (UUID uuid : Utils.nickedPlayers) {
						Player t = Bukkit.getPlayer(uuid);
						
						if(t != null) {
							String nickName = t.getName();
							
							NickManager nickAPI = new NickManager(t);
							nickAPI.unnickPlayer();
							nickAPI.nickPlayer(nickName);
						}
					}
				}
				
				if (MySQLPlayerDataManager.isRegistered(p.getUniqueId())) {
					Utils.oldDisplayNames.put(p.getUniqueId(), p.getDisplayName());
					Utils.oldPlayerListNames.put(p.getUniqueId(), p.getPlayerListName());

					if (Utils.permissionsExStatus()) {
						if (!(Utils.oldPermissionsExGroups.containsKey(p.getUniqueId()))) {
							Utils.oldPermissionsExGroups.put(p.getUniqueId(),
									MySQLPlayerDataManager.getOldPermissionsExRankAsStringArray(p.getUniqueId()));
						}
					}

					if (FileUtils.cfg.getBoolean("Settings.NameChangeOptions.NameTagColored") == true) {
						if (!(Utils.scoreboardTeamManagers.containsKey(p.getUniqueId()))) {
							if (MySQLNickManager.isPlayerNicked(p.getUniqueId())) {
								Utils.scoreboardTeamManagers.put(p.getUniqueId(),
										new ScoreboardTeamManager(p, MySQLNickManager.getNickName(p.getUniqueId()),
												MySQLPlayerDataManager.getTagPrefix(p.getUniqueId()),
												MySQLPlayerDataManager.getTagSuffix(p.getUniqueId())));
							} else {
								Utils.scoreboardTeamManagers.put(p.getUniqueId(),
										new ScoreboardTeamManager(p, p.getName(),
												MySQLPlayerDataManager.getTagPrefix(p.getUniqueId()),
												MySQLPlayerDataManager.getTagSuffix(p.getUniqueId())));
							}
						} else {
							Utils.scoreboardTeamManagers.remove(p.getUniqueId());

							if (MySQLNickManager.isPlayerNicked(p.getUniqueId())) {
								Utils.scoreboardTeamManagers.put(p.getUniqueId(),
										new ScoreboardTeamManager(p, MySQLNickManager.getNickName(p.getUniqueId()),
												MySQLPlayerDataManager.getTagPrefix(p.getUniqueId()),
												MySQLPlayerDataManager.getTagSuffix(p.getUniqueId())));
							} else {
								Utils.scoreboardTeamManagers.put(p.getUniqueId(),
										new ScoreboardTeamManager(p, p.getName(),
												MySQLPlayerDataManager.getTagPrefix(p.getUniqueId()),
												MySQLPlayerDataManager.getTagSuffix(p.getUniqueId())));
							}
						}

						ScoreboardTeamManager sbtm = Utils.scoreboardTeamManagers.get(p.getUniqueId());

						sbtm.destroyTeam();
						sbtm.createTeam();
					}

					if (Utils.nameTagEditStatus()) {
						NametagEdit.getApi().setPrefix(p.getPlayer(), ChatColor.translateAlternateColorCodes('&',
								FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Prefix")));
						NametagEdit.getApi().setSuffix(p.getPlayer(), ChatColor.translateAlternateColorCodes('&',
								FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Suffix")));
					}

					if (!(Main.version.equalsIgnoreCase("1_7_R4"))) {
						if (MySQLNickManager.isPlayerNicked(p.getUniqueId())) {
							if (FileUtils.cfg.getBoolean("Settings.NameChangeOptions.DisplayNameColored")) {
								p.setDisplayName(MySQLPlayerDataManager.getChatPrefix(p.getUniqueId())
										+ MySQLNickManager.getNickName(p.getUniqueId())
										+ MySQLPlayerDataManager.getChatSuffix(p.getUniqueId()));
							}

							if (FileUtils.cfg.getBoolean("Settings.NameChangeOptions.PlayerListNameColored")) {
								api.setPlayerListName(MySQLPlayerDataManager.getTabPrefix(p.getUniqueId())
										+ MySQLNickManager.getNickName(p.getUniqueId())
										+ MySQLPlayerDataManager.getTabSuffix(p.getUniqueId()));
							}
						} else if (Utils.playerNicknames.containsKey(p.getUniqueId())) {
							if (FileUtils.cfg.getBoolean("Settings.NameChangeOptions.DisplayNameColored")) {
								p.setDisplayName(MySQLPlayerDataManager.getChatPrefix(p.getUniqueId())
										+ Utils.playerNicknames.get(p.getUniqueId())
										+ MySQLPlayerDataManager.getChatSuffix(p.getUniqueId()));
							}

							if (FileUtils.cfg.getBoolean("Settings.NameChangeOptions.PlayerListNameColored")) {
								api.setPlayerListName(MySQLPlayerDataManager.getTabPrefix(p.getUniqueId())
										+ Utils.playerNicknames.get(p.getUniqueId())
										+ MySQLPlayerDataManager.getTabSuffix(p.getUniqueId()));
							}
						} else {
							if (FileUtils.cfg.getBoolean("Settings.NameChangeOptions.DisplayNameColored")) {
								p.setDisplayName(MySQLPlayerDataManager.getChatPrefix(p.getUniqueId()) + p.getName()
										+ MySQLPlayerDataManager.getChatSuffix(p.getUniqueId()));
							}

							if (FileUtils.cfg.getBoolean("Settings.NameChangeOptions.PlayerListNameColored")) {
								api.setPlayerListName(MySQLPlayerDataManager.getTabPrefix(p.getUniqueId()) + p.getName()
										+ MySQLPlayerDataManager.getTabSuffix(p.getUniqueId()));
							}
						}
					} else {
						if (MySQLNickManager.isPlayerNicked(p.getUniqueId())) {
							if (FileUtils.cfg.getBoolean("Settings.NameChangeOptions.DisplayNameColored")) {
								String format = MySQLPlayerDataManager.getChatPrefix(p.getUniqueId())
										+ MySQLNickManager.getNickName(p.getUniqueId())
										+ MySQLPlayerDataManager.getChatSuffix(p.getUniqueId());

								if (format.length() <= 16) {
									p.setDisplayName(format);
								} else {
									p.setDisplayName(MySQLNickManager.getNickName(p.getUniqueId()));
								}
							}

							if (FileUtils.cfg.getBoolean("Settings.NameChangeOptions.PlayerListNameColored")) {
								String format = MySQLPlayerDataManager.getTabPrefix(p.getUniqueId())
										+ MySQLNickManager.getNickName(p.getUniqueId())
										+ MySQLPlayerDataManager.getTabSuffix(p.getUniqueId());

								if (format.length() <= 16) {
									api.setPlayerListName(format);
								} else {
									api.setPlayerListName(MySQLNickManager.getNickName(p.getUniqueId()));
								}
							}
						} else if (Utils.playerNicknames.containsKey(p.getUniqueId())) {
							if (FileUtils.cfg.getBoolean("Settings.NameChangeOptions.DisplayNameColored")) {
								String format = MySQLPlayerDataManager.getChatPrefix(p.getUniqueId())
										+ Utils.playerNicknames.get(p.getUniqueId())
										+ MySQLPlayerDataManager.getChatSuffix(p.getUniqueId());

								if (format.length() <= 16) {
									p.setDisplayName(format);
								} else {
									p.setDisplayName(Utils.playerNicknames.get(p.getUniqueId()));
								}

								p.setDisplayName(MySQLPlayerDataManager.getChatPrefix(p.getUniqueId())
										+ Utils.playerNicknames.get(p.getUniqueId())
										+ MySQLPlayerDataManager.getChatSuffix(p.getUniqueId()));
							}

							if (FileUtils.cfg.getBoolean("Settings.NameChangeOptions.PlayerListNameColored")) {
								String format = MySQLPlayerDataManager.getTabPrefix(p.getUniqueId())
										+ Utils.playerNicknames.get(p.getUniqueId())
										+ MySQLPlayerDataManager.getTabSuffix(p.getUniqueId());

								if (format.length() <= 16) {
									api.setPlayerListName(format);
								} else {
									api.setPlayerListName(Utils.playerNicknames.get(p.getUniqueId()));
								}
							}
						} else {
							if (FileUtils.cfg.getBoolean("Settings.NameChangeOptions.DisplayNameColored")) {
								String format = MySQLPlayerDataManager.getChatPrefix(p.getUniqueId()) + p.getName()
										+ MySQLPlayerDataManager.getChatSuffix(p.getUniqueId());

								if (format.length() <= 16) {
									p.setDisplayName(format);
								} else {
									p.setDisplayName(p.getName());
								}
							}

							if (FileUtils.cfg.getBoolean("Settings.NameChangeOptions.PlayerListNameColored")) {
								String format = MySQLPlayerDataManager.getTabPrefix(p.getUniqueId()) + p.getName()
										+ MySQLPlayerDataManager.getTabSuffix(p.getUniqueId());

								if (format.length() <= 16) {
									api.setPlayerListName(format);
								} else {
									api.setPlayerListName(p.getName());
								}
							}
						}
					}
				}

				for (Player all : Bukkit.getOnlinePlayers()) {
					NickManager apiAll = new NickManager(all);

					if (apiAll.isNicked()) {
						if (Utils.scoreboardTeamManagers.containsKey(all.getUniqueId())) {
							ScoreboardTeamManager sbtmAll = Utils.scoreboardTeamManagers.get(all.getUniqueId());

							sbtmAll.destroyTeam();
							sbtmAll.createTeam();
						}
					}
				}

				if (FileUtils.cfg.getBoolean("BungeeCord") == true) {
					if (FileUtils.cfg.getBoolean("LobbyMode") == false) {
						if (MySQLNickManager.isPlayerNicked(p.getUniqueId())) {
							if (!(api.isNicked())) {
								String nickName = MySQLNickManager.getNickName(p.getUniqueId());

								Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), new Runnable() {

									@Override
									public void run() {
										p.chat("/renick " + nickName);
									}
								}, 5);
							}
						}
					} else {
						if (MySQLNickManager.isPlayerNicked(p.getUniqueId())) {
							if (FileUtils.cfg.getBoolean("GetNewNickOnEveryServerSwitch") == true) {
								MySQLNickManager.removePlayer(p.getUniqueId());
								MySQLNickManager.addPlayer(p.getUniqueId(),
										Utils.nickNames.get((new Random().nextInt(Utils.nickNames.size()))));
							}
						}
					}

					if (FileUtils.cfg.getBoolean("NickItem.getOnJoin") == true) {
						if (p.hasPermission("nick.item")) {
							if (!(MySQLNickManager.isPlayerNicked(p.getUniqueId()))) {
								p.getInventory().setItem(FileUtils.cfg.getInt("NickItem.Slot") - 1, Utils.createItem(
										Material.getMaterial(FileUtils.cfg.getString("NickItem.ItemType.Disabled")),
										FileUtils.cfg.getInt("NickItem.ItemAmount.Disabled"),
										FileUtils.cfg.getInt("NickItem.MetaData.Disabled"),
										ChatColor.translateAlternateColorCodes('&',
												FileUtils.cfg.getString("NickItem.BungeeCord.DisplayName.Disabled")),
										ChatColor.translateAlternateColorCodes('&',
												FileUtils.cfg.getString("NickItem.ItemLore.Disabled").replace("&n",
														"\n")),
										FileUtils.cfg.getBoolean("NickItem.Enchanted.Disabled")));
							} else {
								p.getInventory().setItem(FileUtils.cfg.getInt("NickItem.Slot") - 1, Utils.createItem(
										Material.getMaterial(FileUtils.cfg.getString("NickItem.ItemType.Enabled")),
										FileUtils.cfg.getInt("NickItem.ItemAmount.Enabled"),
										FileUtils.cfg.getInt("NickItem.MetaData.Enabled"),
										ChatColor.translateAlternateColorCodes('&',
												FileUtils.cfg.getString("NickItem.BungeeCord.DisplayName.Enabled")),
										ChatColor.translateAlternateColorCodes('&',
												FileUtils.cfg.getString("NickItem.ItemLore.Enabled").replace("&n",
														"\n")),
										FileUtils.cfg.getBoolean("NickItem.Enchanted.Enabled")));
							}
						}
					}
				} else {
					if (FileUtils.cfg.getBoolean("NickItem.getOnJoin") == true) {
						if (p.hasPermission("nick.item")) {
							if (FileUtils.cfg.getBoolean("NickOnWorldChange") == true) {
								p.getInventory().setItem(FileUtils.cfg.getInt("NickItem.Slot") - 1, Utils.createItem(
										Material.getMaterial(FileUtils.cfg.getString("NickItem.ItemType.Disabled")),
										FileUtils.cfg.getInt("NickItem.ItemAmount.Disabled"),
										FileUtils.cfg.getInt("NickItem.MetaData.Disabled"),
										ChatColor.translateAlternateColorCodes('&',
												FileUtils.cfg.getString("NickItem.WorldChange.DisplayName.Disabled")),
										ChatColor.translateAlternateColorCodes('&',
												FileUtils.cfg.getString("NickItem.ItemLore.Disabled").replace("&n",
														"\n")),
										FileUtils.cfg.getBoolean("NickItem.Enchanted.Disabled")));
							} else {
								p.getInventory().setItem(FileUtils.cfg.getInt("NickItem.Slot") - 1,
										Utils.createItem(
												Material.getMaterial(
														FileUtils.cfg.getString("NickItem.ItemType.Disabled")),
												FileUtils.cfg.getInt("NickItem.ItemAmount.Disabled"),
												FileUtils.cfg.getInt("NickItem.MetaData.Disabled"),
												ChatColor.translateAlternateColorCodes('&',
														FileUtils.cfg.getString("NickItem.DisplayName.Disabled")),
												ChatColor.translateAlternateColorCodes('&',
														FileUtils.cfg.getString("NickItem.ItemLore.Disabled")
																.replace("&n", "\n")),
												FileUtils.cfg.getBoolean("NickItem.Enchanted.Disabled")));
							}
						}
					}
				}

				if (FileUtils.cfg.getBoolean("JoinNick") == true) {
					if (!(api.isNicked())) {
						Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), new Runnable() {

							@Override
							public void run() {
								p.chat("/nick " + joinNickName);
							}
						}, 5);
					}
				} else if (FileUtils.cfg.getBoolean("DiconnectUnnick") == false) {
					if (!(api.isNicked())) {
						if (Utils.playerNicknames.containsKey(p.getUniqueId())) {
							String name = Utils.playerNicknames.get(p.getUniqueId());

							Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), new Runnable() {

								@Override
								public void run() {
									api.unnickPlayer();
									p.chat("/renick " + name);
								}
							}, 5);
						}
					}
				}
			}
		}, 5);
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();

		if (FileUtils.cfg.getBoolean("DisconnectUnnick") == true) {
			NickManager api = new NickManager(p);

			if (api.isNicked()) {
				if (Utils.luckPermsStatus()) {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + api.getRealName()
							+ " permission unset prefix.99." + Utils.luckPermsPrefixes.get(p.getUniqueId()));
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + api.getRealName()
							+ " permission unset suffix.99." + Utils.luckPermsSufixes.get(p.getUniqueId()));

					Utils.luckPermsPrefixes.remove(p.getUniqueId());
					Utils.luckPermsSufixes.remove(p.getUniqueId());
				}

				if (Utils.permissionsExStatus()) {
					PermissionUser user = PermissionsEx.getUser(p);

					if (FileUtils.cfg.getBoolean("SwitchPermissionsExGroupByNicking")) {
						if (Utils.oldPermissionsExGroups.containsKey(p.getUniqueId())) {
							user.setGroups(Utils.oldPermissionsExGroups.get(p.getUniqueId()));
						}
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

				if (FileUtils.cfg.getBoolean("BungeeCord") == true) {
					if (!(MySQLNickManager.isPlayerNicked(p.getUniqueId()))) {
						MySQLPlayerDataManager.removeData(p.getUniqueId());
					}
				}

				if (FileUtils.cfg.getBoolean("Settings.NameChangeOptions.NameTagColored") == true) {
					if (Utils.scoreboardTeamManagers.containsKey(p.getUniqueId())) {
						if (Utils.scoreboardTeamContents.contains(api.getRealName())) {
							ScoreboardTeamManager sbtm = Utils.scoreboardTeamManagers.get(p.getUniqueId());

							Utils.scoreboardTeamContents.remove(api.getRealName());

							sbtm.destroyTeam();
							sbtm.createTeam();

							Utils.scoreboardTeamManagers.remove(p.getUniqueId());
						}
					}
				}

				if (Utils.cloudNetStatus()) {
					CloudPlayer cloudPlayer = CloudAPI.getInstance().getOnlinePlayer(p.getUniqueId());

					CloudServer.getInstance().updateNameTags(p);
					CloudAPI.getInstance().updatePlayer(cloudPlayer);
				}

				if (Utils.nameTagEditStatus()) {
					NametagEdit.getApi().reloadNametag(p);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();

		if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if ((e.getItem() != null) && (e.getItem().getType() != Material.AIR && e.getItem().getItemMeta() != null
					&& e.getItem().getItemMeta().getDisplayName() != null)) {
				if (FileUtils.cfg.getBoolean("NickItem.getOnJoin") == true) {
					if (e.getItem().getItemMeta().getDisplayName()
							.equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&',
									FileUtils.cfg.getString("NickItem.DisplayName.Disabled")))) {
						e.setCancelled(true);
						p.chat("/nick");

						p.getInventory().setItem(p.getInventory().getHeldItemSlot(), Utils.createItem(
								Material.getMaterial(FileUtils.cfg.getString("NickItem.ItemType.Enabled")),
								FileUtils.cfg.getInt("NickItem.ItemAmount.Enabled"),
								FileUtils.cfg.getInt("NickItem.MetaData.Enabled"),
								ChatColor.translateAlternateColorCodes('&',
										FileUtils.cfg.getString("NickItem.DisplayName.Enabled")),
								ChatColor.translateAlternateColorCodes('&',
										FileUtils.cfg.getString("NickItem.ItemLore.Enabled").replace("&n", "\n")),
								FileUtils.cfg.getBoolean("NickItem.Enchanted.Enabled")));
					} else if (e.getItem().getItemMeta().getDisplayName()
							.equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&',
									FileUtils.cfg.getString("NickItem.DisplayName.Enabled")))) {
						e.setCancelled(true);
						p.chat("/unnick");

						p.getInventory().setItem(p.getInventory().getHeldItemSlot(), Utils.createItem(
								Material.getMaterial(FileUtils.cfg.getString("NickItem.ItemType.Disabled")),
								FileUtils.cfg.getInt("NickItem.ItemAmount.Disabled"),
								FileUtils.cfg.getInt("NickItem.MetaData.Disabled"),
								ChatColor.translateAlternateColorCodes('&',
										FileUtils.cfg.getString("NickItem.DisplayName.Disabled")),
								ChatColor.translateAlternateColorCodes('&',
										FileUtils.cfg.getString("NickItem.ItemLore.Disabled").replace("&n", "\n")),
								FileUtils.cfg.getBoolean("NickItem.Enchanted.Disabled")));
					} else {
						if (FileUtils.cfg.getBoolean("NickOnWorldChange") == true) {
							if (e.getItem().getItemMeta().getDisplayName()
									.equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&',
											FileUtils.cfg.getString("NickItem.WorldChange.DisplayName.Disabled")))) {
								e.setCancelled(true);

								Utils.nickOnWorldChangePlayers.add(p.getUniqueId());
								p.getInventory().setItem(p.getInventory().getHeldItemSlot(), Utils.createItem(
										Material.getMaterial(FileUtils.cfg.getString("NickItem.ItemType.Enabled")),
										FileUtils.cfg.getInt("NickItem.ItemAmount.Enabled"),
										FileUtils.cfg.getInt("NickItem.MetaData.Enabled"),
										ChatColor.translateAlternateColorCodes('&',
												FileUtils.cfg.getString("NickItem.WorldChange.DisplayName.Enabled")),
										ChatColor.translateAlternateColorCodes('&',
												FileUtils.cfg.getString("NickItem.ItemLore.Enabled").replace("&n",
														"\n")),
										FileUtils.cfg.getBoolean("NickItem.Enchanted.Enabled")));

								p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&',
										FileUtils.cfg.getString("Messages.WorldChangeAutoNickEnabled")));
							} else if (e.getItem().getItemMeta().getDisplayName()
									.equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&',
											FileUtils.cfg.getString("NickItem.WorldChange.DisplayName.Enabled")))) {
								e.setCancelled(true);

								Utils.nickOnWorldChangePlayers.remove(p.getUniqueId());
								p.getInventory().setItem(p.getInventory().getHeldItemSlot(), Utils.createItem(
										Material.getMaterial(FileUtils.cfg.getString("NickItem.ItemType.Disabled")),
										FileUtils.cfg.getInt("NickItem.ItemAmount.Disabled"),
										FileUtils.cfg.getInt("NickItem.MetaData.Disabled"),
										ChatColor.translateAlternateColorCodes('&',
												FileUtils.cfg.getString("NickItem.WorldChange.DisplayName.Disabled")),
										ChatColor.translateAlternateColorCodes('&',
												FileUtils.cfg.getString("NickItem.ItemLore.Disabled").replace("&n",
														"\n")),
										FileUtils.cfg.getBoolean("NickItem.Enchanted.Disabled")));

								p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&',
										FileUtils.cfg.getString("Messages.WorldChangeAutoNickDisabled")));
							}
						}

						if (FileUtils.cfg.getBoolean("BungeeCord") == true) {
							if (!(Utils.nickedPlayers.contains(p.getUniqueId()))) {
								if (e.getItem().getItemMeta().getDisplayName()
										.equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&',
												FileUtils.cfg.getString("NickItem.BungeeCord.DisplayName.Disabled")))) {
									e.setCancelled(true);

									p.chat("/togglenick");
								} else if (e.getItem().getItemMeta().getDisplayName()
										.equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&',
												FileUtils.cfg.getString("NickItem.BungeeCord.DisplayName.Enabled")))) {
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
		if (e.getItemDrop() != null && e.getItemDrop().getItemStack().getType() != Material.AIR
				&& e.getItemDrop().getItemStack().getItemMeta() != null
				&& e.getItemDrop().getItemStack().getItemMeta().getDisplayName() != null) {
			if (e.getItemDrop().getItemStack().getItemMeta().getDisplayName()
					.equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&',
							FileUtils.cfg.getString("NickItem.DisplayName.Enabled")))
					|| e.getItemDrop().getItemStack().getItemMeta().getDisplayName()
							.equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&',
									FileUtils.cfg.getString("NickItem.DisplayName.Disabled")))
					|| e.getItemDrop().getItemStack().getItemMeta().getDisplayName()
							.equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&',
									FileUtils.cfg.getString("NickItem.WorldChange.DisplayName.Enabled")))
					|| e.getItemDrop().getItemStack().getItemMeta().getDisplayName()
							.equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&',
									FileUtils.cfg.getString("NickItem.WorldChange.DisplayName.Disabled")))
					|| e.getItemDrop().getItemStack().getItemMeta().getDisplayName()
							.equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&',
									FileUtils.cfg.getString("NickItem.BungeeCord.DisplayName.Enabled")))
					|| e.getItemDrop().getItemStack().getItemMeta().getDisplayName()
							.equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&',
									FileUtils.cfg.getString("NickItem.BungeeCord.DisplayName.Disabled")))) {
				if (FileUtils.cfg.getBoolean("NickItem.InventorySettings.PlayersCanDropItem") == false) {
					e.setCancelled(true);
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClick(InventoryClickEvent e) {
		if (e.getWhoClicked() instanceof Player) {
			Player p = (Player) e.getWhoClicked();

			if ((e.getCurrentItem() != null)
					&& (e.getCurrentItem().getType() != Material.AIR && e.getCurrentItem().getItemMeta() != null
							&& e.getCurrentItem().getItemMeta().getDisplayName() != null)) {
				if (e.getCurrentItem().getItemMeta().getDisplayName()
						.equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&',
								FileUtils.cfg.getString("NickItem.DisplayName.Enabled")))
						|| e.getCurrentItem().getItemMeta().getDisplayName()
								.equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&',
										FileUtils.cfg.getString("NickItem.DisplayName.Disabled")))
						|| e.getCurrentItem().getItemMeta().getDisplayName()
								.equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&',
										FileUtils.cfg.getString("NickItem.WorldChange.DisplayName.Enabled")))
						|| e.getCurrentItem().getItemMeta().getDisplayName()
								.equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&',
										FileUtils.cfg.getString("NickItem.WorldChange.DisplayName.Disabled")))
						|| e.getCurrentItem().getItemMeta().getDisplayName()
								.equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&',
										FileUtils.cfg.getString("NickItem.BungeeCord.DisplayName.Enabled")))
						|| e.getCurrentItem().getItemMeta().getDisplayName()
								.equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&',
										FileUtils.cfg.getString("NickItem.BungeeCord.DisplayName.Disabled")))) {
					if (FileUtils.cfg.getBoolean("NickItem.InventorySettings.PlayersCanMoveItem") == false) {
						e.setCancelled(true);
					}
				}

				if (e.getInventory().getName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&',
						FileUtils.cfg.getString("NickGUI.InventoryTitle")))) {
					e.setCancelled(true);

					if (e.getCurrentItem().getItemMeta().getDisplayName()
							.equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&',
									FileUtils.cfg.getString("NickGUI.NickItem.DisplayName")))) {
						p.closeInventory();
						p.chat("/nick");
					} else if (e.getCurrentItem().getItemMeta().getDisplayName()
							.equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&',
									FileUtils.cfg.getString("NickGUI.UnnickItem.DisplayName")))) {
						p.closeInventory();
						p.chat("/unnick");
					}
				} else if (Utils.nickNameListPage.containsKey(p.getUniqueId())) {
					String shownPage = "" + (Utils.nickNameListPage.get(p.getUniqueId()) + 1);
					String inventoryName = ChatColor
							.translateAlternateColorCodes('&', FileUtils.cfg.getString("NickNameGUI.InventoryTitle"))
							.replace("%currentPage%", shownPage);

					if (e.getInventory().getName().equalsIgnoreCase(inventoryName)) {
						Integer page = Utils.nickNameListPage.get(p.getUniqueId());
						e.setCancelled(true);

						if (e.getCurrentItem().getItemMeta().getDisplayName()
								.equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&',
										FileUtils.cfg.getString("NickNameGUI.BackItem.DisplayName")))) {
							if (!(page == 0)) {
								Utils.nickNamesHandler.createPage(p, page - 1);
								Utils.nickNameListPage.put(p.getUniqueId(), page - 1);
							} else {
								p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&',
										FileUtils.cfg.getString("Messages.NoMorePages")));
							}
						} else if (e.getCurrentItem().getItemMeta().getDisplayName()
								.equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&',
										FileUtils.cfg.getString("NickNameGUI.NextItem.DisplayName")))) {
							if (page < (Utils.nickNamesHandler.getPages().size() - 1)) {
								if (page != 99) {
									Utils.nickNamesHandler.createPage(p, page + 1);
									Utils.nickNameListPage.put(p.getUniqueId(), page + 1);
								} else {
									p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&',
											FileUtils.cfg.getString("Messages.NoMorePagesCanBeLoaded")));
								}
							} else {
								p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&',
										FileUtils.cfg.getString("Messages.NoMorePages")));
							}
						} else {
							if (e.getCurrentItem().getType().equals(Material.LEGACY_SKULL_ITEM)) {
								String nickName = "";
								String skullOwner = ((SkullMeta) e.getCurrentItem().getItemMeta()).getOwner();

								for (String name : Utils.nickNames) {
									if (skullOwner.equalsIgnoreCase(name)) {
										nickName = name;
									}
								}

								p.closeInventory();
								p.chat("/nick " + nickName);
							}
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();

		if (Utils.health.containsKey(p.getUniqueId())) {
			e.getDrops().clear();
			e.setDeathMessage(null);
			p.spigot().respawn();
		}

		NickManager api = new NickManager(p);
		String deathMessage = (e.getDeathMessage() != null && e.getDeathMessage() != "") ? e.getDeathMessage() : null;

		if (deathMessage != null) {
			if (api.isNicked()) {
				if (FileUtils.cfg.getBoolean("SeeNickSelf") == false) {
					e.setDeathMessage(null);

					for (Player all : Bukkit.getOnlinePlayers()) {
						if (all != p) {
							all.sendMessage(deathMessage);
						} else {
							String msg = deathMessage;
							msg = msg.replace(api.getNickFormat(), api.getOldDisplayName());

							all.sendMessage(msg);
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();

		if (!(e.isCancelled())) {
			NickManager api = new NickManager(p);

			if (api.isNicked()) {
				e.setCancelled(true);

				String format;

				if (FileUtils.cfg.getBoolean("ReplaceNickedChatFormat") == true) {
					format = ChatColor.translateAlternateColorCodes('&',
							FileUtils.cfg.getString("Settings.ChatFormat"));
					format = format.replace("%displayName%", p.getDisplayName());
					format = format.replace("%prefix%", api.getChatPrefix());
					format = format.replace("%suffix%", api.getChatSuffix());
					format = format.replace("%message%", e.getMessage());
				} else
					format = e.getFormat().replace("%1$s", p.getDisplayName()).replace("%2$s", e.getMessage());

				Bukkit.getConsoleSender().sendMessage(format);

				for (Player all : Bukkit.getOnlinePlayers()) {
					if (new NickManager(all).getRealName().equalsIgnoreCase(api.getRealName())
							&& !(all.hasPermission("nick.bypass"))) {
						if (FileUtils.cfg.getBoolean("SeeNickSelf") == true)
							all.sendMessage(format);
						else
							all.sendMessage(format.replace(p.getDisplayName(), api.getOldDisplayName()));
					} else if (all.hasPermission("nick.bypass")) {
						all.sendMessage(format.replace(p.getDisplayName(), api.getOldDisplayName()));
					} else {
						all.sendMessage(format);
					}
				}
			}
		}
	}

	@EventHandler
	public void onWorldChanged(PlayerChangedWorldEvent e) {
		Player p = e.getPlayer();

		if (!(Utils.worldBlackList.contains(p.getWorld().getName().toUpperCase()))) {
			if (FileUtils.cfg.getBoolean("NickOnWorldChange")) {
				if (Utils.nickOnWorldChangePlayers.contains(p.getUniqueId())) {
					if (!(Utils.nickedPlayers.contains(p.getUniqueId()))) {
						p.chat("/renick");
					} else {
						p.chat("/unnick");
						p.chat("/renick");
					}
				}
			}
		}
	}

	@EventHandler
	public void onCommandPreprocess(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();

		if (e.getMessage().toLowerCase().startsWith("/help nick")
				|| e.getMessage().toLowerCase().startsWith("/help eazynick")
				|| e.getMessage().toLowerCase().startsWith("/? nick")
				|| e.getMessage().toLowerCase().startsWith("/? eazynick")) {
			if (p.hasPermission("bukkit.command.help")
					|| Utils.hasLuckPermsPermission(p.getUniqueId(), "bukkit.command.help")) {
				e.setCancelled(true);

				p.sendMessage("§e--------- §fHep: " + Main.getPlugin(Main.class).getDescription().getName()
						+ " §e----------------------");
				p.sendMessage("§7Below is a list of all " + Main.getPlugin(Main.class).getDescription().getName()
						+ " commands:");
				p.sendMessage("§6/eazynick: §r" + Main.getPlugin(Main.class).getCommand("eazynick").getDescription());
			}
		}
	}

}
