package com.justixdev.eazynick.listeners;

import com.justixdev.eazynick.EazyNick;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

public class ServerCommandListener implements Listener {

    @EventHandler
    public void onServerCommand(ServerCommandEvent event) {
        // Command system
        if(EazyNick.getInstance().getCommandManager().execute(event.getSender(), event.getCommand()))
            event.setCancelled(true);
    }

}
