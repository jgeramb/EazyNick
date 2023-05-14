package com.justixdev.eazynick.nms;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.utilities.configuration.yaml.SetupYamlFile;
import lombok.Getter;
import lombok.Setter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;

import static com.justixdev.eazynick.nms.ReflectionHelper.*;

public class ScoreboardTeamHandler {

    private final Player player;
    @Getter
    private final String nickName, realName;
    private final List<Player> receivedPacket;
    private String teamName;
    @Setter
    private String prefix, suffix;
    private Object packet;

    private final EazyNick eazyNick;

    public ScoreboardTeamHandler(Player player, String nickName, String realName, String prefix, String suffix, int sortID, String rank) {
        this.player = player;
        this.nickName = nickName;
        this.realName = realName;
        this.receivedPacket = new ArrayList<>(Bukkit.getOnlinePlayers());

        this.teamName = sortID + rank.substring(0, Math.min(14 - String.valueOf(sortID).length(), rank.length())) + player.getName();
        this.teamName = this.teamName.substring(0, Math.min(this.teamName.length(), 16));

        this.prefix = (prefix == null) ? "" : prefix;
        this.suffix = (suffix == null) ? "" : suffix;

        this.eazyNick = EazyNick.getInstance();
    }

    public void destroyTeam() {
        if(NMS_VERSION.equals("v1_7_R4") || NMS_VERSION.equals("v1_8_R1"))
            return;

        try {
            boolean is1_17 = NMS_VERSION.startsWith("v1_17"),
                    is1_18 = NMS_VERSION.startsWith("v1_18"),
                    is1_19 = NMS_VERSION.startsWith("v1_19");

            this.packet = this.newPacket();

            // Set packet fields
            if(this.eazyNick.getUtils().isVersion13OrLater()) {
                try {
                    setField(this.packet, "a", this.teamName);
                    setField(this.packet, "b", this.getAsIChatBaseComponent(this.teamName));
                    setField(this.packet, "e", "ALWAYS");
                    setField(this.packet, "i", 1);
                } catch (Exception ex) {
                    if(is1_17 || is1_18 || is1_19) {
                        setField(this.packet, "h", 1);
                        setField(this.packet, "i", this.teamName);

                        Object scoreboardTeam = newInstance(
                                getNMSClass("world.scores.ScoreboardTeam"),
                                new Class<?>[]{
                                        getNMSClass("world.scores.Scoreboard"),
                                        String.class
                                },
                                null,
                                this.teamName
                        );
                        setField(
                                scoreboardTeam,
                                (is1_18 || is1_19)
                                        ? "d"
                                        : "e",
                                this.teamName
                        );

                        setField(
                                this.packet,
                                "k",
                                newInstance(getSubClass(this.packet.getClass(), "b"))
                        );
                    } else {
                        setField(this.packet, "a", this.teamName);
                        setField(this.packet, "b", this.getAsIChatBaseComponent(this.teamName));
                        setField(this.packet, "e", "ALWAYS");
                        setField(this.packet, "j", 1);
                    }
                }
            } else {
                try {
                    setField(this.packet, "a", this.teamName);
                    setField(this.packet, "b", this.teamName);
                    setField(this.packet, "e", "ALWAYS");
                    setField(this.packet, "h", 1);
                } catch (Exception ignore) {
                    setField(this.packet, "a", this.teamName);
                    setField(this.packet, "b", this.teamName);
                    setField(this.packet, "e", "ALWAYS");
                    setField(this.packet, "i", 1);
                }
            }

            // Send packet to destroy team
            Bukkit.getOnlinePlayers()
                    .stream()
                    .filter(this.receivedPacket::contains)
                    .forEach(currentPlayer -> sendPacketNMS(currentPlayer, this.packet));
        } catch (Exception ex) {
            Bukkit.getLogger().log(
                    Level.SEVERE,
                    "Could not send packet to destroy scoreboard team of "
                            + this.realName
                            + " ("
                            + this.nickName
                            + "): "
                            + ex.getMessage()
            );
        }
    }

    public void createTeam() {
        SetupYamlFile setupYamlFile = this.eazyNick.getSetupYamlFile();

        if(NMS_VERSION.equals("v1_7_R4") || NMS_VERSION.equals("v1_8_R1"))
            return;

        boolean is1_17 = NMS_VERSION.startsWith("v1_17"),
            is1_18 = NMS_VERSION.startsWith("v1_18"),
            is1_19 = NMS_VERSION.startsWith("v1_19");

        Bukkit.getOnlinePlayers().forEach(currentPlayer -> {
            try {
                // Create packet instance
                this.packet = this.newPacket();

                // Determine which prefix should be shown
                String prefixForPlayer = this.prefix;
                String suffixForPlayer = this.suffix;
                List<String> contents = Arrays.asList(this.nickName, this.realName);

                if(currentPlayer.hasPermission("eazynick.bypass")
                        && setupYamlFile.getConfiguration().getBoolean("EnableBypassPermission")
                        && setupYamlFile.getConfiguration().getBoolean("BypassFormat.Show")) {
                    prefixForPlayer = setupYamlFile.getConfigString(this.player, "BypassFormat.NameTagPrefix");
                    suffixForPlayer = setupYamlFile.getConfigString(this.player, "BypassFormat.NameTagSuffix");
                }

                // Replace placeholders
                if(this.eazyNick.getUtils().isPluginInstalled("PlaceholderAPI")) {
                    prefixForPlayer = PlaceholderAPI.setPlaceholders(this.player, prefixForPlayer);
                    suffixForPlayer = PlaceholderAPI.setPlaceholders(this.player, suffixForPlayer);
                }

                // Make sure the prefix and suffix are not longer than 16 characters
                prefixForPlayer = prefixForPlayer.substring(0, Math.min(prefixForPlayer.length(), 16));
                suffixForPlayer = suffixForPlayer.substring(0, Math.min(suffixForPlayer.length(), 16));

                // Set packet fields
                if(this.eazyNick.getUtils().isVersion13OrLater()) {
                    try {
                        this.setTeamFields_1_16(prefixForPlayer, suffixForPlayer);
                        setField(this.packet, "g", contents);
                        setField(this.packet, "i", 0);
                    } catch (Exception ex) {
                        String colorName = (is1_17 || is1_18 || is1_19) ? "v" : "RESET";

                        if(this.prefix.length() > 1) {
                            for (int i = this.prefix.length() - 1; i >= 0; i--) {
                                if(i < (this.prefix.length() - 1)) {
                                    if(this.prefix.charAt(i) == '§') {
                                        char c = this.prefix.charAt(i + 1);

                                        if((c != 'k') && (c != 'l') && (c != 'm') && (c != 'n') && (c != 'o')) {
                                            switch (c) {
                                                case '0':
                                                    colorName = (is1_17 || is1_18 || is1_19) ? "a" : "BLACK";
                                                    break;
                                                case '1':
                                                    colorName = (is1_17 || is1_18 || is1_19) ? "b" : "DARK_BLUE";
                                                    break;
                                                case '2':
                                                    colorName = (is1_17 || is1_18 || is1_19) ? "c" : "DARK_GREEN";
                                                    break;
                                                case '3':
                                                    colorName = (is1_17 || is1_18 || is1_19) ? "d" : "DARK_AQUA";
                                                    break;
                                                case '4':
                                                    colorName = (is1_17 || is1_18 || is1_19) ? "e" : "DARK_RED";
                                                    break;
                                                case '5':
                                                    colorName = (is1_17 || is1_18 || is1_19) ? "f" : "DARK_PURPLE";
                                                    break;
                                                case '6':
                                                    colorName = (is1_17 || is1_18 || is1_19) ? "g" : "GOLD";
                                                    break;
                                                case '7':
                                                    colorName = (is1_17 || is1_18 || is1_19) ? "h" : "GRAY";
                                                    break;
                                                case '8':
                                                    colorName = (is1_17 || is1_18 || is1_19) ? "i" : "DARK_GRAY";
                                                    break;
                                                case '9':
                                                    colorName = (is1_17 || is1_18 || is1_19) ? "j" : "BLUE";
                                                    break;
                                                case 'a':
                                                    colorName = (is1_17 || is1_18 || is1_19) ? "k" : "GREEN";
                                                    break;
                                                case 'b':
                                                    colorName = (is1_17 || is1_18 || is1_19) ? "l" : "AQUA";
                                                    break;
                                                case 'c':
                                                    colorName = (is1_17 || is1_18 || is1_19) ? "m" : "RED";
                                                    break;
                                                case 'd':
                                                    colorName = (is1_17 || is1_18 || is1_19) ? "n" : "LIGHT_PURPLE";
                                                    break;
                                                case 'e':
                                                    colorName = (is1_17 || is1_18 || is1_19) ? "o" : "YELLOW";
                                                    break;
                                                case 'f':
                                                    colorName = (is1_17 || is1_18 || is1_19) ? "p" : "WHITE";
                                                    break;
                                                case 'r':
                                                    colorName = (is1_17 || is1_18 || is1_19) ? "v" : "RESET";
                                                    break;
                                                default:
                                                    break;
                                            }

                                            break;
                                        }
                                    }
                                }
                            }
                        }

                        if(is1_17 || is1_18 || is1_19) {
                            setField(this.packet, "h", 0);
                            setField(this.packet, "i", this.teamName);
                            setField(this.packet, "j", contents);

                            Object scoreboardTeam = newInstance(
                                    getNMSClass("world.scores.ScoreboardTeam"),
                                    new Class<?>[] {
                                            getNMSClass("world.scores.Scoreboard"),
                                            String.class
                                    },
                                    this.teamName
                            );
                            setField(scoreboardTeam, (is1_18 || is1_19) ? "d" : "e", this.teamName);
                            setField(scoreboardTeam, (is1_18 || is1_19) ? "g" : "h", this.getAsIChatBaseComponent(this.prefix));
                            setField(scoreboardTeam, (is1_18 || is1_19) ? "h" : "i", this.getAsIChatBaseComponent(this.suffix));
                            setField(scoreboardTeam, (is1_18 || is1_19) ? "i" : "j", false);
                            setField(scoreboardTeam, (is1_18 || is1_19) ? "j" : "k", false);
                            setField(
                                    scoreboardTeam,
                                    is1_18 || is1_19
                                            ? "m"
                                            : "n",
                                    getStaticFieldValue(getNMSClass("EnumChatFormat"), colorName)
                            );

                            setField(
                                    this.packet,
                                    "k",
                                    newInstance(getSubClass(this.packet.getClass(), "b"))
                            );
                        } else {
                            this.setTeamFields_1_16(prefixForPlayer, suffixForPlayer);
                            setField(
                                    this.packet,
                                    "g",
                                    Objects.requireNonNull(getField(
                                            getNMSClass("EnumChatFormat"),
                                            colorName
                                    )).get(null)
                            );
                            setField(this.packet, "h", contents);
                            setField(this.packet, "j", 0);
                        }
                    }
                } else {
                    try {
                        setField(this.packet, "a", this.teamName);
                        setField(this.packet, "b", this.teamName);
                        setField(this.packet, "c", prefixForPlayer);
                        setField(this.packet, "d", suffixForPlayer);
                        setField(this.packet, "e", "ALWAYS");
                        setField(this.packet, "g", contents);
                        setField(this.packet, "h", 0);
                    } catch (Exception ignore) {
                        setField(this.packet, "a", this.teamName);
                        setField(this.packet, "b", this.teamName);
                        setField(this.packet, "c", prefixForPlayer);
                        setField(this.packet, "d", suffixForPlayer);
                        setField(this.packet, "e", "ALWAYS");
                        setField(this.packet, "h", contents);
                        setField(this.packet, "i", 0);
                    }
                }

                // Send packet to create team
                sendPacketNMS(currentPlayer, this.packet);

                if(!this.receivedPacket.contains(currentPlayer))
                    this.receivedPacket.add(currentPlayer);
            } catch (Exception ex) {
                Bukkit.getLogger().log(
                        Level.SEVERE,
                        "Could not send packet to create scoreboard team of "
                                + this.realName
                                + " ("
                                + this.nickName
                                + "): "
                                + ex.getMessage()
                );
            }
        });
    }

    private Object newPacket() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        boolean is1_17 = NMS_VERSION.startsWith("v1_17"),
                is1_18 = NMS_VERSION.startsWith("v1_18"),
                is1_19 = NMS_VERSION.startsWith("v1_19");

        return newInstance(
                getNMSClass(
                        is1_17 || is1_18 || is1_19
                                ? "network.protocol.game.PacketPlayOutScoreboardTeam"
                                : "PacketPlayOutScoreboardTeam"
                ),
                is1_17 || is1_18 || is1_19
                        ? types(
                                String.class,
                                int.class,
                                Optional.class,
                                Collection.class
                        )
                        : new Class[0],
                is1_17 || is1_18 || is1_19
                        ? new Object[] { null, 0, null, new ArrayList<>() }
                        : new Object[0]
        );
    }

    private void setTeamFields_1_16(String prefixForPlayer, String suffixForPlayer) throws IllegalAccessException {
        setField(this.packet, "a", this.teamName);
        setField(this.packet, "b", this.getAsIChatBaseComponent(this.teamName));
        setField(this.packet, "c", this.getAsIChatBaseComponent(prefixForPlayer));
        setField(this.packet, "d", this.getAsIChatBaseComponent(suffixForPlayer));
        setField(this.packet, "e", "ALWAYS");
    }

    private Object getAsIChatBaseComponent(String text) {
        boolean is1_18 = NMS_VERSION.startsWith("1_18"),
                is1_19 = NMS_VERSION.startsWith("1_19");

        try {
            // Create IChatBaseComponent from String using ChatSerializer
            return invokeStatic(
                    getNMSClass(
                            NMS_VERSION.startsWith("v1_17") || is1_18 || is1_19
                                    ? "network.chat.IChatBaseComponent"
                                    : "IChatBaseComponent"
                    )
                            .getDeclaredClasses()[0],
                    is1_18 || is1_19
                            ? "b"
                            : "a",
                    new Class<?>[] {
                            String.class
                    },
                    ComponentSerializer.toString(TextComponent.fromLegacyText(text))
            );
        } catch (Exception ignore) {
            return null;
        }
    }

}