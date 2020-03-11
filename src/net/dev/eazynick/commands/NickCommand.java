package net.dev.eazynick.commands;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.PlayerNickEvent;
import net.dev.eazynick.api.PlayerUnnickEvent;
import net.dev.eazynick.utils.FileUtils;
import net.dev.eazynick.utils.LanguageFileUtils;
import net.dev.eazynick.utils.StringUtils;
import net.dev.eazynick.utils.Utils;

public class NickCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(p.hasPermission("nick.use")) {
				if((Utils.canUseNick.get(p.getUniqueId()))) {
					if(Utils.nickedPlayers.contains(p.getUniqueId()))
						Bukkit.getPluginManager().callEvent(new PlayerUnnickEvent(p));
					else {
						if(args.length == 0) {
							if(FileUtils.cfg.getBoolean("OpenBookGUIOnNickCommand")) {
								if(!(p.hasPermission("nick.gui"))) {
									PermissionAttachment pa = p.addAttachment(EazyNick.getInstance());
									pa.setPermission("nick.gui", true);
									p.recalculatePermissions();
									
									p.chat("/bookgui");
									
									p.removeAttachment(pa);
									p.recalculatePermissions();
								} else
									p.chat("/bookgui");
							} else if(FileUtils.cfg.getBoolean("OpenNicknameGUIInsteadOfRandomNick")) {
								if(!(p.hasPermission("nick.gui"))) {
									PermissionAttachment pa = p.addAttachment(EazyNick.getInstance());
									pa.setPermission("nick.gui", true);
									p.recalculatePermissions();
									
									p.chat("/nicklist");
									
									p.removeAttachment(pa);
									p.recalculatePermissions();
								} else
									p.chat("/nicklist");
							} else {
								String name = Utils.nickNames.get((new Random().nextInt(Utils.nickNames.size())));
								boolean nickNameIsInUse = false;
								
								for (String nickName : Utils.playerNicknames.values()) {
									if(nickName.toUpperCase().equalsIgnoreCase(name.toUpperCase()))
										nickNameIsInUse = true;
								}
								
								while (nickNameIsInUse) {
									nickNameIsInUse = false;
									name = Utils.nickNames.get((new Random().nextInt(Utils.nickNames.size())));
									
									for (String nickName : Utils.playerNicknames.values()) {
										if(nickName.toUpperCase().equalsIgnoreCase(name.toUpperCase()))
											nickNameIsInUse = true;
									}
								}
	
								boolean serverFull = Utils.getOnlinePlayers() >= Bukkit.getMaxPlayers();
								String prefix = serverFull ? FileUtils.getConfigString("Settings.NickFormat.ServerFullRank.NameTag.Prefix") : FileUtils.getConfigString("Settings.NickFormat.NameTag.Prefix");
								String suffix = serverFull ? FileUtils.getConfigString("Settings.NickFormat.ServerFullRank.NameTag.Suffix") : FileUtils.getConfigString("Settings.NickFormat.NameTag.Suffix");
								
								Bukkit.getPluginManager().callEvent(new PlayerNickEvent(p, name, name,
										serverFull ? FileUtils.getConfigString("Settings.NickFormat.ServerFullRank.Chat.Prefix") : FileUtils.getConfigString("Settings.NickFormat.Chat.Prefix"),
										serverFull ? FileUtils.getConfigString("Settings.NickFormat.ServerFullRank.Chat.Suffix") : FileUtils.getConfigString("Settings.NickFormat.Chat.Suffix"),
										serverFull ? FileUtils.getConfigString("Settings.NickFormat.ServerFullRank.PlayerList.Prefix") : FileUtils.getConfigString("Settings.NickFormat.PlayerList.Prefix"),
										serverFull ? FileUtils.getConfigString("Settings.NickFormat.ServerFullRank.PlayerList.Suffix") : FileUtils.getConfigString("Settings.NickFormat.PlayerList.Suffix"),
										prefix,
										suffix,
										false,
										false,
										(Utils.getOnlinePlayers() >= Bukkit.getMaxPlayers()) ? FileUtils.getConfigString("Settings.NickFormat.ServerFullRank.PermissionsEx.GroupName") : FileUtils.getConfigString("Settings.NickFormat.PermissionsEx.GroupName")));
							}
						} else {
							if(p.hasPermission("nick.customnickname")) {
								String name = args[0].replace("\"", "");
								boolean isCancelled = false;
								
								if(new StringUtils(name).removeColorCodes().getString().length() <= 16) {
									if(!(Utils.blackList.contains(args[0].toUpperCase()))) {
										boolean nickNameIsInUse = false;
										
										for (String nickName : Utils.playerNicknames.values()) {
											if(nickName.toUpperCase().equalsIgnoreCase(name.toUpperCase()))
												nickNameIsInUse = true;
										}

										if(!(nickNameIsInUse)) {
											boolean playerWithNameIsKnown = false;
											
											for (Player all : Bukkit.getOnlinePlayers()) {
												if(all.getName().toUpperCase().equalsIgnoreCase(name.toUpperCase()))
													playerWithNameIsKnown = true;
											}
												
											for (OfflinePlayer all : Bukkit.getOfflinePlayers()) {
												if((all != null) && (all.getName() != null) && all.getName().toUpperCase().equalsIgnoreCase(name.toUpperCase()))
													playerWithNameIsKnown = true;
											}
											
											if(!(FileUtils.cfg.getBoolean("AllowPlayersToNickAsKnownPlayers")) && playerWithNameIsKnown)
												isCancelled = true;
											
											if(!(isCancelled)) {
												if(!(name.equalsIgnoreCase(p.getName()))) {
													name = ChatColor.translateAlternateColorCodes('&', name);
													
													boolean serverFull = Utils.getOnlinePlayers() >= Bukkit.getMaxPlayers();
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
														chatPrefix = (serverFull ? FileUtils.getConfigString("Settings.NickFormat.ServerFullRank.Chat.Prefix") : FileUtils.getConfigString("Settings.NickFormat.Chat.Prefix"));
														chatSuffix = (serverFull ? FileUtils.getConfigString("Settings.NickFormat.ServerFullRank.Chat.Suffix") : FileUtils.getConfigString("Settings.NickFormat.Chat.Suffix"));
														tabPrefix = (serverFull ? FileUtils.getConfigString("Settings.NickFormat.ServerFullRank.PlayerList.Prefix") : FileUtils.getConfigString("Settings.NickFormat.PlayerList.Prefix"));
														tabSuffix = (serverFull ? FileUtils.getConfigString("Settings.NickFormat.ServerFullRank.PlayerList.Suffix") : FileUtils.getConfigString("Settings.NickFormat.PlayerList.Suffix"));
														tagPrefix = (serverFull ? FileUtils.getConfigString("Settings.NickFormat.ServerFullRank.NameTag.Prefix") : FileUtils.getConfigString("Settings.NickFormat.NameTag.Prefix"));
														tagSuffix = (serverFull ? FileUtils.getConfigString("Settings.NickFormat.ServerFullRank.NameTag.Suffix") : FileUtils.getConfigString("Settings.NickFormat.NameTag.Suffix"));
													}
													
													Bukkit.getPluginManager().callEvent(new PlayerNickEvent(p, nameWhithoutColors, nameWhithoutColors, chatPrefix, chatSuffix, tabPrefix, tabSuffix, tagPrefix, tagSuffix, false, false, (Utils.getOnlinePlayers() >= Bukkit.getMaxPlayers()) ? FileUtils.getConfigString("Settings.NickFormat.ServerFullRank.PermissionsEx.GroupName") : FileUtils.getConfigString("Settings.NickFormat.PermissionsEx.GroupName")));
												} else
													p.sendMessage(Utils.prefix + LanguageFileUtils.getConfigString("Messages.CanNotNickAsSelf"));
											} else
												p.sendMessage(Utils.prefix + LanguageFileUtils.getConfigString("Messages.PlayerWithThisNameIsKnown"));
										} else
											p.sendMessage(Utils.prefix + LanguageFileUtils.getConfigString("Messages.NickNameAlreadyInUse"));
									} else
										p.sendMessage(Utils.prefix + LanguageFileUtils.getConfigString("Messages.NameNotAllowed"));
								} else
									p.sendMessage(Utils.prefix + LanguageFileUtils.getConfigString("Messages.NickTooLong"));
							} else
								p.sendMessage(Utils.noPerm);
						}
					}
				} else
					p.sendMessage(Utils.prefix + LanguageFileUtils.getConfigString("Messages.NickDelay"));
			} else
				p.sendMessage(Utils.noPerm);
		} else
			Utils.sendConsole(Utils.notPlayer);
		
		return true;
	}
	
}
