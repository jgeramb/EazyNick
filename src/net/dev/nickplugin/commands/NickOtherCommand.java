package net.dev.nickplugin.commands;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nametagedit.plugin.NametagEdit;

import net.dev.nickplugin.main.Main;
import net.dev.nickplugin.sql.MySQLPlayerDataManager;
import net.dev.nickplugin.utils.FileUtils;
import net.dev.nickplugin.utils.NickManager;
import net.dev.nickplugin.utils.Utils;
import net.dev.nickplugin.utils.scoreboard.ScoreboardTeamManager;

import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class NickOtherCommand implements CommandExecutor {

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(p.hasPermission("nick.other") || Utils.hasLuckPermsPermission(p.getUniqueId(), "nick.other")) {
				if(args.length >= 1) {
					Player t = Bukkit.getPlayer(args[0]);
					
					if(t != null) {
						NickManager api = new NickManager(t);
						
						if(!(Utils.nickedPlayers.contains(t.getUniqueId()))) {
							if(args.length >= 2) {
								if(args[1].length() <= 16) {
									String name = args[1].trim();
									
									p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Messages.Other.SelectedNick")).replace("%playerName%", t.getName()).replace("%nickName%", ChatColor.translateAlternateColorCodes('&', name)));
									
									if(t.hasPermission("nick.use")) {
										t.chat("/nick " + name);
									} else {
										boolean isCancelled;
										
										if(!(Utils.blackList.contains(args[0].toUpperCase()))) {
											boolean nickNameIsInUse = false;
											
											for (String nickName : Utils.playerNicknames.values()) {
												if(nickName.toUpperCase().equalsIgnoreCase(name.toUpperCase())) {
													nickNameIsInUse = true;
												}
											}

											if(!(nickNameIsInUse)) {
												boolean playerWithNameIsKnown = false;
												
												for (Player all : Bukkit.getOnlinePlayers()) {
													if(all.getName().toUpperCase().equalsIgnoreCase(name.toUpperCase())) {
														playerWithNameIsKnown = true;
													}
												}
												
												for (OfflinePlayer all : Bukkit.getOfflinePlayers()) {
													if((all != null) && (all.getName() != null) && all.getName().toUpperCase().equalsIgnoreCase(name.toUpperCase())) {
														playerWithNameIsKnown= true;
													}
												}
												
												if(FileUtils.cfg.getBoolean("AllowPlayersToNickAsKnownPlayers") == false && playerWithNameIsKnown) {
													isCancelled = true;
												} else {
													isCancelled = false;
												}
												
												if(!(isCancelled)) {
													Utils.oldDisplayNames.put(t.getUniqueId(), t.getDisplayName());
													Utils.oldPlayerListNames.put(t.getUniqueId(), t.getPlayerListName());
													
													api.nickPlayer(name);
													
													if(Utils.cloudNetStatus()) {
														String prefix = "";
														String suffix = "";
														
														if(Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers()) {
															prefix = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Prefix"));
															suffix = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Suffix"));
														} else {
															prefix = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Prefix"));
															suffix = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Suffix"));
														}
														
														api.changeCloudNET(prefix, suffix);
													}
													
													if(Utils.luckPermsStatus()) {
														String prefix = ((Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers()) ? FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Prefix") : FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Prefix"));
														String suffix = ((Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers()) ? FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Suffix") : FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Suffix"));
														
														Utils.luckPermsPrefixes.put(p.getUniqueId(), prefix);
														Utils.luckPermsSuffixes.put(p.getUniqueId(), suffix);
														
														Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " permission set prefix.99." + prefix);
														Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " permission set suffix.99." + suffix);
													}
													
													if(FileUtils.cfg.getBoolean("Settings.NameChangeOptions.NameTagColored") == true) {
														String prefix = "";
														String suffix = "";
														
														if(Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers()) {
															prefix = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.NameTag.Prefix"));
															suffix = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.NameTag.Suffix"));
														} else {
															prefix = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.NameTag.Prefix"));
															suffix = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.NameTag.Suffix"));
														}
			
														if(!(Utils.scoreboardTeamManagers.containsKey(t.getUniqueId()))) {
															Utils.scoreboardTeamManagers.put(t.getUniqueId(), new ScoreboardTeamManager(p, prefix, suffix));
														} else {
															Utils.scoreboardTeamManagers.remove(t.getUniqueId());
															Utils.scoreboardTeamManagers.put(t.getUniqueId(), new ScoreboardTeamManager(p, prefix, suffix));
														}
														
														ScoreboardTeamManager sbtm = Utils.scoreboardTeamManagers.get(t.getUniqueId());
														
														sbtm.destroyTeam();
														sbtm.createTeam();
													}
													
													String groupName = "";
													
													if(Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers()) {
														groupName = FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PermissionsEx.GroupName");
														
														if(FileUtils.cfg.getBoolean("Settings.NameChangeOptions.PlayerListNameColored") == true) {
															String nameFormatTab = ChatColor.translateAlternateColorCodes('&', (FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.Chat.Prefix") + name + FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.Chat.Suffix")));
															
															if(Main.version == "1_7_R4") {
																if(nameFormatTab.length() <= 16) {
																	t.setPlayerListName(nameFormatTab);
																} else {
																	t.setPlayerListName(t.getName());
																}
															} else {
																t.setPlayerListName(nameFormatTab);
															}
														}
														
														if(FileUtils.cfg.getBoolean("Settings.NameChangeOptions.DisplayNameColored") == true) {
															String nameFormatChat = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Prefix") + name + FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Suffix"));
															
															t.setDisplayName(nameFormatChat);
														}
														
														if(Utils.nameTagEditStatus()) {
															NametagEdit.getApi().setPrefix(t.getPlayer(), ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Prefix")));
															NametagEdit.getApi().setSuffix(t.getPlayer(), ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Suffix")));
														}
													} else {
														groupName = FileUtils.cfg.getString("Settings.NickFormat.PermissionsEx.GroupName");
														
														if(FileUtils.cfg.getBoolean("Settings.NameChangeOptions.PlayerListNameColored") == true) {
															String nameFormatTab = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Prefix") + name + FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Suffix"));
															
															if(Main.version == "1_7_R4") {
																if(nameFormatTab.length() <= 16) {
																	t.setPlayerListName(nameFormatTab);
																} else {
																	t.setPlayerListName(t.getName());
																}
															} else {
																t.setPlayerListName(nameFormatTab);
															}
														}
														
														if(FileUtils.cfg.getBoolean("Settings.NameChangeOptions.DisplayNameColored") == true) {
															String nameFormatChat = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.Chat.Prefix") + name + FileUtils.cfg.getString("Settings.NickFormat.Chat.Suffix"));
															
															t.setDisplayName(nameFormatChat);
														}
														
														if(Utils.nameTagEditStatus()) {
															NametagEdit.getApi().setPrefix(t.getPlayer(), ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Prefix")));
															NametagEdit.getApi().setSuffix(t.getPlayer(), ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Suffix")));
														}
													}
													
													if(Utils.permissionsExStatus()) {
														PermissionUser user = PermissionsEx.getUser(p);
													
														if(FileUtils.cfg.getBoolean("SwitchPermissionsExGroupByNicking")) {
															String groupNames = "";
			
															for (PermissionGroup group : user.getGroups()) {
																groupNames += (" " + group.getName());
															}
															
															if(!(Utils.oldPermissionsExGroups.containsKey(t.getUniqueId()))) {
																Utils.oldPermissionsExGroups.put(t.getUniqueId(), groupNames.trim().split(" "));
															}
															
															user.setGroups(new String[] { groupName });
														} else {
															Utils.oldPermissionsExPrefixes.put(t.getUniqueId(), user.getPrefix());
															Utils.oldPermissionsExSuffixes.put(t.getUniqueId(), user.getSuffix());
															
															if(Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers()) {
																user.setPrefix(ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Prefix")), t.getWorld().getName());
																user.setSuffix(ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Suffix")), t.getWorld().getName());
															} else {
																user.setPrefix(ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Prefix")), t.getWorld().getName());
																user.setSuffix(ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Suffix")), t.getWorld().getName());
															}
														}
													}
													
													Utils.canUseNick.put(t.getUniqueId(), false);
													Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), new Runnable() {
														
														@Override
														public void run() {
															
															Utils.canUseNick.put(t.getUniqueId(), true);
														}
													}, FileUtils.cfg.getLong("Settings.NickDelay") * 20);
													
													if(FileUtils.cfg.getBoolean("BungeeCord") == true) {
														String oldPermissionsExRank = "";
														String prefix = "";
														String suffix = "";
														
														if(Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers()) {
															prefix = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Prefix"));
															suffix = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Suffix"));
														} else {
															prefix = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Prefix"));
															suffix = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Suffix"));
														}
														
														if(Utils.permissionsExStatus()) {
															if(Utils.oldPermissionsExGroups.containsKey(t.getUniqueId())) {
																oldPermissionsExRank = Utils.oldPermissionsExGroups.get(t.getUniqueId()).toString();
															}
														}
														
														MySQLPlayerDataManager.insertData(t.getUniqueId(), oldPermissionsExRank, prefix, suffix, prefix, suffix, prefix, suffix);
													}
													
													t.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Messages.Nick").replace("%name%", name)));
												} else {
													p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Messages.PlayerWithThisNameIsKnown")));
												}
											} else {
												p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Messages.NickNameAlreadyInUse")));
											}
										} else {
											p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Messages.NameNotAllowed")));
										}
									}
								} else {
									p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Messages.NickTooLong")));
								}
							} else {
								p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Messages.Other.RandomNick")).replace("%playerName%", t.getName()));
								
								if(t.hasPermission("nick.use")) {
									t.chat("/nick");
								} else {
									String name = Utils.nickNames.get((new Random().nextInt(Utils.nickNames.size())));
									
									Utils.oldDisplayNames.put(t.getUniqueId(), t.getDisplayName());
									Utils.oldPlayerListNames.put(t.getUniqueId(), t.getPlayerListName());
									
									api.nickPlayer(name);
									
									if(Utils.cloudNetStatus()) {
										String prefix = "";
										String suffix = "";
										
										if(Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers()) {
											prefix = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Prefix"));
											suffix = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Suffix"));
										} else {
											prefix = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Prefix"));
											suffix = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Suffix"));
										}
										
										api.changeCloudNET(prefix, suffix);
									}
									
									if(Utils.luckPermsStatus()) {
										String prefix = ((Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers()) ? FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Prefix") : FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Prefix"));
										String suffix = ((Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers()) ? FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Suffix") : FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Suffix"));
										
										Utils.luckPermsPrefixes.put(p.getUniqueId(), prefix);
										Utils.luckPermsSuffixes.put(p.getUniqueId(), suffix);
										
										Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " permission set prefix.99." + prefix);
										Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " permission set suffix.99." + suffix);
									}
									
									if(FileUtils.cfg.getBoolean("Settings.NameChangeOptions.NameTagColored") == true) {
										String prefix = "";
										String suffix = "";
										
										if(Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers()) {
											prefix = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.NameTag.Prefix"));
											suffix = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.NameTag.Suffix"));
										} else {
											prefix = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.NameTag.Prefix"));
											suffix = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.NameTag.Suffix"));
										}

										if(!(Utils.scoreboardTeamManagers.containsKey(t.getUniqueId()))) {
											Utils.scoreboardTeamManagers.put(t.getUniqueId(), new ScoreboardTeamManager(p, prefix, suffix));
										} else {
											Utils.scoreboardTeamManagers.remove(t.getUniqueId());
											Utils.scoreboardTeamManagers.put(t.getUniqueId(), new ScoreboardTeamManager(p, prefix, suffix));
										}
										
										ScoreboardTeamManager sbtm = Utils.scoreboardTeamManagers.get(t.getUniqueId());
										
										sbtm.destroyTeam();
										sbtm.createTeam();
									}
									
									String groupName = "";
									
									if(Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers()) {
										groupName = FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PermissionsEx.GroupName");
										
										if(FileUtils.cfg.getBoolean("Settings.NameChangeOptions.PlayerListNameColored") == true) {
											String nameFormatTab = ChatColor.translateAlternateColorCodes('&', (FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.Chat.Prefix") + name + FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.Chat.Suffix")));
											
											if(Main.version == "1_7_R4") {
												if(nameFormatTab.length() <= 16) {
													t.setPlayerListName(nameFormatTab);
												} else {
													t.setPlayerListName(t.getName());
												}
											} else {
												t.setPlayerListName(nameFormatTab);
											}
										}
										
										if(FileUtils.cfg.getBoolean("Settings.NameChangeOptions.DisplayNameColored") == true) {
											String nameFormatChat = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Prefix") + name + FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Suffix"));
											
											t.setDisplayName(nameFormatChat);
										}
										
										if(Utils.nameTagEditStatus()) {
											NametagEdit.getApi().setPrefix(t.getPlayer(), ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Prefix")));
											NametagEdit.getApi().setSuffix(t.getPlayer(), ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Suffix")));
										}
									} else {
										groupName = FileUtils.cfg.getString("Settings.NickFormat.PermissionsEx.GroupName");
										
										if(FileUtils.cfg.getBoolean("Settings.NameChangeOptions.DisplayNameColored") == true) {
											String nameFormatTab = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Prefix") + name + FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Suffix"));
											
											if(Main.version == "1_7_R4") {
												if(nameFormatTab.length() <= 16) {
													t.setPlayerListName(nameFormatTab);
												} else {
													t.setPlayerListName(t.getName());
												}
											} else {
												t.setPlayerListName(nameFormatTab);
											}
										}
										
										if(FileUtils.cfg.getBoolean("Settings.NameChangeOptions.PlayerListNameColored") == true) {
											String nameFormatChat = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.Chat.Prefix") + name + FileUtils.cfg.getString("Settings.NickFormat.Chat.Suffix"));
											
											t.setDisplayName(nameFormatChat);
										}
										
										if(Utils.nameTagEditStatus()) {
											NametagEdit.getApi().setPrefix(t.getPlayer(), ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Prefix")));
											NametagEdit.getApi().setSuffix(t.getPlayer(), ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Suffix")));
										}
									}
									
									if(Utils.permissionsExStatus()) {
										PermissionUser user = PermissionsEx.getUser(p);
									
										if(FileUtils.cfg.getBoolean("SwitchPermissionsExGroupByNicking")) {
											String groupNames = "";

											for (PermissionGroup group : user.getGroups()) {
												groupNames += (" " + group.getName());
											}
											
											if(!(Utils.oldPermissionsExGroups.containsKey(t.getUniqueId()))) {
												Utils.oldPermissionsExGroups.put(t.getUniqueId(), groupNames.trim().split(" "));
											}
											
											user.setGroups(new String[] { groupName });
										} else {
											Utils.oldPermissionsExPrefixes.put(t.getUniqueId(), user.getPrefix());
											Utils.oldPermissionsExSuffixes.put(t.getUniqueId(), user.getSuffix());
											
											if(Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers()) {
												user.setPrefix(ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Prefix")), t.getWorld().getName());
												user.setSuffix(ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Suffix")), t.getWorld().getName());
											} else {
												user.setPrefix(ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Prefix")), t.getWorld().getName());
												user.setSuffix(ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Suffix")), t.getWorld().getName());
											}
										}
									}
									
									Utils.canUseNick.put(t.getUniqueId(), false);
									Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), new Runnable() {
										
										@Override
										public void run() {
											
											Utils.canUseNick.put(t.getUniqueId(), true);
										}
									}, FileUtils.cfg.getLong("Settings.NickDelay") * 20);
									
									if(FileUtils.cfg.getBoolean("BungeeCord") == true) {
										String oldPermissionsExRank = "";
										String prefix = "";
										String suffix = "";
										
										if(Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers()) {
											prefix = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Prefix"));
											suffix = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Suffix"));
										} else {
											prefix = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Prefix"));
											suffix = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Suffix"));
										}
										
										if(Utils.permissionsExStatus()) {
											if(Utils.oldPermissionsExGroups.containsKey(t.getUniqueId())) {
												oldPermissionsExRank = Utils.oldPermissionsExGroups.get(t.getUniqueId()).toString();
											}
										}
										
										MySQLPlayerDataManager.insertData(t.getUniqueId(), oldPermissionsExRank, prefix, suffix, prefix, suffix, prefix, suffix);
									}
									
									t.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Messages.Nick").replace("%name%", name)));
								}
							}
						} else {
							p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Messages.Other.Unnick")).replace("%playerName%", t.getName()));
							
							if(t.hasPermission("nick.use")) {
								t.chat("/unnick ");
							} else {
								if(Utils.luckPermsStatus()) {
									Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " permission unset prefix.99." + Utils.luckPermsPrefixes.get(p.getUniqueId()));
									Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " permission unset suffix.99." + Utils.luckPermsSuffixes.get(p.getUniqueId()));
								
									Utils.luckPermsPrefixes.remove(p.getUniqueId());
									Utils.luckPermsSuffixes.remove(p.getUniqueId());
								}
								
								api.unnickPlayer();
								
								if(Utils.cloudNetStatus())
									api.resetCloudNET();
								
								if(Utils.nameTagEditStatus()) {
									NametagEdit.getApi().reloadNametag(p);
								}
								
								if(Utils.permissionsExStatus()) {
									PermissionUser user = PermissionsEx.getUser(p);
								
									if(FileUtils.cfg.getBoolean("SwitchPermissionsExGroupByNicking")) {
										if(Utils.oldPermissionsExGroups.containsKey(t.getUniqueId())) {
											user.setGroups(Utils.oldPermissionsExGroups.get(t.getUniqueId()));
										}
									} else {
										user.setPrefix(Utils.oldPermissionsExPrefixes.get(t.getUniqueId()), t.getWorld().getName());
										user.setSuffix(Utils.oldPermissionsExSuffixes.get(t.getUniqueId()), t.getWorld().getName());
									}
								}
								
								if(FileUtils.cfg.getBoolean("Settings.NameChangeOptions.NameTagColored") == true) {
									if(Utils.scoreboardTeamManagers.containsKey(t.getUniqueId())) {
										ScoreboardTeamManager sbtm = Utils.scoreboardTeamManagers.get(t.getUniqueId());
										
										sbtm.removePlayerFromTeam();
										sbtm.destroyTeam();
										sbtm.createTeam();
										
										Utils.scoreboardTeamManagers.remove(t.getUniqueId());
									}
								}
								
								if(FileUtils.cfg.getBoolean("BungeeCord") == true) {
									MySQLPlayerDataManager.removeData(t.getUniqueId());
								}
								
								t.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Messages.Unnick")));
							}
						}
					} else {
						p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Messages.PlayerNotFound")));
					}	
				}
			} else {
				p.sendMessage(Utils.NO_PERM);
			}
		} else {
			Utils.sendConsole(Utils.NOT_PLAYER);
		}
		
		return true;
	}

}
