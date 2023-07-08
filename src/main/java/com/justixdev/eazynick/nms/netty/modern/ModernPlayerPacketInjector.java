package com.justixdev.eazynick.nms.netty.modern;

import com.justixdev.eazynick.nms.netty.InjectorType;
import com.justixdev.eazynick.nms.netty.PlayerPacketInjector;
import io.netty.channel.Channel;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.Objects;

import static com.justixdev.eazynick.nms.ReflectionHelper.*;

public class ModernPlayerPacketInjector extends ModernPacketInjector implements PlayerPacketInjector {

    @Getter
    protected Player player;

    public ModernPlayerPacketInjector(Player player, InjectorType type) {
        super(Objects.requireNonNull(player.getAddress()).getAddress(), type);

        this.player = player;

        this.initChannel();
    }

    @Override
    public void initChannel() {
        try {
            boolean is1_17 = NMS_VERSION.startsWith("v1_17"),
                    is1_18 = NMS_VERSION.startsWith("v1_18"),
                    is1_19 = NMS_VERSION.startsWith("v1_19"),
                    is1_20 = NMS_VERSION.startsWith("v1_20");
            Object entityPlayer = invoke(this.player, "getHandle");
            Object playerConnection = getFieldValue(entityPlayer,
                    is1_20
                            ? "c"
                            : is1_17 || is1_18 || is1_19
                                    ? "b"
                                    : "playerConnection"
            );
            Object networkManager = getFieldValue(playerConnection,
                    NMS_VERSION.equals("v1_19_R3") || is1_20
                            ? "h"
                            : is1_19
                                    ? "b"
                                    : is1_17 || is1_18
                                            ? "a"
                                            : "networkManager"
            );

            this.channel = (Channel) getFieldValue(networkManager,
                    is1_17 || is1_18 || is1_19 || is1_20
                            ? NMS_VERSION.equals("v1_18_R2") || is1_19 || is1_20
                                    ? "m"
                                    : "k"
                            : "channel"
            );
            this.inject();
        } catch (Exception ex) {
            if(!ex.getMessage().startsWith("Duplicate handler name"))
                ex.printStackTrace();
        }
    }

}
