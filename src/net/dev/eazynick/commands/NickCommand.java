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
import net.dev.eazynick.api.NickManager;
import net.dev.eazynick.api.PlayerNickEvent;
import net.dev.eazynick.api.PlayerUnnickEvent;
import net.dev.eazynick.utils.FileUtils;
import net.dev.eazynick.utils.LanguageFileUtils;
import net.dev.eazynick.utils.StringUtils;
import net.dev.eazynick.utils.Utils;

public class NickCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		FileUtils fileUtils = eazyNick.getFileUtils();
		LanguageFileUtils languageFileUtils = eazyNick.getLanguageFileUtils();
		
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(p.hasPermission("nick.use")) {
				if((utils.getCanUseNick().get(p.getUniqueId()))) {
					if(utils.getNickedPlayers().contains(p.getUniqueId()))
						Bukkit.getPluginManager().callEvent(new PlayerUnnickEvent(p));
					else {
						if(args.length == 0) {
							if(fileUtils.cfg.getBoolean("OpenBookGUIOnNickCommand")) {
								if(!(p.hasPermission("nick.gui"))) {
									PermissionAttachment pa = p.addAttachment(eazyNick);
									pa.setPermission("nick.gui", true);
									p.recalculatePermissions();
									
									p.chat("/bookgui");
									
									p.removeAttachment(pa);
									p.recalculatePermissions();
								} else
									p.chat("/bookgui");
							} else if(fileUtils.cfg.getBoolean("OpenNicknameGUIInsteadOfRandomNick")) {
								if(!(p.hasPermission("nick.gui"))) {
									PermissionAttachment pa = p.addAttachment(eazyNick);
									pa.setPermission("nick.gui", true);
									p.recalculatePermissions();
									
									p.chat("/nicklist");
									
									p.removeAttachment(pa);
									p.recalculatePermissions();
								} else
									p.chat("/nicklist");
							} else {
								String name = utils.getNickNames().get((new Random().nextInt(utils.getNickNames().size())));
								boolean nickNameIsInUse = false;
								
								for (String nickName : utils.getPlayerNicknames().values()) {
									if(nickName.toUpperCase().equalsIgnoreCase(name.toUpperCase()))
										nickNameIsInUse = true;
								}
								
								while (nickNameIsInUse) {
									nickNameIsInUse = false;
									name = utils.getNickNames().get((new Random().nextInt(utils.getNickNames().size())));
									
									for (String nickName : utils.getPlayerNicknames().values()) {
										if(nickName.toUpperCase().equalsIgnoreCase(name.toUpperCase()))
											nickNameIsInUse = true;
									}
								}
	
								boolean serverFull = utils.getOnlinePlayerCount() >= Bukkit.getMaxPlayers();
								String prefix = serverFull ? fileUtils.getConfigString("Settings.NickFormat.ServerFullRank.NameTag.Prefix") : fileUtils.getConfigString("Settings.NickFormat.NameTag.Prefix");
								String suffix = serverFull ? fileUtils.getConfigString("Settings.NickFormat.ServerFullRank.NameTag.Suffix") : fileUtils.getConfigString("Settings.NickFormat.NameTag.Suffix");
								
								Bukkit.getPluginManager().callEvent(new PlayerNickEvent(p, name, name,
										serverFull ? fileUtils.getConfigString("Settings.NickFormat.ServerFullRank.Chat.Prefix") : fileUtils.getConfigString("Settings.NickFormat.Chat.Prefix"),
										serverFull ? fileUtils.getConfigString("Settings.NickFormat.ServerFullRank.Chat.Suffix") : fileUtils.getConfigString("Settings.NickFormat.Chat.Suffix"),
										serverFull ? fileUtils.getConfigString("Settings.NickFormat.ServerFullRank.PlayerList.Prefix") : fileUtils.getConfigString("Settings.NickFormat.PlayerList.Prefix"),
										serverFull ? fileUtils.getConfigString("Settings.NickFormat.ServerFullRank.PlayerList.Suffix") : fileUtils.getConfigString("Settings.NickFormat.PlayerList.Suffix"),
										prefix,
										suffix,
										false,
										false,
										(utils.getOnlinePlayerCount() >= Bukkit.getMaxPlayers()) ? fileUtils.getConfigString("Settings.NickFormat.ServerFullRank.GroupName") : fileUtils.getConfigString("Settings.NickFormat.GroupName")));
							}
						} else {
							if(p.hasPermission("nick.customnickname")) {
								String name = args[0].replace("\"", "");
								boolean isCancelled = false;
								
								if(new StringUtils(name).removeColorCodes().getString().length() <= 16) {
									if(!(utils.getBlackList().contains(args[0].toUpperCase()))) {
										boolean nickNameIsInUse = false;
										
										for (String nickName : utils.getPlayerNicknames().values()) {
											if(nickName.toUpperCase().equalsIgnoreCase(name.toUpperCase()))
												nickNameIsInUse = true;
										}

										if(!(nickNameIsInUse)) {
											boolean playerWithNameIsKnown = false;
											
											for (Player all : Bukkit.getOnlinePlayers()) {
												if(all.getName().toUpperCase().equalsIgnoreCase(name.toUpperCase()))
													playerWithNameIsKnown = true;
											}
												
											if(Bukkit.getOfflinePlayers() != null) {
												for (OfflinePlayer all : Bukkit.getOfflinePlayers()) {
													if((all != null) && (all.getName() != null) && all.getName().toUpperCase().equalsIgnoreCase(name.toUpperCase()))
														playerWithNameIsKnown = true;
												}
											}
											
											if(!(fileUtils.cfg.getBoolean("AllowPlayersToNickAsKnownPlayers")) && playerWithNameIsKnown)
												isCancelled = true;
											
											if(!(isCancelled)) {
												if(!(name.equalsIgnoreCase(p.getName()))) {
													name = ChatColor.translateAlternateColorCodes('&', eazyNick.getVersion().equals("1_7_R4") ? ((eazyNick.getUUIDFetcher_1_7().getUUID(name) != null) ? eazyNick.getUUIDFetcher_1_7().getName(eazyNick.getUUIDFetcher_1_7().getUUID(name)) : name) : (eazyNick.getVersion().equals("1_8_R1") ? ((eazyNick.getUUIDFetcher_1_8_R1().getUUID(name) != null) ? eazyNick.getUUIDFetcher_1_8_R1().getName(eazyNick.getUUIDFetcher_1_8_R1().getUUID(name)) : name) : ((eazyNick.getUUIDFetcher().getUUID(name) != null) ? eazyNick.getUUIDFetcher().getName(eazyNick.getUUIDFetcher().getUUID(name)) : name)));
													
													boolean serverFull = utils.getOnlinePlayerCount() >= Bukkit.getMaxPlayers();
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
														chatPrefix = (serverFull ? fileUtils.getConfigString("Settings.NickFormat.ServerFullRank.Chat.Prefix") : fileUtils.getConfigString("Settings.NickFormat.Chat.Prefix"));
														chatSuffix = (serverFull ? fileUtils.getConfigString("Settings.NickFormat.ServerFullRank.Chat.Suffix") : fileUtils.getConfigString("Settings.NickFormat.Chat.Suffix"));
														tabPrefix = (serverFull ? fileUtils.getConfigString("Settings.NickFormat.ServerFullRank.PlayerList.Prefix") : fileUtils.getConfigString("Settings.NickFormat.PlayerList.Prefix"));
														tabSuffix = (serverFull ? fileUtils.getConfigString("Settings.NickFormat.ServerFullRank.PlayerList.Suffix") : fileUtils.getConfigString("Settings.NickFormat.PlayerList.Suffix"));
														tagPrefix = (serverFull ? fileUtils.getConfigString("Settings.NickFormat.ServerFullRank.NameTag.Prefix") : fileUtils.getConfigString("Settings.NickFormat.NameTag.Prefix"));
														tagSuffix = (serverFull ? fileUtils.getConfigString("Settings.NickFormat.ServerFullRank.NameTag.Suffix") : fileUtils.getConfigString("Settings.NickFormat.NameTag.Suffix"));
													}

													new NickManager(p).setGroupName(serverFull ? fileUtils.getConfigString("Settings.NickFormat.ServerFullRank.GroupName") : fileUtils.getConfigString("Settings.NickFormat.GroupName"));
													
													Bukkit.getPluginManager().callEvent(new PlayerNickEvent(p, nameWhithoutColors, nameWhithoutColors, chatPrefix, chatSuffix, tabPrefix, tabSuffix, tagPrefix, tagSuffix, false, false, (utils.getOnlinePlayerCount() >= Bukkit.getMaxPlayers()) ? fileUtils.getConfigString("Settings.NickFormat.ServerFullRank.GroupName") : fileUtils.getConfigString("Settings.NickFormat.GroupName")));
												} else
													p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString("Messages.CanNotNickAsSelf"));
											} else
												p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString("Messages.PlayerWithThisNameIsKnown"));
										} else
											p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString("Messages.NickNameAlreadyInUse"));
									} else
										p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString("Messages.NameNotAllowed"));
								} else
									p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString("Messages.NickTooLong"));
							} else
								p.sendMessage(utils.getNoPerm());
						}
					}
				} else
					p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString("Messages.NickDelay"));
			} else
				p.sendMessage(utils.getNoPerm());
		} else
			utils.sendConsole(utils.getNotPlayer());
		
		return true;
	}
	
}
