package net.dev.nickplugin.utils.bookUtils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.dev.nickplugin.main.Main;
import net.dev.nickplugin.utils.ReflectUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class NMSBookUtils extends ReflectUtils {

	public static void open(Player p, ItemStack book) {
		ItemStack hand = p.getItemInHand();
		
		try {
			Object entityPlayer = p.getClass().getMethod("getHandle").invoke(p);
			Object craftItemStack = getCraftClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(getCraftClass("inventory.CraftItemStack"), book);
			
			p.setItemInHand(book);
			
			if(!(Main.version.startsWith("1_8"))) {
				Class<?> enumHand = getNMSClass("EnumHand");
				Object mainHand = getField(enumHand, "MAIN_HAND").get(enumHand);
				
				if(Bukkit.getVersion().contains("1.14.4")) {
					Class<?> itemWrittenBook = getNMSClass("ItemWrittenBook");
					
					if ((boolean) itemWrittenBook.getMethod("a").invoke(itemWrittenBook, craftItemStack, entityPlayer.getClass().getMethod("getCommandListener").invoke(entityPlayer), entityPlayer)) {
						Object activeContainer = entityPlayer.getClass().getField("activeContainer").get(entityPlayer);
						
		                activeContainer.getClass().getMethod("c").invoke(activeContainer);
		            }
					
		            Object packet = getNMSClass("PacketPlayOutOpenBook").getConstructor(enumHand).newInstance(mainHand);
					Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
					playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
				} else {
					entityPlayer.getClass().getMethod("a", getNMSClass("ItemStack"), enumHand).invoke(entityPlayer, craftItemStack, mainHand);
				}
			} else {
				Object packet = getNMSClass("PacketPlayOutCustomPayload").getConstructor(String.class, getNMSClass("PacketDataSerializer")).newInstance("MC|BOpen", getNMSClass("PacketDataSerializer").getConstructor(ByteBuf.class).newInstance(Unpooled.buffer()));
				Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
				playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			p.setItemInHand(hand);
		}
	}
}
