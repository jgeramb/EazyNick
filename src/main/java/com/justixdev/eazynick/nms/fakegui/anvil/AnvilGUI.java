package com.justixdev.eazynick.nms.fakegui.anvil;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.nms.ReflectionHelper;
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
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class AnvilGUI {

    private final EazyNick eazyNick;
    private Player player;
    public AnvilClickEventHandler handler;
    private Map<AnvilSlot, ItemStack> items;
    private final Class<?> blockPositionClass, containerAnvil, entityHuman;
    private Inventory inventory;
    private Listener listener;

    public AnvilGUI(final Player player, final AnvilClickEventHandler handler) {
        eazyNick = EazyNick.getInstance();
        ReflectionHelper reflectionHelper = eazyNick.getReflectionHelper();

        boolean is1_17 = eazyNick.getVersion().startsWith("1_17"), is1_18 = eazyNick.getVersion().startsWith("1_18"), is1_19 = eazyNick.getVersion().startsWith("1_19");

        blockPositionClass = reflectionHelper.getNMSClass(
                (is1_17 || is1_18 || is1_19)
                        ? "core.BlockPosition"
                        : "BlockPosition"
        );
        containerAnvil = reflectionHelper.getNMSClass(
                (is1_17 || is1_18 || is1_19)
                        ? "world.inventory.ContainerAnvil"
                        : "ContainerAnvil"
        );
        entityHuman = reflectionHelper.getNMSClass(
                (is1_17 || is1_18 || is1_19)
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
                                ((item != null) && (item.getItemMeta() != null) && item.getItemMeta().hasDisplayName())
                                        ? item.getItemMeta().getDisplayName()
                                        : ""
                        );

                        handler.onAnvilClick(clickEvent);

                        if (clickEvent.getWillClose())
                            event.getWhoClicked().closeInventory();

                        if (clickEvent.getWillDestroy())
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

                    if (inv.equals(AnvilGUI.this.inventory)) {
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

        Bukkit.getPluginManager().registerEvents(listener, eazyNick);
    }

    public Player getPlayer() {
        return player;
    }

    public void setSlot(AnvilSlot slot, ItemStack item) {
        items.put(slot, item);
    }

    @SuppressWarnings("ConstantConditions")
    public void open() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        ReflectionHelper reflectionHelper = eazyNick.getReflectionHelper();

        player.closeInventory();
        player.setLevel(player.getLevel() + 1);

        try {
            String version = eazyNick.getVersion();
            boolean is1_17 = version.startsWith("1_17"), is1_18 = version.startsWith("1_18"), is1_19 = version.startsWith("1_19");
            Class<?> iChatBaseComponentClass = reflectionHelper.getNMSClass(
                    (is1_17 || is1_18 || is1_19)
                            ? "network.chat.IChatBaseComponent"
                            : "IChatBaseComponent"
            ), playerInventoryClass = reflectionHelper.getNMSClass(
                    (is1_17 || is1_18 || is1_19)
                            ? "world.entity.player.PlayerInventory"
                            : "PlayerInventory"
            ), worldClass = reflectionHelper.getNMSClass(
                    (is1_17 || is1_18 || is1_19)
                            ? "world.level.World"
                            : "World"
            );
            Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player),
                    playerInventory = reflectionHelper.getField(
                            reflectionHelper.getNMSClass(
                                    (is1_17 || is1_18 || is1_19)
                                            ? "world.entity.player.EntityHuman"
                                            : "EntityHuman"
                            ),
                            ((is1_18 || is1_19) && !(version.equals("1_18_R2")))
                                    ? "cp"
                                    : ((is1_17 || is1_18)
                                    ? "co"
                                    : "inventory"
                            )
                    ).get(entityPlayer),
                    world = reflectionHelper.getField(
                            reflectionHelper.getNMSClass(
                                    (is1_17 || is1_18 || is1_19)
                                            ? "world.entity.Entity"
                                            : "Entity"
                            ),
                            (is1_18 || is1_19)
                                    ? "s"
                                    : is1_17
                                    ? "t"
                                    : "world"
                    ).get(entityPlayer),
                    blockPosition = blockPositionClass.getConstructor(
                            int.class,
                            int.class,
                            int.class
                    ).newInstance(0, 0, 0);
            int c = (int) invokeMethod("nextContainerCounter", entityPlayer);
            Object container = (version.startsWith("1_14") || version.startsWith("1_15") || version.startsWith("1_16") || is1_17 || is1_18 || is1_19)
                    ? containerAnvil.getConstructor(
                    int.class,
                    playerInventoryClass,
                    reflectionHelper.getNMSClass(
                            (is1_17 || is1_18 || is1_19)
                                    ? "world.inventory.ContainerAccess"
                                    : "ContainerAccess"
                    )
            ).newInstance(
                    c,
                    playerInventory,
                    reflectionHelper.getNMSClass(
                            (is1_17 || is1_18 || is1_19)
                                    ? "world.inventory.ContainerAccess"
                                    : "ContainerAccess"
                    ).getMethod(
                            (is1_18 || is1_19)
                                    ? "a"
                                    : "at",
                            worldClass,
                            blockPositionClass
                    ).invoke(
                            null,
                            world,
                            blockPosition
                    )
            )
                    : containerAnvil.getConstructor(
                    playerInventoryClass,
                    worldClass,
                    blockPositionClass,
                    entityHuman
            ).newInstance(
                    playerInventory,
                    world,
                    blockPosition,
                    entityPlayer
            );

            reflectionHelper.getField(
                    reflectionHelper.getNMSClass(
                            (is1_17 || is1_18 || is1_19)
                                    ? "world.inventory.Container"
                                    : "Container"
                    ),
                    "checkReachable"
            ).set(container, false);

            inventory = ((InventoryView) invokeMethod("getBukkitView", container)).getTopInventory();

            for (AnvilSlot slot : items.keySet())
                inventory.setItem(slot.getSlot(), items.get(slot));

            String title = "Repairing";
            Object iChatBaseComponent = (is1_17 || is1_18 || is1_19)
                    ? iChatBaseComponentClass.getDeclaredClasses()[0]
                    .getMethod((is1_18 || is1_19) ? "b" : "a", String.class)
                    .invoke(null, "{\"text\":\"" + title + "\"}")
                    : reflectionHelper.getNMSClass("ChatMessage")
                    .getConstructor(String.class, Object[].class)
                    .newInstance(title, new Object[0]);

            reflectionHelper.getField(
                    entityHuman,
                    is1_19
                            ? "bU"
                            : (version.equals("1_18_R2")
                            ? "bV"
                            : (is1_18
                            ? "bW"
                            : (is1_17
                            ? "bV"
                            : "activeContainer"
                    )
                    )
                    )
            ).set(entityPlayer, container);

            reflectionHelper.getField(reflectionHelper.getNMSClass(
                            (is1_17 || is1_18 || is1_19)
                                    ? "world.inventory.ContainerAnvil" :
                                    "ContainerAnvil"
                    ), (is1_17 || is1_18 || is1_19)
                            ? "v"
                            : (
                            (version.startsWith("1_1") && !(version.startsWith("1_10") || version.startsWith("1_11")))
                                    ? "renameText"
                                    : "l"
                    )
            ).set(container, "Type in some text");

            reflectionHelper.getField(reflectionHelper.getNMSClass(
                            (is1_17 || is1_18 || is1_19)
                                    ? "world.inventory.Container"
                                    : "Container"
                    ),
                    (is1_17 || is1_18 || is1_19)
                            ? "j"
                            : "windowId"
            ).set(container, c);

            if(is1_17 || is1_18 || is1_19)
                reflectionHelper.getField(
                        reflectionHelper.getNMSClass("world.inventory.Container"),
                        "title"
                ).set(container, iChatBaseComponent);

            Object playerConnection = reflectionHelper.getField(reflectionHelper.getNMSClass(
                            (is1_17 || is1_18 || is1_19)
                                    ? "server.level.EntityPlayer"
                                    : "EntityPlayer"
                    ),
                    (is1_17 || is1_18 || is1_19)
                            ? "b"
                            : "playerConnection"
            ).get(entityPlayer);
            Method sendPacket = getMethod(
                    (is1_18 || is1_19)
                            ? "a"
                            : "sendPacket",
                    playerConnection.getClass(),
                    reflectionHelper.getNMSClass(
                            (is1_17 || is1_18 || is1_19)
                                    ? "network.protocol.Packet"
                                    : "Packet"
                    )
            );
            sendPacket.invoke(
                    playerConnection,
                    (version.startsWith("1_16") || is1_17 || is1_18 || is1_19)
                            ? reflectionHelper.getNMSClass(
                            (is1_17 || is1_18 || is1_19)
                                    ? "network.protocol.game.PacketPlayOutOpenWindow"
                                    : "PacketPlayOutOpenWindow"
                    ).getConstructor(
                            int.class,
                            reflectionHelper.getNMSClass(
                                    (is1_17 || is1_18 || is1_19)
                                            ? "world.inventory.Containers"
                                            : "Containers"
                            ),
                            iChatBaseComponentClass
                    ).newInstance(
                            c,
                            reflectionHelper.getNMSClass(
                                    (is1_17 || is1_18 || is1_19)
                                            ? "world.inventory.Containers"
                                            : "Containers"
                            ).getDeclaredField(
                                    (is1_17 || is1_18 || is1_19)
                                            ? "h"
                                            : "ANVIL"
                            ).get(null),
                            iChatBaseComponent)
                            : reflectionHelper.getNMSClass("PacketPlayOutOpenWindow")
                            .getConstructor(
                                    int.class,
                                    String.class,
                                    iChatBaseComponentClass,
                                    int.class
                            ).newInstance(
                                    c,
                                    "minecraft:anvil",
                                    iChatBaseComponent,
                                    0
                            )
            );

            container.getClass().getMethod(
                    (is1_18 || is1_19)
                            ? "a"
                            : "addSlotListener",
                    reflectionHelper.getNMSClass(
                            (is1_17 || is1_18 || is1_19)
                                    ? "world.inventory.ICrafting"
                                    : "ICrafting"
                    )
            ).invoke(
                    container,
                    (is1_17 || is1_18 || is1_19)
                            ? reflectionHelper.getField(
                            entityPlayer.getClass(),
                            (is1_19 || version.equals("1_18_R2"))
                                    ? "da"
                                    : (is1_18
                                    ? "db"
                                    : "cX"
                            )
                    ).get(entityPlayer)
                            : entityPlayer
            );

            if(is1_17 || is1_18 || is1_19) {
                container.getClass().getMethod(
                        "a",
                        reflectionHelper.getNMSClass("world.inventory.ContainerSynchronizer")
                ).invoke(
                        container,
                        reflectionHelper.getField(
                                entityPlayer.getClass(),
                                (is1_19 || version.equals("1_18_R2"))
                                        ? "cZ"
                                        : (is1_18
                                        ? "da"
                                        : "cW"
                                )
                        ).get(entityPlayer)
                );
                container.getClass().getMethod(
                        (is1_18 || is1_19)
                                ? "b"
                                : "updateInventory"
                ).invoke(container);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Method getMethod(String name, Class<?> clazz, Class<?>... args) {
        try {
            return clazz.getMethod(name, args);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private Object invokeMethod(String name, Object clazz) {
        try {
            return clazz.getClass().getMethod(name).invoke(clazz);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public void destroy() {
        player = null;
        handler = null;
        items = null;

        HandlerList.unregisterAll(listener);

        listener = null;
    }

    public enum AnvilSlot {

        INPUT_LEFT(0), INPUT_RIGHT(1), OUTPUT(2);

        private final int slot;

        AnvilSlot(int slot) {
            this.slot = slot;
        }

        public static AnvilSlot bySlot(int slot) {
            for (AnvilSlot anvilSlot : values()) {
                if (anvilSlot.getSlot() == slot)
                    return anvilSlot;
            }

            return null;
        }

        public int getSlot() {
            return slot;
        }

    }

    public interface AnvilClickEventHandler {

        void onAnvilClick(AnvilClickEvent event);

    }

    public static class AnvilClickEvent {

        private final AnvilSlot slot;
        private final String name;
        private boolean close = true;
        private boolean destroy = true;

        public AnvilClickEvent(AnvilSlot slot, String name) {
            this.slot = slot;
            this.name = name;
        }

        public AnvilSlot getSlot() {
            return slot;
        }

        public String getName() {
            return name;
        }

        public boolean getWillClose() {
            return close;
        }

        public void setWillClose(boolean close) {
            this.close = close;
        }

        public boolean getWillDestroy() {
            return destroy;
        }

        public void setWillDestroy(boolean destroy) {
            this.destroy = destroy;
        }

    }

}