package com.justixdev.eazynick.utilities;

import com.justixdev.eazynick.EazyNick;
import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

	private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("&#([A-Fa-f\\d]{3,6})");
	private static final char COLOR_CHAR = ChatColor.COLOR_CHAR;

	private String string;
	
	public StringUtils(String string) {
		this.string = string;
	}

	public String getPureString() {
		return ChatColor.stripColor(getColoredString());
	}

	public String getColoredString() {
		String version = EazyNick.getInstance().getVersion(), coloredString = ChatColor.translateAlternateColorCodes('&', string);

		// HEX-Color-Support
		if(version.startsWith("1_16") || version.startsWith("1_17") || version.startsWith("1_18")) {
			Matcher matcher = HEX_COLOR_PATTERN.matcher(string);
			StringBuffer buffer = new StringBuffer(string.length() + 4 * 8);

			while (matcher.find()) {
				String group = matcher.group(1);

				// Convert for example #fff to #ffffff
				if(group.length() == 3)
					matcher.appendReplacement(buffer, COLOR_CHAR + "x" + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1) + COLOR_CHAR + group.charAt(1) + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(2));
				else
					matcher.appendReplacement(buffer, COLOR_CHAR + "x" + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1) + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3) + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5));
			}

			string = matcher.appendTail(buffer).toString();
		}

		return coloredString;
	}
	
}
