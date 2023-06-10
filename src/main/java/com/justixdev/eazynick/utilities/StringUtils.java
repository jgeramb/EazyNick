package com.justixdev.eazynick.utilities;

import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.justixdev.eazynick.nms.ReflectionHelper.NMS_VERSION;

public class StringUtils {

    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("&#([A-Fa-f\\d]{3,6})");
    private static final char COLOR_CHAR = ChatColor.COLOR_CHAR;

    private String string;

    public StringUtils(String string) {
        this.string = string;
    }

    public String repeat(int count) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < count; i++)
            builder.append(this.string);

        return builder.toString();
    }

    public String getPureString() {
        return ChatColor.stripColor(getColoredString());
    }

    public String getColoredString() {
        String version = NMS_VERSION;

        // HEX-Color-Support
        if(version.startsWith("v1_16")
                || version.startsWith("v1_17")
                || version.startsWith("v1_18")
                || version.startsWith("v1_19")
                || version.startsWith("v1_20")) {
            Matcher matcher = HEX_COLOR_PATTERN.matcher(this.string);
            StringBuffer buffer = new StringBuffer(this.string.length() + 4 * 8);

            while (matcher.find()) {
                String group = matcher.group(1);

                // Convert #fff to #ffffff
                if(group.length() == 3)
                    matcher.appendReplacement(
                            buffer,
                            COLOR_CHAR + "x"
                                    + COLOR_CHAR + group.charAt(0)
                                    + COLOR_CHAR + group.charAt(0)
                                    + COLOR_CHAR + group.charAt(1)
                                    + COLOR_CHAR + group.charAt(1)
                                    + COLOR_CHAR + group.charAt(2)
                                    + COLOR_CHAR + group.charAt(2));
                else
                    matcher.appendReplacement(
                            buffer,
                            COLOR_CHAR + "x"
                                    + COLOR_CHAR + group.charAt(0)
                                    + COLOR_CHAR + group.charAt(1)
                                    + COLOR_CHAR + group.charAt(2)
                                    + COLOR_CHAR + group.charAt(3)
                                    + COLOR_CHAR + group.charAt(4)
                                    + COLOR_CHAR + group.charAt(5));
            }

            this.string = matcher.appendTail(buffer).toString();
        }

        return ChatColor.translateAlternateColorCodes('&', this.string);
    }

}
