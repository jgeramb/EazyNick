package com.justixdev.eazynick.nms.netty.legacy;

import com.justixdev.eazynick.nms.netty.InjectorType;
import net.minecraft.util.io.netty.channel.Channel;
import org.bukkit.Bukkit;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;

import static com.justixdev.eazynick.nms.ReflectionHelper.*;

public class LegacyAddressPacketInjector extends LegacyPacketInjector {

    public LegacyAddressPacketInjector(InetAddress address, InjectorType type) {
        super(address, type);

        this.initChannel();
    }

    @Override
    public void initChannel() {
        getFirstFieldByType(
                getNMSClass("NetworkManager"),
                Channel.class
        ).ifPresent(field -> {
            field.setAccessible(true);

            try {
                // Get MinecraftServer from CraftServer
                Object minecraftServer = getFieldValue(Bukkit.getServer(), "console");

                for (Object manager : Collections.synchronizedList((List<?>) getLastFieldByTypeValue(
                        invoke(minecraftServer, "getServerConnection"),
                        List.class
                )).toArray()) {
                    Channel channel = (Channel) field.get(manager);

                    if ((channel == null) || (channel.pipeline() == null))
                        continue;

                    if(!(channel.remoteAddress() instanceof InetSocketAddress))
                        continue;

                    if(!((InetSocketAddress) channel.remoteAddress()).getAddress().equals(this.address))
                        continue;

                    if (channel.pipeline().get("packet_handler") != null) {
                        this.channel = channel;
                        this.inject();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

}
