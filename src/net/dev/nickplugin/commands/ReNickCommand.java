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
import net.dev.nickplugin.utils.LanguageFileUtils;
import net.dev.nickplugin.utils.NickManager;
import net.dev.nickplugin.utils.StringUtils;
import net.dev.nickplugin.utils.Utils;
import net.dev.nickplugin.utils.scoreboard.ScoreboardTeamManager;

import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.storage.objects.User;

import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class ReNickCommand implements CommandExecutor {

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(p.hasPermission("nick.use") || Utils.hasLuckPermsPermission(p.getUniqueId(), "nick.use")) {
				NickManager api = new NickManager(p);
				
				if((Utils.canUseNick.get(p.getUniqueId()))) {
					if(Utils.nickedPlayers.contains(p.getUniqueId())) {
						api.unnickPlayer();
						
						if(Utils.cloudNetStatus())
							api.resetCloudNET();
						
						if(Utils.luckPermsStatus()) {
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " permission unset prefix.99." + Utils.luckPermsPrefixes.get(p.getUniqueId()));
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " permission unset suffix.99." + Utils.luckPermsSuffixes.get(p.getUniqueId()));
						
							Utils.luckPermsPrefixes.remove(p.getUniqueId());
							Utils.luckPermsSuffixes.remove(p.getUniqueId());
						}
						
						if(Utils.nameTagEditStatus()) {
							NametagEdit.getApi().reloadNametag(p);
						}
						
						if(Utils.permissionsExStatus()) {
							PermissionUser user = PermissionsEx.getUser(p);
						
							if(FileUtils.cfg.getBoolean("SwitchPermissionsExGroupByNicking")) {
								if(Utils.oldPermissionsExGroups.containsKey(p.getUniqueId())) {
									user.setGroups(Utils.oldPermissionsExGroups.get(p.getUniqueId()));
								}
							} else {
								user.setPrefix(Utils.oldPermissionsExPrefixes.get(p.getUniqueId()), p.getWorld().getName());
								user.setSuffix(Utils.oldPermissionsExSuffixes.get(p.getUniqueId()), p.getWorld().getName());
							}
						}
						
						if(FileUtils.cfg.getBoolean("Settings.NameChangeOptions.NameTagColored")) {
							if(Utils.scoreboardTeamManagers.containsKey(p.getUniqueId())) {
								ScoreboardTeamManager sbtm = Utils.scoreboardTeamManagers.get(p.getUniqueId());
								
								sbtm.removePlayerFromTeam();
								sbtm.destroyTeam();
								sbtm.createTeam();
								
								Utils.scoreboardTeamManagers.remove(p.getUniqueId());
							}
						}
						
						if(FileUtils.cfg.getBoolean("BungeeCord")) {
							MySQLPlayerDataManager.removeData(p.getUniqueId());
						}
						
						if(Utils.ultraPermissionsStatus()) {
							User user = UltraPermissions.getAPI().getUsers().uuid(p.getUniqueId());
							
							user.setPrefix(Utils.ultraPermsPrefixes.get(p.getUniqueId()));
							user.setSuffix(Utils.ultraPermsSuffixes.get(p.getUniqueId()));
							user.save();
							
							Utils.ultraPermsPrefixes.remove(p.getUniqueId());
							Utils.ultraPermsSuffixes.remove(p.getUniqueId());
						}
						
						p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("Messages.Unnick")));
					} else {
						if(args.length == 0) {
							String name = Utils.nickNames.get((new Random().nextInt(Utils.nickNames.size())));
							boolean nickNameIsInUse = false;
							
							for (String nickName : Utils.playerNicknames.values()) {
								if(nickName.toUpperCase().equalsIgnoreCase(name.toUpperCase())) {
									nickNameIsInUse = true;
								}
							}
							
							while (nickNameIsInUse ) {
								nickNameIsInUse = false;
								name = Utils.nickNames.get((new Random().nextInt(Utils.nickNames.size())));
								
								for (String nickName : Utils.playerNicknames.values()) {
									if(nickName.toUpperCase().equalsIgnoreCase(name.toUpperCase())) {
										nickNameIsInUse = true;
									}
								}
							}
							
							if(Utils.luckPermsStatus()) {
								String prefix = ((Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers()) ? FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Prefix") : FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Prefix"));
								String suffix = ((Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers()) ? FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Suffix") : FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Suffix"));
								
								Utils.luckPermsPrefixes.put(p.getUniqueId(), prefix);
								Utils.luckPermsSuffixes.put(p.getUniqueId(), suffix);
								
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " permission set prefix.99." + prefix);
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " permission set suffix.99." + suffix);
							}
							
							String oldDisplayName = p.getDisplayName();
							String oldPlayerListName = p.getPlayerListName();
							
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
							
							if(FileUtils.cfg.getBoolean("Settings.NameChangeOptions.NameTagColored")) {
								if(!(Utils.scoreboardTeamManagers.containsKey(p.getUniqueId()))) {
									String prefix = "";
									String suffix = "";
									
									if(Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers()) {
										prefix = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.NameTag.Prefix"));
										suffix = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.NameTag.Suffix"));
									} else {
										prefix = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.NameTag.Prefix"));
										suffix = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.NameTag.Suffix"));
									}
									
									Utils.scoreboardTeamManagers.put(p.getUniqueId(), new ScoreboardTeamManager(p, prefix, suffix));
									
									ScoreboardTeamManager sbtm = Utils.scoreboardTeamManagers.get(p.getUniqueId());
									
									sbtm.destroyTeam();
									sbtm.createTeam();
								}
							}
							
							String groupName = "";
							
							if(!(Utils.oldDisplayNames.containsKey(p.getUniqueId()) && Utils.oldPlayerListNames.containsKey(p.getUniqueId()))) {
								Utils.oldDisplayNames.put(p.getUniqueId(), oldDisplayName);
								Utils.oldPlayerListNames.put(p.getUniqueId(), oldPlayerListName);
								
								if(Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers()) {
									groupName = FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PermissionsEx.GroupName");
									
									if(FileUtils.cfg.getBoolean("Settings.NameChangeOptions.PlayerListNameColored")) {
										String nameFormatTab = ChatColor.translateAlternateColorCodes('&', (FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.Chat.Prefix") + name + FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.Chat.Suffix")));
										
										api.setPlayerListName(nameFormatTab);
									}
									
									if(FileUtils.cfg.getBoolean("Settings.NameChangeOptions.DisplayNameColored")) {
										String nameFormatChat = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Prefix") + name + FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Suffix"));
										
										p.setDisplayName(nameFormatChat);
									}
									
									if(Utils.nameTagEditStatus()) {
										NametagEdit.getApi().setPrefix(p.getPlayer(), ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Prefix")));
										NametagEdit.getApi().setSuffix(p.getPlayer(), ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Suffix")));
									}
								} else {
									groupName = FileUtils.cfg.getString("Settings.NickFormat.PermissionsEx.GroupName");
									
									if(FileUtils.cfg.getBoolean("Settings.NameChangeOptions.PlayerListNameColored")) {
										String nameFormatTab = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Prefix") + name + FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Suffix"));
											
										api.setPlayerListName(nameFormatTab);
									}
									
									if(FileUtils.cfg.getBoolean("Settings.NameChangeOptions.DisplayNameColored")) {
										String nameFormatChat = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.Chat.Prefix") + name + FileUtils.cfg.getString("Settings.NickFormat.Chat.Suffix"));
										
										p.setDisplayName(nameFormatChat);
									}
									
									if(Utils.nameTagEditStatus()) {
										NametagEdit.getApi().setPrefix(p.getPlayer(), ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Prefix")));
										NametagEdit.getApi().setSuffix(p.getPlayer(), ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Suffix")));
									}
								}
							}
								
							if(Utils.ultraPermissionsStatus()) {
								String prefix = ((Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers()) ? FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Prefix") : FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Prefix"));
								String suffix = ((Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers()) ? FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Suffix") : FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Suffix"));
								
								if(Utils.ultraPermsPrefixes.containsKey(p.getUniqueId()) || Utils.ultraPermsSuffixes.containsKey(p.getUniqueId())) {
									Utils.ultraPermsPrefixes.remove(p.getUniqueId());
									Utils.ultraPermsSuffixes.remove(p.getUniqueId());
								}
								
								User user = UltraPermissions.getAPI().getUsers().uuid(p.getUniqueId());
								
								Utils.ultraPermsPrefixes.put(p.getUniqueId(), user.getPrefix());
								Utils.ultraPermsSuffixes.put(p.getUniqueId(), user.getSuffix());
								
								user.setPrefix(prefix);
								user.setSuffix(suffix);
								user.save();
							}
							
							if(Utils.permissionsExStatus()) {
								PermissionUser user = PermissionsEx.getUser(p);
							
								if(FileUtils.cfg.getBoolean("SwitchPermissionsExGroupByNicking")) {
									String groupNames = "";

									for (PermissionGroup group : user.getGroups()) {
										groupNames += (" " + group.getName());
									}

									if(!(Utils.oldPermissionsExGroups.containsKey(p.getUniqueId()))) {
										Utils.oldPermissionsExGroups.put(p.getUniqueId(), groupNames.trim().split(" "));
									}
									
									user.setGroups(new String[] { groupName });
								} else {
									Utils.oldPermissionsExPrefixes.put(p.getUniqueId(), user.getPrefix());
									Utils.oldPermissionsExSuffixes.put(p.getUniqueId(), user.getSuffix());
									
									if(Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers()) {
										user.setPrefix(ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Prefix")), p.getWorld().getName());
										user.setSuffix(ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Suffix")), p.getWorld().getName());
									} else {
										user.setPrefix(ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Prefix")), p.getWorld().getName());
										user.setSuffix(ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Suffix")), p.getWorld().getName());
									}
								}
							}
							
							Utils.canUseNick.put(p.getUniqueId(), false);
							Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), new Runnable() {
								
								@Override
								public void run() {
									
									Utils.canUseNick.put(p.getUniqueId(), true);
								}
							}, FileUtils.cfg.getLong("Settings.NickDelay") * 20);
							
							if(FileUtils.cfg.getBoolean("BungeeCord")) {
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
									if(Utils.oldPermissionsExGroups.containsKey(p.getUniqueId())) {
										oldPermissionsExRank = Utils.oldPermissionsExGroups.get(p.getUniqueId()).toString();
									}
								}
								
								MySQLPlayerDataManager.insertData(p.getUniqueId(), oldPermissionsExRank, prefix, suffix, prefix, suffix, prefix, suffix);
							}
							
							p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("Messages.ActiveNick").replace("%name%", name)));
						} else {
							if(p.hasPermission("nick.customnickname") || Utils.hasLuckPermsPermission(p.getUniqueId(), "nick.customnickname")) {
								String name = args[0].replace("\"", "");
								
								if(new StringUtils(name).removeColorCodes().getString().length() <= 16) {
									if(!(Utils.blackList.contains(args[0].toUpperCase()))) {
										boolean nickNameIsInUse = false;
										boolean isCancelled;
										
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
													playerWithNameIsKnown = true;
												}
											}
											
											if(!(FileUtils.cfg.getBoolean("AllowPlayersToNickAsKnownPlayers")) && playerWithNameIsKnown) {
												isCancelled = true;
											} else {
												isCancelled = false;
											}
											
											if(!(isCancelled)) {
												if(!(name.equalsIgnoreCase(p.getName()))) {
													String oldDisplayName = p.getDisplayName();
													String oldPlayerListName = p.getPlayerListName();
													
													name = ChatColor.translateAlternateColorCodes('&', name);
													
													String nameWhithoutColors = ChatColor.stripColor(name);
													String[] prefixSuffix = name.split(nameWhithoutColors);
													String prefix = "";
													String suffix = "";
													
													if(prefixSuffix.length >= 1) {
														prefix = ChatColor.translateAlternateColorCodes('&', prefixSuffix[0]);
														
														if(prefixSuffix.length >= 2) {
															suffix = ChatColor.translateAlternateColorCodes('&', prefixSuffix[1]);
														}
													} else {
														if(Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers()) {
															prefix = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.NameTag.Prefix"));
															suffix = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.NameTag.Suffix"));
														} else {
															prefix = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.NameTag.Prefix"));
															suffix = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.NameTag.Suffix"));
														}
													}
													
													if(prefix.length() > 16)
														prefix = prefix.substring(0, 16);
													
													if(suffix.length() > 16)
														suffix = suffix.substring(0, 16);
													
													if(Utils.luckPermsStatus()) {
														Utils.luckPermsPrefixes.put(p.getUniqueId(), prefix);
														Utils.luckPermsSuffixes.put(p.getUniqueId(), suffix);
														
														Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " permission set prefix.99." + prefix);
														Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " permission set suffix.99." + suffix);
													}
													
													api.nickPlayer(name);
													
													if(Utils.cloudNetStatus())
														api.changeCloudNET(prefix, suffix);
													
													if(FileUtils.cfg.getBoolean("Settings.NameChangeOptions.NameTagColored")) {
														if(!(Utils.scoreboardTeamManagers.containsKey(p.getUniqueId()))) {
															Utils.scoreboardTeamManagers.put(p.getUniqueId(), new ScoreboardTeamManager(p, prefix, suffix));
															
															ScoreboardTeamManager sbtm = Utils.scoreboardTeamManagers.get(p.getUniqueId());
															
															sbtm.destroyTeam();
															sbtm.createTeam();
														}
													}
													
													String groupName = "";
													
													if(!(Utils.oldDisplayNames.containsKey(p.getUniqueId()) && Utils.oldPlayerListNames.containsKey(p.getUniqueId()))) {
														Utils.oldDisplayNames.put(p.getUniqueId(), oldDisplayName);
														Utils.oldPlayerListNames.put(p.getUniqueId(), oldPlayerListName);												
														
														if(Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers()) {
															groupName = FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PermissionsEx.GroupName");
															
															if(FileUtils.cfg.getBoolean("Settings.NameChangeOptions.PlayerListNameColored")) {
																String nameFormatTab = ChatColor.translateAlternateColorCodes('&', (FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.Chat.Prefix") + name + FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.Chat.Suffix")));
																	
																api.setPlayerListName(nameFormatTab);
															}
															
															if(FileUtils.cfg.getBoolean("Settings.NameChangeOptions.DisplayNameColored")) {
																String nameFormatChat = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Prefix") + name + FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Suffix"));
																
																p.setDisplayName(nameFormatChat);
															}
															
															if(Utils.nameTagEditStatus()) {
																NametagEdit.getApi().setPrefix(p.getPlayer(), ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Prefix")));
																NametagEdit.getApi().setSuffix(p.getPlayer(), ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Suffix")));
															}
														} else {
															groupName = FileUtils.cfg.getString("Settings.NickFormat.PermissionsEx.GroupName");
															
															if(FileUtils.cfg.getBoolean("Settings.NameChangeOptions.PlayerListNameColored")) {
																String nameFormatTab = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Prefix") + name + FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Suffix"));
																	
																api.setPlayerListName(nameFormatTab);
															}
															
															if(FileUtils.cfg.getBoolean("Settings.NameChangeOptions.DisplayNameColored")) {
																String nameFormatChat = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.Chat.Prefix") + name + FileUtils.cfg.getString("Settings.NickFormat.Chat.Suffix"));
																
																p.setDisplayName(nameFormatChat);
															}
															
															if(Utils.nameTagEditStatus()) {
																NametagEdit.getApi().setPrefix(p.getPlayer(), ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Prefix")));
																NametagEdit.getApi().setSuffix(p.getPlayer(), ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Suffix")));
															}
														}
													}
													
													if(Utils.ultraPermissionsStatus()) {
														if(Utils.ultraPermsPrefixes.containsKey(p.getUniqueId()) || Utils.ultraPermsSuffixes.containsKey(p.getUniqueId())) {
															Utils.ultraPermsPrefixes.remove(p.getUniqueId());
															Utils.ultraPermsSuffixes.remove(p.getUniqueId());
														}
														
														User user = UltraPermissions.getAPI().getUsers().uuid(p.getUniqueId());
														
														Utils.ultraPermsPrefixes.put(p.getUniqueId(), user.getPrefix());
														Utils.ultraPermsSuffixes.put(p.getUniqueId(), user.getSuffix());
														
														user.setPrefix(prefix);
														user.setSuffix(suffix);
														user.save();
													}
													
													if(Utils.permissionsExStatus()) {
														PermissionUser user = PermissionsEx.getUser(p);
													
														if(FileUtils.cfg.getBoolean("SwitchPermissionsExGroupByNicking")) {
															String groupNames = "";
	
															for (PermissionGroup group : user.getGroups()) {
																groupNames += (" " + group.getName());
															}
	
															if(!(Utils.oldPermissionsExGroups.containsKey(p.getUniqueId()))) {
																Utils.oldPermissionsExGroups.put(p.getUniqueId(), groupNames.trim().split(" "));
															}
															
															user.setGroups(new String[] { groupName });
														} else {
															Utils.oldPermissionsExPrefixes.put(p.getUniqueId(), user.getPrefix());
															Utils.oldPermissionsExSuffixes.put(p.getUniqueId(), user.getSuffix());
															
															if(Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers()) {
																user.setPrefix(ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Prefix")), p.getWorld().getName());
																user.setSuffix(ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Suffix")), p.getWorld().getName());
															} else {
																user.setPrefix(ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Prefix")), p.getWorld().getName());
																user.setSuffix(ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Suffix")), p.getWorld().getName());
															}
														}
													}
													
													Utils.canUseNick.put(p.getUniqueId(), false);
													Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), new Runnable() {
														
														@Override
														public void run() {
															
															Utils.canUseNick.put(p.getUniqueId(), true);
														}
													}, FileUtils.cfg.getLong("Settings.NickDelay") * 20);
													
													if(FileUtils.cfg.getBoolean("BungeeCord")) {
														String oldPermissionsExRank = "";
														
														if(Utils.permissionsExStatus()) {
															if(Utils.oldPermissionsExGroups.containsKey(p.getUniqueId())) {
																oldPermissionsExRank = Utils.oldPermissionsExGroups.get(p.getUniqueId()).toString();
															}
														}
														
														MySQLPlayerDataManager.insertData(p.getUniqueId(), oldPermissionsExRank, prefix, suffix, prefix, suffix, prefix, suffix);
													}
													
													p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("Messages.ActiveNick").replace("%name%", name)));
												} else {
													p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("Messages.CanNotNickAsSelf")));
												}
											} else {
												p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("Messages.PlayerWithThisNameIsKnown")));
											}
										} else {
											p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("Messages.NickNameAlreadyInUse")));
										}
									} else {
										p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("Messages.NameNotAllowed")));
									}
								} else {
									p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("Messages.NickTooLong")));
								}
							} else {
								p.sendMessage(Utils.NO_PERM);
							}
						}
					}
				} else {
					p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("Messages.NickDelay")));
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
