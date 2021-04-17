package net.dev.eazynick.listeners;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.NickedPlayerData;

public class TabCompleteListener implements Listener {

	@EventHandler
	public void onTabComplete(TabCompleteEvent event) {
		EazyNick eazyNick = EazyNick.getInstance();
		
		List<String> completions = event.getCompletions();
		
		if(!(event.getSender().hasPermission("nick.bypass") && eazyNick.getSetupYamlFile().getConfiguration().getBoolean("EnableBypassPermission"))) {
			for (ListIterator<String> iterator = completions.listIterator(); iterator.hasNext();) {
				String currentComppletion = iterator.next();
				
				for (NickedPlayerData nickedPlayerData : eazyNick.getUtils().getNickedPlayers().values()) {
					if(currentComppletion.equalsIgnoreCase(nickedPlayerData.getRealName())) {
						if(completions.size() == Bukkit.getOnlinePlayers().size()) {
							iterator.set(nickedPlayerData.getNickName());
							break;
						} else {
							event.setCompletions(new ArrayList<>());
							return;
						}
					}
				}
			}
		}
	}

}
