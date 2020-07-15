package net.dev.eazynick.utils;

import org.bukkit.entity.Player;

import net.dev.eazynick.EazyNick;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class ActionBarUtils {
	
	public void sendActionBar(Player p, String text) {
		EazyNick eazyNick = EazyNick.getInstance();
		ReflectUtils reflectUtils = eazyNick.getReflectUtils();
		
		try {
			if (eazyNick.getVersion().startsWith("1_7_") || eazyNick.getVersion().startsWith("1_8_")) {
				Class<?> chatSerializer = reflectUtils.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0];

				sendPacket(p, reflectUtils.getNMSClass("PacketPlayOutChat").getConstructor(reflectUtils.getNMSClass("IChatBaseComponent"), byte.class).newInstance(chatSerializer.getMethod("a", String.class).invoke(chatSerializer, "{\"text\":\"" + text + "\"}", (byte) 2)));
			} else
				p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(text));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void sendPacket(Player p, Object packet) {
		if(packet == null)
			return;
		
		try {
			Object handle = p.getClass().getMethod("getHandle").invoke(p);
			Object playerConnection = handle.getClass().getDeclaredField("playerConnection").get(handle);
			playerConnection.getClass().getMethod("sendPacket", EazyNick.getInstance().getReflectUtils().getNMSClass("Packet")).invoke(playerConnection, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
