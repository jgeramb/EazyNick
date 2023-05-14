package com.justixdev.eazynick.nms.netty.legacy.impl;

import com.justixdev.eazynick.api.NickedPlayerData;
import com.justixdev.eazynick.nms.netty.InjectorType;
import com.justixdev.eazynick.nms.netty.legacy.LegacyPlayerPacketInjector;
import com.justixdev.eazynick.utilities.Utils;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.justixdev.eazynick.nms.ReflectionHelper.*;
import static com.justixdev.eazynick.nms.netty.NMSPacketHelper.replaceTabCompletions;

@SuppressWarnings("unchecked")
public class OutgoingLegacyPacketInjector extends LegacyPlayerPacketInjector {

    public OutgoingLegacyPacketInjector(Player player) {
        super(player, InjectorType.OUTGOING);
    }

    @Override
    public Object onPacketSend(Object packet) {
        try {
            switch (packet.getClass().getSimpleName()) {
                case "PacketPlayOutNamedEntitySpawn":
                    UUID uniqueIdToSpawn = ((GameProfile) Objects.requireNonNull(getField(packet.getClass(), "b")).get(packet)).getId();

                    if(!this.utils.getSoonNickedPlayers().contains(uniqueIdToSpawn)) {
                        if(this.utils.getNickedPlayers().containsKey(uniqueIdToSpawn)) {
                            // Replace game profile with fake game profile (nicked player profile)
                            setField(
                                    packet,
                                    "b",
                                    this.utils.getNickedPlayers().get(uniqueIdToSpawn).getFakeGameProfile(false)
                            );
                        }
                    }
                    break;
                case "PacketPlayOutPlayerInfo":
                    Object playerObject = Objects.requireNonNull(getField(packet.getClass(), "player")).get(packet);

                    if(playerObject != null) {
                        UUID uniqueIdToUpdate = ((GameProfile) playerObject).getId();

                        if(this.utils.getSoonNickedPlayers().contains(uniqueIdToUpdate)
                                && this.setupYamlFile.getConfiguration().getBoolean("SeeNickSelf")
                                && ((int) getFieldValue(packet, "action") == 0))
                            return null;

                        if(this.utils.getNickedPlayers().containsKey(uniqueIdToUpdate)) {
                            // Replace game profile with fake game profile (nicked player profile)
                            NickedPlayerData nickedPlayerData = this.utils.getNickedPlayers().get(uniqueIdToUpdate);

                            setField(
                                    packet,
                                    "player",
                                    nickedPlayerData.getFakeGameProfile(false)
                            );
                            setField(
                                    packet,
                                    "username",
                                    nickedPlayerData.getNickName()
                            );
                        }
                    }
                    break;
                case "PacketPlayOutTabComplete":
                    setField(
                            packet,
                            "a",
                            replaceTabCompletions(this.player, (String[]) getFieldValue(packet, "a"))
                    );
                    break;
                case "PacketPlayOutChat":
                    if(this.setupYamlFile.getConfiguration().getBoolean("OverwriteMessagePackets")) {
                        // Get chat message from packet and replace names
                        Object editedComponent = this.replaceNames(getFieldValue(packet, "a"));

                        // Overwrite chat message
                        if (editedComponent != null)
                            setField(packet, "a", editedComponent);
                    }
                    break;
                case "PacketPlayOutScoreboardObjective":
                    String objectivePlayerName = (String) getFieldValue(packet, "b");

                    if(objectivePlayerName != null) {
                        setField(
                                packet,
                                "b",
                                this.utils.getNickedPlayers()
                                        .values()
                                        .stream()
                                        .filter(nickedPlayerData -> objectivePlayerName.contains(nickedPlayerData.getRealName()))
                                        .findFirst()
                                        .map(nickedPlayerData -> objectivePlayerName.replace(nickedPlayerData.getRealName(), nickedPlayerData.getNickName()))
                                        .orElse(objectivePlayerName)
                        );
                    }
                    break;
                case "PacketPlayOutScoreboardScore":
                    // Replace name
                    String playerName = (String) getFieldValue(packet, "a");

                    if(playerName != null) {
                        setField(
                                packet,
                                "a",
                                this.utils.getNickedPlayers()
                                        .values()
                                        .stream()
                                        .filter(nickedPlayerData -> playerName.contains(nickedPlayerData.getRealName()))
                                        .findFirst()
                                        .map(nickedPlayerData -> playerName.replace(nickedPlayerData.getRealName(), nickedPlayerData.getNickName()))
                                        .orElse(playerName)
                        );
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return packet;
    }

    private Object replaceNames(Object iChatBaseComponent) {
        Utils utils = this.eazyNick.getUtils();

        Object editedComponent = iChatBaseComponent;

        try {
            if(iChatBaseComponent != null) {
                // Extract text from message component
                StringBuilder fullText = new StringBuilder();

                for (Object partlyIChatBaseComponent : ((List<Object>) invoke(iChatBaseComponent, "a"))) {
                    if(partlyIChatBaseComponent.getClass().getSimpleName().equals("ChatComponentText")) {
                        Arrays.stream(this.serialize(partlyIChatBaseComponent)
                                            .replace("\"", "")
                                            .replace("{", "")
                                            .replace("}", "")
                                            .split(",")
                        )
                                .forEach(s -> {
                                    if(s.startsWith("text:"))
                                        fullText.append(s.substring(5));
                                    else if(!s.contains(":"))
                                        fullText.append(s);
                                });
                    }
                }

                // Replace real names with nicknames
                if(!(fullText.toString().contains(ChatColor.stripColor(utils.getLastChatMessage()))
                        || fullText.toString().startsWith(ChatColor.stripColor(utils.getPrefix())))) {
                    String json = this.serialize(iChatBaseComponent);

                    for (NickedPlayerData nickedPlayerData : utils.getNickedPlayers().values()) {
                        Player targetPlayer = Bukkit.getPlayer(nickedPlayerData.getUniqueId());

                        if (targetPlayer != null) {
                            String name = targetPlayer.getName();

                            if (json.contains(name))
                                json = json.replace(name, nickedPlayerData.getNickName());
                        }
                    }

                    editedComponent = this.deserialize(json);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return editedComponent;
    }

    private String serialize(Object iChatBaseComponent) {
        try {
            return (String) invokeStatic(
                    getNMSClass("ChatSerializer"),
                    "a",
                    types(getNMSClass("IChatBaseComponent")),
                    iChatBaseComponent
            );
        } catch (Exception ex) {
            return "";
        }
    }

    public Object deserialize(String json) {
        try {
            return invokeStatic(getNMSClass("ChatSerializer"), "a", types(String.class), json);
        } catch (Exception ex) {
            return "";
        }
    }

}
