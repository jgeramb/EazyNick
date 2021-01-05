package net.dev.eazynick.utils.bookutils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.utils.ReflectUtils;

public class NMSBookBuilder {
	
	private EazyNick eazyNick;
	private ReflectUtils reflectUtils;
	
	public NMSBookBuilder() {
		this.eazyNick = EazyNick.getInstance();
		this.reflectUtils = eazyNick.getReflectUtils();
	}
	
	public ItemStack create(String title, BookPage... bookPages) {
		ItemStack itemStack = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
		bookMeta.setTitle(title);
		bookMeta.setAuthor(eazyNick.getDescription().getName());
		
		try {
			Class<?> craftChatMessage = reflectUtils.getCraftClass("util.CraftChatMessage");
			Field f = reflectUtils.getField(reflectUtils.getCraftClass("inventory.CraftMetaBook"), "pages");
			List<Object> list = (List<Object>) f.get(bookMeta);
			
			if(list == null) {
				f.set(bookMeta, new ArrayList<>());
				
				list = (List<Object>) f.get(bookMeta);
			}
			
			for (BookPage bookPage : bookPages) {
				if(!((bookPage == null) || bookPage.isEmpty()))
					list.add(eazyNick.getVersion().equals("1_16_R3") ? craftChatMessage.getMethod("toJSON", reflectUtils.getNMSClass("IChatBaseComponent")).invoke(null, craftChatMessage.getMethod("fromJSON", String.class).invoke(null, bookPage.getAsString())) : bookPage.getAsIChatBaseComponent());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		itemStack.setItemMeta(bookMeta);
		
		return itemStack;
	}
	
}