package net.dev.eazynick.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerRespawnEvent;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.NickManager;

public class PlayerRespawnListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		NickManager api = new NickManager(player);
		
		if(api.isNicked() && !(EazyNick.getInstance().getSetupYamlFile().getConfiguration().getBoolean("KeepNickOnDeath")))
			api.unnickPlayerWithoutRemovingMySQL(false, true);
	}

}
