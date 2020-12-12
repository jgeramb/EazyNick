package net.dev.eazynick.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.NickManager;
import net.dev.eazynick.utils.FileUtils;
import net.dev.eazynick.utils.Utils;

public class AsyncPlayerChatListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onFirstAsyncPlayerChat(AsyncPlayerChatEvent e) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		
		Player p = e.getPlayer();
		
		if(utils.getPlayersTypingNameInChat().containsKey(p.getUniqueId())) {
			String[] args = (utils.getPlayersTypingNameInChat().get(p.getUniqueId()) + " " + e.getMessage().trim()).split(" ");
			
			utils.getPlayersTypingNameInChat().remove(p.getUniqueId());
			utils.performRankedNick(p, args[0], args[1], args[2]);
			
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled=true)
	public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		FileUtils fileUtils = eazyNick.getFileUtils();
		
		Player p = e.getPlayer();

		utils.setLastChatMessage(e.getMessage());
		
		if (fileUtils.getConfig().getBoolean("ReplaceNickedChatFormat")) {
			if (!(e.isCancelled())) {
				NickManager api = new NickManager(p);
	
				if (api.isNicked()) {
					e.setCancelled(true);
					
					String format = fileUtils.getConfigString(p, "Settings.ChatFormat").replace("%displayName%", p.getDisplayName()).replace("%nickName%", api.getNickName()).replace("%playerName%", p.getName()).replace("%displayname%", p.getDisplayName()).replace("%nickname%", api.getNickName()).replace("%playername%", p.getName()).replace("%prefix%", api.getChatPrefix()).replace("%suffix%", api.getChatSuffix()).replace("%message%", e.getMessage()).replaceAll("%", "%%");
					
					e.setFormat(format);
					
					Bukkit.getConsoleSender().sendMessage(format);
	
					for (Player all : Bukkit.getOnlinePlayers()) {
						if (all.getName().equalsIgnoreCase(p.getName())) {
							if (fileUtils.getConfig().getBoolean("SeeNickSelf"))
								all.sendMessage(format);
							else
								all.sendMessage(format.replace(p.getDisplayName(), api.getOldDisplayName()));
						} else if (all.hasPermission("nick.bypass") && fileUtils.getConfig().getBoolean("EnableBypassPermission"))
							all.sendMessage(format.replace(p.getDisplayName(), api.getOldDisplayName()));
						else
							all.sendMessage(format);
					}
				}
			}
		}
	}

}
