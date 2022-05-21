package com.justixdev.eazynick.nms.fakegui.book;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.nms.ReflectionHelper;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class NMSBookBuilder {
	
	private final EazyNick eazyNick;
	private final ReflectionHelper reflectionHelper;
	
	public NMSBookBuilder(EazyNick eazyNick) {
		this.eazyNick = eazyNick;
		this.reflectionHelper = eazyNick.getReflectionHelper();
	}
	
	@SuppressWarnings("unchecked")
	public ItemStack create(String title, BookPage... bookPages) {
		ItemStack itemStack = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();

		if (bookMeta != null) {
			bookMeta.setTitle(title);
			bookMeta.setAuthor(eazyNick.getDescription().getName());

			try {
				String version = eazyNick.getVersion();
				Class<?> craftChatMessage = reflectionHelper.getCraftClass("util.CraftChatMessage");
				Field f = reflectionHelper.getField(reflectionHelper.getCraftClass("inventory.CraftMetaBook"), "pages");
				List<Object> list = (List<Object>) f.get(bookMeta);

				if (list == null) {
					f.set(bookMeta, new ArrayList<>());

					list = (List<Object>) f.get(bookMeta);
				}

				for (BookPage bookPage : bookPages) {
					if (!((bookPage == null) || bookPage.isEmpty()))
						list.add((version.equals("1_16_R3") || version.startsWith("1_17") || version.startsWith("1_18")) ? craftChatMessage.getMethod("toJSON", reflectionHelper.getNMSClass((version.startsWith("1_17") || version.startsWith("1_18")) ? "network.chat.IChatBaseComponent" : "IChatBaseComponent")).invoke(null, craftChatMessage.getMethod("fromJSON", String.class).invoke(null, bookPage.getAsString())) : bookPage.getAsIChatBaseComponent());
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			itemStack.setItemMeta(bookMeta);
		}
		
		return itemStack;
	}
	
}