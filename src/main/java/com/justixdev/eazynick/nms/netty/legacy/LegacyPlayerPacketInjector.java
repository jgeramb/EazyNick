package com.justixdev.eazynick.nms.netty.legacy;

import com.justixdev.eazynick.nms.netty.InjectorType;
import lombok.Getter;
import net.minecraft.util.io.netty.channel.Channel;
import org.bukkit.entity.Player;

import java.util.Objects;

import static com.justixdev.eazynick.nms.ReflectionHelper.getFieldValue;
import static com.justixdev.eazynick.nms.ReflectionHelper.invoke;

public abstract class LegacyPlayerPacketInjector extends LegacyPacketInjector {

    @Getter
    protected Player player;

    public LegacyPlayerPacketInjector(Player player, InjectorType type) {
        super(Objects.requireNonNull(player.getAddress()).getAddress(), type);

        this.player = player;

        this.initChannel();
    }

    @Override
    public void initChannel() {
        try {
            Object entityPlayer = invoke(this.player, "getHandle");
            Object playerConnection = getFieldValue(entityPlayer, "playerConnection");
            Object networkManager = getFieldValue(playerConnection, "networkManager");

            this.channel = (Channel) getFieldValue(networkManager, "m");
            this.inject();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
