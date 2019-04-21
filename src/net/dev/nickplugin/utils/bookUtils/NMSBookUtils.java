package net.dev.nickplugin.utils.bookUtils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.dev.nickplugin.main.Main;
import net.dev.nickplugin.utils.ReflectUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class NMSBookUtils {

	public static void open(Player p, ItemStack book) {
		ItemStack hand = p.getItemInHand();
		
		try {
			Object entityPlayer = p.getClass().getMethod("getHandle").invoke(p);
			Object craftItemStack = ReflectUtils.getCraftClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(ReflectUtils.getCraftClass("inventory.CraftItemStack"), book);
			
			p.setItemInHand(book);
			
			if(!(Main.version.startsWith("1_8"))) {
				Class<?> enumHand = ReflectUtils.getNMSClass("EnumHand");
				
				entityPlayer.getClass().getMethod("a", ReflectUtils.getNMSClass("ItemStack"), enumHand).invoke(entityPlayer, craftItemStack, ReflectUtils.getField(enumHand, "MAIN_HAND").get(enumHand));
			} else {
				Object packet = ReflectUtils.getNMSClass("PacketPlayOutCustomPayload").getConstructor(String.class, ReflectUtils.getNMSClass("PacketDataSerializer")).newInstance("MC|BOpen", ReflectUtils.getNMSClass("PacketDataSerializer").getConstructor(ByteBuf.class).newInstance(Unpooled.buffer()));
				Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
				playerConnection.getClass().getMethod("sendPacket", ReflectUtils.getNMSClass("Packet")).invoke(playerConnection, packet);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			p.setItemInHand(hand);
		}
	}
}
