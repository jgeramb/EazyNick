package net.dev.nickplugin.commands;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nametagedit.plugin.NametagEdit;

import net.dev.nickplugin.sql.MySQLPlayerDataManager;
import net.dev.nickplugin.utils.BookGUIFileUtils;
import net.dev.nickplugin.utils.FileUtils;
import net.dev.nickplugin.utils.NickManager;
import net.dev.nickplugin.utils.StringUtils;
import net.dev.nickplugin.utils.Utils;
import net.dev.nickplugin.utils.scoreboard.ScoreboardTeamManager;

import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class BookNickCommand implements CommandExecutor {

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(p.hasPermission("nick.use") || Utils.hasLuckPermsPermission(p.getUniqueId(), "nick.use")) {
				NickManager api = new NickManager(p);
				
				if(args.length >= 3) {
					String prefix = "";
					String suffix = "";
					String tagPrefix = "";
					String tagSuffix = "";
					String name = args[2];
					String skinName = "";
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
						
						if(((FileUtils.cfg.getBoolean("AllowPlayersToNickAsOnlinePlayers")) == false) && playerWithNameIsOnline) {
							isCancelled = true;
						} else {
							isCancelled = false;
						}
						
						if(!(isCancelled)) {
							if(new StringUtils(name).removeColorCodes().getString().length() <= 16) {
								if(!(Utils.blackList.contains(name.toUpperCase()))) {
									String groupName = "";
									
									if(args[0].equalsIgnoreCase(BookGUIFileUtils.cfg.getString("BookGUI.Rank1.RankName")) && (BookGUIFileUtils.cfg.getBoolean("BookGUI.Rank1.Enabled") == true)) {
										prefix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank1.Prefix"));
										suffix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank1.Suffix"));
										tagPrefix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank1.TagPrefix"));
										tagSuffix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank1.TagSuffix"));
										groupName = BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank1.PermissionsEx.GroupName");
									} else if(args[0].equalsIgnoreCase(BookGUIFileUtils.cfg.getString("BookGUI.Rank2.RankName")) && (BookGUIFileUtils.cfg.getBoolean("BookGUI.Rank2.Enabled") == true)) {
										prefix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank2.Prefix"));
										suffix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank2.Suffix"));
										tagPrefix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank2.TagPrefix"));
										tagSuffix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank2.TagSuffix"));
										groupName = BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank2.PermissionsEx.GroupName");
									} else if(args[0].equalsIgnoreCase(BookGUIFileUtils.cfg.getString("BookGUI.Rank3.RankName")) && (BookGUIFileUtils.cfg.getBoolean("BookGUI.Rank3.Enabled") == true)) {
										prefix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank3.Prefix"));
										suffix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank3.Suffix"));
										tagPrefix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank3.TagPrefix"));
										tagSuffix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank3.TagSuffix"));
										groupName = BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank3.PermissionsEx.GroupName");
									} else if(args[0].equalsIgnoreCase(BookGUIFileUtils.cfg.getString("BookGUI.Rank4.RankName")) && (BookGUIFileUtils.cfg.getBoolean("BookGUI.Rank4.Enabled") == true)) {
										prefix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank4.Prefix"));
										suffix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank4.Suffix"));
										tagPrefix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank4.TagPrefix"));
										tagSuffix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank4.TagSuffix"));
										groupName = BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank4.PermissionsEx.GroupName");
									} else if(args[0].equalsIgnoreCase(BookGUIFileUtils.cfg.getString("BookGUI.Rank5.RankName")) && (BookGUIFileUtils.cfg.getBoolean("BookGUI.Rank5.Enabled") == true)) {
										prefix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank5.Prefix"));
										suffix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank5.Suffix"));
										tagPrefix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank5.TagPrefix"));
										tagSuffix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank5.TagSuffix"));
										groupName = BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank5.PermissionsEx.GroupName");
									} else if(args[0].equalsIgnoreCase(BookGUIFileUtils.cfg.getString("BookGUI.Rank6.RankName")) && (BookGUIFileUtils.cfg.getBoolean("BookGUI.Rank6.Enabled") == true)) {
										prefix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank6.Prefix"));
										suffix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank6.Suffix"));
										tagPrefix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank6.TagPrefix"));
										tagSuffix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank6.TagSuffix"));
										groupName = BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank5.PermissionsEx.GroupName");
									} else if(args[0].equalsIgnoreCase(BookGUIFileUtils.cfg.getString("BookGUI.Rank7.RankName")) && (BookGUIFileUtils.cfg.getBoolean("BookGUI.Rank7.Enabled") == true)) {
										prefix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank7.Prefix"));
										suffix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank7.Suffix"));
										tagPrefix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank7.TagPrefix"));
										tagSuffix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank7.TagSuffix"));
										groupName = BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank5.PermissionsEx.GroupName");
									} else if(args[0].equalsIgnoreCase(BookGUIFileUtils.cfg.getString("BookGUI.Rank8.RankName")) && (BookGUIFileUtils.cfg.getBoolean("BookGUI.Rank8.Enabled") == true)) {
										prefix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank8.Prefix"));
										suffix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank8.Suffix"));
										tagPrefix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank8.TagPrefix"));
										tagSuffix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank8.TagSuffix"));
										groupName = BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank5.PermissionsEx.GroupName");
									}
									
									if(args[1].equalsIgnoreCase("DEFAULT")) {
										skinName = api.getRealName();
									} else if(args[1].equalsIgnoreCase("NORMAL")) {
										skinName = ((new Random().nextInt(2)) == 1) ? "Steve" : "Alex";
									} else if(args[1].equalsIgnoreCase("RANDOM")) {
										skinName = Utils.nickNames.get((new Random().nextInt(Utils.nickNames.size())));
									}
									
									if(Utils.luckPermsStatus()) {
										if(Utils.luckPermsPrefixes.containsKey(p.getUniqueId()) || Utils.luckPermsSufixes.containsKey(p.getUniqueId())) {
											Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " permission unset prefix.99." + Utils.luckPermsPrefixes.get(p.getUniqueId()));
											Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " permission unset suffix.99." + Utils.luckPermsSufixes.get(p.getUniqueId()));
										
											Utils.luckPermsPrefixes.remove(p.getUniqueId());
											Utils.luckPermsSufixes.remove(p.getUniqueId());
										}
										
										Utils.luckPermsPrefixes.put(p.getUniqueId(), prefix);
										Utils.luckPermsSufixes.put(p.getUniqueId(), suffix);
										
										Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " permission set prefix.99." + prefix);
										Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " permission set suffix.99." + suffix);
									}
									
									api.nickPlayer(name, skinName);
									
									if(FileUtils.cfg.getBoolean("Settings.NameChangeOptions.NameTagColored") == true) {
										if(!(Utils.scoreboardTeamManagers.containsKey(p.getUniqueId()))) {
											Utils.scoreboardTeamManagers.put(p.getUniqueId(), new ScoreboardTeamManager(p, tagPrefix, tagSuffix));
										} else {
											Utils.scoreboardTeamManagers.remove(p.getUniqueId());
											Utils.scoreboardTeamManagers.put(p.getUniqueId(), new ScoreboardTeamManager(p, tagPrefix, tagSuffix));
										}
										
										ScoreboardTeamManager sbtm = Utils.scoreboardTeamManagers.get(p.getUniqueId());
										
										sbtm.destroyTeam();
										sbtm.createTeam();
									}
									
									if(FileUtils.cfg.getBoolean("Settings.NameChangeOptions.DisplayNameColored") == true) {
										p.setDisplayName(prefix + name + suffix);
									}
									
									if(FileUtils.cfg.getBoolean("Settings.NameChangeOptions.PlayerListNameColored") == true) {
										p.setPlayerListName(prefix + name + suffix);
									}
									
									if(Utils.nameTagEditStatus()) {
										NametagEdit.getApi().setPrefix(p, prefix);
										NametagEdit.getApi().setSuffix(p, suffix);
									}
									
									if(Utils.permissionsExStatus()) {
										PermissionUser user = PermissionsEx.getUser(p);
									
										if(FileUtils.cfg.getBoolean("SwitchPermissionsExGroupByNicking")) {
											String groupNames = "";
		
											for (PermissionGroup group : user.getGroups()) {
												groupNames += (" " + group.getName());
											}
		
											Utils.oldPermissionsExGroups.put(p.getUniqueId(), groupNames.trim().split(" "));
		
											user.setGroups(new String[] { groupName });
										} else {
											Utils.oldPermissionsExPrefixes.put(p.getUniqueId(), user.getPrefix());
											Utils.oldPermissionsExSuffixes.put(p.getUniqueId(), user.getSuffix());
											
											user.setPrefix(ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Prefix")), p.getWorld().getName());
											user.setSuffix(ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Settings.NickFormat.PlayerList.Suffix")), p.getWorld().getName());
										}
									}
									
									if(Utils.cloudNetStatus())
										api.changeCloudNET(prefix, suffix);
									
									if(FileUtils.cfg.getBoolean("BungeeCord") == true) {
										String oldPermissionsExRank = "";
										
										if(Utils.permissionsExStatus()) {
											if(FileUtils.cfg.getBoolean("SwitchPermissionsExGroupByNicking") == true) {
												if(Utils.oldPermissionsExGroups.containsKey(p.getUniqueId())) {
													oldPermissionsExRank = Utils.oldPermissionsExGroups.get(p.getUniqueId()).toString();
												}
											}
										}
										
										MySQLPlayerDataManager.insertData(p.getUniqueId(), oldPermissionsExRank, prefix, suffix, prefix, suffix, tagPrefix, tagSuffix);
									}
									
									p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Messages.Nick").replace("%name%", name)));
								} else {
									p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Messages.NickTooLong")));
								}
							} else {
								p.sendMessage(Utils.NO_PERM);
							}
						} else {
							p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Messages.PlayerWithThisNameIsOnServer")));
						}
					} else {
						p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Messages.NickNameAlreadyInUse")));
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