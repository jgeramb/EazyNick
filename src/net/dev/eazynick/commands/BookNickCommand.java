package net.dev.eazynick.commands;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.NickManager;
import net.dev.eazynick.api.PlayerNickEvent;
import net.dev.eazynick.utils.BookGUIFileUtils;
import net.dev.eazynick.utils.FileUtils;
import net.dev.eazynick.utils.LanguageFileUtils;
import net.dev.eazynick.utils.StringUtils;
import net.dev.eazynick.utils.Utils;
import net.dev.eazynick.utils.bookutils.NMSBookBuilder;
import net.dev.eazynick.utils.bookutils.NMSBookUtils;
import net.md_5.bungee.api.chat.TextComponent;

public class BookNickCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		FileUtils fileUtils = eazyNick.getFileUtils();
		LanguageFileUtils languageFileUtils = eazyNick.getLanguageFileUtils();
		BookGUIFileUtils bookGUIFileUtils = eazyNick.getBookGUIFileUtils();
		NMSBookUtils nmsBookUtils = eazyNick.getNMSBookUtils();
		NMSBookBuilder nmsBookBuilder = eazyNick.getNMSBookBuilder();
		
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
									
									for (OfflinePlayer all : Bukkit.getOfflinePlayers()) {
										if((all != null) && (all.getName() != null) && all.getName().toUpperCase().equalsIgnoreCase(name.toUpperCase()))
											playerWithNameIsKnown = true;
									}
									
									if(!(fileUtils.cfg.getBoolean("AllowPlayersToNickAsKnownPlayers")) && playerWithNameIsKnown)
										isCancelled = true;
									
									if(!(isCancelled)) {
										String groupName = "";
										
										if(args[0].equalsIgnoreCase(bookGUIFileUtils.cfg.getString("BookGUI.Rank1.RankName")) && bookGUIFileUtils.cfg.getBoolean("BookGUI.Rank1.Enabled") && (bookGUIFileUtils.cfg.getString("BookGUI.Rank1.Permission").equalsIgnoreCase("NONE") ? true : p.hasPermission(bookGUIFileUtils.cfg.getString("BookGUI.Rank1.Permission")))) {
											chatPrefix = bookGUIFileUtils.getConfigString("Settings.NickFormat.Rank1.ChatPrefix");
											chatSuffix = bookGUIFileUtils.getConfigString("Settings.NickFormat.Rank1.ChatSuffix");
											tabPrefix = bookGUIFileUtils.getConfigString("Settings.NickFormat.Rank1.TabPrefix");
											tabSuffix = bookGUIFileUtils.getConfigString("Settings.NickFormat.Rank1.TabSuffix");
											tagPrefix = bookGUIFileUtils.getConfigString("Settings.NickFormat.Rank1.TagPrefix");
											tagSuffix = bookGUIFileUtils.getConfigString("Settings.NickFormat.Rank1.TagSuffix");
											groupName = bookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank1.GroupName");
										} else if(args[0].equalsIgnoreCase(bookGUIFileUtils.cfg.getString("BookGUI.Rank2.RankName")) && bookGUIFileUtils.cfg.getBoolean("BookGUI.Rank2.Enabled") && (bookGUIFileUtils.cfg.getString("BookGUI.Rank2.Permission").equalsIgnoreCase("NONE") ? true : p.hasPermission(bookGUIFileUtils.cfg.getString("BookGUI.Rank2.Permission")))) {
											chatPrefix = bookGUIFileUtils.getConfigString("Settings.NickFormat.Rank2.ChatPrefix");
											chatSuffix = bookGUIFileUtils.getConfigString("Settings.NickFormat.Rank2.ChatSuffix");
											tabPrefix = bookGUIFileUtils.getConfigString("Settings.NickFormat.Rank2.TabPrefix");
											tabSuffix = bookGUIFileUtils.getConfigString("Settings.NickFormat.Rank2.TabSuffix");
											tagPrefix = bookGUIFileUtils.getConfigString("Settings.NickFormat.Rank2.TagPrefix");
											tagSuffix = bookGUIFileUtils.getConfigString("Settings.NickFormat.Rank2.TagSuffix");
											groupName = bookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank2.GroupName");
										} else if(args[0].equalsIgnoreCase(bookGUIFileUtils.cfg.getString("BookGUI.Rank3.RankName")) && bookGUIFileUtils.cfg.getBoolean("BookGUI.Rank3.Enabled") && (bookGUIFileUtils.cfg.getString("BookGUI.Rank3.Permission").equalsIgnoreCase("NONE") ? true : p.hasPermission(bookGUIFileUtils.cfg.getString("BookGUI.Rank3.Permission")))) {
											chatPrefix = bookGUIFileUtils.getConfigString("Settings.NickFormat.Rank3.ChatPrefix");
											chatSuffix = bookGUIFileUtils.getConfigString("Settings.NickFormat.Rank3.ChatSuffix");
											tabPrefix = bookGUIFileUtils.getConfigString("Settings.NickFormat.Rank3.TabPrefix");
											tabSuffix = bookGUIFileUtils.getConfigString("Settings.NickFormat.Rank3.TabSuffix");
											tagPrefix = bookGUIFileUtils.getConfigString("Settings.NickFormat.Rank3.TagPrefix");
											tagSuffix = bookGUIFileUtils.getConfigString("Settings.NickFormat.Rank3.TagSuffix");
											groupName = bookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank3.GroupName");
										} else if(args[0].equalsIgnoreCase(bookGUIFileUtils.cfg.getString("BookGUI.Rank4.RankName")) && bookGUIFileUtils.cfg.getBoolean("BookGUI.Rank4.Enabled") && (bookGUIFileUtils.cfg.getString("BookGUI.Rank4.Permission").equalsIgnoreCase("NONE") ? true : p.hasPermission(bookGUIFileUtils.cfg.getString("BookGUI.Rank4.Permission")))) {
											chatPrefix = bookGUIFileUtils.getConfigString("Settings.NickFormat.Rank4.ChatPrefix");
											chatSuffix = bookGUIFileUtils.getConfigString("Settings.NickFormat.Rank4.ChatSuffix");
											tabPrefix = bookGUIFileUtils.getConfigString("Settings.NickFormat.Rank4.TabPrefix");
											tabSuffix = bookGUIFileUtils.getConfigString("Settings.NickFormat.Rank4.TabSuffix");
											tagPrefix = bookGUIFileUtils.getConfigString("Settings.NickFormat.Rank4.TagPrefix");
											tagSuffix = bookGUIFileUtils.getConfigString("Settings.NickFormat.Rank4.TagSuffix");
											groupName = bookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank4.GroupName");
										} else if(args[0].equalsIgnoreCase(bookGUIFileUtils.cfg.getString("BookGUI.Rank5.RankName")) && bookGUIFileUtils.cfg.getBoolean("BookGUI.Rank5.Enabled") && (bookGUIFileUtils.cfg.getString("BookGUI.Rank5.Permission").equalsIgnoreCase("NONE") ? true : p.hasPermission(bookGUIFileUtils.cfg.getString("BookGUI.Rank5.Permission")))) {
											chatPrefix = bookGUIFileUtils.getConfigString("Settings.NickFormat.Rank5.ChatPrefix");
											chatSuffix = bookGUIFileUtils.getConfigString("Settings.NickFormat.Rank5.ChatSuffix");
											tabPrefix = bookGUIFileUtils.getConfigString("Settings.NickFormat.Rank5.TabPrefix");
											tabSuffix = bookGUIFileUtils.getConfigString("Settings.NickFormat.Rank5.TabSuffix");
											tagPrefix = bookGUIFileUtils.getConfigString("Settings.NickFormat.Rank5.TagPrefix");
											tagSuffix = bookGUIFileUtils.getConfigString("Settings.NickFormat.Rank5.TagSuffix");
											groupName = bookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank5.GroupName");
										} else if(args[0].equalsIgnoreCase(bookGUIFileUtils.cfg.getString("BookGUI.Rank6.RankName")) && bookGUIFileUtils.cfg.getBoolean("BookGUI.Rank6.Enabled") && (bookGUIFileUtils.cfg.getString("BookGUI.Rank6.Permission").equalsIgnoreCase("NONE") ? true : p.hasPermission(bookGUIFileUtils.cfg.getString("BookGUI.Rank6.Permission")))) {
											chatPrefix = bookGUIFileUtils.getConfigString("Settings.NickFormat.Rank6.ChatPrefix");
											chatSuffix = bookGUIFileUtils.getConfigString("Settings.NickFormat.Rank6.ChatSuffix");
											tabPrefix = bookGUIFileUtils.getConfigString("Settings.NickFormat.Rank6.TabPrefix");
											tabSuffix = bookGUIFileUtils.getConfigString("Settings.NickFormat.Rank6.TabSuffix");
											tagPrefix = bookGUIFileUtils.getConfigString("Settings.NickFormat.Rank6.TagPrefix");
											tagSuffix = bookGUIFileUtils.getConfigString("Settings.NickFormat.Rank6.TagSuffix");
											groupName = bookGUIFileUtils.cfg.getString("Settings.NickFormat.Rank6.GroupName");
										} else
											return true;
										
										if(args[1].equalsIgnoreCase("DEFAULT"))
											skinName = api.getRealName();
										else if(args[1].equalsIgnoreCase("NORMAL"))
											skinName = (new Random().nextBoolean()) ? "Steve" : "Alex";
										else if(args[1].equalsIgnoreCase("RANDOM"))
											skinName = utils.getNickNames().get((new Random().nextInt(utils.getNickNames().size())));
										else
											skinName = args[1];
										
										if(utils.getLastSkinNames().containsKey(p.getUniqueId()))
											utils.getLastSkinNames().remove(p.getUniqueId());
										
										if(utils.getLastNickNames().containsKey(p.getUniqueId()))
											utils.getLastNickNames().remove(p.getUniqueId());
										
										utils.getLastSkinNames().put(p.getUniqueId(), skinName);
										utils.getLastNickNames().put(p.getUniqueId(), name);
										
										new NickManager(p).setGroupName(args[0]);
										
										if(fileUtils.cfg.getBoolean("BungeeCord") && fileUtils.cfg.getBoolean("LobbyMode")) {
											eazyNick.getMySQLNickManager().addPlayer(p.getUniqueId(), name, skinName);
											eazyNick.getMySQLPlayerDataManager().insertData(p.getUniqueId(), groupName, chatPrefix, chatSuffix, tabPrefix, tabSuffix, tagPrefix, tagSuffix);
											
											if(bookGUIFileUtils.cfg.getBoolean("BookGUI.Page6.Enabled"))
												nmsBookUtils.open(p, nmsBookBuilder.create("Done", new TextComponent(bookGUIFileUtils.getConfigString("BookGUI.Page6.Text.BungeeCord").replace("%name%", tagPrefix + name + tagSuffix))));
										} else {
											Bukkit.getPluginManager().callEvent(new PlayerNickEvent(p, name, skinName, chatPrefix, chatSuffix, tabPrefix, tabSuffix, tagPrefix, tagSuffix, false, false, groupName));
										
											if(bookGUIFileUtils.cfg.getBoolean("BookGUI.Page6.Enabled"))
												nmsBookUtils.open(p, nmsBookBuilder.create("Done", new TextComponent(bookGUIFileUtils.getConfigString("BookGUI.Page6.Text.SingleServer").replace("%name%", tagPrefix + name + tagSuffix))));
										}
									} else
										p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString("Messages.PlayerWithThisNameIsKnown"));
								} else
									p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString("Messages.NickNameAlreadyInUse"));
							} else
								p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString("Messages.NameNotAllowed"));
						} else
							p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString("Messages.NickTooLong"));
					}
				}
			} else
				p.sendMessage(utils.getNoPerm());
		} else
			utils.sendConsole(utils.getNotPlayer());
		
		return true;
	}

}