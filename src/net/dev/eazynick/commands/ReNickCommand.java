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
import net.dev.eazynick.api.PlayerUnnickEvent;
import net.dev.eazynick.sql.MySQLNickManager;
import net.dev.eazynick.sql.MySQLPlayerDataManager;
import net.dev.eazynick.utils.FileUtils;
import net.dev.eazynick.utils.LanguageFileUtils;
import net.dev.eazynick.utils.Utils;

public class ReNickCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		FileUtils fileUtils = eazyNick.getFileUtils();
		LanguageFileUtils languageFileUtils = eazyNick.getLanguageFileUtils();
		MySQLNickManager mysqlNickManager = eazyNick.getMySQLNickManager();
		MySQLPlayerDataManager mysqlPlayerDataManager = eazyNick.getMySQLPlayerDataManager();
		
		if(sender instanceof Player) {
			Player p = (Player) sender;
			boolean isNickOnWorldChange = utils.getNickOnWorldChangePlayers().contains(p.getUniqueId());
			
			if(isNickOnWorldChange || ((mysqlNickManager != null) && mysqlNickManager.isPlayerNicked(p.getUniqueId()))) {
				if(utils.getNickedPlayers().contains(p.getUniqueId()))
					Bukkit.getPluginManager().callEvent(new PlayerUnnickEvent(p));
				else {
					String name = isNickOnWorldChange ? utils.getNickNames().get((new Random().nextInt(utils.getNickNames().size()))) : mysqlNickManager.getNickName(p.getUniqueId());
					boolean isCancelled = false;
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
							if(!(name.equalsIgnoreCase(p.getName()))) {
								if(mysqlPlayerDataManager.isRegistered(p.getUniqueId())) {
									new NickManager(p).setRank("Default");
									
									Bukkit.getPluginManager().callEvent(new PlayerNickEvent(p, name, mysqlNickManager.getSkinName(p.getUniqueId()),
											mysqlPlayerDataManager.getChatPrefix(p.getUniqueId()),
											mysqlPlayerDataManager.getChatSuffix(p.getUniqueId()),
											mysqlPlayerDataManager.getTabPrefix(p.getUniqueId()),
											mysqlPlayerDataManager.getTabSuffix(p.getUniqueId()),
											mysqlPlayerDataManager.getTagPrefix(p.getUniqueId()),
											mysqlPlayerDataManager.getTagSuffix(p.getUniqueId()),
											true,
											false,
											"NONE"));
								} else {
									boolean serverFull = utils.getOnlinePlayers() >= Bukkit.getMaxPlayers();
									String prefix = serverFull ? fileUtils.getConfigString("Settings.NickFormat.ServerFullRank.NameTag.Prefix") : fileUtils.getConfigString("Settings.NickFormat.NameTag.Prefix");
									String suffix = serverFull ? fileUtils.getConfigString("Settings.NickFormat.ServerFullRank.NameTag.Suffix") : fileUtils.getConfigString("Settings.NickFormat.NameTag.Suffix");
								
									new NickManager(p).setRank("ServerFull");
									
									Bukkit.getPluginManager().callEvent(new PlayerNickEvent(p, name, mysqlNickManager.getSkinName(p.getUniqueId()),
											serverFull ? fileUtils.getConfigString("Settings.NickFormat.ServerFullRank.Chat.Prefix") : fileUtils.getConfigString("Settings.NickFormat.Chat.Prefix"),
											serverFull ? fileUtils.getConfigString("Settings.NickFormat.ServerFullRank.Chat.Suffix") : fileUtils.getConfigString("Settings.NickFormat.Chat.Suffix"),
											serverFull ? fileUtils.getConfigString("Settings.NickFormat.ServerFullRank.PlayerList.Prefix") : fileUtils.getConfigString("Settings.NickFormat.PlayerList.Prefix"),
											serverFull ? fileUtils.getConfigString("Settings.NickFormat.ServerFullRank.PlayerList.Suffix") : fileUtils.getConfigString("Settings.NickFormat.PlayerList.Suffix"),
											prefix,
											suffix,
											true,
											false,
											(utils.getOnlinePlayers() >= Bukkit.getMaxPlayers()) ? fileUtils.getConfigString("Settings.NickFormat.ServerFullRank.GroupName") : fileUtils.getConfigString("Settings.NickFormat.GroupName")));
								}
							} else
								p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString("Messages.CanNotNickAsSelf"));
						} else
							p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString("Messages.PlayerWithThisNameIsKnown"));
					} else
						p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString("Messages.NickNameAlreadyInUse"));
				}
			}
		} else
			utils.sendConsole(utils.getNotPlayer());
		
		return true;
	}
	
}
