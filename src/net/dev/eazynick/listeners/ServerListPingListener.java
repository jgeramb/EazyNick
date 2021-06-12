package net.dev.eazynick.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.nms.netty.server.OutgoingPacketInjector;
import net.dev.eazynick.nms.netty.server.OutgoingPacketInjector_1_7;

public class ServerListPingListener implements Listener {

	@EventHandler
	public void onServerListPing(ServerListPingEvent event) {
		EazyNick eazyNick = EazyNick.getInstance();
		
		if(eazyNick.getVersion().equals("1_7_R4")) {
			((OutgoingPacketInjector_1_7) eazyNick.getOutgoingPacketInjector()).unregister();
			
			OutgoingPacketInjector_1_7 outgoingPacketInjector = new OutgoingPacketInjector_1_7();
			outgoingPacketInjector.init();
			
			eazyNick.setOutgoingPacketInjector(outgoingPacketInjector);
		} else {
			((OutgoingPacketInjector) eazyNick.getOutgoingPacketInjector()).unregister();
			
			OutgoingPacketInjector outgoingPacketInjector = new OutgoingPacketInjector();
			outgoingPacketInjector.init();
			
			eazyNick.setOutgoingPacketInjector(outgoingPacketInjector);
		}
	}
	
}
