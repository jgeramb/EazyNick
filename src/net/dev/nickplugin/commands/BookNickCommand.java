package net.dev.nickplugin.commands;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dev.nickplugin.api.NickManager;
import net.dev.nickplugin.api.PlayerNickEvent;
import net.dev.nickplugin.utils.BookGUIFileUtils;
import net.dev.nickplugin.utils.FileUtils;
import net.dev.nickplugin.utils.LanguageFileUtils;
import net.dev.nickplugin.utils.StringUtils;
import net.dev.nickplugin.utils.Utils;

public class BookNickCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(p.hasPermission("nick.use") || Utils.hasLuckPermsPermission(p.getUniqueId(), "nick.use")) {
				NickManager api = new NickManager(p);
				
				if(!(api.isNicked())) {
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
										
										if(args[0].equalsIgnoreCase(BookGUIFileUtils.cfg.getString("BookGUI.Rank1.RankName")) && (BookGUIFileUtils.cfg.getBoolean("BookGUI.Rank1.Enabled"))) {
											prefix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank1.Prefix"));
											suffix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank1.Suffix"));
											tagPrefix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank1.TagPrefix"));
											tagSuffix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank1.TagSuffix"));
											groupName = BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank1.PermissionsEx.GroupName");
										} else if(args[0].equalsIgnoreCase(BookGUIFileUtils.cfg.getString("BookGUI.Rank2.RankName")) && (BookGUIFileUtils.cfg.getBoolean("BookGUI.Rank2.Enabled"))) {
											prefix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank2.Prefix"));
											suffix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank2.Suffix"));
											tagPrefix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank2.TagPrefix"));
											tagSuffix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank2.TagSuffix"));
											groupName = BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank2.PermissionsEx.GroupName");
										} else if(args[0].equalsIgnoreCase(BookGUIFileUtils.cfg.getString("BookGUI.Rank3.RankName")) && (BookGUIFileUtils.cfg.getBoolean("BookGUI.Rank3.Enabled"))) {
											prefix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank3.Prefix"));
											suffix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank3.Suffix"));
											tagPrefix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank3.TagPrefix"));
											tagSuffix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank3.TagSuffix"));
											groupName = BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank3.PermissionsEx.GroupName");
										} else if(args[0].equalsIgnoreCase(BookGUIFileUtils.cfg.getString("BookGUI.Rank4.RankName")) && (BookGUIFileUtils.cfg.getBoolean("BookGUI.Rank4.Enabled"))) {
											prefix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank4.Prefix"));
											suffix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank4.Suffix"));
											tagPrefix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank4.TagPrefix"));
											tagSuffix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank4.TagSuffix"));
											groupName = BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank4.PermissionsEx.GroupName");
										} else if(args[0].equalsIgnoreCase(BookGUIFileUtils.cfg.getString("BookGUI.Rank5.RankName")) && (BookGUIFileUtils.cfg.getBoolean("BookGUI.Rank5.Enabled"))) {
											prefix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank5.Prefix"));
											suffix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank5.Suffix"));
											tagPrefix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank5.TagPrefix"));
											tagSuffix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank5.TagSuffix"));
											groupName = BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank5.PermissionsEx.GroupName");
										} else if(args[0].equalsIgnoreCase(BookGUIFileUtils.cfg.getString("BookGUI.Rank6.RankName")) && (BookGUIFileUtils.cfg.getBoolean("BookGUI.Rank6.Enabled"))) {
											prefix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank6.Prefix"));
											suffix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank6.Suffix"));
											tagPrefix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank6.TagPrefix"));
											tagSuffix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank6.TagSuffix"));
											groupName = BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank6.PermissionsEx.GroupName");
										} else if(args[0].equalsIgnoreCase(BookGUIFileUtils.cfg.getString("BookGUI.Rank7.RankName")) && (BookGUIFileUtils.cfg.getBoolean("BookGUI.Rank7.Enabled"))) {
											prefix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank7.Prefix"));
											suffix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank7.Suffix"));
											tagPrefix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank7.TagPrefix"));
											tagSuffix = ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank7.TagSuffix"));
											groupName = BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank7.PermissionsEx.GroupName");
										}
										
										if(args[1].equalsIgnoreCase("DEFAULT")) {
											skinName = api.getRealName();
										} else if(args[1].equalsIgnoreCase("NORMAL")) {
											skinName = ((new Random().nextInt(2)) == 1) ? "Steve" : "Alex";
										} else if(args[1].equalsIgnoreCase("RANDOM")) {
											skinName = Utils.nickNames.get((new Random().nextInt(Utils.nickNames.size())));
										}
										
										Bukkit.getPluginManager().callEvent(new PlayerNickEvent(p, name, skinName, prefix, suffix, prefix, suffix, tagPrefix, tagSuffix, false, groupName));
									} else {
										p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("Messages.NickTooLong")));
									}
								} else {
									p.sendMessage(Utils.NO_PERM);
								}
							} else {
								p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("Messages.PlayerWithThisNameIsOnServer")));
							}
						} else {
							p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("Messages.NickNameAlreadyInUse")));
						}
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