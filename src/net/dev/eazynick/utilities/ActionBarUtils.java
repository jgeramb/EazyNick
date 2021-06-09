package net.dev.eazynick.utilities;

import org.bukkit.entity.Player;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.nms.ReflectionHelper;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class ActionBarUtils {
	
	public void sendActionBar(Player player, String text) {
		EazyNick eazyNick = EazyNick.getInstance();
		ReflectionHelper reflectionHelper = eazyNick.getReflectionHelper();
		
		try {
			//Send action bar message
			if (eazyNick.getVersion().startsWith("1_7_") || eazyNick.getVersion().startsWith("1_8_")) {
				Class<?> chatSerializer = reflectionHelper.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0];

				sendPacket(player, reflectionHelper.getNMSClass("PacketPlayOutChat").getConstructor(reflectionHelper.getNMSClass("IChatBaseComponent"), byte.class).newInstance(chatSerializer.getMethod("a", String.class).invoke(chatSerializer, "{\"text\":\"" + text + "\"}"), (byte) 2));
			} else
				player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(text));
		} catch (Exception ignore) {
		}
	}
	
	private void sendPacket(Player player, Object packet) {
		if(packet == null)
			return;
		
		try {
			//Send packet via reflections
			Object handle = player.getClass().getMethod("getHandle").invoke(player);
			Object playerConnection = handle.getClass().getDeclaredField("playerConnection").get(handle);
			playerConnection.getClass().getMethod("sendPacket", EazyNick.getInstance().getReflectionHelper().getNMSClass("Packet")).invoke(playerConnection, packet);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
