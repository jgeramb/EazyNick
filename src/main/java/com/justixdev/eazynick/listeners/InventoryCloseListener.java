package com.justixdev.eazynick.listeners;

import com.justixdev.eazynick.EazyNick;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryCloseListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClose(InventoryCloseEvent event) {
        EazyNick
                .getInstance()
                .getUtils()
                .getNickNameListPages()
                .remove(event.getPlayer().getUniqueId());
    }

}
