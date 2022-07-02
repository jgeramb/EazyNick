package com.justixdev.eazynick.nms.fakegui.book;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.nms.ReflectionHelper;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BookPage {

	private final List<TextComponent> texts;

	public BookPage(List<TextComponent> texts) {
		this.texts = texts;
	}
	
	public Object getAsIChatBaseComponent() {
		EazyNick eazyNick = EazyNick.getInstance();
		ReflectionHelper reflectionHelper = eazyNick.getReflectionHelper();
		
		String version = eazyNick.getVersion();
		Class<?> chatSerializer = (version.startsWith("1_7") || version.equals("1_8_R1"))
				? reflectionHelper.getNMSClass("ChatSerializer")
				: reflectionHelper.getNMSClass(
						(version.startsWith("1_17") || version.startsWith("1_18") || version.startsWith("1_19"))
								? "network.chat.IChatBaseComponent"
								: "IChatBaseComponent"
				).getDeclaredClasses()[0];
		
		try {
			return chatSerializer.getMethod(
					(version.startsWith("1_18") || version.startsWith("1_19"))
							? "b"
							: "a",
					String.class
			).invoke(chatSerializer, getAsString());
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
		return texts.stream().allMatch(textComponent -> textComponent.getText().isEmpty());
	}
	
}
