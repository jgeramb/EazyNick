package net.dev.eazynick.utils.bookutils;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.utils.ReflectUtils;
import net.md_5.bungee.api.chat.TextComponent;

public class NMSBookBuilder {
	
	private EazyNick eazyNick;
	private ReflectUtils reflectUtils;
	
	public NMSBookBuilder() {
		this.eazyNick = EazyNick.getInstance();
		this.reflectUtils = eazyNick.getReflectUtils();
	}
	
	public ItemStack create(String title, TextComponent... texts) {
		return create(title, new BookPage(texts));
	}
	
	public ItemStack create(String title, BookPage... bookPages) {
		ItemStack is = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta m = (BookMeta) is.getItemMeta();
		m.setTitle(title);
		m.setAuthor("");
		
		try {
			List<Object> list = (List<Object>) reflectUtils.getField(reflectUtils.getCraftClass("inventory.CraftMetaBook"), "pages").get(m);
			
			for (BookPage bookPage : bookPages) {
				if(!(bookPage.isEmpty()))
					list.add(bookPage.getAsIChatBaseComponent());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		is.setItemMeta(m);
		
		return is;
	}
	
}