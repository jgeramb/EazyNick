package com.justixdev.eazynick.api.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class PlayerNickEvent extends Event implements Cancellable {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private boolean cancelled = false;
    private final Player player;
    private String nickName, skinName, chatPrefix, chatSuffix, tabPrefix, tabSuffix, tagPrefix, tagSuffix, groupName;
    private UUID spoofedUniqueId;
    private final boolean isBungeeOrJoinNick, isRenick;
    private int sortID;

    public PlayerNickEvent(Player player, String nickName, String skinName, UUID spoofedUniqueId, String chatPrefix, String chatSuffix, String tabPrefix, String tabSuffix, String tagPrefix, String tagSuffix, boolean isBungeeOrJoinNick, boolean isRenick, int sortID, String groupName) {
        this.player = player;
        this.nickName = nickName;
        this.skinName = skinName;
        this.spoofedUniqueId = spoofedUniqueId;
        this.chatPrefix = chatPrefix;
        this.chatSuffix = chatSuffix;
        this.tabPrefix = tabPrefix;
        this.tabSuffix = tabSuffix;
        this.tagPrefix = tagPrefix;
        this.tagSuffix = tagSuffix;
        this.isBungeeOrJoinNick = isBungeeOrJoinNick;
        this.isRenick = isRenick;
        this.sortID = sortID;
        this.groupName = groupName;
    }

    public UUID getSpoofedUniqueId() {
        return this.spoofedUniqueId != null
                ? this.spoofedUniqueId
                : this.player.getUniqueId();
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}