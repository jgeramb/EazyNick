package net.dev.eazynick.nms.fakegui.book;

import java.util.*;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.nms.ReflectionHelper;
import net.md_5.bungee.api.chat.BaseComponent;
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
		ReflectionHelper reflectionHelper = eazyNick.getReflectionHelper();
		
		Class<?> chatSerializer = (eazyNick.getVersion().startsWith("1_7") || eazyNick.getVersion().equals("1_8_R1")) ? reflectionHelper.getNMSClass("ChatSerializer") : reflectionHelper.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0];
		
		try {
			return chatSerializer.getMethod("a", String.class).invoke(chatSerializer, getAsString());
		} catch (Exception ex) {
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
