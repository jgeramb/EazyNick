package net.dev.eazynick.hooks;

import org.bukkit.entity.Player;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.utils.Utils;

import me.neznamy.tab.api.EnumProperty;
import me.neznamy.tab.api.TABAPI;
import me.neznamy.tab.api.TabPlayer;

public class TABHook {

	private Player p;
	
	public TABHook(Player p) {
		this.p = p;
	}

	public void update(String name, String tabPrefix, String tabSuffix, String tagPrefix, String tagSuffix, int sortID) {
		TabPlayer tabPlayer = TABAPI.getPlayer(p.getUniqueId());
		
		tabPlayer.setValueTemporarily(EnumProperty.TABPREFIX, tabPrefix);
		tabPlayer.setValueTemporarily(EnumProperty.TABSUFFIX, tabSuffix);
		tabPlayer.setValueTemporarily(EnumProperty.CUSTOMTABNAME, name);
		
		if(!(tabPlayer.hasHiddenNametag())) {
			tabPlayer.setValueTemporarily(EnumProperty.TAGPREFIX, tagPrefix);
			tabPlayer.setValueTemporarily(EnumProperty.TAGSUFFIX, tagSuffix);
			tabPlayer.setValueTemporarily(EnumProperty.CUSTOMTAGNAME, name);
		}
		
		EazyNick.getInstance().getUtils().getTABTeams().put(p.getUniqueId(), tabPlayer.getTeamName());
		
		String teamName = sortID + name;
		
		if(teamName.length() > 16)
			teamName = teamName.substring(0, 16);
		
		tabPlayer.setTeamName(teamName);
		tabPlayer.forceRefresh();
	}
	
	public void reset() {
		Utils utils = EazyNick.getInstance().getUtils();
		
		TabPlayer tabPlayer = TABAPI.getPlayer(p.getUniqueId());
		tabPlayer.removeTemporaryValue(EnumProperty.TABPREFIX);
		tabPlayer.removeTemporaryValue(EnumProperty.TABSUFFIX);
		tabPlayer.removeTemporaryValue(EnumProperty.CUSTOMTABNAME);
		tabPlayer.removeTemporaryValue(EnumProperty.TAGPREFIX);
		tabPlayer.removeTemporaryValue(EnumProperty.TAGSUFFIX);
		tabPlayer.removeTemporaryValue(EnumProperty.CUSTOMTAGNAME);
		tabPlayer.setTeamName(utils.getTABTeams().get(p.getUniqueId()));
		tabPlayer.forceRefresh();
		
		utils.getTABTeams().remove(p.getUniqueId());
	}

}
