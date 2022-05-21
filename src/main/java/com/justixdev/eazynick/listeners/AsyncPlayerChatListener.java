package com.justixdev.eazynick.listeners;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.api.NickManager;
import com.justixdev.eazynick.utilities.Utils;
import com.justixdev.eazynick.utilities.configuration.yaml.SetupYamlFile;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class AsyncPlayerChatListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onFirstAsyncPlayerChat(AsyncPlayerChatEvent event) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		
		Player player = event.getPlayer();
		
		if(utils.getPlayersTypingNameInChat().containsKey(player.getUniqueId())) {
			String[] args = (utils.getPlayersTypingNameInChat().get(player.getUniqueId()) + " " + event.getMessage().trim()).split(" ");
			
			utils.getPlayersTypingNameInChat().remove(player.getUniqueId());
			utils.performRankedNick(player, args[0], args[1], args[2]);
			
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled=true)
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
		
		Player player = event.getPlayer();

		utils.setLastChatMessage(event.getMessage());
		
		if (setupYamlFile.getConfiguration().getBoolean("ReplaceNickedChatFormat")) {
			if (!(event.isCancelled())) {
				NickManager api = new NickManager(player);
	
				if (api.isNicked()) {
					event.setCancelled(true);
					
					String format = setupYamlFile.getConfigString(player, "Settings.ChatFormat").replace("%displayName%", player.getDisplayName()).replace("%nickName%", api.getNickName()).replace("%playerName%", api.getNickName()).replace("%displayname%", player.getDisplayName()).replace("%nickname%", api.getNickName()).replace("%playername%", api.getNickName()).replace("%prefix%", api.getChatPrefix()).replace("%suffix%", api.getChatSuffix());
					
					if(utils.isPluginInstalled("PlaceholderAPI"))
						format = PlaceholderAPI.setPlaceholders(player, format);
					
					format = format.replace("%message%", event.getMessage()).replaceAll("%", "%%");
					
					event.setFormat(format);
					
					Bukkit.getConsoleSender().sendMessage(format);
	
					for (Player currentPlayer : Bukkit.getOnlinePlayers()) {
						if (currentPlayer.getName().equalsIgnoreCase(player.getName())) {
							if (setupYamlFile.getConfiguration().getBoolean("SeeNickSelf"))
								currentPlayer.sendMessage(format);
							else
								currentPlayer.sendMessage(format.replace(player.getDisplayName(), api.getOldDisplayName()));
						} else if (currentPlayer.hasPermission("eazynick.bypass") && setupYamlFile.getConfiguration().getBoolean("EnableBypassPermission"))
							currentPlayer.sendMessage(format.replace(player.getDisplayName(), api.getOldDisplayName()));
						else
							currentPlayer.sendMessage(format);
					}
				}
			}
		}
	}

}
