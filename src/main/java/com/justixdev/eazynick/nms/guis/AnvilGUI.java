package com.justixdev.eazynick.nms.guis;

import com.justixdev.eazynick.EazyNick;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.justixdev.eazynick.nms.ReflectionHelper.*;

public class AnvilGUI {

    @Getter
    private Player player;
    public AnvilClickEventHandler handler;
    private Map<AnvilSlot, ItemStack> items;
    private final Class<?> blockPositionClass, containerAnvil, entityHuman;
    private Inventory inventory;
    private Listener listener;

    public AnvilGUI(final Player player, final AnvilClickEventHandler handler) {
        boolean is1_17 = NMS_VERSION.startsWith("v1_17"),
                is1_18 = NMS_VERSION.startsWith("v1_18"),
                is1_19 = NMS_VERSION.startsWith("v1_19");

        this.blockPositionClass = getNMSClass(is1_17 || is1_18 || is1_19
                ? "core.BlockPosition"
                : "BlockPosition"
        );
        this.containerAnvil = getNMSClass(is1_17 || is1_18 || is1_19
                ? "world.inventory.ContainerAnvil"
                : "ContainerAnvil"
        );
        this.entityHuman = getNMSClass(is1_17 || is1_18 || is1_19
                ? "world.entity.player.EntityHuman"
                : "EntityHuman"
        );

        this.player = player;
        this.handler = handler;
        this.items = new HashMap<>();

        this.listener = new Listener() {

            @EventHandler
            public void onInventoryClick(InventoryClickEvent event) {
                if (event.getWhoClicked() instanceof Player) {
                    if (event.getInventory().equals(inventory)) {
                        event.setCancelled(true);

                        ItemStack item = event.getCurrentItem();
                        AnvilClickEvent clickEvent = new AnvilClickEvent(
                                AnvilSlot.bySlot(event.getRawSlot()),
                                (item != null) && (item.getItemMeta() != null) && item.getItemMeta().hasDisplayName()
                                        ? item.getItemMeta().getDisplayName()
                                        : ""
                        );

                        handler.onAnvilClick(clickEvent);

                        if (clickEvent.isWillClose())
                            event.getWhoClicked().closeInventory();

                        if (clickEvent.isWillDestroy())
                            destroy();
                    }
                }
            }

            @EventHandler
            public void onInventoryClose(InventoryCloseEvent event) {
                if (event.getPlayer() instanceof Player) {
                    Inventory inv = event.getInventory();

                    if(player.getLevel() > 0)
                        player.setLevel(player.getLevel() - 1);

                    if (inv.equals(inventory)) {
                        inv.clear();

                        destroy();
                    }
                }
            }

            @EventHandler
            public void onPlayerQuit(PlayerQuitEvent event) {
                if (event.getPlayer().equals(player)) {
                    if(player.getLevel() > 0)
                        player.setLevel(player.getLevel() - 1);

                    destroy();
                }
            }

        };

        Bukkit.getPluginManager().registerEvents(this.listener, EazyNick.getInstance());
    }

    public void setSlot(AnvilSlot slot, ItemStack item) {
        this.items.put(slot, item);
    }

    public void open() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        this.player.closeInventory();
        this.player.setLevel(this.player.getLevel() + 1);

        try {
            boolean is1_17 = NMS_VERSION.startsWith("v1_17"),
                    is1_18 = NMS_VERSION.startsWith("v1_18"),
                    is1_19 = NMS_VERSION.startsWith("v1_19");
            Class<?> iChatBaseComponentClass =
                    getNMSClass(is1_17 || is1_18 || is1_19
                            ? "network.chat.IChatBaseComponent"
                            : "IChatBaseComponent"
                    ),
                    playerInventoryClass =
                            getNMSClass(is1_17 || is1_18 || is1_19
                                    ? "world.entity.player.PlayerInventory"
                                    : "PlayerInventory"
                            ),
                    worldClass =
                            getNMSClass(is1_17 || is1_18 || is1_19
                                    ? "world.level.World"
                                    : "World"
                            );
            Object entityPlayer = invoke(this.player, "getHandle"),
                    playerInventory =
                            Objects.requireNonNull(getField(
                                    this.entityHuman,
                                    NMS_VERSION.equals("v1_19_R3")
                                            ? "ck"
                                            : NMS_VERSION.equals("v1_18_R1") || NMS_VERSION.equals("v1_19_R1")
                                                    ? "cp"
                                                    : is1_17 || is1_18 || is1_19
                                                            ? "co"
                                                            : "inventory"
                            )).get(entityPlayer),
                    world =
                            getFieldValue(
                                    entityPlayer,
                                    NMS_VERSION.equals("v1_19_R3")
                                            ? "H"
                                            : is1_18 || is1_19
                                                    ? "s"
                                                    : is1_17
                                                            ? "t"
                                                            : "world"
                            ),
                    blockPosition = newInstance(this.blockPositionClass, types(int.class, int.class, int.class), 0, 0, 0);
            int c = (int) invoke(entityPlayer, "nextContainerCounter");
            boolean newContainer = NMS_VERSION.startsWith("v1_14")
                    || NMS_VERSION.startsWith("v1_15")
                    || NMS_VERSION.startsWith("v1_16")
                    || is1_17
                    || is1_18
                    || is1_19;
            Object container = newContainer
                    ? newInstance(
                            this.containerAnvil,
                            types(
                                    int.class,
                                    playerInventoryClass,
                                    getNMSClass(is1_17 || is1_18 || is1_19
                                            ? "world.inventory.ContainerAccess"
                                            : "ContainerAccess"
                                    )
                            ),
                            c,
                            playerInventory,
                            invokeStatic(
                                    getNMSClass(is1_17 || is1_18 || is1_19
                                            ? "world.inventory.ContainerAccess"
                                            : "ContainerAccess"
                                    ),
                                    is1_18 || is1_19
                                            ? "a"
                                            : "at",
                                    types(worldClass, this.blockPositionClass),
                                    world,
                                    blockPosition
                            )
                    )
                    : newInstance(
                            this.containerAnvil,
                            types(
                                    playerInventoryClass,
                                    worldClass,
                                    this.blockPositionClass,
                                    this.entityHuman
                            ),
                            playerInventory,
                            world,
                            blockPosition,
                            entityPlayer
                    );

            setField(container, "checkReachable", false);

            this.inventory = ((InventoryView) invoke(container, "getBukkitView")).getTopInventory();

            for (AnvilSlot slot : this.items.keySet())
                this.inventory.setItem(slot.getSlot(), this.items.get(slot));

            String title = "Repairing";
            Object iChatBaseComponent = is1_17 || is1_18 || is1_19
                    ? invokeStatic(
                            iChatBaseComponentClass.getDeclaredClasses()[0],
                            (is1_18 || is1_19) ? "b" : "a",
                            types(String.class),
                    "{\"text\":\"" + title + "\"}"
                    )
                    : newInstance(
                            getNMSClass("ChatMessage"),
                            types(String.class, Object[].class),
                            title, new Object[0]
                    );

            setField(
                    entityPlayer,
                    NMS_VERSION.equals("v1_19_R3")
                            ? "bP"
                            : is1_19
                                    ? "bU"
                                    : NMS_VERSION.equals("v1_18_R2")
                                            ? "bV"
                                            : is1_18
                                                    ? "bW"
                                                    : is1_17
                                                            ? "bV"
                                                            : "activeContainer",
                    container
            );
            setField(
                    container,
                    is1_17 || is1_18 || is1_19
                            ? "v"
                            : NMS_VERSION.startsWith("v1_1")
                                    && !(NMS_VERSION.startsWith("v1_10") || NMS_VERSION.startsWith("v1_11"))
                                    ? "renameText"
                                    : "l",
                    "Type in some text"
            );
            setField(
                    container,
                    is1_17 || is1_18 || is1_19
                            ? "j"
                            : "windowId",
                    c
            );

            if(newContainer) {
                setField(
                        container,
                        "title",
                        iChatBaseComponent
                );
            }

            sendPacketNMS(this.player,
                    newContainer
                            ? newInstance(
                                    getNMSClass(is1_17 || is1_18 || is1_19
                                            ? "network.protocol.game.PacketPlayOutOpenWindow"
                                            : "PacketPlayOutOpenWindow"
                                    ),
                                    types(
                                            int.class,
                                            getNMSClass(is1_17 || is1_18 || is1_19
                                                    ? "world.inventory.Containers"
                                                    : "Containers"
                                            ),
                                            iChatBaseComponentClass
                                    ),
                                    c,
                                    getStaticFieldValue(
                                            getNMSClass(is1_17 || is1_18 || is1_19
                                                    ? "world.inventory.Containers"
                                                    : "Containers"
                                            ),
                                            is1_17 || is1_18 || is1_19
                                                    ? "h"
                                                    : "ANVIL"
                                    ),
                                    iChatBaseComponent
                            )
                            : newInstance(
                                    getNMSClass("PacketPlayOutOpenWindow"),
                                    types(int.class, String.class, iChatBaseComponentClass, int.class),
                                    c,
                                    "minecraft:anvil",
                                    iChatBaseComponent,
                                    0
                            )
                    );

            invoke(
                    container,
                    is1_18 || is1_19
                            ? "a"
                            : "addSlotListener",
                    types(getNMSClass(is1_17 || is1_18 || is1_19
                            ? "world.inventory.ICrafting"
                            : "ICrafting"
                    )),
                    is1_17 || is1_18 || is1_19
                            ? getFieldValue(
                                    entityPlayer,
                                    NMS_VERSION.equals("v1_19_R3")
                                            ? "cW"
                                            : is1_19 || NMS_VERSION.equals("v1_18_R2")
                                                    ? "da"
                                                    : is1_18
                                                            ? "db"
                                                            : "cX"
                            )
                            : entityPlayer
            );

            if(is1_17 || is1_18 || is1_19) {
                invoke(
                        container,
                        "a",
                        types(getNMSClass("world.inventory.ContainerSynchronizer")),
                        getFieldValue(
                                entityPlayer,
                                NMS_VERSION.equals("v1_19_R3")
                                        ? "cV"
                                        : is1_19 || NMS_VERSION.equals("v1_18_R2")
                                                ? "cZ"
                                                : is1_18
                                                        ? "da"
                                                        : "cW"
                        )
                );
                invoke(container, (is1_18 || is1_19) ? "b" : "updateInventory");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void destroy() {
        this.player = null;
        this.handler = null;
        this.items = null;

        HandlerList.unregisterAll(this.listener);

        this.listener = null;
    }

    @AllArgsConstructor
    public enum AnvilSlot {

        INPUT_LEFT(0), INPUT_RIGHT(1), OUTPUT(2);

        @Getter
        private final int slot;

        public static AnvilSlot bySlot(int slot) {
            return Arrays.stream(values())
                    .filter(anvilSlot -> anvilSlot.slot == slot)
                    .findFirst()
                    .orElse(null);
        }

    }

    public interface AnvilClickEventHandler {

        void onAnvilClick(AnvilClickEvent event);

    }

    @Data
    @RequiredArgsConstructor
    public static class AnvilClickEvent {

        private final AnvilSlot slot;
        private final String name;
        private boolean willClose = true;
        private boolean willDestroy = true;

    }

}