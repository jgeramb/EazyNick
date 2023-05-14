package com.justixdev.eazynick.api.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@RequiredArgsConstructor
public class PlayerUnnickEvent extends Event implements Cancellable {

    @Getter
    private static final HandlerList handlerList = new HandlerList();
    private boolean cancelled = false;
    private final Player player;

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

}