package net.dev.nickplugin.utils;

import org.bukkit.ChatColor;

public class StringUtils {

	private String string;
	
	public StringUtils(String string) {
		this.string = string;
	}
	
	public boolean isColoredString() {
		return (getColoredString().contains("§"));
	}

	public StringUtils translateColorCodes() {
		string = ChatColor.translateAlternateColorCodes('&', string);
		
		return this;
	}
	
	public StringUtils removeColorCodes() {
		string = ChatColor.stripColor(getColoredString());
		
		return this;
	}
	
	public String getString() {
		return string;
	}
	
	private String getColoredString() {
		return (string.replace("&", "§"));
	}
	
}
