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

import me.clip.placeholderapi.PlaceholderAPI;

public class AsyncPlayerChatListener implements Listener {

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
					
					String format = fileUtils.getConfigString("Settings.ChatFormat").replace("%displayName%", p.getDisplayName()).replace("%nickName%", api.getNickName()).replace("%playerName%", p.getName()).replace("%prefix%", api.getChatPrefix()).replace("%suffix%", api.getChatSuffix()).replace("%message%", e.getMessage().replaceAll("%", "%%"));
					
					if(utils.placeholderAPIStatus())
						format = PlaceholderAPI.setPlaceholders(p, format);
					
					e.setFormat(format);
					
					Bukkit.getConsoleSender().sendMessage(format);
	
					for (Player all : Bukkit.getOnlinePlayers()) {
						if (all.getName().equalsIgnoreCase(p.getName())) {
							if (fileUtils.getConfig().getBoolean("SeeNickSelf"))
								all.sendMessage(format);
							else
								all.sendMessage(format.replace(p.getDisplayName(), api.getOldDisplayName()));
						} else if (all.hasPermission("nick.bypass"))
							all.sendMessage(format.replace(p.getDisplayName(), api.getOldDisplayName()));
						else
							all.sendMessage(format);
					}
				}
			}
		}
	}

}
