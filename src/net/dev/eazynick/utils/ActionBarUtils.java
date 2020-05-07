package net.dev.eazynick.utils;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.dev.eazynick.EazyNick;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class ActionBarUtils {
	
	public void sendActionBar(Player p, String text, int time) {
		EazyNick eazyNick = EazyNick.getInstance();
		ReflectUtils reflectUtils = eazyNick.getReflectUtils();
		
		try {
			if (eazyNick.getVersion().startsWith("1_7_") || eazyNick.getVersion().startsWith("1_8_")) {
				Class<?> mainChatPacket = reflectUtils.getNMSClass("PacketPlayOutChat");
				Class<?> chatSerializer = reflectUtils.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0];

				Object chatText = chatSerializer.getMethod("a", new Class[] { String.class }).invoke(chatSerializer, new Object[] { "{\"text\":\"" + text + "\"}" });
				Object chatPacket = mainChatPacket.getConstructor(new Class[] { reflectUtils.getNMSClass("IChatBaseComponent"), byte.class }).newInstance(new Object[] { chatText, (byte) 2 });

				new BukkitRunnable() {

					int i = 0;

					@Override
					public void run() {
						i++;

						if ((i == time) || !(p.isOnline()))
							cancel();
						else
							sendPacket(p, chatPacket);
					}
				}.runTaskTimer(eazyNick, 1, 1);
			} else
				p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(text));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendActionBar(Player p, String text) {
		sendActionBar(p, text, 100);
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
