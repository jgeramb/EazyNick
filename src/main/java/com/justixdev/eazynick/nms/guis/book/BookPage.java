package com.justixdev.eazynick.nms.guis.book;

import lombok.AllArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.justixdev.eazynick.nms.ReflectionHelper.*;

@AllArgsConstructor
public class BookPage {

    private final List<TextComponent> texts;

    public Object getAsIChatBaseComponent() {
        try {
            return invokeStatic(
                    NMS_VERSION.startsWith("v1_7") || NMS_VERSION.equals("v1_8_R1")
                            ? getNMSClass("ChatSerializer")
                            : getNMSClass(
                                    NMS_VERSION.startsWith("v1_17")
                                            || NMS_VERSION.startsWith("v1_18")
                                            || NMS_VERSION.startsWith("v1_19")
                                            ? "network.chat.IChatBaseComponent"
                                            : "IChatBaseComponent")
                                    .getDeclaredClasses()[0],
                    NMS_VERSION.startsWith("1_18") || NMS_VERSION.startsWith("1_19")
                            ? "b"
                            : "a",
                    types(String.class),
                    this.getAsString());
        } catch (Exception ex) {
            return null;
        }
    }

    public String getAsString() {
        return ComponentSerializer.toString(new TextComponent(
                this.texts
                        .stream()
                        .map(textComponent ->
                                Arrays.stream(TextComponent.fromLegacyText(textComponent.getText()))
                                        .peek(baseComponent -> {
                                            if (textComponent.getClickEvent() != null)
                                                baseComponent.setClickEvent(textComponent.getClickEvent());

                                            if (textComponent.getHoverEvent() != null)
                                                baseComponent.setHoverEvent(textComponent.getHoverEvent());

                                        })
                                        .collect(Collectors.toList()))
                        .flatMap(List::stream)
                        .toArray(BaseComponent[]::new)));
    }

    public boolean isEmpty() {
        return this.texts.stream().allMatch(textComponent -> textComponent.getText().isEmpty());
    }

}
