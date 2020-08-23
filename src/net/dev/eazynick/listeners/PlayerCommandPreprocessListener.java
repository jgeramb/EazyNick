package net.dev.eazynick.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.NickManager;

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
		
		e.setMessage(msg);
		
		String msg2 = msg.toLowerCase().replace("bukkit:", "");
		
		if (msg2.startsWith("/help nick") || msg2.startsWith("/help eazynick") || msg2.startsWith("/? nick") || msg2.startsWith("/? eazynick")) {
			if (p.hasPermission("bukkit.command.help")) {
				e.setCancelled(true);

				p.sendMessage("§e--------- §fHelp: " + eazyNick.getDescription().getName() + " §e----------------------");
				p.sendMessage("§7Below is a list of all " + eazyNick.getDescription().getName() + " commands:");
				p.sendMessage("§6/eazynick: §f" + eazyNick.getCommand("eazynick").getDescription());
			}
		}
	}

}
