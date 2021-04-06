package net.dev.eazynick.nms.fakegui.book;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.nms.ReflectionHelper;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class NMSBookUtils extends ReflectionHelper {

	public void open(Player player, ItemStack book) {
		EazyNick eazyNick = EazyNick.getInstance();
		
		ItemStack hand = player.getItemInHand();
		
		player.setItemInHand(book);
		
		Bukkit.getScheduler().runTaskLater(eazyNick, () -> {
			try {
				Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
				Class<?> craftItemStackClass = getCraftClass("inventory.CraftItemStack");
				Object nmsItemStack = craftItemStackClass.getMethod("asNMSCopy", ItemStack.class).invoke(null, book);
				
				if(!(eazyNick.getVersion().startsWith("1_7") || eazyNick.getVersion().startsWith("1_8"))) {
					Class<?> enumHand = getNMSClass("EnumHand");
					Object mainHand = getField(enumHand, "MAIN_HAND").get(enumHand);
					
					if(Bukkit.getVersion().contains("1.14.4") || eazyNick.getVersion().startsWith("1_15") || eazyNick.getVersion().startsWith("1_16")) {
						Class<?> itemWrittenBook = getNMSClass("ItemWrittenBook");
						
						if ((boolean) itemWrittenBook.getMethod("a", getNMSClass("ItemStack"), getNMSClass("CommandListenerWrapper"), getNMSClass("EntityHuman")).invoke(itemWrittenBook, nmsItemStack, entityPlayer.getClass().getMethod("getCommandListener").invoke(entityPlayer), entityPlayer)) {
							Object activeContainer = entityPlayer.getClass().getField("activeContainer").get(entityPlayer);
							
			                activeContainer.getClass().getMethod("c").invoke(activeContainer);
			            }
						
			            Object packet = getNMSClass("PacketPlayOutOpenBook").getConstructor(enumHand).newInstance(mainHand);
						Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
						playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
					} else
						entityPlayer.getClass().getMethod("a", getNMSClass("ItemStack"), enumHand).invoke(entityPlayer, nmsItemStack, mainHand);
				} else {
					Object packet;
					
					if(eazyNick.getVersion().startsWith("1_7"))
						packet = getNMSClass("PacketPlayOutCustomPayload").getConstructor(String.class, net.minecraft.util.io.netty.buffer.ByteBuf.class).newInstance("MC|BOpen", net.minecraft.util.io.netty.buffer.Unpooled.EMPTY_BUFFER);
					else
						packet = getNMSClass("PacketPlayOutCustomPayload").getConstructor(String.class, getNMSClass("PacketDataSerializer")).newInstance("MC|BOpen", getNMSClass("PacketDataSerializer").getConstructor(ByteBuf.class).newInstance(Unpooled.EMPTY_BUFFER));
				
					Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
					playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				player.setItemInHand(hand);
			}
		}, 2);
	}
}
