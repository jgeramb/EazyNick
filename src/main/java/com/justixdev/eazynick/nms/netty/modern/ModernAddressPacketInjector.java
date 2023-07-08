package com.justixdev.eazynick.nms.netty.modern;

import com.justixdev.eazynick.nms.netty.InjectorType;
import io.netty.channel.Channel;
import org.bukkit.Bukkit;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

import static com.justixdev.eazynick.nms.ReflectionHelper.*;

public class ModernAddressPacketInjector extends ModernPacketInjector {

    public ModernAddressPacketInjector(InetAddress address, InjectorType type) {
        super(address, type);

        this.initChannel();
    }

    @Override
    public void initChannel() {
        boolean is1_17 = NMS_VERSION.startsWith("v1_17"),
                is1_18 = NMS_VERSION.startsWith("v1_18"),
                is1_19 = NMS_VERSION.startsWith("v1_19"),
                is1_20 = NMS_VERSION.startsWith("v1_20");

        getFirstFieldByType(
                is1_17 || is1_18 || is1_19 || is1_20
                        ? getNMSClass("network.NetworkManager")
                        : getNMSClass("NetworkManager"),
                Channel.class
        ).ifPresent(field -> {
            field.setAccessible(true);

            try {
                // Get MinecraftServer from CraftServer
                Object minecraftServer = getFieldValue(Bukkit.getServer(), "console");
                Object[] managers;

                if(NMS_VERSION.startsWith("v1_13") || NMS_VERSION.startsWith("v1_14"))
                    managers = Collections.synchronizedList((List<?>) getFieldValue(
                            invoke(minecraftServer, "getServerConnection"),
                            "g"
                    )).toArray();
                else if(NMS_VERSION.startsWith("v1_15")
                        || NMS_VERSION.startsWith("v1_16")
                        || is1_17
                        || is1_18
                        || is1_19
                        || is1_20)
                    managers = ((Queue<?>) getFieldValue(
                            invoke(
                                    minecraftServer,
                                    NMS_VERSION.equals("v1_19_R2")
                                            ? "ac"
                                            : is1_18 || is1_19 || is1_20
                                                    ? "ad"
                                                    : "getServerConnection"
                            ),
                            "pending"
                    )).toArray();
                else
                    managers = Collections.synchronizedList((List<?>) getLastFieldByTypeValue(
                            invoke(minecraftServer, "getServerConnection"),
                            List.class
                    )).toArray();

                for (Object manager : managers) {
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
                if(!ex.getMessage().startsWith("Duplicate handler name"))
                    ex.printStackTrace();
            }
        });
    }

}
