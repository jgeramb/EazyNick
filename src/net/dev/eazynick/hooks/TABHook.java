package net.dev.eazynick.hooks;

import org.bukkit.entity.Player;

import me.neznamy.tab.api.*;

public class TABHook {

	private Player player;
	
	public TABHook(Player player) {
		this.player = player;
	}

	public void update(String name, String tabPrefix, String tabSuffix, String tagPrefix, String tagSuffix, int sortID) {
		TabPlayer tabPlayer = TABAPI.getPlayer(player.getUniqueId());
		
		if(tabPlayer != null) {
			//Set temporarily tablist values
			tabPlayer.setValueTemporarily(EnumProperty.TABPREFIX, tabPrefix);
			tabPlayer.setValueTemporarily(EnumProperty.TABSUFFIX, tabSuffix);
			tabPlayer.setValueTemporarily(EnumProperty.CUSTOMTABNAME, name);
			
			if(!(tabPlayer.hasHiddenNametag())) {
				//Set temporarily nametag values
				tabPlayer.setValueTemporarily(EnumProperty.TAGPREFIX, tagPrefix);
				tabPlayer.setValueTemporarily(EnumProperty.TAGSUFFIX, tagSuffix);
				
				try {
					tabPlayer.setValueTemporarily(EnumProperty.CUSTOMTAGNAME, name);
				} catch (IllegalStateException ignore) {
				}
			}
			
			//Update TAB team
			String teamName = sortID + name;
			
			if(teamName.length() > 16)
				teamName = teamName.substring(0, 16);
			
			tabPlayer.forceTeamName(teamName);
		}
	}
	
	public void reset() {
		TabPlayer tabPlayer = TABAPI.getPlayer(player.getUniqueId());
		
		if(tabPlayer != null) {
			//Reset temporarily tablist values
			tabPlayer.removeTemporaryValue(EnumProperty.TABPREFIX);
			tabPlayer.removeTemporaryValue(EnumProperty.TABSUFFIX);
			tabPlayer.removeTemporaryValue(EnumProperty.CUSTOMTABNAME);
			
			if(!(tabPlayer.hasHiddenNametag())) {
				//Reset temporarily nametag values
				tabPlayer.removeTemporaryValue(EnumProperty.TAGPREFIX);
				tabPlayer.removeTemporaryValue(EnumProperty.TAGSUFFIX);
				
				try {
					if(tabPlayer.hasTemporaryValue(EnumProperty.CUSTOMTAGNAME))
						tabPlayer.removeTemporaryValue(EnumProperty.CUSTOMTAGNAME);
				} catch (NullPointerException ignore) {
				}
			}
			
			//Reset TAB team
			tabPlayer.forceTeamName(null);
		}
	}

}
