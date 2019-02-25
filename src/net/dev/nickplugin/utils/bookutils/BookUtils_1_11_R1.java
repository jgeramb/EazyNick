package net.dev.nickplugin.utils.bookutils;

import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_11_R1.EntityPlayer;
import net.minecraft.server.v1_11_R1.EnumHand;

public class BookUtils_1_11_R1 {

	@SuppressWarnings("deprecation")
	public static void open(Player p, ItemStack book, boolean addStats) {
		EntityPlayer player = ((CraftPlayer) p).getHandle();
		org.bukkit.inventory.ItemStack hand = p.getItemInHand();
		try {
			p.setItemInHand(book);
			player.a(CraftItemStack.asNMSCopy(book), EnumHand.MAIN_HAND);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			p.setItemInHand(hand);
		}
	}
}
