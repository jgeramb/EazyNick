package net.dev.eazynick.hooks;

import org.bukkit.entity.Player;

import me.neznamy.tab.api.EnumProperty;
import me.neznamy.tab.api.TABAPI;
import me.neznamy.tab.api.TabPlayer;

public class TABHook {

	private Player p;
	
	public TABHook(Player p) {
		this.p = p;
	}

	public void update(String name, String tabPrefix, String tabSuffix, String tagPrefix, String tagSuffix) {
		TabPlayer tabPlayer = TABAPI.getPlayer(p.getUniqueId());
		
		if(!(tabPlayer.hasHiddenNametag())) {
			tabPlayer.setValueTemporarily(EnumProperty.TAGPREFIX, tagPrefix);
			tabPlayer.setValueTemporarily(EnumProperty.TAGSUFFIX, tagSuffix);
		}
		
		tabPlayer.setValueTemporarily(EnumProperty.TABPREFIX, tabPrefix);
		tabPlayer.setValueTemporarily(EnumProperty.TABSUFFIX, tabSuffix);
		tabPlayer.forceRefresh();
	}
	
	public void reset() {
		TabPlayer tabPlayer = TABAPI.getPlayer(p.getUniqueId());
		tabPlayer.removeTemporaryValue(EnumProperty.TABPREFIX);
		tabPlayer.removeTemporaryValue(EnumProperty.TABSUFFIX);
		tabPlayer.removeTemporaryValue(EnumProperty.TAGPREFIX);
		tabPlayer.removeTemporaryValue(EnumProperty.TAGSUFFIX);
	}

}
