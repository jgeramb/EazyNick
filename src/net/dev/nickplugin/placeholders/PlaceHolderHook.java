package net.dev.nickplugin.placeholders;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.dev.nickplugin.utils.NickManager;

import me.clip.placeholderapi.external.EZPlaceholderHook;

public class PlaceHolderHook extends EZPlaceholderHook {
	
	public PlaceHolderHook(Plugin plugin) {
		super(plugin, plugin.getName().toLowerCase());
	}

	@Override
	public String onPlaceholderRequest(Player p, String identifier) {
		if(p != null) {
			if(identifier.equals("is_nicked") || identifier.equals("is_disguised"))
				return String.valueOf(new NickManager(p).isNicked());
			
			if(identifier.equals("display_name"))
				return p.getName();
		}
		
		return null;
	}
	
}
