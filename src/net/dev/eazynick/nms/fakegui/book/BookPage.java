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
		
		String version = eazyNick.getVersion();
		Class<?> chatSerializer = (version.startsWith("1_7") || version.equals("1_8_R1")) ? reflectionHelper.getNMSClass("ChatSerializer") : reflectionHelper.getNMSClass((version.startsWith("1_17") || version.startsWith("1_18")) ? "network.chat.IChatBaseComponent" : "IChatBaseComponent").getDeclaredClasses()[0];
		
		try {
			return chatSerializer.getMethod(version.startsWith("1_18") ? "b" : "a", String.class).invoke(chatSerializer, getAsString());
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
