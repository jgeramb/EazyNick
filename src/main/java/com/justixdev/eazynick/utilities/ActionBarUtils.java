package com.justixdev.eazynick.utilities;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import static com.justixdev.eazynick.nms.ReflectionHelper.*;

public class ActionBarUtils {

    public void sendActionBar(Player player, String text) {
        try {
            // Send action bar message
            if (NMS_VERSION.startsWith("v1_7") || NMS_VERSION.startsWith("v1_8")) {
                sendPacketNMS(
                        player,
                        newInstance(
                                getNMSClass("PacketPlayOutChat"),
                                types(getNMSClass("IChatBaseComponent"), byte.class),
                                invokeStatic(
                                        getNMSClass("IChatBaseComponent").getDeclaredClasses()[0],
                                        "a",
                                        types(String.class),
                                        "{\"text\":\"" + text + "\")"),
                                (byte) 2));
            } else
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(text));
        } catch (Exception ignore) {
        }
    }

}
