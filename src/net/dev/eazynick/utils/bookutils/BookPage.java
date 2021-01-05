package net.dev.eazynick.utils.bookutils;

import java.util.*;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.utils.ReflectUtils;
import net.md_5.bungee.api.chat.*;
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
		
		Class<?> chatSerializer = (eazyNick.getVersion().startsWith("1_7") || eazyNick.getVersion().equals("1_8_R1")) ? reflectUtils.getNMSClass("ChatSerializer") : reflectUtils.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0];
		
		try {
			return chatSerializer.getMethod("a", String.class).invoke(chatSerializer, getAsString());
		} catch (Exception e) {
			return null;
		}
	}
	
	public String getAsString() {
		List<BaseComponent> baseComponents = new ArrayList<>();
		
		for (TextComponent textComponent : texts) {
			BaseComponent[] toAdd = TextComponent.fromLegacyText(textComponent.getText());

			for (BaseComponent baseComponent : toAdd) {
				if(textComponent.getClickEvent() != null)
					baseComponent.setClickEvent(textComponent.getClickEvent());
				
				if(textComponent.getHoverEvent() != null)
					baseComponent.setHoverEvent(textComponent.getHoverEvent());
			}
			
			baseComponents.addAll(Arrays.asList(toAdd));
		}
		
		return ComponentSerializer.toString(new TextComponent(baseComponents.toArray(new BaseComponent[0])));
	}
	
	public boolean isEmpty() {
		boolean empty = true;
		
		for (TextComponent textComponent : texts) {
			if(!(textComponent.getText().isEmpty()))
				empty = false;
		}
		
		return empty;
	}
	
}
