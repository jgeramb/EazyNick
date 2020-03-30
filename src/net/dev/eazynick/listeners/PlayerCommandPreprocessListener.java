package net.dev.eazynick.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.NickManager;

import me.clip.placeholderapi.PlaceholderAPI;

public class PlayerCommandPreprocessListener implements Listener {

	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
		EazyNick eazyNick = EazyNick.getInstance();
		Player p = e.getPlayer();
		String msg = e.getMessage();
		
		for (Player all : Bukkit.getOnlinePlayers()) {
			NickManager api = new NickManager(all);
			
			if(api.isNicked()) {
				String realName = api.getRealName();
				
				if(msg.contains(realName))
					msg = msg.replace(realName, realName + "§r");
			}
		}
		
		if(eazyNick.getUtils().placeholderAPIStatus())
			msg = PlaceholderAPI.setPlaceholders(p, msg);
		
		e.setMessage(msg);
		
		if (e.getMessage().toLowerCase().startsWith("/help nick") || e.getMessage().toLowerCase().startsWith("/help eazynick") || e.getMessage().toLowerCase().startsWith("/? nick") || e.getMessage().toLowerCase().startsWith("/? eazynick")) {
			if (p.hasPermission("bukkit.command.help")) {
				e.setCancelled(true);

				p.sendMessage("§e--------- §fHelp: " + EazyNick.getInstance().getDescription().getName() + " §e----------------------");
				p.sendMessage("§7Below is a list of all " + EazyNick.getInstance().getDescription().getName() + " commands:");
				p.sendMessage("§6/eazynick: §r" + EazyNick.getInstance().getCommand("eazynick").getDescription());
			}
		}
	}

}
