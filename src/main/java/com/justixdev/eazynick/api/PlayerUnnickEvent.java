package com.justixdev.eazynick.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerUnnickEvent extends Event implements Cancellable {

	private static final HandlerList HANDLER_LIST = new HandlerList();
	private boolean cancelled = false;
	private final Player player;
	
	public PlayerUnnickEvent(Player player) {
		this.player = player;
	}
	
	public Player getPlayer() {
		return player;
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
	public @NotNull HandlerList getHandlers() {
		return HANDLER_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}

}