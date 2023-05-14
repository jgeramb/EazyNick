package com.justixdev.eazynick.nms;

import com.google.common.hash.Hashing;
import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.utilities.AsyncTask;
import com.justixdev.eazynick.utilities.ItemBuilder;
import com.justixdev.eazynick.utilities.Utils;
import com.justixdev.eazynick.utilities.configuration.yaml.SetupYamlFile;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static com.justixdev.eazynick.nms.ReflectionHelper.*;

public class NMSNickManager {

    private final Player player;

    private final EazyNick eazyNick;
    private final SetupYamlFile setupYamlFile;
    private final Utils utils;

    public NMSNickManager(Player player) {
        this.player = player;

        this.eazyNick = EazyNick.getInstance();
        this.setupYamlFile = eazyNick.getSetupYamlFile();
        this.utils = eazyNick.getUtils();
    }

    private void sendPacket(Player player, Object packet) {
        if(!(player.canSee(this.player)
                || player.getWorld().getName().equals(this.player.getWorld().getName())))
            return;

        if(player.getEntityId() != this.player.getEntityId()) {
            // Send packet to player who is not the player being nicked
            if(!(player.hasPermission("eazynick.bypass")
                    && this.setupYamlFile.getConfiguration().getBoolean("EnableBypassPermission")))
                sendPacketNMS(player, packet);
        } else if(this.setupYamlFile.getConfiguration().getBoolean("SeeNickSelf")) {
            // Send packet to player being nicked
            sendPacketNMS(player, packet);
        }
    }

    private void sendPacketExceptSelf(Object packet, Collection<? extends Player> players) {
        // Send packet to player who is not the player being nicked
        players.stream()
                .filter(Player::isOnline)
                .filter(currentPlayer ->
                        currentPlayer.getWorld().getName().equals(this.player.getWorld().getName())
                )
                .filter(currentPlayer ->
                        NMS_VERSION.startsWith("v1_7")
                                || !this.player.getGameMode().equals(GameMode.SPECTATOR)
                )
                .filter(currentPlayer -> (currentPlayer.getEntityId() != this.player.getEntityId()))
                .filter(currentPlayer ->
                        !(currentPlayer.hasPermission("eazynick.bypass")
                                && this.setupYamlFile.getConfiguration().getBoolean("EnableBypassPermission"))
                )
                .forEach(currentPlayer -> sendPacketNMS(currentPlayer, packet));
    }

    private void updatePlayerListName(Object packetPlayOutPlayerInfoUpdate,
                                      Object packetPlayOutPlayerInfoRemove,
                                      Object packetPlayOutPlayerInfoAdd,
                                      Player currentPlayer,
                                      Object playerConnection)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Object networkManager = getFieldValue(playerConnection, "networkManager");
        int version = (int) invoke(networkManager, "getVersion");

        if (version < 28) {
            // Send packets to remove player from list and add it again
            sendPacketNMS(currentPlayer, packetPlayOutPlayerInfoRemove);
            sendPacketNMS(currentPlayer, packetPlayOutPlayerInfoAdd);
        } else
            // Send packet to update list name
            sendPacketNMS(currentPlayer, packetPlayOutPlayerInfoUpdate);
    }

    public void setPlayerListName(String name) {
        if (NMS_VERSION.equals("v1_7_R4")) {
            try {
                Object entityPlayer = invoke(this.player, "getHandle");

                // Make sure name is not longer than 16 characters
                name = name.substring(0, Math.min(name.length(), 16));

                // Set listName field of EntityPlayer -> getPlayerListName()
                setField(entityPlayer, "listName", name);

                Class<?> playOutPlayerInfo = Objects.requireNonNull(getNMSClass("PacketPlayOutPlayerInfo")),
                        entityPlayerClass = Objects.requireNonNull(getNMSClass("EntityPlayer"));

                for (Player currentPlayer : Bukkit.getOnlinePlayers()) {
                    Object packetPlayOutPlayerInfoUpdate =
                            invokeStatic(
                                    playOutPlayerInfo,
                                    "updateDisplayName",
                                    types(entityPlayerClass),
                                    entityPlayer
                            ),
                            packetPlayOutPlayerInfoRemove = invokeStatic(
                                    playOutPlayerInfo,
                                    "removePlayer",
                                    types(entityPlayerClass),
                                    entityPlayer
                            ),
                            packetPlayOutPlayerInfoAdd = invokeStatic(
                                    playOutPlayerInfo,
                                    "addPlayer",
                                    types(entityPlayerClass),
                                    entityPlayer
                            );

                    if (!currentPlayer.canSee(this.player))
                        return;

                    if (!currentPlayer.getUniqueId().equals(this.player.getUniqueId())) {
                        if (currentPlayer.hasPermission("eazynick.bypass")
                                && this.setupYamlFile.getConfiguration().getBoolean("EnableBypassPermission"))
                            return;

                        Object playerConnection = getFieldValue(
                                invoke(currentPlayer, "getHandle"),
                                "playerConnection"
                        );

                        updatePlayerListName(
                                packetPlayOutPlayerInfoUpdate,
                                packetPlayOutPlayerInfoRemove,
                                packetPlayOutPlayerInfoAdd,
                                currentPlayer,
                                playerConnection
                        );
                    } else if (this.setupYamlFile.getConfiguration().getBoolean("SeeNickSelf")) {
                        Object playerConnection = getFieldValue(entityPlayer, "playerConnection");

                        updatePlayerListName(
                                packetPlayOutPlayerInfoUpdate,
                                packetPlayOutPlayerInfoRemove,
                                packetPlayOutPlayerInfoAdd,
                                currentPlayer,
                                playerConnection
                        );
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            final String finalName = name;
            boolean is1_17To1_19 = NMS_VERSION.startsWith("v1_17")
                    || NMS_VERSION.startsWith("v1_18")
                    || NMS_VERSION.startsWith("v1_19"),
                    is1_19_R2OrLater = NMS_VERSION.equals("v1_19_R2") || NMS_VERSION.equals("v1_19_R3");

            Class<?> enumPlayerInfoActionClass =
                    NMS_VERSION.equals("v1_8_R1")
                            ? getNMSClass("EnumPlayerInfoAction")
                            : getSubClass(
                                    getNMSClass(
                                            is1_19_R2OrLater
                                                    ? "network.protocol.game.ClientboundPlayerInfoUpdatePacket"
                                                    : is1_17To1_19
                                                            ? "network.protocol.game.PacketPlayOutPlayerInfo"
                                                            : "PacketPlayOutPlayerInfo"
                                    ),
                                    is1_19_R2OrLater
                                            ? "a"
                                            : "EnumPlayerInfoAction"
                            );

            Object entityPlayer;

            try {
                entityPlayer = invoke(this.player, "getHandle");
            } catch (Exception ignore) {
                return;
            }

            Object entityPlayerArray = toArray(entityPlayer);

            // Run task synchronously
            Bukkit.getScheduler().runTask(this.eazyNick, () -> {
                try {
                    // Set listName field of EntityPlayer -> getPlayerListName()
                    Class<?> craftChatMessage = getCraftClass("util.CraftChatMessage");

                    setField(
                            entityPlayer,
                            "listName",
                            VERSION_13_OR_LATER
                                    ? invokeStatic(
                                            craftChatMessage,
                                            "fromStringOrNull",
                                            types(String.class),
                                            finalName)
                                    : ((Object[]) Objects.requireNonNull(invokeStatic(
                                            craftChatMessage,
                                            "fromString",
                                            types(String.class),
                                            finalName)))[0]);

                    for (Player currentPlayer : Bukkit.getOnlinePlayers()) {
                        // Send packet to update list name
                        sendPacket(
                                currentPlayer,
                                newInstance(
                                        getNMSClass(
                                                is1_19_R2OrLater
                                                        ? "network.protocol.game.ClientboundPlayerInfoUpdatePacket"
                                                        : is1_17To1_19
                                                                ? "network.protocol.game.PacketPlayOutPlayerInfo"
                                                                : "PacketPlayOutPlayerInfo"),
                                        types(
                                                enumPlayerInfoActionClass,
                                                is1_19_R2OrLater ? entityPlayer.getClass() : entityPlayerArray.getClass()
                                        ),
                                        getStaticFieldValue(
                                                enumPlayerInfoActionClass,
                                                NMS_VERSION.equals("v1_19_R3")
                                                        ? "f"
                                                        : is1_17To1_19
                                                                ? "d"
                                                                : "UPDATE_DISPLAY_NAME"
                                        ),
                                        is1_19_R2OrLater ? entityPlayer : entityPlayerArray
                                )
                        );
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    public void updatePlayer() {
        try {
            boolean is1_17 = NMS_VERSION.startsWith("v1_17"),
                    is1_18 = NMS_VERSION.startsWith("v1_18"),
                    is1_19 = NMS_VERSION.startsWith("v1_19");
            boolean skinsRestorer = this.utils.isPluginInstalled("SkinsRestorer")
                    && this.setupYamlFile.getConfiguration().getBoolean("ChangeSkinsRestorerSkin");

            final Location playerLocation = this.player.getLocation().clone();
            final Collection<? extends Player> players = Bukkit.getOnlinePlayers();
            Object entityPlayer = invoke(this.player, "getHandle");
            Object entityPlayerArray = toArray(entityPlayer);
            Object worldClient = invoke(
                    entityPlayer,
                    NMS_VERSION.equals("v1_19_R3")
                            ? "Y"
                            : NMS_VERSION.equals("v1_19_R2")
                                    ? "cH"
                                    : Bukkit.getVersion().contains("1.19.2")
                                            ? "cC"
                                            : is1_19
                                                    ? "cD"
                                                    : is1_18
                                                            ? "cA"
                                                            : "getWorld"
            );
            Object worldData = invoke(
                    worldClient,
                    is1_18 || is1_19
                            ? NMS_VERSION.equals("v1_19_R2")
                                    ? "o_"
                                    : "n_"
                            : "getWorldData"
            );
            Object interactManager = getFieldValue(
                    entityPlayer,
                    is1_17 || is1_18 || is1_19
                            ? "d"
                            : "playerInteractManager"
            );

            // Remove player from world and list
            players.stream().filter(Player::isOnline).forEach(currentPlayer -> {
                try {
                    if(!this.utils.getSoonNickedPlayers().contains(this.player.getUniqueId())) {
                        // Destroy entity
                        if(
                                !(
                                this.utils.isPluginInstalled("ViaRewind")
                                        || utils.isPluginInstalled("ViaBackwards")
                                        || utils.isPluginInstalled("ProtocolSupport")
                                        && currentPlayer.equals(this.player)
                                ) && !skinsRestorer
                        ) {
                            if(is1_17 && !Bukkit.getVersion().contains("1.17.1"))
                                sendPacket(currentPlayer, newInstance(
                                        getNMSClass("network.protocol.game.PacketPlayOutEntityDestroy"),
                                        types(int.class),
                                        this.player.getEntityId())
                                );
                            sendPacket(currentPlayer, newInstance(
                                    getNMSClass(
                                            is1_17 || is1_18 || is1_19
                                                    ? "network.protocol.game.PacketPlayOutEntityDestroy"
                                                    : "PacketPlayOutEntityDestroy"
                                    ),
                                    types(int[].class),
                                    Array.newInstance(int.class, this.player.getEntityId())
                            ));
                        }

                        // Remove player from list
                        if(NMS_VERSION.equals("v1_19_R2") || NMS_VERSION.equals("v1_19_R3")) {
                            sendPacket(
                                    currentPlayer,
                                    newInstance(
                                            getNMSClass("network.protocol.game.ClientboundPlayerInfoRemovePacket"),
                                            types(List.class),
                                            Collections.singletonList(this.player.getUniqueId())
                                    )
                            );
                        } else {
                            Class<?> packetPlayOutPlayerInfoClass = getNMSClass(
                                    is1_17 || is1_18 || is1_19
                                            ? "network.protocol.game.PacketPlayOutPlayerInfo"
                                            : "PacketPlayOutPlayerInfo"
                            );
                            Class<?> enumPlayerInfoActionClass = NMS_VERSION.equals("v1_8_R1")
                                    ? getNMSClass("EnumPlayerInfoAction")
                                    : getSubClass(
                                    packetPlayOutPlayerInfoClass,
                                    "EnumPlayerInfoAction"
                            );

                            sendPacket(
                                    currentPlayer,
                                    NMS_VERSION.equals("v1_7_R4")
                                            ? invokeStatic(
                                                    getNMSClass("PacketPlayOutPlayerInfo"),
                                                    "removePlayer",
                                                    types(getNMSClass("EntityPlayer")),
                                                    entityPlayer
                                            )
                                            : newInstance(
                                                    packetPlayOutPlayerInfoClass,
                                                    types(enumPlayerInfoActionClass, entityPlayerArray.getClass()),
                                                    getStaticFieldValue(
                                                            enumPlayerInfoActionClass,
                                                            is1_17 || is1_18 || is1_19
                                                                    ? "e"
                                                                    : "REMOVE_PLAYER"
                                                    ),
                                                    entityPlayerArray
                                            )
                            );
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            new AsyncTask(new AsyncTask.AsyncRunnable() {

                @Override
                public void run() {
                    if(!eazyNick.isEnabled() || !player.isOnline())
                        return;

                    try {
                        utils.getSoonNickedPlayers().remove(player.getUniqueId());

                        // Add to list and spawn
                        if(!utils.getSoonNickedPlayers().contains(player.getUniqueId())) {
                            players.stream().filter(Player::isOnline).forEach(currentPlayer -> {
                                try {
                                    // Add player to list
                                    if(NMS_VERSION.equals("v1_19_R2") || NMS_VERSION.equals("v1_19_R3")) {
                                        Class<?> enumPlayerInfoActionClass = getSubClass(
                                                getNMSClass("network.protocol.game.ClientboundPlayerInfoUpdatePacket"),
                                                "a"
                                        );

                                        sendPacket(
                                                currentPlayer,
                                                newInstance(
                                                        getNMSClass("network.protocol.game.ClientboundPlayerInfoUpdatePacket"),
                                                        types(enumPlayerInfoActionClass, entityPlayer.getClass()),
                                                        getStaticFieldValue(enumPlayerInfoActionClass, "a"),
                                                        entityPlayer
                                                )
                                        );
                                        sendPacket(
                                                currentPlayer,
                                                newInstance(
                                                        getNMSClass("network.protocol.game.ClientboundPlayerInfoUpdatePacket"),
                                                        types(enumPlayerInfoActionClass, entityPlayer.getClass()),
                                                        getStaticFieldValue(enumPlayerInfoActionClass, "d"),
                                                        entityPlayer
                                                )
                                        );
                                    } else {
                                        Class<?> enumPlayerInfoActionClass = NMS_VERSION.equals("v1_8_R1")
                                                ? getNMSClass("EnumPlayerInfoAction")
                                                : getSubClass(
                                                        getNMSClass(
                                                                is1_17 || is1_18 || is1_19
                                                                        ? "network.protocol.game.PacketPlayOutPlayerInfo"
                                                                        : "PacketPlayOutPlayerInfo"
                                                        ),
                                                        "EnumPlayerInfoAction"
                                                );

                                        sendPacket(
                                                currentPlayer,
                                                NMS_VERSION.equals("v1_7_R4")
                                                        ? invokeStatic(
                                                                getNMSClass("PacketPlayOutPlayerInfo"),
                                                                "addPlayer",
                                                                types(getNMSClass("EntityPlayer")),
                                                                entityPlayer
                                                        )
                                                        : newInstance(
                                                                getNMSClass(
                                                                        is1_17 || is1_18 || is1_19
                                                                                ? "network.protocol.game.PacketPlayOutPlayerInfo"
                                                                                : "PacketPlayOutPlayerInfo"
                                                                ),
                                                                types(
                                                                        enumPlayerInfoActionClass,
                                                                        entityPlayerArray.getClass()
                                                                ),
                                                                getStaticFieldValue(
                                                                        enumPlayerInfoActionClass,
                                                                        is1_17 || is1_18 || is1_19
                                                                                ? "a"
                                                                                : "ADD_PLAYER"
                                                                ),
                                                                entityPlayerArray
                                                        )
                                        );
                                    }
                                } catch(Exception ex) {
                                    ex.printStackTrace();
                                }
                            });

                            // Spawn player
                            sendPacketExceptSelf(
                                    newInstance(
                                            getNMSClass(is1_17 || is1_18 || is1_19
                                                    ? "network.protocol.game.PacketPlayOutNamedEntitySpawn"
                                                    : "PacketPlayOutNamedEntitySpawn"),
                                            types(getNMSClass(
                                                    is1_17 || is1_18 || is1_19
                                                            ? "world.entity.player.EntityHuman"
                                                            : "EntityHuman"
                                            )),
                                            entityPlayer
                                    ),
                                    players
                            );
                        }

                        // Fix head and body rotation (Yaw + Pitch)
                        Class<?> packetPlayOutEntityLook =
                                NMS_VERSION.equals("v1_7_R4") || NMS_VERSION.equals("v1_8_R1")
                                        ? getNMSClass("PacketPlayOutEntityLook")
                                        : getSubClass(
                                                getNMSClass(is1_17 || is1_18 || is1_19
                                                        ? "network.protocol.game.PacketPlayOutEntity"
                                                        : "PacketPlayOutEntity"
                                                ),
                                                "PacketPlayOutEntityLook"
                                        );

                        if(packetPlayOutEntityLook != null) {
                            sendPacketExceptSelf(
                                    newInstance(
                                            packetPlayOutEntityLook,
                                            types(
                                                    int.class,
                                                    byte.class,
                                                    byte.class,
                                                    boolean.class
                                            ),
                                            player.getEntityId(),
                                            (byte) ((int) (playerLocation.getYaw() * 256.0F / 360.0F)),
                                            (byte) ((int) (playerLocation.getPitch() * 256.0F / 360.0F)),
                                            true
                                    ),
                                    players
                            );
                        }

                        Object packetHeadRotation;

                        if(is1_17 || is1_18 || is1_19)
                            packetHeadRotation = newInstance(
                                    getNMSClass("network.protocol.game.PacketPlayOutEntityHeadRotation"),
                                    types(
                                            getNMSClass("world.entity.Entity"),
                                            byte.class
                                    ),
                                    entityPlayer,
                                    (byte) ((int) (playerLocation.getYaw() * 256.0F / 360.0F))
                            );
                        else {
                            packetHeadRotation = newInstance(getNMSClass("PacketPlayOutEntityHeadRotation"));
                            setField(packetHeadRotation, "a", player.getEntityId());
                            setField(packetHeadRotation, "b", (byte) ((int) (playerLocation.getYaw() * 256.0F / 360.0F)));
                        }

                        sendPacketExceptSelf(packetHeadRotation, players);

                        if(!(
                                utils.isPluginInstalled("ViaRewind")
                                        || utils.isPluginInstalled("ViaBackwards")
                                        || utils.isPluginInstalled("ProtocolSupport")
                        ) && setupYamlFile.getConfiguration().getBoolean("SeeNickSelf") && !skinsRestorer) {
                            // Self skin update
                            Object packetRespawnPlayer;

                            if(NMS_VERSION.equals("v1_19_R2") || NMS_VERSION.equals("v1_19_R3")) {
                                Object worldServer = invoke(player.getWorld(), "getHandle");
                                Class<?> resourceKeyClass = getNMSClass("resources.ResourceKey"),
                                        enumGameModeClass = getNMSClass("world.level.EnumGamemode");

                                packetRespawnPlayer = newInstance(
                                        getNMSClass("network.protocol.game.PacketPlayOutRespawn"),
                                        types(
                                                resourceKeyClass,
                                                resourceKeyClass,
                                                long.class,
                                                enumGameModeClass,
                                                enumGameModeClass,
                                                boolean.class,
                                                boolean.class,
                                                byte.class,
                                                Optional.class
                                        ),
                                        invoke(worldServer, NMS_VERSION.equals("v1_19_R3") ? "Z" : "aa"),
                                        invoke(worldServer, NMS_VERSION.equals("v1_19_R3") ? "ab" : "ac"),
                                        invokeStatic(
                                                getNMSClass("world.level.biome.BiomeManager"),
                                                "a",
                                                types(long.class),
                                                player.getWorld().getSeed()
                                        ),
                                        invoke(interactManager, "b"),
                                        invoke(interactManager, "c"),
                                        invoke(worldServer, NMS_VERSION.equals("v1_19_R3") ? "ae" : "af"),
                                        invoke(worldServer, NMS_VERSION.equals("v1_19_R3") ? "z" : "A"),
                                        (byte) 3, // keep all data
                                        invoke(entityPlayer, NMS_VERSION.equals("v1_19_R3") ? "gi" : "gd")
                                );
                            } else if(is1_19) {
                                Object worldServer = invoke(player.getWorld(), "getHandle");
                                Class<?> resourceKeyClass = getNMSClass("resources.ResourceKey"),
                                        enumGameModeClass = getNMSClass("world.level.EnumGamemode");

                                packetRespawnPlayer = newInstance(
                                        getNMSClass("network.protocol.game.PacketPlayOutRespawn"),
                                        types(
                                                resourceKeyClass,
                                                resourceKeyClass,
                                                long.class,
                                                enumGameModeClass,
                                                enumGameModeClass,
                                                boolean.class,
                                                boolean.class,
                                                boolean.class,
                                                Optional.class
                                        ),
                                        invoke(worldServer, "Z"),
                                        invoke(worldServer, "ab"),
                                        invokeStatic(
                                                getNMSClass("world.level.biome.BiomeManager"),
                                                "a",
                                                types(long.class),
                                                player.getWorld().getSeed()
                                        ),
                                        invoke(interactManager, "b"),
                                        invoke(interactManager, "c"),
                                        invoke(worldServer, "ae"),
                                        invoke(worldServer, "A"),
                                        true,
                                        invoke(entityPlayer, "ga")
                                );
                            } else if(NMS_VERSION.equals("v1_18_R2")) {
                                Object worldServer = invoke(player.getWorld(), "getHandle");
                                Class<?> enumGameModeClass = getNMSClass("world.level.EnumGamemode");

                                packetRespawnPlayer = newInstance(
                                        getNMSClass("network.protocol.game.PacketPlayOutRespawn"),
                                        types(
                                                getNMSClass("core.Holder"),
                                                getNMSClass("resources.ResourceKey"),
                                                long.class,
                                                enumGameModeClass,
                                                enumGameModeClass,
                                                boolean.class,
                                                boolean.class,
                                                boolean.class
                                        ),
                                        invoke(worldServer, "Z"),
                                        invoke(worldServer, "aa"),
                                        invokeStatic(
                                                getNMSClass("world.level.biome.BiomeManager"),
                                                "a",
                                                types(long.class),
                                                player.getWorld().getSeed()
                                        ),
                                        invoke(interactManager, "b"),
                                        invoke(interactManager, "c"),
                                        invoke(worldServer, "ad"),
                                        invoke(worldServer, "C"),
                                        true
                                );
                            } else if(NMS_VERSION.startsWith("v1_16") || is1_17 || is1_18) {
                                Object worldServer = invoke(player.getWorld(), "getHandle");
                                Class<?> resourceKeyClass = getNMSClass(is1_17 || is1_18
                                                ? "resources.ResourceKey"
                                                : "ResourceKey"
                                        ), enumGameModeClass = getNMSClass(
                                                is1_17 || is1_18
                                                        ? "world.level.EnumGamemode"
                                                        : "EnumGamemode"
                                        );

                                packetRespawnPlayer = NMS_VERSION.equals("v1_16_R1")
                                        ? newInstance(
                                                getNMSClass("PacketPlayOutRespawn"),
                                                types(
                                                        resourceKeyClass,
                                                        resourceKeyClass,
                                                        long.class,
                                                        enumGameModeClass,
                                                        enumGameModeClass,
                                                        boolean.class,
                                                        boolean.class,
                                                        boolean.class
                                                ),
                                                invoke(worldServer, "getTypeKey"),
                                                invoke(worldServer, "getDimensionKey"),
                                                invokeStatic(
                                                        getNMSClass("world.level.biome.BiomeManager"),
                                                        "a",
                                                        types(long.class),
                                                        player.getWorld().getSeed()
                                                ),
                                                invoke(interactManager, "getGameMode"),
                                                invoke(interactManager, "c"),
                                                invoke(worldServer, "isDebugWorld"),
                                                invoke(worldServer, "isFlatWorld")
                                        )
                                        : newInstance(
                                                getNMSClass(is1_17 || is1_18
                                                        ? "network.protocol.game.PacketPlayOutRespawn"
                                                        : "PacketPlayOutRespawn"
                                                ),
                                                types(
                                                        getNMSClass(is1_17 || is1_18
                                                                ? "world.level.dimension.DimensionManager"
                                                                : "DimensionManager"
                                                        ),
                                                        resourceKeyClass,
                                                        long.class,
                                                        enumGameModeClass,
                                                        enumGameModeClass,
                                                        boolean.class,
                                                        boolean.class,
                                                        boolean.class
                                                ),
                                                invoke(worldServer, is1_18 ? "q_" : "getDimensionManager"),
                                                invoke(worldServer, is1_18 ? "aa" : "getDimensionKey"),
                                                invokeStatic(
                                                        getNMSClass(is1_17 || is1_18
                                                                ? "world.level.biome.BiomeManager"
                                                                : "BiomeManager"
                                                        ),
                                                        "a",
                                                        types(long.class),
                                                        player.getWorld().getSeed()
                                                ),
                                                invoke(interactManager, is1_18 ? "b" : "getGameMode"),
                                                invoke(interactManager, "c"),
                                                invoke(worldServer, is1_18 ? "ad" : "isDebugWorld"),
                                                invoke(worldServer, is1_18 ? "D" : "isFlatWorld"),
                                                true
                                        );
                            } else {
                                Object environment = player.getWorld().getEnvironment();
                                int environmentId = (int) invoke(environment, "getId");

                                if(NMS_VERSION.startsWith("v1_15")) {
                                    Class<?> dimensionManagerClass = getNMSClass("DimensionManager"),
                                            worldTypeClass = getNMSClass("WorldType"),
                                            enumGameModeClass = getNMSClass("EnumGamemode");

                                    packetRespawnPlayer = newInstance(
                                            getNMSClass("PacketPlayOutRespawn"),
                                            types(
                                                    dimensionManagerClass,
                                                    long.class,
                                                    worldTypeClass,
                                                    enumGameModeClass
                                            ),
                                            invokeStatic(
                                                    dimensionManagerClass,
                                                    "a",
                                                    types(int.class),
                                                    environmentId
                                            ),
                                            Hashing.sha256().hashLong(player.getWorld().getSeed()).asLong(),
                                            invokeStatic(
                                                    worldTypeClass,
                                                    "getType",
                                                    types(String.class),
                                                    invoke(invoke(player.getWorld(), "getWorldType"), "getName")
                                            ),
                                            invokeStatic(
                                                    enumGameModeClass,
                                                    "getById",
                                                    types(int.class),
                                                    invoke(player.getGameMode(), "getValue"))
                                    );
                                } else if(NMS_VERSION.startsWith("v1_14")) {
                                    Class<?> dimensionManagerClass = getNMSClass("DimensionManager"),
                                            worldTypeClass = getNMSClass("WorldType"),
                                            enumGameModeClass = getNMSClass("EnumGamemode");

                                    packetRespawnPlayer = newInstance(
                                            getNMSClass("PacketPlayOutRespawn"),
                                            types(
                                                    dimensionManagerClass,
                                                    worldTypeClass,
                                                    enumGameModeClass
                                            ),
                                            invokeStatic(
                                                    dimensionManagerClass,
                                                    "a",
                                                    types(int.class),
                                                    environmentId
                                            ),
                                            invokeStatic(
                                                    worldTypeClass,
                                                    "getType",
                                                    types(String.class),
                                                    invoke(
                                                            invoke(player.getWorld(), "getWorldType"),
                                                            "getName"
                                                    )
                                            ),
                                            invokeStatic(
                                                    enumGameModeClass,
                                                    "getById",
                                                    types(int.class),
                                                    invoke(player.getGameMode(), "getValue")
                                            )
                                    );
                                } else if(NMS_VERSION.equals("v1_13_R2")) {
                                    packetRespawnPlayer = newInstance(
                                            getNMSClass("PacketPlayOutRespawn"),
                                            types(
                                                    getNMSClass("DimensionManager"),
                                                    getNMSClass("EnumDifficulty"),
                                                    getNMSClass("WorldType"),
                                                    getNMSClass("EnumGamemode")
                                            ),
                                            getFieldValue(worldClient, "dimension"),
                                            invoke(worldData, "getDifficulty"),
                                            invoke(worldData, "getType"),
                                            invoke(interactManager, "getGameMode")
                                    );
                                } else {
                                    packetRespawnPlayer = newInstance(
                                            getNMSClass("PacketPlayOutRespawn"),
                                            types(
                                                    int.class,
                                                    getNMSClass("EnumDifficulty"),
                                                    getNMSClass("WorldType"),
                                                    NMS_VERSION.equals("v1_8_R2")
                                                            || NMS_VERSION.equals("v1_8_R3")
                                                            || NMS_VERSION.equals("v1_9_R1")
                                                            || NMS_VERSION.equals("v1_9_R2")
                                                            ? getNMSClass("WorldSettings").getDeclaredClasses()[0]
                                                            : getNMSClass("EnumGamemode")
                                            ),
                                            environmentId,
                                            NMS_VERSION.equals("v1_7_R4")
                                                    ? getFieldValue(worldClient, "difficulty")
                                                    : invoke(worldClient, "getDifficulty"
                                            ),
                                            invoke(worldData, "getType"),
                                            invoke(interactManager, "getGameMode")
                                    );
                                }
                            }

                            boolean allowFlight = player.getAllowFlight(),
                                    flying = player.isFlying();

                            sendPacketNMS(player, packetRespawnPlayer);

                            player.updateInventory();

                            // Reload chunks
                            if(NMS_VERSION.equals("v1_8_R3")) {
                                Bukkit.getScheduler().runTask(eazyNick, () -> {
                                    Chunk currentChunk = player.getLocation().getChunk();
                                    World world = player.getWorld();
                                    int viewDistance = Bukkit.getViewDistance(),
                                            currentChunkX = currentChunk.getX(),
                                            currentChunkZ = currentChunk.getZ();

                                    for(int x = currentChunkX - viewDistance; x <= (currentChunkX + viewDistance); x++) {
                                        for (int z = currentChunkZ - viewDistance; z <= (currentChunkZ + viewDistance); z++) {
                                            try {
                                                invoke(
                                                        world,
                                                        "refreshChunk",
                                                        types(int.class, int.class),
                                                        x,
                                                        z
                                                );
                                            } catch (Exception ignore) {
                                            }
                                        }
                                    }
                                });
                            }

                            // Fix position
                            boolean teleportFar = is1_17 || is1_18;
                            Object playerConnection = getFieldValue(entityPlayer, (is1_17 || is1_18 || is1_19) ? "b" : "playerConnection");

                            if(player.getLocation().getBlock().getRelative(BlockFace.UP, 2).getType().equals(Material.AIR)) {
                                Bukkit.getScheduler().runTask(eazyNick, () -> {
                                    try {
                                        invoke(
                                                playerConnection,
                                                "teleport",
                                                types(Location.class),
                                                new Location(
                                                        player.getWorld(),
                                                        playerLocation.getX() + (teleportFar ? 100 : 0),
                                                        playerLocation.getY() + (teleportFar ? 100.25 : 0.25),
                                                        playerLocation.getZ() + (teleportFar ? 100 : 0),
                                                        playerLocation.getYaw(),
                                                        playerLocation.getPitch()
                                                )
                                        );
                                    } catch (Exception ignore) {
                                    }
                                });
                            }

                            if(teleportFar) {
                                new AsyncTask(new AsyncTask.AsyncRunnable() {

                                    @Override
                                    public void run() {
                                        Bukkit.getScheduler().runTask(eazyNick, () -> {
                                            try {
                                                invoke(
                                                        playerConnection,
                                                        "teleport",
                                                        types(Location.class),
                                                        new Location(
                                                                player.getWorld(),
                                                                playerLocation.getX(),
                                                                playerLocation.getY() + 0.25,
                                                                playerLocation.getZ(),
                                                                playerLocation.getYaw(),
                                                                playerLocation.getPitch()
                                                        )
                                                );
                                            } catch (Exception ignore) {
                                            }

                                            player.setAllowFlight(allowFlight);
                                            player.setFlying(flying);
                                        });
                                    }
                                }, 50).run();
                            } else {
                                Bukkit.getScheduler().runTask(eazyNick, () -> {
                                    player.setAllowFlight(allowFlight);
                                    player.setFlying(flying);
                                });
                            }

                            if(setupYamlFile.getConfiguration().getBoolean("UpdatePlayerStats")) {
                                // Fix armor, inventory, health, food level & experience level
                                double oldHealth = player.getHealth(),
                                        oldHealthScale = player.isHealthScaled()
                                                ? player.getHealthScale()
                                                : 0;
                                int oldLevel = player.getLevel();
                                ItemStack oldHelmet = player.getInventory().getHelmet(),
                                        oldChestplate = player.getInventory().getChestplate(),
                                        oldLeggings = player.getInventory().getLeggings(),
                                        oldBoots = player.getInventory().getBoots();

                                if(oldHelmet != null)
                                    player.getInventory().setHelmet(
                                            new ItemBuilder(Material.LEATHER_HELMET)
                                                    .setDurability(1)
                                                    .setDisplayName("§r")
                                                    .build()
                                    );

                                if(oldChestplate != null)
                                    player.getInventory().setChestplate(
                                            new ItemBuilder(Material.LEATHER_CHESTPLATE)
                                                    .setDurability(1)
                                                    .setDisplayName("§r")
                                                    .build()
                                    );

                                if(oldLeggings != null)
                                    player.getInventory().setLeggings(
                                            new ItemBuilder(Material.LEATHER_LEGGINGS)
                                                    .setDurability(1)
                                                    .setDisplayName("§r")
                                                    .build()
                                    );

                                if(oldBoots != null)
                                    player.getInventory().setBoots(
                                            new ItemBuilder(Material.LEATHER_BOOTS)
                                                    .setDurability(1)
                                                    .setDisplayName("§r")
                                                    .build()
                                    );

                                player.updateInventory();

                                if(player.getFoodLevel() != 20)
                                    player.setFoodLevel(player.getFoodLevel() + 1);

                                player.setLevel((oldLevel == 10) ? 5 : 10);

                                if(player.isHealthScaled())
                                    player.setHealthScale((oldHealthScale == 10) ? 20 : 10);

                                player.setHealth((oldHealth == 10) ? 5 : 10);

                                new AsyncTask(new AsyncTask.AsyncRunnable() {

                                    @Override
                                    public void run() {
                                        if(oldHelmet != null)
                                            player.getInventory().setHelmet(oldHelmet);

                                        if(oldChestplate != null)
                                            player.getInventory().setChestplate(oldChestplate);

                                        if(oldLeggings != null)
                                            player.getInventory().setLeggings(oldLeggings);

                                        if(oldBoots != null)
                                            player.getInventory().setBoots(oldBoots);

                                        if(player.isHealthScaled())
                                            player.setHealthScale(oldHealthScale);

                                        player.setHealth(oldHealth);
                                        player.setLevel(oldLevel);
                                    }
                                }, 150).run();
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            },
            50L * (5 +
                    (setupYamlFile.getConfiguration().getBoolean("RandomDisguiseDelay")
                            ? 20 * new Random().nextInt(3)
                            : 0))
            ).run();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
