package com.justixdev.eazynick.hooks;

import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.TablistFormatManager;
import me.neznamy.tab.api.team.TeamManager;
import org.bukkit.entity.Player;

public class TABHook {

	private static TabAPI api;
	
	private final Player player;
	
	public TABHook(Player player) {
		if(api == null)
			api = TabAPI.getInstance();
		
		this.player = player;
	}

	public void update(String tabPrefix, String tabSuffix, String tagPrefix, String tagSuffix, String groupName) {
		TeamManager teamManager = api.getTeamManager();
		TablistFormatManager tablistFormatManager = api.getTablistFormatManager();
		TabPlayer tabPlayer = api.getPlayer(player.getUniqueId());
		
		if((teamManager == null) || (tablistFormatManager == null) || (tabPlayer == null)) return;

		//Set temporarily nametag values
		teamManager.setPrefix(tabPlayer, tagPrefix);
		teamManager.setSuffix(tabPlayer, tagSuffix);

		//Set temporarily tablist values
		tablistFormatManager.setPrefix(tabPlayer, tabPrefix);
		tablistFormatManager.setSuffix(tabPlayer, tabSuffix);

		//Change group name
		tabPlayer.setTemporaryGroup(groupName);
	}
	
	public void reset() {
		TeamManager teamManager = api.getTeamManager();
		TablistFormatManager tablistFormatManager = api.getTablistFormatManager();
		TabPlayer tabPlayer = api.getPlayer(player.getUniqueId());

		if((teamManager == null) || (tablistFormatManager == null) || (tabPlayer == null)) return;

		//Unset temporarily nametag values
		teamManager.resetPrefix(tabPlayer);
		teamManager.resetSuffix(tabPlayer);

		//Unset temporarily tablist values
		tablistFormatManager.resetPrefix(tabPlayer);
		tablistFormatManager.resetSuffix(tabPlayer);

		//Reset group name
		tabPlayer.resetTemporaryGroup();
	}

}
