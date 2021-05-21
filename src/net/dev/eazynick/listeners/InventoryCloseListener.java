package net.dev.eazynick.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryCloseEvent;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.utilities.Utils;

public class InventoryCloseListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInventoryClose(InventoryCloseEvent event) {
		Utils utils = EazyNick.getInstance().getUtils();
		
		if (event.getPlayer() instanceof Player) {
			Player player = (Player) event.getPlayer();
			
			if(utils.getNickNameListPages().containsKey(player.getUniqueId()))
				utils.getNickNameListPages().remove(player.getUniqueId());
		}
	}

}
