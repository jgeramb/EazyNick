package net.dev.nickplugin.utils.bookUtils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.dev.nickplugin.NickPlugin;
import net.dev.nickplugin.utils.ReflectUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class NMSBookUtils extends ReflectUtils {

	public static void open(Player p, ItemStack book) {
		ItemStack hand = p.getItemInHand();
		
		try {
			Object entityPlayer = p.getClass().getMethod("getHandle").invoke(p);
			Class<?> craftItemStackClass = getCraftClass("inventory.CraftItemStack");
			Object nmsItemStack = craftItemStackClass.getMethod("asNMSCopy", ItemStack.class).invoke(craftItemStackClass, book);
			
			p.setItemInHand(book);
			
			if(!(NickPlugin.version.startsWith("1_8"))) {
				Class<?> enumHand = getNMSClass("EnumHand");
				Object mainHand = getField(enumHand, "MAIN_HAND").get(enumHand);
				
				if(Bukkit.getVersion().contains("1.14.4")) {
					Class<?> itemWrittenBook = getNMSClass("ItemWrittenBook");
					
					if ((boolean) itemWrittenBook.getMethod("a", getNMSClass("ItemStack"), getNMSClass("CommandListenerWrapper"), getNMSClass("EntityHuman")).invoke(itemWrittenBook, nmsItemStack, entityPlayer.getClass().getMethod("getCommandListener").invoke(entityPlayer), entityPlayer)) {
						Object activeContainer = entityPlayer.getClass().getField("activeContainer").get(entityPlayer);
						
		                activeContainer.getClass().getMethod("c").invoke(activeContainer);
		            }
					
		            Object packet = getNMSClass("PacketPlayOutOpenBook").getConstructor(enumHand).newInstance(mainHand);
					Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
					playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
				} else {
					entityPlayer.getClass().getMethod("a", getNMSClass("ItemStack"), enumHand).invoke(entityPlayer, nmsItemStack, mainHand);
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
