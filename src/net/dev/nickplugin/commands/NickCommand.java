package net.dev.nickplugin.commands;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dev.nickplugin.main.Main;
import net.dev.nickplugin.sql.MySQLPlayerDataManager;
import net.dev.nickplugin.utils.FileUtils;
import net.dev.nickplugin.utils.LanguageFileUtils;
import net.dev.nickplugin.utils.NickManager;
import net.dev.nickplugin.utils.StringUtils;
import net.dev.nickplugin.utils.Utils;

public class NickCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(p.hasPermission("nick.use") || Utils.hasLuckPermsPermission(p.getUniqueId(), "nick.use")) {
				NickManager api = new NickManager(p);
				
				if((Utils.canUseNick.get(p.getUniqueId()))) {
					if(Utils.nickedPlayers.contains(p.getUniqueId())) {
						api.unnickPlayer();
						
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

							boolean serverFull = Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers();
							String prefix = ChatColor.translateAlternateColorCodes('&', (serverFull ? FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.NameTag.Prefix") : FileUtils.cfg.getString("Settings.NickFormat.NameTag.Prefix")));
							String suffix = ChatColor.translateAlternateColorCodes('&', (serverFull ? FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.NameTag.Suffix") : FileUtils.cfg.getString("Settings.NickFormat.NameTag.Suffix")));
							
							api.updateLuckPerms(prefix, suffix);
							api.nickPlayer(name);
							api.updatePrefixSuffix(prefix, suffix,
									ChatColor.translateAlternateColorCodes('&', serverFull ? FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.Chat.Prefix") : FileUtils.cfg.getString("Settings.NickFormat.Chat.Prefix")),
									ChatColor.translateAlternateColorCodes('&', serverFull ? FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.Chat.Suffix") : FileUtils.cfg.getString("Settings.NickFormat.Chat.Suffix")),
									ChatColor.translateAlternateColorCodes('&', serverFull ? FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Prefix") : FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Prefix")),
									ChatColor.translateAlternateColorCodes('&', serverFull ? FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Suffix") : FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Suffix")));
							
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
							
							p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("Messages.Nick").replace("%name%", name)));
						} else {
							if(p.hasPermission("nick.customnickname") || Utils.hasLuckPermsPermission(p.getUniqueId(), "nick.customnickname")) {
								String name = args[0].replace("\"", "");
								boolean isCancelled;
								
								if(new StringUtils(name).removeColorCodes().getString().length() <= 16) {
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
													Utils.oldDisplayNames.put(p.getUniqueId(), p.getDisplayName());
													Utils.oldPlayerListNames.put(p.getUniqueId(), p.getPlayerListName());
													
													name = ChatColor.translateAlternateColorCodes('&', name);
													
													boolean serverFull = Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers();
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
														prefix = ChatColor.translateAlternateColorCodes('&', (serverFull ? FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.NameTag.Prefix") : FileUtils.cfg.getString("Settings.NickFormat.NameTag.Prefix")));
														suffix = ChatColor.translateAlternateColorCodes('&', (serverFull ? FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.NameTag.Suffix") : FileUtils.cfg.getString("Settings.NickFormat.NameTag.Suffix")));
													}
													
													if(prefix.length() > 16)
														prefix = prefix.substring(0, 16);
													
													if(suffix.length() > 16)
														suffix = suffix.substring(0, 16);
													
													api.updateLuckPerms(prefix, suffix);
													api.nickPlayer(name);
													api.updatePrefixSuffix(prefix, suffix, prefix, suffix, prefix, suffix);
													
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
													
													p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("Messages.Nick").replace("%name%", name)));
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
