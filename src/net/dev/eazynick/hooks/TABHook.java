package net.dev.eazynick.hooks;

import org.bukkit.entity.Player;

import me.neznamy.tab.api.*;
import me.neznamy.tab.api.team.TeamManager;

public class TABHook {

	private static TabAPI api;
	
	private Player player;
	
	public TABHook(Player player) {
		if(api == null)
			api = TabAPI.getInstance();
		
		this.player = player;
	}

	public void update(String name, String tabPrefix, String tabSuffix, String tagPrefix, String tagSuffix, String groupName) {
		TeamManager teamManager = api.getTeamManager();
		TablistFormatManager tablistFormatManager = api.getTablistFormatManager();
		TabPlayer tabPlayer = api.getPlayer(player.getUniqueId());
		
		if(tabPlayer != null) {
			//Set temporarily nametag values
			teamManager.setPrefix(tabPlayer, tagPrefix);
			teamManager.setSuffix(tabPlayer, tagSuffix);
			
			//Set temporarily tablist values
			tablistFormatManager.setPrefix(tabPlayer, tabPrefix);
			tablistFormatManager.setSuffix(tabPlayer, tabSuffix);
		}
	}
	
	public void reset() {
		TeamManager teamManager = api.getTeamManager();
		TablistFormatManager tablistFormatManager = api.getTablistFormatManager();
		TabPlayer tabPlayer = api.getPlayer(player.getUniqueId());
		
		if(tabPlayer != null) {
			//Unset temporarily nametag values
			teamManager.resetPrefix(tabPlayer);
			teamManager.resetSuffix(tabPlayer);
			
			//Unset temporarily tablist values
			tablistFormatManager.resetPrefix(tabPlayer);
			tablistFormatManager.resetSuffix(tabPlayer);
		}
	}

}
