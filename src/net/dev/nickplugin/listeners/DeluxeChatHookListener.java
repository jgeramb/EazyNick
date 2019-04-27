package net.dev.nickplugin.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.dev.nickplugin.api.NickManager;

import me.clip.deluxechat.events.DeluxeChatEvent;

public class DeluxeChatHookListener implements Listener {

	@EventHandler
	public void onDeluxeChat(DeluxeChatEvent e) {
		Player p = e.getPlayer();
		NickManager api = new NickManager(p);
		
		if(api.isNicked())
			e.setCancelled(true);
	}

}
