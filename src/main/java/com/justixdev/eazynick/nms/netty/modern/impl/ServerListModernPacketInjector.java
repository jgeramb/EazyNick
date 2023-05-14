package com.justixdev.eazynick.nms.netty.modern.impl;

import com.justixdev.eazynick.nms.netty.InjectorType;
import com.justixdev.eazynick.nms.netty.modern.ModernAddressPacketInjector;
import com.mojang.authlib.GameProfile;

import java.net.InetAddress;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.justixdev.eazynick.nms.ReflectionHelper.*;

public class ServerListModernPacketInjector extends ModernAddressPacketInjector {

    public ServerListModernPacketInjector(InetAddress address) {
        super(address, InjectorType.OUTGOING);
    }

    @Override
    public Object onPacketSend(Object packet) {
        boolean is1_17 = NMS_VERSION.startsWith("v1_17"),
                is1_18 = NMS_VERSION.startsWith("v1_18"),
                is1_19 = NMS_VERSION.startsWith("v1_19");

        try {
            if (packet.getClass().getSimpleName().equals("PacketStatusOutServerInfo")) {
                Object serverPing = getFieldValue(packet, NMS_VERSION.equals("v1_19_R3") ? "a" : "b");
                Object serverPingPlayerSample = getFieldValue(
                        serverPing,
                        NMS_VERSION.equals("v1_19_R3")
                                ? "c"
                                : is1_17 || is1_18 || is1_19
                                        ? "d"
                                        : "b"
                );

                if(NMS_VERSION.equals("v1_19_R3")) {
                    serverPingPlayerSample = invoke(serverPingPlayerSample, "orElse", types(Object.class), (Object) null);

                    List<GameProfile> gameProfileList = (List<GameProfile>) getFieldValue(
                            serverPingPlayerSample,
                            "d"
                    );
                    List<GameProfile> newGameProfileList = gameProfileList
                            .stream()
                            .map(gameProfile -> this.utils.getNickedPlayers().containsKey(gameProfile.getId())
                                    ? (GameProfile) this.utils.getNickedPlayers()
                                            .get(gameProfile.getId())
                                            .getFakeGameProfile(this.setupYamlFile.getConfiguration().getBoolean("Settings.ChangeOptions.UUID"))
                                    : gameProfile
                            )
                            .collect(Collectors.toList());

                    gameProfileList.clear();
                    gameProfileList.addAll(newGameProfileList);
                } else {
                    GameProfile[] gameProfileArray = (GameProfile[]) getFieldValue(serverPingPlayerSample, "c");

                    for (int i = 0; i < gameProfileArray.length; i++) {
                        UUID uuid = gameProfileArray[i].getId();

                        if (this.utils.getNickedPlayers().containsKey(uuid))
                            // Replace game profile with fake game profile (nicked player profile)
                            gameProfileArray[i] = (GameProfile) this.utils.getNickedPlayers()
                                    .get(uuid)
                                    .getFakeGameProfile(this.setupYamlFile.getConfiguration().getBoolean("Settings.ChangeOptions.UUID"));
                    }

                    // Replace game profiles in ServerPingPlayerSample
                    setField(serverPingPlayerSample, "c", gameProfileArray);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return packet;
    }

}
