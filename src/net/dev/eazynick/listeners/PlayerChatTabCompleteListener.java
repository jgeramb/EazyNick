package net.dev.eazynick.listeners;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.NickedPlayerData;

public class PlayerChatTabCompleteListener implements Listener {

	@EventHandler
	public void onPlayerChatTabComplete(PlayerChatTabCompleteEvent event) {
		EazyNick eazyNick = EazyNick.getInstance();
		
		List<String> completions = (List<String>) event.getTabCompletions();
		
		if(!(event.getPlayer().hasPermission("nick.bypass") && eazyNick.getSetupYamlFile().getConfiguration().getBoolean("EnableBypassPermission"))) {
			for (ListIterator<String> iterator = completions.listIterator(); iterator.hasNext();) {
				String currentComppletion = iterator.next();
				
				for (NickedPlayerData nickedPlayerData : eazyNick.getUtils().getNickedPlayers().values()) {
					if(currentComppletion.equalsIgnoreCase(nickedPlayerData.getRealName())) {
						if(completions.size() == Bukkit.getOnlinePlayers().size()) {
							iterator.set(nickedPlayerData.getNickName());
							break;
						} else {
							completions.clear();
							return;
						}
					}
				}
			}
		}
	}

}
