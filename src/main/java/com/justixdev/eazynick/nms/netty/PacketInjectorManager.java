package com.justixdev.eazynick.nms.netty;

import com.justixdev.eazynick.nms.netty.legacy.impl.IncomingLegacyPacketInjector;
import com.justixdev.eazynick.nms.netty.legacy.impl.OutgoingLegacyPacketInjector;
import com.justixdev.eazynick.nms.netty.legacy.impl.ServerListLegacyPacketInjector;
import com.justixdev.eazynick.nms.netty.modern.impl.IncomingModernPacketInjector;
import com.justixdev.eazynick.nms.netty.modern.impl.OutgoingModernPacketInjector;
import com.justixdev.eazynick.nms.netty.modern.impl.ServerListModernPacketInjector;
import org.bukkit.entity.Player;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.justixdev.eazynick.nms.ReflectionHelper.NMS_VERSION;

public class PacketInjectorManager {

    private final Map<InetAddress, Set<PacketInjector>> packetInjectors;

    public PacketInjectorManager() {
        this.packetInjectors = new HashMap<>();
    }

    public void inject(Player player) {
        if(player.getAddress() == null)
            return;

        InetAddress address = player.getAddress().getAddress();
        Set<PacketInjector> playerInjectors = this.packetInjectors.getOrDefault(address, new HashSet<>());

        if(NMS_VERSION.equals("v1_7_R4")) {
            playerInjectors.add(new OutgoingLegacyPacketInjector(player));
            playerInjectors.add(new IncomingLegacyPacketInjector(player));
        } else {
            playerInjectors.add(new OutgoingModernPacketInjector(player));
            playerInjectors.add(new IncomingModernPacketInjector(player));
        }

        this.packetInjectors.putIfAbsent(address, playerInjectors);
    }

    public void inject(InetAddress address) {
        Set<PacketInjector> addressInjectors = this.packetInjectors.getOrDefault(address, new HashSet<>());

        if(NMS_VERSION.equals("v1_7_R4"))
            addressInjectors.add(new ServerListLegacyPacketInjector(address));
        else
            addressInjectors.add(new ServerListModernPacketInjector(address));

        this.packetInjectors.putIfAbsent(address, addressInjectors);
    }

    public void remove(Player player) {
        if(player.getAddress() == null)
            return;

        this.remove(player.getAddress().getAddress());
    }

    public void remove(InetAddress address) {
        if(this.packetInjectors.containsKey(address))
            this.packetInjectors.remove(address).forEach(PacketInjector::remove);
    }

    public void removeAll() {
        this.packetInjectors.values().forEach(injectors -> injectors.forEach(PacketInjector::remove));
    }

}
