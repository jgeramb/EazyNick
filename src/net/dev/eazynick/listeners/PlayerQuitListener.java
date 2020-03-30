package net.dev.eazynick.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.NickManager;
import net.dev.eazynick.utils.Utils;

public class PlayerQuitListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent e) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		
		Player p = e.getPlayer();
		NickManager api = new NickManager(p);

		if(utils.getNameCache().containsKey(p.getUniqueId()))
			utils.getNameCache().remove(p.getUniqueId());
		
		if (api.isNicked()) {
			if (eazyNick.getFileUtils().cfg.getBoolean("DisconnectUnnick"))
				api.unnickPlayerWithoutRemovingMySQL(true);
		}
	}

}
