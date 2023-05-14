package com.justixdev.eazynick.nms.guis.book;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;

import static com.justixdev.eazynick.nms.ReflectionHelper.*;

public class BookComponentBuilder {

    private final TextComponent component;

    public BookComponentBuilder(String text) {
        this.component = new TextComponent(text);
    }

    public BookComponentBuilder clickEvent(ClickEvent.Action action, String value) {
        this.component.setClickEvent(new ClickEvent(action, value));
        return this;
    }

    public BookComponentBuilder hoverEvent(HoverEvent.Action action, String text) {
        HoverEvent event = null;

        if(NMS_VERSION.startsWith("v1_17")
                || NMS_VERSION.startsWith("v1_18")
                || NMS_VERSION.startsWith("v1_19"))
            event = new HoverEvent(
                    action,
                    Collections.singletonList(new net.md_5.bungee.api.chat.hover.content.Text(text)));
        else {
            try {
                event = (HoverEvent) newInstance(
                        HoverEvent.class,
                        types(HoverEvent.Action.class, BaseComponent[].class),
                        action,
                        TextComponent.fromLegacyText(text));
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException |
                     InstantiationException ignore) {
            }
        }

        if(event != null)
            this.component.setHoverEvent(event);

        return this;
    }

    public TextComponent build() {
        return this.component;
    }

}