package net.dev.eazynick.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.NickManager;
import net.dev.eazynick.api.PlayerUnnickEvent;

public class PlayerUnnickListener implements Listener {

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerUnnick(PlayerUnnickEvent e) {
		EazyNick eazyNick = EazyNick.getInstance();
		
		if(!(e.isCancelled())) {
			Player p = e.getPlayer();
			String name = p.getName();
	
			new NickManager(p).unnickPlayer();
			
			if(eazyNick.getFileUtils().getConfig().getBoolean("LogNicknames"))
				eazyNick.getUtils().sendConsole("§6" + name + " §7(" + p.getUniqueId().toString() + ") §4reset his nickname to §a" + p.getName());
			
			p.sendMessage(eazyNick.getUtils().getPrefix() + eazyNick.getLanguageFileUtils().getConfigString(p, "Messages.Unnick"));
		}
	}

}
