package net.dev.nickplugin.commands;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dev.nickplugin.api.PlayerNickEvent;
import net.dev.nickplugin.api.PlayerUnnickEvent;
import net.dev.nickplugin.sql.MySQLNickManager;
import net.dev.nickplugin.sql.MySQLPlayerDataManager;
import net.dev.nickplugin.utils.FileUtils;
import net.dev.nickplugin.utils.LanguageFileUtils;
import net.dev.nickplugin.utils.Utils;

public class ReNickCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(MySQLNickManager.isPlayerNicked(p.getUniqueId())) {
				if(Utils.nickedPlayers.contains(p.getUniqueId()))
					Bukkit.getPluginManager().callEvent(new PlayerUnnickEvent(p));
				else {
					String name = MySQLNickManager.getNickName(p.getUniqueId());
					boolean isCancelled = false;
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
								if(MySQLPlayerDataManager.isRegistered(p.getUniqueId())) {
									Bukkit.getPluginManager().callEvent(new PlayerNickEvent(p, name, MySQLNickManager.getSkinName(p.getUniqueId()),
											MySQLPlayerDataManager.getChatPrefix(p.getUniqueId()),
											MySQLPlayerDataManager.getChatSuffix(p.getUniqueId()),
											MySQLPlayerDataManager.getTabPrefix(p.getUniqueId()),
											MySQLPlayerDataManager.getTabSuffix(p.getUniqueId()),
											MySQLPlayerDataManager.getTagPrefix(p.getUniqueId()),
											MySQLPlayerDataManager.getTagSuffix(p.getUniqueId()),
											true,
											false,
											"NONE"));
								} else {
									boolean serverFull = Utils.getOnlinePlayers() >= Bukkit.getMaxPlayers();
									String prefix = serverFull ? FileUtils.getConfigString("Settings.NickFormat.ServerFullRank.NameTag.Prefix") : FileUtils.getConfigString("Settings.NickFormat.NameTag.Prefix");
									String suffix = serverFull ? FileUtils.getConfigString("Settings.NickFormat.ServerFullRank.NameTag.Suffix") : FileUtils.getConfigString("Settings.NickFormat.NameTag.Suffix");
									
									Bukkit.getPluginManager().callEvent(new PlayerNickEvent(p, name, MySQLNickManager.getSkinName(p.getUniqueId()),
											serverFull ? FileUtils.getConfigString("Settings.NickFormat.ServerFullRank.Chat.Prefix") : FileUtils.getConfigString("Settings.NickFormat.Chat.Prefix"),
											serverFull ? FileUtils.getConfigString("Settings.NickFormat.ServerFullRank.Chat.Suffix") : FileUtils.getConfigString("Settings.NickFormat.Chat.Suffix"),
											serverFull ? FileUtils.getConfigString("Settings.NickFormat.ServerFullRank.PlayerList.Prefix") : FileUtils.getConfigString("Settings.NickFormat.PlayerList.Prefix"),
											serverFull ? FileUtils.getConfigString("Settings.NickFormat.ServerFullRank.PlayerList.Suffix") : FileUtils.getConfigString("Settings.NickFormat.PlayerList.Suffix"),
											prefix,
											suffix,
											true,
											false,
											(Utils.getOnlinePlayers() >= Bukkit.getMaxPlayers()) ? FileUtils.getConfigString("Settings.NickFormat.ServerFullRank.PermissionsEx.GroupName") : FileUtils.getConfigString("Settings.NickFormat.PermissionsEx.GroupName")));
								}
							} else
								p.sendMessage(Utils.prefix + LanguageFileUtils.getConfigString("Messages.CanNotNickAsSelf"));
						} else
							p.sendMessage(Utils.prefix + LanguageFileUtils.getConfigString("Messages.PlayerWithThisNameIsKnown"));
					} else
						p.sendMessage(Utils.prefix + LanguageFileUtils.getConfigString("Messages.NickNameAlreadyInUse"));
				}
			}
		} else
			Utils.sendConsole(Utils.notPlayer);
		
		return true;
	}
	
}
