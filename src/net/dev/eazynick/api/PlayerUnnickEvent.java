package net.dev.eazynick.api;

import org.bukkit.entity.Player;
import org.bukkit.event.*;

public class PlayerUnnickEvent extends Event implements Cancellable {

	private static HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	private Player player;
	
	public PlayerUnnickEvent(Player player) {
		this.player = player;
	}
	
	public Player getPlayer() {
		return player;
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