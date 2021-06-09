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
		if(EazyNick.getInstance().getVersion().equals("1_7_R4"))
			new OutgoingPacketInjector_1_7().init();
		else
			new OutgoingPacketInjector().init();
	}
	
}
