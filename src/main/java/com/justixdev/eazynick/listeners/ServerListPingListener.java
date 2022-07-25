package com.justixdev.eazynick.listeners;

import com.justixdev.eazynick.EazyNick;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerListPingListener implements Listener {

	@EventHandler
	public void onServerListPing(ServerListPingEvent event) {
		try {
			Object outgoingPacketInjector = EazyNick.getInstance().getOutgoingPacketInjector();

			if(outgoingPacketInjector != null)
				outgoingPacketInjector.getClass().getMethod("init").invoke(outgoingPacketInjector);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
}
