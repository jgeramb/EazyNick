package net.dev.eazynick.hooks;

import org.bukkit.entity.Player;

import me.neznamy.tab.api.EnumProperty;
import me.neznamy.tab.api.TABAPI;

public class TABHook {

	private Player p;
	
	public TABHook(Player p) {
		this.p = p;
	}

	public void update(String name, String tabPrefix, String tabSuffix, String tagPrefix, String tagSuffix) {
		if(!(TABAPI.hasHiddenNametag(p.getUniqueId()))) {
			TABAPI.setValueTemporarily(p.getUniqueId(), EnumProperty.TAGPREFIX, tagPrefix);
			TABAPI.setValueTemporarily(p.getUniqueId(), EnumProperty.TAGSUFFIX, tagSuffix);
		}
		
		TABAPI.setValueTemporarily(p.getUniqueId(), EnumProperty.TABPREFIX, tabPrefix);
		TABAPI.setValueTemporarily(p.getUniqueId(), EnumProperty.TABSUFFIX, tabSuffix);
	}
	
	public void reset() {
		TABAPI.removeTemporaryValue(p.getUniqueId(), EnumProperty.TABPREFIX);
		TABAPI.removeTemporaryValue(p.getUniqueId(), EnumProperty.TABSUFFIX);
		TABAPI.removeTemporaryValue(p.getUniqueId(), EnumProperty.TAGPREFIX);
		TABAPI.removeTemporaryValue(p.getUniqueId(), EnumProperty.TAGSUFFIX);
	}

}
