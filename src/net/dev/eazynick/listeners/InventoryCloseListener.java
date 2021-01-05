package net.dev.eazynick.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryCloseEvent;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.utils.Utils;

public class InventoryCloseListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInventoryClose(InventoryCloseEvent e) {
		Utils utils = EazyNick.getInstance().getUtils();
		
		if (e.getPlayer() instanceof Player) {
			Player p = (Player) e.getPlayer();
			
			if(utils.getNickNameListPages().containsKey(p.getUniqueId()))
				utils.getNickNameListPages().remove(p.getUniqueId());
		}
	}

}
