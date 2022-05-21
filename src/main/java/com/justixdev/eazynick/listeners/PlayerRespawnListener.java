package com.justixdev.eazynick.listeners;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.api.NickManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawnListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		NickManager api = new NickManager(player);
		
		if(api.isNicked() && !(EazyNick.getInstance().getSetupYamlFile().getConfiguration().getBoolean("KeepNickOnDeath")))
			api.unnickPlayerWithoutRemovingMySQL(false, true);
	}

}
