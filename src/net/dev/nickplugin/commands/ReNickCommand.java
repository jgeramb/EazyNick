package net.dev.nickplugin.commands;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dev.nickplugin.api.PlayerNickEvent;
import net.dev.nickplugin.api.PlayerUnnickEvent;
import net.dev.nickplugin.utils.FileUtils;
import net.dev.nickplugin.utils.LanguageFileUtils;
import net.dev.nickplugin.utils.StringUtils;
import net.dev.nickplugin.utils.Utils;

public class ReNickCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(p.hasPermission("nick.use") || Utils.hasLuckPermsPermission(p.getUniqueId(), "nick.use")) {
				if((Utils.canUseNick.get(p.getUniqueId()))) {
					if(Utils.nickedPlayers.contains(p.getUniqueId())) {
						Bukkit.getPluginManager().callEvent(new PlayerUnnickEvent(p));
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
							
							Bukkit.getPluginManager().callEvent(new PlayerNickEvent(p, name, name,
									prefix,
									suffix,
									ChatColor.translateAlternateColorCodes('&', serverFull ? FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.Chat.Prefix") : FileUtils.cfg.getString("Settings.NickFormat.Chat.Prefix")),
									ChatColor.translateAlternateColorCodes('&', serverFull ? FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.Chat.Suffix") : FileUtils.cfg.getString("Settings.NickFormat.Chat.Suffix")),
									ChatColor.translateAlternateColorCodes('&', serverFull ? FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Prefix") : FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Prefix")),
									ChatColor.translateAlternateColorCodes('&', serverFull ? FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Suffix") : FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Suffix")),
									true));
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
													String chatPrefix, chatSuffix, tabPrefix, tabSuffix, tagPrefix, tagSuffix;
													
													if(prefixSuffix.length >= 1) {
														chatPrefix = ChatColor.translateAlternateColorCodes('&', prefixSuffix[0]);
														
														if(chatPrefix.length() > 16)
															chatPrefix = chatPrefix.substring(0, 16);
														
														if(prefixSuffix.length >= 2) {
															chatSuffix = ChatColor.translateAlternateColorCodes('&', prefixSuffix[1]);
															
															if(chatSuffix.length() > 16)
																chatSuffix = chatSuffix.substring(0, 16);
														} else
															chatSuffix = "§r";
														
														tabPrefix = chatPrefix;
														tabSuffix = chatSuffix;
														tagPrefix = chatPrefix;
														tagSuffix = chatSuffix;
													} else {
														chatPrefix = ChatColor.translateAlternateColorCodes('&', (serverFull ? FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.Chat.Prefix") : FileUtils.cfg.getString("Settings.NickFormat.Chat.Prefix")));
														chatSuffix = ChatColor.translateAlternateColorCodes('&', (serverFull ? FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.Chat.Suffix") : FileUtils.cfg.getString("Settings.NickFormat.Chat.Suffix")));
														tabPrefix = ChatColor.translateAlternateColorCodes('&', (serverFull ? FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Prefix") : FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Prefix")));
														tabSuffix = ChatColor.translateAlternateColorCodes('&', (serverFull ? FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.PlayerList.Suffix") : FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Suffix")));
														tagPrefix = ChatColor.translateAlternateColorCodes('&', (serverFull ? FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.NameTag.Prefix") : FileUtils.cfg.getString("Settings.NickFormat.NameTag.Prefix")));
														tagSuffix = ChatColor.translateAlternateColorCodes('&', (serverFull ? FileUtils.cfg.getString("Settings.NickFormat.ServerFullRank.NameTag.Suffix") : FileUtils.cfg.getString("Settings.NickFormat.NameTag.Suffix")));
													}
													
													Bukkit.getPluginManager().callEvent(new PlayerNickEvent(p, nameWhithoutColors, nameWhithoutColors, chatPrefix, chatSuffix, tabPrefix, tabSuffix, tagPrefix, tagSuffix, true));
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
