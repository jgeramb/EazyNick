package com.justixdev.eazynick.nms.netty.legacy.impl;

import com.justixdev.eazynick.api.NickedPlayerData;
import com.justixdev.eazynick.nms.netty.InjectorType;
import com.justixdev.eazynick.nms.netty.legacy.LegacyPlayerPacketInjector;
import com.justixdev.eazynick.utilities.Utils;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

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
            if (packet.getClass().getSimpleName().equals("PacketPlayOutNamedEntitySpawn")) {
                UUID uuid = ((GameProfile) Objects.requireNonNull(getField(packet.getClass(), "b")).get(packet)).getId();

                if(!this.utils.getSoonNickedPlayers().contains(uuid)) {
                    if(this.utils.getNickedPlayers().containsKey(uuid)) {
                        // Replace game profile with fake game profile (nicked player profile)
                        setField(
                                packet,
                                "b",
                                this.utils.getNickedPlayers().get(uuid).getFakeGameProfile(false)
                        );
                    }
                }
            } else if(packet.getClass().getSimpleName().equals("PacketPlayOutPlayerInfo")) {
                Object playerObject = Objects.requireNonNull(getField(packet.getClass(), "player")).get(packet);

                if(playerObject != null) {
                    UUID uuid = ((GameProfile) playerObject).getId();

                    if(this.utils.getSoonNickedPlayers().contains(uuid)
                            && this.setupYamlFile.getConfiguration().getBoolean("SeeNickSelf")
                            && ((int) getFieldValue(packet, "action") == 0))
                        return null;

                    if(this.utils.getNickedPlayers().containsKey(uuid)) {
                        // Replace game profile with fake game profile (nicked player profile)
                        NickedPlayerData nickedPlayerData = this.utils.getNickedPlayers().get(uuid);

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
            } else if(packet.getClass().getSimpleName().equals("PacketPlayOutTabComplete")) {
                setField(
                        packet,
                        "a",
                        replaceTabCompletions(this.player, (String[]) getFieldValue(packet, "a"))
                );
            } else if (packet.getClass().getSimpleName().equals("PacketPlayOutChat")
                    && this.setupYamlFile.getConfiguration().getBoolean("OverwriteMessagePackets")) {
                // Get chat message from packet and replace names
                Object editedComponent = this.replaceNames(getFieldValue(packet, "a"));

                // Overwrite chat message
                if(editedComponent != null)
                    setField(packet, "a", editedComponent);
            } else if(packet.getClass().getSimpleName().equals("PacketPlayOutScoreboardObjective")) {
                String name = (String) getFieldValue(packet, "b");

                if(name != null) {
                    setField(
                            packet,
                            "b",
                            this.utils.getNickedPlayers()
                                    .values()
                                    .stream()
                                    .filter(nickedPlayerData -> name.contains(nickedPlayerData.getRealName()))
                                    .findFirst()
                                    .map(nickedPlayerData -> name.replace(nickedPlayerData.getRealName(), nickedPlayerData.getNickName()))
                                    .orElse(name)
                    );
                }
            } else if(packet.getClass().getSimpleName().equals("PacketPlayOutScoreboardScore")) {
                // Replace name
                String name = (String) getFieldValue(packet, "a");

                if(name != null) {
                    setField(
                            packet,
                            "a",
                            this.utils.getNickedPlayers()
                                    .values()
                                    .stream()
                                    .filter(nickedPlayerData -> name.contains(nickedPlayerData.getRealName()))
                                    .findFirst()
                                    .map(nickedPlayerData -> name.replace(nickedPlayerData.getRealName(), nickedPlayerData.getNickName()))
                                    .orElse(name)
                    );
                }
            } else if(packet.getClass().getSimpleName().equals("PacketPlayOutScoreboardTeam")) {
                if(!(this.player.hasPermission("eazynick.bypass") && this.setupYamlFile.getConfiguration().getBoolean("EnableBypassPermission"))
                        && !(this.utils.isPluginInstalled("TAB", "NEZNAMY")
                        && this.setupYamlFile.getConfiguration().getBoolean("ChangeGroupAndPrefixAndSuffixInTAB"))
                        && this.setupYamlFile.getConfiguration().getBoolean("Settings.ChangeOptions.NameTag")) {
                    Object contentsList = getFieldValue(packet, "e");

                    if(contentsList != null) {
                        List<String> contents = new ArrayList<>((List<String>) contentsList);

                        setField(
                                packet,
                                "e",
                                contents
                                        .stream()
                                        .map(name ->
                                                this.utils.getNickedPlayers()
                                                        .values()
                                                        .stream()
                                                        .filter(nickedPlayerData -> name.contains(nickedPlayerData.getRealName()))
                                                        .findFirst()
                                                        .map(nickedPlayerData -> {
                                                            if(contents.stream().noneMatch(name2 -> name2.contains(nickedPlayerData.getNickName())))
                                                                return name.replace(nickedPlayerData.getRealName(), nickedPlayerData.getNickName());

                                                            return null;
                                                        }).orElse(null))
                                        .filter(Objects::nonNull)
                                        .collect(Collectors.toList())
                        );
                    }
                }
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
