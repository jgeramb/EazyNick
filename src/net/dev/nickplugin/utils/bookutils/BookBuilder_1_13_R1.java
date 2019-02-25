package net.dev.nickplugin.utils.bookutils;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_13_R1.inventory.CraftMetaBook;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.server.v1_13_R1.IChatBaseComponent;

public class BookBuilder_1_13_R1 {
	
	@SuppressWarnings("unchecked")
	public static ItemStack create(String title, TextComponent... texts) {
		ItemStack is = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta m = (BookMeta) is.getItemMeta();
		m.setTitle(title);
		m.setAuthor("");
		
		try {
			List<IChatBaseComponent> list = (List<IChatBaseComponent>) CraftMetaBook.class.getDeclaredField("pages").get(m);
			
			TextComponent text = new TextComponent("");
			for (TextComponent tc : texts) text.addExtra(tc);
			
			list.add(toIChatBaseComponent(text));
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		
		is.setItemMeta(m);
		
		return is;
	}
	
	public static IChatBaseComponent toIChatBaseComponent(TextComponent tc) {
		return IChatBaseComponent.ChatSerializer.a(ComponentSerializer.toString(tc));
	}
	
}