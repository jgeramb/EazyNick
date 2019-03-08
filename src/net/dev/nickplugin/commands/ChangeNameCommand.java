package net.dev.nickplugin.commands;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dev.nickplugin.sql.MySQLNickManager;
import net.dev.nickplugin.sql.MySQLPlayerDataManager;
import net.dev.nickplugin.utils.FileUtils;
import net.dev.nickplugin.utils.NickManager;
import net.dev.nickplugin.utils.StringUtils;
import net.dev.nickplugin.utils.Utils;
import net.dev.nickplugin.utils.scoreboard.ScoreboardTeamManager;

public class ChangeNameCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(p.hasPermission("nick.name") || Utils.hasLuckPermsPermission(p.getUniqueId(), "nick.name")) {
				NickManager api = new NickManager(p);
				
				if((Utils.canUseNick.get(p.getUniqueId()) == true)) {
					if(args.length >= 1) {
						String name = args[0].replace("\"", "");
						boolean isCancelled;
						
						boolean nickNameIsInUse = false;
						
						for (String nickName : Utils.playerNicknames.values()) {
							if(nickName.toUpperCase().equalsIgnoreCase(name.toUpperCase())) {
								nickNameIsInUse = true;
							}
						}

						if(!(nickNameIsInUse)) {
							boolean playerWithNameIsOnline = false;
							
							for (Player all : Bukkit.getOnlinePlayers()) {
								if(all.getName().toUpperCase().equalsIgnoreCase(name.toUpperCase())) {
									playerWithNameIsOnline = true;
								}
							}
						
							if(FileUtils.cfg.getBoolean("AllowPlayersToNickAsOnlinePlayers") == false) {
								if(playerWithNameIsOnline) {
									isCancelled = true;
								} else {
									isCancelled = false;
								}
							} else {
								isCancelled = false;
							}
							
							if(!(isCancelled)) {
								if(new StringUtils(name).removeColorCodes().getString().length() <= 16) {
									if(!(Utils.blackList.contains(name.toUpperCase()))) {
										if(name.contains("&")) {
											name = ChatColor.translateAlternateColorCodes('&', name);
											
											String nameWhithoutColors = new StringUtils(name).removeColorCodes().getString();
											String[] prefixSuffix = name.split(nameWhithoutColors);
											String prefix = "";
											String suffix = "";
											
											if(prefixSuffix.length >= 1) {
												prefix = ChatColor.translateAlternateColorCodes('&', prefixSuffix[0]);
												
												if(prefixSuffix.length >= 2) {
													suffix = ChatColor.translateAlternateColorCodes('&', prefixSuffix[1]);
												}
											}
											
											if(prefix.length() > 16)
												prefix = prefix.substring(0, 16);
											
											if(suffix.length() > 16)
												suffix = suffix.substring(0, 16);
											
											if(Utils.luckPermsStatus()) {
												if(Utils.luckPermsPrefixes.containsKey(p.getUniqueId()) || Utils.luckPermsSuffixes.containsKey(p.getUniqueId())) {
													Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " permission unset prefix.99." + Utils.luckPermsPrefixes.get(p.getUniqueId()));
													Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " permission unset suffix.99." + Utils.luckPermsSuffixes.get(p.getUniqueId()));
												
													Utils.luckPermsPrefixes.remove(p.getUniqueId());
													Utils.luckPermsSuffixes.remove(p.getUniqueId());
												}
												
												Utils.luckPermsPrefixes.put(p.getUniqueId(), prefix);
												Utils.luckPermsSuffixes.put(p.getUniqueId(), suffix);
												
												Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " permission set prefix.99." + prefix);
												Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " permission set suffix.99." + suffix);
											}
											
											if(Utils.cloudNetStatus())
												api.changeCloudNET(prefix, suffix);
											
											if(FileUtils.cfg.getBoolean("Settings.NameChangeOptions.NameTagColored") == true) {
												if(!(Utils.scoreboardTeamManagers.containsKey(p.getUniqueId()))) {
													Utils.scoreboardTeamManagers.put(p.getUniqueId(), new ScoreboardTeamManager(p, prefix, suffix));
												} else {
													Utils.scoreboardTeamManagers.remove(p.getUniqueId());
													Utils.scoreboardTeamManagers.put(p.getUniqueId(), new ScoreboardTeamManager(p, prefix, suffix));
												}
												
												ScoreboardTeamManager sbtm = Utils.scoreboardTeamManagers.get(p.getUniqueId());
												
												sbtm.destroyTeam();
												sbtm.createTeam();
											}
											
											p.setDisplayName(prefix + nameWhithoutColors + suffix);
											api.setPlayerListName(prefix + nameWhithoutColors + suffix);
											
											api.setName(nameWhithoutColors);
											api.refreshPlayer();
											
											if(FileUtils.cfg.getBoolean("BungeeCord") == true) {
												String oldPermissionsExRank = "";
												
												if(Utils.permissionsExStatus()) {
													if(Utils.oldPermissionsExGroups.containsKey(p.getUniqueId())) {
														oldPermissionsExRank = Utils.oldPermissionsExGroups.get(p.getUniqueId()).toString();
													}
												}
												
												MySQLNickManager.addPlayer(p.getUniqueId(), nameWhithoutColors);
												MySQLPlayerDataManager.insertData(p.getUniqueId(), oldPermissionsExRank, prefix, suffix, prefix, suffix, prefix, suffix);
											}
										} else {
											api.setName(name);
											api.refreshPlayer();
										}
										
										p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Messages.NameChanged").replace("%nickName%", name)));
									} else {
										p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Messages.NameNotAllowed")));
									}
								} else {
									p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Messages.NickTooLong")));
								}
							} else {
								p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Messages.PlayerWithThisNameIsOnServer")));
							}
						} else {
							p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Messages.NickNameAlreadyInUse")));
						}
					} else {
						String name = Utils.nickNames.get((new Random().nextInt(Utils.nickNames.size())));
						
						boolean nickNameIsInUse = false;
						
						for (String nickName : Utils.playerNicknames.values()) {
							if(nickName.toUpperCase().equalsIgnoreCase(name.toUpperCase())) {
								nickNameIsInUse = true;
							}
						}

						while (nickNameIsInUse == true) {
							nickNameIsInUse = false;
							name = Utils.nickNames.get((new Random().nextInt(Utils.nickNames.size())));
							
							for (String nickName : Utils.playerNicknames.values()) {
								if(nickName.toUpperCase().equalsIgnoreCase(name.toUpperCase())) {
									nickNameIsInUse = true;
								}
							}
						}
						
						if(!(nickNameIsInUse)) {
							api.setName(name);
							api.refreshPlayer();
							
							p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Messages.NameChanged").replace("%nickName%", name)));
						}
					}
				} else {
					p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Messages.NickDelay")));
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
