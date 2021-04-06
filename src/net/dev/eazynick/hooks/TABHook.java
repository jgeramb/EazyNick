package net.dev.eazynick.hooks;

import org.bukkit.entity.Player;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.utilities.Utils;

import me.neznamy.tab.api.*;

public class TABHook {

	private Player player;
	
	public TABHook(Player player) {
		this.player = player;
	}

	public void update(String name, String tabPrefix, String tabSuffix, String tagPrefix, String tagSuffix, int sortID) {
		TabPlayer tabPlayer = TABAPI.getPlayer(player.getUniqueId());
		
		if(tabPlayer != null) {
			tabPlayer.setValueTemporarily(EnumProperty.TABPREFIX, tabPrefix);
			tabPlayer.setValueTemporarily(EnumProperty.TABSUFFIX, tabSuffix);
			tabPlayer.setValueTemporarily(EnumProperty.CUSTOMTABNAME, name);
			
			if(!(tabPlayer.hasHiddenNametag())) {
				tabPlayer.setValueTemporarily(EnumProperty.TAGPREFIX, tagPrefix);
				tabPlayer.setValueTemporarily(EnumProperty.TAGSUFFIX, tagSuffix);
				
				try {
					tabPlayer.setValueTemporarily(EnumProperty.CUSTOMTAGNAME, name);
				} catch (IllegalStateException ex) {
				}
			}
			
			EazyNick.getInstance().getUtils().getTABTeams().put(player.getUniqueId(), tabPlayer.getTeamName());
			
			String teamName = sortID + name;
			
			if(teamName.length() > 16)
				teamName = teamName.substring(0, 16);
			
			tabPlayer.setTeamName(teamName);
			tabPlayer.forceRefresh();
		}
	}
	
	public void reset() {
		Utils utils = EazyNick.getInstance().getUtils();
		
		TabPlayer tabPlayer = TABAPI.getPlayer(player.getUniqueId());
		
		try {
			if(tabPlayer != null) {
				tabPlayer.removeTemporaryValue(EnumProperty.TABPREFIX);
				tabPlayer.removeTemporaryValue(EnumProperty.TABSUFFIX);
				tabPlayer.removeTemporaryValue(EnumProperty.CUSTOMTABNAME);
				tabPlayer.removeTemporaryValue(EnumProperty.TAGPREFIX);
				tabPlayer.removeTemporaryValue(EnumProperty.TAGSUFFIX);
				
				if(tabPlayer.hasTemporaryValue(EnumProperty.CUSTOMTAGNAME))
					tabPlayer.removeTemporaryValue(EnumProperty.CUSTOMTAGNAME);
				
				tabPlayer.setTeamName(utils.getTABTeams().get(player.getUniqueId()));
				tabPlayer.forceRefresh();
			}
		} catch (NullPointerException ex) {
		}
		
		utils.getTABTeams().remove(player.getUniqueId());
	}

}
