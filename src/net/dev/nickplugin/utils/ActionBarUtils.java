package net.dev.nickplugin.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.dev.nickplugin.main.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class ActionBarUtils {
	
	public static void sendActionBar(Player p, String text, int time) {
		try {
			if (Main.version.startsWith("1_7_") || Main.version.startsWith("1_8_") || Bukkit.getVersion().contains("1.14.3")) {
				Class<?> mainChatPacket = ReflectUtils.getNMSClass("PacketPlayOutChat");
				Class<?> chatSerializer = ReflectUtils.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0];

				Object chatText = chatSerializer.getMethod("a", new Class[] { String.class }).invoke(chatSerializer, new Object[] { "{\"text\":\"" + text + "\"}" });
				Object chatPacket = mainChatPacket.getConstructor(new Class[] { ReflectUtils.getNMSClass("IChatBaseComponent"), Bukkit.getVersion().contains("1.14.3") ? ChatMessageType.class : byte.class }).newInstance(new Object[] { chatText, Bukkit.getVersion().contains("1.14.3") ? ChatMessageType.ACTION_BAR : (byte) 2 });

				new BukkitRunnable() {

					int i = 0;

					@Override
					public void run() {
						i++;

						if ((i == time) || (p == null))
							cancel();
						else
							sendPacket(p, chatPacket);
					}
				}.runTaskTimer(Main.getInstance(), 1, 1);
			} else
				p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(text));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void sendActionBar(Player p, String text) {
		sendActionBar(p, text, 100);
	}
	
	private static void sendPacket(Player p, Object packet) {
		if(packet == null)
			return;
		
		try {
			Object handle = p.getClass().getMethod("getHandle").invoke(p);
			Object playerConnection = handle.getClass().getDeclaredField("playerConnection").get(handle);
			playerConnection.getClass().getMethod("sendPacket", ReflectUtils.getNMSClass("Packet")).invoke(playerConnection, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
