package net.dev.nickplugin.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerNickEvent extends Event implements Cancellable {

	private static HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	private Player p;
	private String nickName, skinName, chatPrefix, chatSuffix, tabPrefix, tabSuffix, tagPrefix, tagSuffix, groupName;
	private boolean isBungeeOrJoinNick, isRenick;
	
	public PlayerNickEvent(Player p, String nickName, String skinName, String chatPrefix, String chatSuffix, String tabPrefix, String tabSuffix, String tagPrefix, String tagSuffix, boolean isBungeeOrJoinNick, boolean isRenick, String groupName) {
		this.p = p;
		
		this.nickName = nickName;
		this.skinName = skinName;
		
		this.chatPrefix = chatPrefix;
		this.chatSuffix = chatSuffix;
		this.tabPrefix = tabPrefix;
		this.tabSuffix = tabSuffix;
		this.tagPrefix = tagPrefix;
		this.tagSuffix = tagSuffix;
		
		this.isBungeeOrJoinNick = isBungeeOrJoinNick;
		this.isRenick = isRenick;
		
		this.groupName = groupName;
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public String getNickName() {
		return nickName;
	}
	
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
	public String getSkinName() {
		return skinName;
	}
	
	public void setSkinName(String skinName) {
		this.skinName = skinName;
	}
	
	public String getChatPrefix() {
		return chatPrefix;
	}

	public void setChatPrefix(String chatPrefix) {
		this.chatPrefix = chatPrefix;
	}

	public String getChatSuffix() {
		return chatSuffix;
	}

	public void setChatSuffix(String chatSuffix) {
		this.chatSuffix = chatSuffix;
	}

	public String getTabPrefix() {
		return tabPrefix;
	}

	public void setTabPrefix(String tabPrefix) {
		this.tabPrefix = tabPrefix;
	}

	public String getTabSuffix() {
		return tabSuffix;
	}

	public void setTabSuffix(String tabSuffix) {
		this.tabSuffix = tabSuffix;
	}

	public String getTagPrefix() {
		return tagPrefix;
	}

	public void setTagPrefix(String tagPrefix) {
		this.tagPrefix = tagPrefix;
	}

	public String getTagSuffix() {
		return tagSuffix;
	}

	public void setTagSuffix(String tagSuffix) {
		this.tagSuffix = tagSuffix;
	}
	
	public boolean isJoinNick() {
		return isBungeeOrJoinNick;
	}
	
	public boolean isRenick() {
		return isRenick;
	}
	
	public String getGroupName() {
		return groupName;
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