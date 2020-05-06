package net.dev.eazynick.utils.bookutils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.utils.ReflectUtils;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public class BookPage {

	private List<TextComponent> texts;
	
	public BookPage(TextComponent... texts) {
		this.texts = Arrays.asList(texts);
	}
	
	public BookPage(ArrayList<TextComponent> texts) {
		this.texts = texts;
	}
	
	public Object getAsIChatBaseComponent() {
		EazyNick eazyNick = EazyNick.getInstance();
		ReflectUtils reflectUtils = eazyNick.getReflectUtils();
		
		try {
			Class<?> chatSerializer = eazyNick.getVersion().equals("1_8_R1") ? reflectUtils.getNMSClass("ChatSerializer") : reflectUtils.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0];
			
			TextComponent text = new TextComponent("");
			
			for (TextComponent tc : texts)
				text.addExtra(tc);
			
			return chatSerializer.getMethod("a", String.class).invoke(chatSerializer, ComponentSerializer.toString(text));
		} catch (Exception e) {
			return null;
		}
	}
	
	public boolean isEmpty() {
		return texts.isEmpty();
	}
	
}
