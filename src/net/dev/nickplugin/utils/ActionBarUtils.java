package net.dev.nickplugin.utils;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.dev.nickplugin.main.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class ActionBarUtils {
	
	public static void sendActionBar(Player p, String text, int time) {
		try {
			if (ReflectUtils.getVersion().contains("v1_7_") || ReflectUtils.getVersion().contains("v1_8_")) {
				Class<?> mainChatPacket = ReflectUtils.getNMSClass("PacketPlayOutChat");
				Class<?> chatSerializer = ReflectUtils.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0];

				Object chatText = chatSerializer.getMethod("a", new Class[] { String.class }).invoke(chatSerializer, new Object[] { "{\"text\":\"" + text.replace("&", "§") + "\"}" });
				Object chatPacket = mainChatPacket.getConstructor(new Class[] { ReflectUtils.getNMSClass("IChatBaseComponent"), byte.class }).newInstance(new Object[] { chatText, (byte) 2 });

				new BukkitRunnable() {

					int i = 0;

					@Override
					public void run() {
						i++;

						if (i == time)
							cancel();
						else
							sendPacket(p, chatPacket);
					}
				}.runTaskTimer(Main.getPlugin(Main.class), 1, 1);
			} else {
				p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(text.replace("&", "§")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void sendActionBar(Player p, String text) {
		sendActionBar(p, text, 100);
	}
	
	private static void sendPacket(Player p, Object packet) {
		try {
			Object playerHandle = p.getClass().getMethod("getHandle", new Class[0]).invoke(p, new Object[0]);
			Object playerConnection = playerHandle.getClass().getField("playerConnection").get(playerHandle);
			playerConnection.getClass().getMethod("sendPacket", new Class[] { ReflectUtils.getNMSClass("Packet") }).invoke(playerConnection, new Object[] { packet });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
