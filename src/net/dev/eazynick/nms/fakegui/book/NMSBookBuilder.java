package net.dev.eazynick.nms.fakegui.book;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.nms.ReflectionHelper;

public class NMSBookBuilder {
	
	private EazyNick eazyNick;
	private ReflectionHelper reflectionHelper;
	
	public NMSBookBuilder() {
		this.eazyNick = EazyNick.getInstance();
		this.reflectionHelper = eazyNick.getReflectionHelper();
	}
	
	public ItemStack create(String title, BookPage... bookPages) {
		ItemStack itemStack = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
		bookMeta.setTitle(title);
		bookMeta.setAuthor(eazyNick.getDescription().getName());
		
		try {
			String version = eazyNick.getVersion();
			Class<?> craftChatMessage = reflectionHelper.getCraftClass("util.CraftChatMessage");
			Field f = reflectionHelper.getField(reflectionHelper.getCraftClass("inventory.CraftMetaBook"), "pages");
			List<Object> list = (List<Object>) f.get(bookMeta);
			
			if(list == null) {
				f.set(bookMeta, new ArrayList<>());
				
				list = (List<Object>) f.get(bookMeta);
			}
			
			for (BookPage bookPage : bookPages) {
				if(!((bookPage == null) || bookPage.isEmpty()))
					list.add((version.startsWith("1_17") || version.equals("1_16_R3")) ? craftChatMessage.getMethod("toJSON", reflectionHelper.getNMSClass(eazyNick.getVersion().startsWith("1_17") ? "network.chat.IChatBaseComponent" : "IChatBaseComponent")).invoke(null, craftChatMessage.getMethod("fromJSON", String.class).invoke(null, bookPage.getAsString())) : bookPage.getAsIChatBaseComponent());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		itemStack.setItemMeta(bookMeta);
		
		return itemStack;
	}
	
}