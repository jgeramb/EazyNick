package net.dev.nickplugin.commands;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dev.nickplugin.api.NickManager;
import net.dev.nickplugin.api.PlayerNickEvent;
import net.dev.nickplugin.sql.MySQLNickManager;
import net.dev.nickplugin.sql.MySQLPlayerDataManager;
import net.dev.nickplugin.utils.BookGUIFileUtils;
import net.dev.nickplugin.utils.FileUtils;
import net.dev.nickplugin.utils.LanguageFileUtils;
import net.dev.nickplugin.utils.StringUtils;
import net.dev.nickplugin.utils.Utils;
import net.dev.nickplugin.utils.bookUtils.NMSBookBuilder;
import net.dev.nickplugin.utils.bookUtils.NMSBookUtils;
import net.md_5.bungee.api.chat.TextComponent;

public class BookNickCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(p.hasPermission("nick.use")) {
				NickManager api = new NickManager(p);
				
				if(!(api.isNicked())) {
					if(args.length >= 3) {
						String chatPrefix = "", chatSuffix = "", tabPrefix = "", tabSuffix = "", tagPrefix = "", tagSuffix = "";
						String name = args[2];
						String skinName = "";
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
										String groupName = "";
										
										if(args[0].equalsIgnoreCase(BookGUIFileUtils.cfg.getString("BookGUI.Rank1.RankName")) && BookGUIFileUtils.cfg.getBoolean("BookGUI.Rank1.Enabled") && (BookGUIFileUtils.cfg.getString("BookGUI.Rank1.Permission").equalsIgnoreCase("NONE") ? true : p.hasPermission(BookGUIFileUtils.cfg.getString("BookGUI.Rank1.Permission")))) {
											chatPrefix = BookGUIFileUtils.getConfigString("Settings.NickFormat.Rank1.ChatPrefix");
											chatSuffix = BookGUIFileUtils.getConfigString("Settings.NickFormat.Rank1.ChatSuffix");
											tabPrefix = BookGUIFileUtils.getConfigString("Settings.NickFormat.Rank1.TabPrefix");
											tabSuffix = BookGUIFileUtils.getConfigString("Settings.NickFormat.Rank1.TabSuffix");
											tagPrefix = BookGUIFileUtils.getConfigString("Settings.NickFormat.Rank1.TagPrefix");
											tagSuffix = BookGUIFileUtils.getConfigString("Settings.NickFormat.Rank1.TagSuffix");
											groupName = BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank1.PermissionsEx.GroupName");
										} else if(args[0].equalsIgnoreCase(BookGUIFileUtils.cfg.getString("BookGUI.Rank2.RankName")) && BookGUIFileUtils.cfg.getBoolean("BookGUI.Rank2.Enabled") && (BookGUIFileUtils.cfg.getString("BookGUI.Rank2.Permission").equalsIgnoreCase("NONE") ? true : p.hasPermission(BookGUIFileUtils.cfg.getString("BookGUI.Rank2.Permission")))) {
											chatPrefix = BookGUIFileUtils.getConfigString("Settings.NickFormat.Rank2.ChatPrefix");
											chatSuffix = BookGUIFileUtils.getConfigString("Settings.NickFormat.Rank2.ChatSuffix");
											tabPrefix = BookGUIFileUtils.getConfigString("Settings.NickFormat.Rank2.TabPrefix");
											tabSuffix = BookGUIFileUtils.getConfigString("Settings.NickFormat.Rank2.TabSuffix");
											tagPrefix = BookGUIFileUtils.getConfigString("Settings.NickFormat.Rank2.TagPrefix");
											tagSuffix = BookGUIFileUtils.getConfigString("Settings.NickFormat.Rank2.TagSuffix");
											groupName = BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank2.PermissionsEx.GroupName");
										} else if(args[0].equalsIgnoreCase(BookGUIFileUtils.cfg.getString("BookGUI.Rank3.RankName")) && BookGUIFileUtils.cfg.getBoolean("BookGUI.Rank3.Enabled") && (BookGUIFileUtils.cfg.getString("BookGUI.Rank3.Permission").equalsIgnoreCase("NONE") ? true : p.hasPermission(BookGUIFileUtils.cfg.getString("BookGUI.Rank3.Permission")))) {
											chatPrefix = BookGUIFileUtils.getConfigString("Settings.NickFormat.Rank3.ChatPrefix");
											chatSuffix = BookGUIFileUtils.getConfigString("Settings.NickFormat.Rank3.ChatSuffix");
											tabPrefix = BookGUIFileUtils.getConfigString("Settings.NickFormat.Rank3.TabPrefix");
											tabSuffix = BookGUIFileUtils.getConfigString("Settings.NickFormat.Rank3.TabSuffix");
											tagPrefix = BookGUIFileUtils.getConfigString("Settings.NickFormat.Rank3.TagPrefix");
											tagSuffix = BookGUIFileUtils.getConfigString("Settings.NickFormat.Rank3.TagSuffix");
											groupName = BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank3.PermissionsEx.GroupName");
										} else if(args[0].equalsIgnoreCase(BookGUIFileUtils.cfg.getString("BookGUI.Rank4.RankName")) && BookGUIFileUtils.cfg.getBoolean("BookGUI.Rank4.Enabled") && (BookGUIFileUtils.cfg.getString("BookGUI.Rank4.Permission").equalsIgnoreCase("NONE") ? true : p.hasPermission(BookGUIFileUtils.cfg.getString("BookGUI.Rank4.Permission")))) {
											chatPrefix = BookGUIFileUtils.getConfigString("Settings.NickFormat.Rank4.ChatPrefix");
											chatSuffix = BookGUIFileUtils.getConfigString("Settings.NickFormat.Rank4.ChatSuffix");
											tabPrefix = BookGUIFileUtils.getConfigString("Settings.NickFormat.Rank4.TabPrefix");
											tabSuffix = BookGUIFileUtils.getConfigString("Settings.NickFormat.Rank4.TabSuffix");
											tagPrefix = BookGUIFileUtils.getConfigString("Settings.NickFormat.Rank4.TagPrefix");
											tagSuffix = BookGUIFileUtils.getConfigString("Settings.NickFormat.Rank4.TagSuffix");
											groupName = BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank4.PermissionsEx.GroupName");
										} else if(args[0].equalsIgnoreCase(BookGUIFileUtils.cfg.getString("BookGUI.Rank5.RankName")) && BookGUIFileUtils.cfg.getBoolean("BookGUI.Rank5.Enabled") && (BookGUIFileUtils.cfg.getString("BookGUI.Rank5.Permission").equalsIgnoreCase("NONE") ? true : p.hasPermission(BookGUIFileUtils.cfg.getString("BookGUI.Rank5.Permission")))) {
											chatPrefix = BookGUIFileUtils.getConfigString("Settings.NickFormat.Rank5.ChatPrefix");
											chatSuffix = BookGUIFileUtils.getConfigString("Settings.NickFormat.Rank5.ChatSuffix");
											tabPrefix = BookGUIFileUtils.getConfigString("Settings.NickFormat.Rank5.TabPrefix");
											tabSuffix = BookGUIFileUtils.getConfigString("Settings.NickFormat.Rank5.TabSuffix");
											tagPrefix = BookGUIFileUtils.getConfigString("Settings.NickFormat.Rank5.TagPrefix");
											tagSuffix = BookGUIFileUtils.getConfigString("Settings.NickFormat.Rank5.TagSuffix");
											groupName = BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank5.PermissionsEx.GroupName");
										} else if(args[0].equalsIgnoreCase(BookGUIFileUtils.cfg.getString("BookGUI.Rank6.RankName")) && BookGUIFileUtils.cfg.getBoolean("BookGUI.Rank6.Enabled") && (BookGUIFileUtils.cfg.getString("BookGUI.Rank6.Permission").equalsIgnoreCase("NONE") ? true : p.hasPermission(BookGUIFileUtils.cfg.getString("BookGUI.Rank6.Permission")))) {
											chatPrefix = BookGUIFileUtils.getConfigString("Settings.NickFormat.Rank6.ChatPrefix");
											chatSuffix = BookGUIFileUtils.getConfigString("Settings.NickFormat.Rank6.ChatSuffix");
											tabPrefix = BookGUIFileUtils.getConfigString("Settings.NickFormat.Rank6.TabPrefix");
											tabSuffix = BookGUIFileUtils.getConfigString("Settings.NickFormat.Rank6.TabSuffix");
											tagPrefix = BookGUIFileUtils.getConfigString("Settings.NickFormat.Rank6.TagPrefix");
											tagSuffix = BookGUIFileUtils.getConfigString("Settings.NickFormat.Rank6.TagSuffix");
											groupName = BookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank6.PermissionsEx.GroupName");
										} else
											return true;
										
										if(args[1].equalsIgnoreCase("DEFAULT"))
											skinName = api.getRealName();
										else if(args[1].equalsIgnoreCase("NORMAL"))
											skinName = (new Random().nextBoolean()) ? "Steve" : "Alex";
										else if(args[1].equalsIgnoreCase("RANDOM"))
											skinName = Utils.nickNames.get((new Random().nextInt(Utils.nickNames.size())));
										else
											skinName = args[1];
										
										if(Utils.lastSkinNames.containsKey(p.getUniqueId()))
											Utils.lastSkinNames.remove(p.getUniqueId());
										
										if(Utils.lastNickNames.containsKey(p.getUniqueId()))
											Utils.lastNickNames.remove(p.getUniqueId());
										
										Utils.lastSkinNames.put(p.getUniqueId(), skinName);
										Utils.lastNickNames.put(p.getUniqueId(), name);
										
										if(FileUtils.cfg.getBoolean("BungeeCord") && FileUtils.cfg.getBoolean("LobbyMode")) {
											MySQLNickManager.addPlayer(p.getUniqueId(), name, skinName);
											MySQLPlayerDataManager.insertData(p.getUniqueId(), groupName, chatPrefix, chatSuffix, tabPrefix, tabSuffix, tagPrefix, tagSuffix);
											
											NMSBookUtils.open(p, NMSBookBuilder.create("Done", new TextComponent(BookGUIFileUtils.getConfigString("BookGUI.Page6.Text.BungeeCord").replace("%name%", tagPrefix + p.getName() + tagSuffix))));
										} else {
											Bukkit.getPluginManager().callEvent(new PlayerNickEvent(p, name, skinName, chatPrefix, chatSuffix, tabPrefix, tabSuffix, tagPrefix, tagSuffix, false, false, groupName));
										
											NMSBookUtils.open(p, NMSBookBuilder.create("Done", new TextComponent(BookGUIFileUtils.getConfigString("BookGUI.Page6.Text.SingleServer").replace("%name%", tagPrefix + p.getName() + tagSuffix))));
										}
									} else
										p.sendMessage(Utils.prefix + LanguageFileUtils.getConfigString("Messages.PlayerWithThisNameIsKnown"));
								} else
									p.sendMessage(Utils.prefix + LanguageFileUtils.getConfigString("Messages.NickNameAlreadyInUse"));
							} else
								p.sendMessage(Utils.prefix + LanguageFileUtils.getConfigString("Messages.NameNotAllowed"));
						} else
							p.sendMessage(Utils.prefix + LanguageFileUtils.getConfigString("Messages.NickTooLong"));
					}
				}
			} else
				p.sendMessage(Utils.noPerm);
		} else
			Utils.sendConsole(Utils.notPlayer);
		
		return true;
	}

}