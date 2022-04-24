package net.dev.eazynick.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import net.dev.eazynick.EazyNick;

public class ServerListPingListener implements Listener {

	@EventHandler
	public void onServerListPing(ServerListPingEvent event) {
		try {
			Object outgoingPacketInjector = EazyNick.getInstance().getOutgoingPacketInjector();
			outgoingPacketInjector.getClass().getMethod("init").invoke(outgoingPacketInjector);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
}
