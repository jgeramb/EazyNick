package net.dev.eazynick.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.PlayerDeathEvent;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.NickManager;

public class PlayerDeathListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();

		NickManager api = new NickManager(player);
		String deathMessage = ((event.getDeathMessage() == null) || event.getDeathMessage().isEmpty()) ? null : event.getDeathMessage();

		if (deathMessage != null) {
			if (api.isNicked()) {
				if (!(EazyNick.getInstance().getSetupYamlFile().getConfiguration().getBoolean("SeeNickSelf"))) {
					event.setDeathMessage(null);

					for (Player currentPlayer : Bukkit.getOnlinePlayers()) {
						if (currentPlayer != player)
							currentPlayer.sendMessage(deathMessage);
						else
							currentPlayer.sendMessage(deathMessage.replace(api.getNickFormat(), api.getOldDisplayName()));
					}
				}
			}
		}
	}

}
