package net.dev.nickplugin.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerUnnickEvent extends Event implements Cancellable {

	private static HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	private Player p;
	
	public PlayerUnnickEvent(Player p) {
		this.p = p;
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}
	
	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}