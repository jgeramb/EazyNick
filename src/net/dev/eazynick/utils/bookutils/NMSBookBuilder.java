package net.dev.eazynick.utils.bookutils;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.utils.ReflectUtils;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public class NMSBookBuilder {
	
	public ItemStack create(String title, TextComponent... texts) {
		ReflectUtils reflectUtils = EazyNick.getInstance().getReflectUtils();
		
		ItemStack is = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta m = (BookMeta) is.getItemMeta();
		m.setTitle(title);
		m.setAuthor("");
		
		try {
			List<Object> list = (List<Object>) reflectUtils.getField(reflectUtils.getCraftClass("inventory.CraftMetaBook"), "pages").get(m);
			
			TextComponent text = new TextComponent("");
			
			for (TextComponent tc : texts)
				text.addExtra(tc);
			
			list.add(toIChatBaseComponent(text));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		is.setItemMeta(m);
		
		return is;
	}
	
	public Object toIChatBaseComponent(TextComponent tc) {
		try {
			Class<?> chatSerializer = EazyNick.getInstance().getReflectUtils().getNMSClass("IChatBaseComponent").getDeclaredClasses()[0];
			
			return chatSerializer.getMethod("a", String.class).invoke(chatSerializer, ComponentSerializer.toString(tc));
		} catch (Exception e) {
			return null;
		}
	}
	
}