package com.justixdev.eazynick.nms.netty.legacy.impl;

import com.justixdev.eazynick.nms.netty.InjectorType;
import com.justixdev.eazynick.nms.netty.legacy.LegacyAddressPacketInjector;
import net.minecraft.util.com.mojang.authlib.GameProfile;

import java.net.InetAddress;
import java.util.UUID;

import static com.justixdev.eazynick.nms.ReflectionHelper.getFieldValue;
import static com.justixdev.eazynick.nms.ReflectionHelper.setField;

@SuppressWarnings("SwitchStatementWithTooFewBranches")
public class ServerListLegacyPacketInjector extends LegacyAddressPacketInjector {

    public ServerListLegacyPacketInjector(InetAddress address) {
        super(address, InjectorType.OUTGOING);
    }

    @Override
    public Object onPacketSend(Object packet) {
        try {
            switch (packet.getClass().getSimpleName()) {
                case "PacketStatusOutServerInfo":
                    Object serverPing = getFieldValue(packet, "b");
                    Object serverPingPlayerSample = getFieldValue(serverPing, "b");
                    GameProfile[] gameProfileArray = (GameProfile[]) getFieldValue(serverPingPlayerSample, "c");

                    for (int i = 0; i < gameProfileArray.length; i++) {
                        UUID uuid = gameProfileArray[i].getId();

                        if(this.utils.getNickedPlayers().containsKey(uuid))
                            // Replace game profile with fake game profile (nicked player profile)
                            gameProfileArray[i] = (GameProfile) this.utils.getNickedPlayers()
                                    .get(uuid)
                                    .getFakeGameProfile(false);
                    }

                    // Replace game profiles in ServerPingPlayerSample
                    setField(serverPingPlayerSample, "c", gameProfileArray);
                    break;
                default:
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return packet;
}

}
