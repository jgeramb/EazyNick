package com.justixdev.eazynick.nms.netty.modern.impl;

import com.justixdev.eazynick.nms.guis.SignGUI;
import com.justixdev.eazynick.nms.guis.SignGUI.EditCompleteEvent;
import com.justixdev.eazynick.nms.netty.InjectorType;
import com.justixdev.eazynick.nms.netty.modern.ModernPlayerPacketInjector;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static com.justixdev.eazynick.nms.ReflectionHelper.*;

public class IncomingModernPacketInjector extends ModernPlayerPacketInjector {

    public IncomingModernPacketInjector(Player player) {
        super(player, InjectorType.INCOMING);
    }

    @Override
    public boolean onPacketReceive(Object packet) {
        SignGUI signGUI = this.eazyNick.getSignGUI();
        boolean is1_17 = NMS_VERSION.startsWith("v1_17"),
                is1_18 = NMS_VERSION.startsWith("v1_18"),
                is1_19 = NMS_VERSION.startsWith("v1_19");

        try {
            if (packet.getClass().getSimpleName().equals("PacketPlayInUpdateSign")) {
                if (signGUI.getEditCompleteListeners().containsKey(this.player)) {
                    // Process SignGUI success
                    Object[] rawLines = (Object[]) getFieldValue(
                            packet,
                            is1_17 || is1_18 || is1_19
                                    ? "c"
                                    : "b"
                    );

                    Bukkit.getScheduler().runTask(eazyNick, () -> {
                        String[] lines = new String[4];

                        if (NMS_VERSION.startsWith("v1_8")) {
                            int i = 0;

                            for (Object line : rawLines) {
                                try {
                                    lines[i] = (String) invoke(line, "getText");
                                } catch (Exception ex) {
                                    lines[i] = "";
                                }

                                i++;
                            }
                        } else
                            lines = (String[]) rawLines;

                        signGUI.getEditCompleteListeners().get(this.player).onEditComplete(new EditCompleteEvent(lines));
                        signGUI.getBlocks().get(this.player).setType(signGUI.getOldTypes().get(this.player));
                    });
                }
            } else if (packet.getClass().getName().endsWith("PacketPlayInTabComplete")) {
                // Cache input text
                this.eazyNick.getUtils().getTextsToComplete().put(
                        this.player,
                        (String) getFieldValue(
                                packet,
                                this.eazyNick.getUtils().isVersion13OrLater()
                                        ? "b"
                                        : "a"
                        )
                );
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return true;
    }

}