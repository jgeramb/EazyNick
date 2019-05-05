package net.dev.nickplugin.placeholders;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.dev.nickplugin.api.NickManager;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PlaceHolderExpansion extends PlaceholderExpansion {
	
	private Plugin plugin;
	
	public PlaceHolderExpansion(Plugin plugin) {
		this.plugin = plugin;
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
	
	@Override
	public String getIdentifier() {
		return plugin.getDescription().getName();
	}
	
	@Override
	public boolean canRegister() {
		return true;
	}
	
	@Override
	public String getVersion() {
		return plugin.getDescription().getVersion();
	}
	
	@Override
	public String getAuthor() {
		return plugin.getDescription().getAuthors().toString().replace("[", "").replace("]", "");
	}
	
}
