package com.justixdev.eazynick.nms.netty.legacy.impl;

import com.justixdev.eazynick.nms.guis.SignGUI;
import com.justixdev.eazynick.nms.guis.SignGUI.EditCompleteEvent;
import com.justixdev.eazynick.nms.netty.InjectorType;
import com.justixdev.eazynick.nms.netty.legacy.LegacyPlayerPacketInjector;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;

import static com.justixdev.eazynick.nms.ReflectionHelper.*;

public class IncomingLegacyPacketInjector extends LegacyPlayerPacketInjector {

    public IncomingLegacyPacketInjector(Player player) {
        super(player, InjectorType.INCOMING);
    }

    @Override
    public boolean onPacketReceive(Object packet) {
        SignGUI signGUI = this.eazyNick.getSignGUI();

        try {
            switch (packet.getClass().getSimpleName()) {
                case "PacketPlayInUpdateSign":
                    if (signGUI.getEditCompleteListeners().containsKey(this.player)) {
                        // Process SignGUI success
                        Object[] rawLines = (Object[]) Objects.requireNonNull(getField(packet.getClass(), "b")).get(packet);

                        Bukkit.getScheduler().runTask(this.eazyNick, () -> {
                            try {
                                String[] lines = new String[4];

                                if (NMS_VERSION.startsWith("1_8")) {
                                    int i = 0;

                                    for (Object line : rawLines) {
                                        lines[i] = (String) invoke(line, "getText");

                                        i++;
                                    }
                                } else
                                    lines = (String[]) rawLines;

                                signGUI.getEditCompleteListeners().get(this.player).onEditComplete(new EditCompleteEvent(lines));
                                signGUI.getBlocks().get(this.player).setType(signGUI.getOldTypes().get(this.player));
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        });
                    }
                    break;
                case "PacketPlayInTabComplete":
                    // Cache input text
                    this.eazyNick.getUtils().getTextsToComplete().put(
                            this.player,
                            (String) getFieldValue(packet, "a")
                    );
                    break;
                default:
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return true;
    }

}