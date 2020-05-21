package net.dev.eazynick.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.NickManager;

public class PlayerDeathListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();

		NickManager api = new NickManager(p);
		String deathMessage = (e.getDeathMessage() != null && e.getDeathMessage() != "") ? e.getDeathMessage() : null;

		if (deathMessage != null) {
			if (api.isNicked()) {
				if (!(EazyNick.getInstance().getFileUtils().getConfig().getBoolean("SeeNickSelf"))) {
					e.setDeathMessage(null);

					for (Player all : Bukkit.getOnlinePlayers()) {
						if (all != p)
							all.sendMessage(deathMessage);
						else {
							String msg = deathMessage;
							msg = msg.replace(api.getNickFormat(), api.getOldDisplayName());

							all.sendMessage(msg);
						}
					}
				}
			}
		}
	}

}
