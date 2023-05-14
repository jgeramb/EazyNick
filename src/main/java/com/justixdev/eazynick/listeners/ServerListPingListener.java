package com.justixdev.eazynick.listeners;

import com.justixdev.eazynick.EazyNick;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerListPingListener implements Listener {

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        EazyNick.getInstance().getPacketInjectorManager().inject(event.getAddress());
    }

}
