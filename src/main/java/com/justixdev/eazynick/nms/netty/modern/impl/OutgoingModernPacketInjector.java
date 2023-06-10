package com.justixdev.eazynick.nms.netty.modern.impl;

import com.justixdev.eazynick.api.NickedPlayerData;
import com.justixdev.eazynick.nms.netty.InjectorType;
import com.justixdev.eazynick.nms.netty.modern.ModernPlayerPacketInjector;
import com.justixdev.eazynick.utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.justixdev.eazynick.nms.ReflectionHelper.*;
import static com.justixdev.eazynick.nms.netty.NMSPacketHelper.replaceTabCompletions;

@SuppressWarnings("unchecked")
public class OutgoingModernPacketInjector extends ModernPlayerPacketInjector {

    public OutgoingModernPacketInjector(Player player) {
        super(player, InjectorType.OUTGOING);
    }

    @Override
    public Object onPacketSend(Object packet) {
        boolean is1_17 = NMS_VERSION.startsWith("v1_17"),
                is1_18 = NMS_VERSION.startsWith("v1_18"),
                is1_19 = NMS_VERSION.startsWith("v1_19"),
                is1_20 = NMS_VERSION.startsWith("v1_20");

        try {
            switch (packet.getClass().getSimpleName()) {
                case "PacketPlayOutNamedEntitySpawn":
                    UUID uniqueIdToSpawn = (UUID) getFieldValue(packet, "b");

                    if(this.utils.getSoonNickedPlayers().contains(uniqueIdToSpawn))
                        return null;

                    if(this.utils.getNickedPlayers().containsKey(uniqueIdToSpawn)) {
                        // Replace uuid with fake uuid (spoofed uuid)
                        setField(
                                packet,
                                "b",
                                this.setupYamlFile.getConfiguration().getBoolean("Settings.ChangeOptions.UUID")
                                        ? this.utils.getNickedPlayers().get(uniqueIdToSpawn).getSpoofedUniqueId()
                                        : uniqueIdToSpawn
                        );
                    }
                    break;
                case "ClientboundPlayerInfoRemovePacket":
                    List<UUID> playerUniqueIds = (List<UUID>) getFieldValue(packet, "a");

                    if(playerUniqueIds.stream().noneMatch(currentUniqueId -> this.player.getUniqueId().equals(currentUniqueId))
                            && this.utils.getNickedPlayers()
                            .keySet()
                            .stream()
                            .anyMatch(currentUniqueId -> playerUniqueIds.stream().anyMatch(currentUniqueId::equals))) {
                        if (this.player.hasPermission("eazynick.bypass")
                                && this.setupYamlFile.getConfiguration().getBoolean("EnableBypassPermission"))
                            return null;

                        final UUID bypassUniqueId = UUID.fromString("00000000-0000-0000-0000-000000000000");

                        if(playerUniqueIds.contains(bypassUniqueId))
                            packet = newInstance(
                                    packet.getClass(),
                                    types(List.class),
                                    playerUniqueIds.stream().filter(currentUniqueId -> !bypassUniqueId.equals(currentUniqueId)).collect(Collectors.toList())
                            );
                        else {
                            packet = newInstance(
                                    packet.getClass(),
                                    types(List.class),
                                    playerUniqueIds
                                            .stream()
                                            .map(uniqueId -> {
                                                if(this.utils.getNickedPlayers().containsKey(uniqueId))
                                                    return this.utils.getNickedPlayers().get(uniqueId).getSpoofedUniqueId();

                                                return uniqueId;
                                            })
                                            .collect(Collectors.toList())
                            );
                        }
                    }
                    break;
                case "ClientboundPlayerInfoUpdatePacket":
                    EnumSet<? extends Enum<?>> actions = ((EnumSet<? extends Enum<?>>) getFieldValue(packet, "a"));
                    Object playerInfoDataList = getFieldValue(packet, "b");

                    if(playerInfoDataList != null) {
                        ArrayList<Object> dataToUpdate = new ArrayList<>();

                        for (Object currentPlayerInfoData : (List<Object>) playerInfoDataList) {
                            Object gameProfile = getFieldValue(currentPlayerInfoData, "b");
                            UUID uniqueIdToUpdate = (UUID) gameProfile.getClass().getMethod("getId").invoke(gameProfile);
                            Player infoPlayer = Bukkit.getPlayer(uniqueIdToUpdate);

                            if (this.utils.getSoonNickedPlayers().contains(uniqueIdToUpdate)
                                    && actions.stream().anyMatch(action -> action.name().equals("ADD_PLAYER")))
                                return null;

                            if ((infoPlayer == null)
                                    || !(this.utils.getNickedPlayers().containsKey(uniqueIdToUpdate))
                                    || (this.player.hasPermission("eazynick.bypass")
                                    && this.setupYamlFile.getConfiguration().getBoolean("EnableBypassPermission"))) {
                                dataToUpdate.add(currentPlayerInfoData);
                            } else {
                                Object entityPlayer = this.player.getClass().getMethod("getHandle").invoke(infoPlayer);
                                Object playerInteractManager = getFieldValue(entityPlayer, is1_20 ? "e" : "d");
                                NickedPlayerData nickedPlayerData = this.utils.getNickedPlayers().get(uniqueIdToUpdate);
                                boolean isSelfPacket = uniqueIdToUpdate.equals(this.player.getUniqueId());

                                dataToUpdate.add(
                                        newInstance(
                                                currentPlayerInfoData.getClass(),
                                                types(
                                                        UUID.class,
                                                        gameProfile.getClass(),
                                                        boolean.class,
                                                        int.class,
                                                        getNMSClass("world.level.EnumGamemode"),
                                                        getNMSClass("network.chat.IChatBaseComponent"),
                                                        getNMSClass("network.chat.RemoteChatSession").getDeclaredClasses()[0]
                                                ),
                                                isSelfPacket
                                                        ? uniqueIdToUpdate
                                                        : nickedPlayerData.getSpoofedUniqueId(),
                                                nickedPlayerData.getFakeGameProfile(
                                                        !isSelfPacket
                                                                && this.setupYamlFile.getConfiguration().getBoolean("Settings.ChangeOptions.UUID")
                                                ),
                                                true,
                                                getFieldValue(entityPlayer, is1_20 ? "f" : "e"),
                                                invoke(playerInteractManager, "b"),
                                                invoke(entityPlayer, (NMS_VERSION.equals("v1_19_R3") || is1_20) ? "J" : "K"),
                                                invokeStatic(
                                                        getNMSClass(NMS_VERSION.equals("v1_19_R3") || is1_20
                                                                ? "Optionull"
                                                                : "SystemUtils"
                                                        ),
                                                        "a",
                                                        types(Object.class, Function.class),
                                                        invoke(entityPlayer, (NMS_VERSION.equals("v1_19_R3") || is1_20) ? "X" : "Y"),
                                                        (Function<Object, Object>) (chatSession) -> {
                                                            try {
                                                                return invoke(chatSession, "b");
                                                            } catch (Exception ignore) {
                                                            }

                                                            return null;
                                                        }
                                                )
                                        )
                                );
                            }
                        }

                        setField(packet, "b", dataToUpdate);
                    }
                    break;
                case "PacketPlayOutTabComplete":
                    if(VERSION_13_OR_LATER) {
                        Object suggestions = getFieldValue(packet, "b");
                        Object suggestionsRange = invoke(suggestions, "getRange");
                        int suggestionsStart = (int) invoke(suggestionsRange, "getStart");
                        ArrayList<Object> suggestionsList = new ArrayList<>((List<Object>) invoke(suggestions, "getList"));
                        Map<Object, String> texts = new HashMap<>();
                        String buffer = null;

                        for (Object suggestion : suggestionsList) {
                            try {
                                String text = (String) invoke(suggestion, "getText");
                                Object range = invoke(suggestion, "getRange");

                                buffer = text.substring(
                                        0,
                                        (int) invoke(range, "getEnd") - (int) invoke(range, "getStart")
                                );

                                texts.put(suggestion, text);
                            } catch (IllegalAccessException ex) {
                                ex.printStackTrace();
                            }
                        }

                        // Player names
                        List<String> playerNames = new ArrayList<>();

                        Bukkit.getOnlinePlayers().forEach(currentPlayer -> {
                            if (this.utils.getNickedPlayers().containsKey(currentPlayer.getUniqueId())) {
                                String nickName = this.utils.getNickedPlayers().get(currentPlayer.getUniqueId()).getNickName();

                                if (texts.values().stream().anyMatch(nickName::equalsIgnoreCase)) {
                                    playerNames.add(nickName);
                                    return;
                                }
                            }

                            String name = currentPlayer.getName();

                            if (texts.values().stream().anyMatch(name::equalsIgnoreCase))
                                playerNames.add(name);
                        });

                        if (!playerNames.isEmpty() || (buffer == null) || texts.containsKey("@p")) {
                            if (buffer != null)
                                buffer = buffer.toLowerCase();
                            else
                                buffer = "";

                            // Player names are in the suggestions
                            suggestionsList.removeIf(suggestion ->
                                    playerNames.stream().anyMatch(playerName ->
                                            playerName.equalsIgnoreCase(texts.get(suggestion)))
                            );

                            for (Player currentPlayer : Bukkit.getOnlinePlayers()) {
                                String name = this.utils.getNickedPlayers().containsKey(currentPlayer.getUniqueId())
                                        ? this.utils.getNickedPlayers().get(currentPlayer.getUniqueId()).getNickName()
                                        : currentPlayer.getName();

                                if (buffer.isEmpty() || name.toLowerCase().startsWith(buffer))
                                    suggestionsList.add(this.getAsSuggestion(suggestionsStart, buffer, name));
                            }
                        }

                        StringBuilder command = new StringBuilder();

                        for (int i = 1; i <= (suggestionsStart - 2 /* slash & space */); i++)
                            command.append("?");

                        setField(
                                packet,
                                "b",
                                invokeStatic(
                                        findClass("com.mojang.brigadier.suggestion.Suggestions"),
                                        "create",
                                        types(String.class, Collection.class),
                                        "/" + command + " " + buffer,
                                        suggestionsList
                                )
                        );
                    } else {
                        setField(
                                packet,
                                "a",
                                replaceTabCompletions(this.player, (String[]) getFieldValue(packet, "a"))
                        );
                    }
                    break;
                case "ClientboundSystemChatPacket":
                    if(this.setupYamlFile.getConfiguration().getBoolean("OverwriteMessagePackets")) {
                        // Get content
                        String content = (String) getFieldValue(packet, "content");

                        if(content != null) {
                            // Replace names
                            Object editedComponent = this.replaceNames(this.deserialize(content), true);

                            // Overwrite chat message
                            if (editedComponent != null)
                                return newInstance(
                                        packet.getClass(),
                                        types(
                                                getNMSClass("network.chat.IChatBaseComponent"),
                                                boolean.class
                                        ),
                                        editedComponent,
                                        invoke(packet, "c")
                                );
                        }
                    }
                    break;
                case "PacketPlayOutScoreboardObjective":
                    // Replace name
                    if(VERSION_13_OR_LATER)
                        setField(packet,
                                is1_17 || is1_18 || is1_19 || is1_20
                                        ? "e"
                                        : "b",
                                this.replaceNames(
                                        getFieldValue(
                                                packet,
                                                is1_17 || is1_18 || is1_19 || is1_20
                                                        ? "e"
                                                        : "b"
                                        ),
                                        false
                                )
                        );
                    else {
                        String name = (String) getFieldValue(packet, "b");

                        if(name != null)
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
                    break;
                case "PacketPlayOutScoreboardScore":
                    String name = (String) getFieldValue(packet, "a");

                    if(name != null)
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
                    break;
                // old packets
                case "PacketPlayOutPlayerInfo":
                    Object b = getFieldValue(packet, "b");

                    if(b != null) {
                        for (Object playerInfoData : (List<?>) b) {
                            Object gameProfile = getFieldValue(
                                    playerInfoData,
                                    is1_17 || is1_18 || is1_19 || is1_20
                                            ? "c"
                                            : "d"
                            );
                            UUID playerInfoDataUniqueId = (UUID) invoke(gameProfile, "getId");

                            if(this.utils.getSoonNickedPlayers().contains(playerInfoDataUniqueId)
                                    && this.setupYamlFile.getConfiguration().getBoolean("SeeNickSelf")
                                    && getFieldValue(packet, "a").toString().endsWith("ADD_PLAYER"))
                                return null;

                            if(this.utils.getNickedPlayers().containsKey(playerInfoDataUniqueId)) {
                                // Replace game profile with fake game profile (nicked player profile)
                                setField(
                                        playerInfoData,
                                        is1_17 || is1_18 || is1_19 || is1_20
                                                ? "c"
                                                : "d",
                                        this.utils.getNickedPlayers().get(playerInfoDataUniqueId).getFakeGameProfile(
                                                !playerInfoDataUniqueId.equals(player.getUniqueId())
                                                        && setupYamlFile.getConfiguration().getBoolean("Settings.ChangeOptions.UUID")
                                        )
                                );
                            }
                        }
                    }
                    break;
                case "PacketPlayOutChat":
                    if(this.setupYamlFile.getConfiguration().getBoolean("OverwriteMessagePackets")) {
                        // Get chat message from packet and replace names
                        Object editedComponent = this.replaceNames(getFieldValue(packet, "a"), true);

                        // Overwrite chat message
                        if (editedComponent != null)
                            setField(packet, "a", editedComponent);
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

    private Object getAsSuggestion(int suggestionsStart, String buffer, String match) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        int matchingChars = 0;

        for (int i = 0; i < buffer.length(); i++) {
            if (match.toLowerCase().charAt(i) == buffer.charAt(i))
                matchingChars++;
            else
                break;
        }

        Class<?> stringRangeClass = Class.forName("com.mojang.brigadier.context.StringRange");

        return newInstance(
                findClass("com.mojang.brigadier.suggestion.Suggestion"),
                types(stringRangeClass, String.class),
                newInstance(
                        stringRangeClass,
                        types(int.class, int.class),
                        suggestionsStart,
                        suggestionsStart + matchingChars
                ),
                match
        );
    }

    private Object replaceNames(Object iChatBaseComponent, boolean isChatPacket) {
        Utils utils = this.eazyNick.getUtils();

        boolean is1_17 = NMS_VERSION.startsWith("v1_17"),
                is1_18 = NMS_VERSION.startsWith("v1_18"),
                is1_19 = NMS_VERSION.startsWith("v1_19"),
                is1_20 = NMS_VERSION.startsWith("v1_20");
        Object editedComponent = null;

        try {
            if(iChatBaseComponent != null) {
                // Extract text from message component
                StringBuilder fullText = new StringBuilder();

                for (Object partlyIChatBaseComponent : ((List<Object>) invoke(
                        iChatBaseComponent,
                        is1_19 || is1_20
                                ? "c"
                                : is1_18
                                        ? "b"
                                        :
                                        Bukkit.getVersion().contains("1.14.4")
                                                || NMS_VERSION.startsWith("v1_15")
                                                || NMS_VERSION.startsWith("v1_16")
                                                || is1_17
                                                ? "getSiblings"
                                                : "a"
                ))) {
                    if (partlyIChatBaseComponent.getClass().getSimpleName().equals("ChatComponentText")
                            || partlyIChatBaseComponent.getClass().getSimpleName().equals("IChatMutableComponent")) {
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
                if (!((fullText.toString().contains(ChatColor.stripColor(utils.getLastChatMessage())) && isChatPacket)
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
        } catch (Exception ignore) {
        }

        return editedComponent;
    }

    private String serialize(Object iChatBaseComponent) {
        boolean is1_18 = NMS_VERSION.startsWith("v1_18"),
                is1_19 = NMS_VERSION.startsWith("v1_19"),
                is1_20 = NMS_VERSION.startsWith("v1_20");

        try {
            Class<?> iChatBaseComponentClass = getNMSClass(
                    NMS_VERSION.startsWith("v1_17")
                            || is1_18
                            || is1_19
                            || is1_20
                            ? "network.chat.IChatBaseComponent"
                            : "IChatBaseComponent"
            );

            return (String) invokeStatic(
                    NMS_VERSION.equals("v1_8_R1")
                            ? getNMSClass("ChatSerializer")
                            : iChatBaseComponentClass.getDeclaredClasses()[0],
                    "a",
                    types(iChatBaseComponentClass),
                    iChatBaseComponent
            );
        } catch (Exception ex) {
            return "";
        }
    }

    public Object deserialize(String json) {
        boolean is1_18 = NMS_VERSION.startsWith("v1_18"),
                is1_19 = NMS_VERSION.startsWith("v1_19"),
                is1_20 = NMS_VERSION.startsWith("v1_20");

        try {
            return invokeStatic(
                    NMS_VERSION.equals("v1_8_R1")
                            ? getNMSClass("ChatSerializer")
                            : getNMSClass(
                                    NMS_VERSION.startsWith("v1_17")
                                            || is1_18
                                            || is1_19
                                            || is1_20
                                            ? "network.chat.IChatBaseComponent"
                                            : "IChatBaseComponent"
                            )
                                    .getDeclaredClasses()[0],
                    is1_18 || is1_19 || is1_20
                            ? "b"
                            : "a",
                    types(String.class),
                    json
            );
        } catch (Exception ex) {
            return "";
        }
    }

}
