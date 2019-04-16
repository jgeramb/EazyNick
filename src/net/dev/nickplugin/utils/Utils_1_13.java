package net.dev.nickplugin.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class Utils_1_13 {

	public static ItemStack createSkull(int amount, String displayName, String owner) {
		ItemStack is = new ItemStack(Material.LEGACY_SKULL_ITEM, amount, (byte) 3);
		SkullMeta m = (SkullMeta) is.getItemMeta();
		m.setDisplayName(displayName);
		m.setOwner(owner);
		is.setItemMeta(m);
		
		return is;
	}

}