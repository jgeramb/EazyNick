package com.justixdev.eazynick.nms.guis.book;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.nms.ReflectionHelper;
import com.justixdev.eazynick.utilities.AsyncTask;
import com.justixdev.eazynick.utilities.AsyncTask.AsyncRunnable;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NMSBookUtils extends ReflectionHelper {

    private final EazyNick eazyNick;

    public NMSBookUtils(EazyNick eazyNick) {
        this.eazyNick = eazyNick;
    }

    @SuppressWarnings("unchecked")
    public ItemStack create(String title, BookPage... bookPages) {
        ItemStack itemStack = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();

        if (bookMeta != null) {
            bookMeta.setTitle(title);
            bookMeta.setAuthor(this.eazyNick.getDescription().getName());

            try {
                Class<?> craftChatMessage = getCraftClass("util.CraftChatMessage");
                Field pagesField = getField(getCraftClass("inventory.CraftMetaBook"), "pages");
                List<Object> list = (List<Object>) Objects.requireNonNull(pagesField).get(bookMeta);

                if (list == null) {
                    pagesField.set(bookMeta, new ArrayList<>());

                    list = (List<Object>) pagesField.get(bookMeta);
                }

                for (BookPage bookPage : bookPages) {
                    if (!((bookPage == null) || bookPage.isEmpty()))
                        list.add(
                                NMS_VERSION.equals("v1_16_R3")
                                        || NMS_VERSION.startsWith("v1_17")
                                        || NMS_VERSION.startsWith("v1_18")
                                        || NMS_VERSION.startsWith("v1_19")
                                        ? invokeStatic(
                                                craftChatMessage,
                                                "toJSON",
                                                types(
                                                        getNMSClass(
                                                                NMS_VERSION.startsWith("v1_17")
                                                                        || NMS_VERSION.startsWith("v1_18")
                                                                        || NMS_VERSION.startsWith("v1_19")
                                                                        ? "network.chat.IChatBaseComponent"
                                                                        : "IChatBaseComponent")),
                                                invokeStatic(
                                                        craftChatMessage,
                                                        "fromJSON",
                                                        types(String.class),
                                                        bookPage.getAsString()))
                                        : bookPage.getAsIChatBaseComponent());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            itemStack.setItemMeta(bookMeta);
        }

        return itemStack;
    }

    public void open(Player player, ItemStack book) {
        try {
            PlayerInventory playerInventory = player.getInventory();
            boolean noOffhand = NMS_VERSION.startsWith("v1_7") || NMS_VERSION.startsWith("v1_8");

            ItemStack hand = noOffhand
                    ? (ItemStack) invoke(playerInventory, "getItemInHand")
                    : playerInventory.getItemInMainHand();

            if(noOffhand)
                invoke(
                        playerInventory,
                        "setItemInHand",
                        types(ItemStack.class),
                        book
                );
            else
                playerInventory.setItemInMainHand(book);

            new AsyncTask(new AsyncRunnable() {

                @Override
                public void run() {
                    try {
                        boolean is1_17 = NMS_VERSION.startsWith("v1_17"),
                                is1_18 = NMS_VERSION.startsWith("v1_18"),
                                is1_19 = NMS_VERSION.startsWith("v1_19");
                        Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
                        Class<?> craftItemStackClass = getCraftClass("inventory.CraftItemStack");
                        Object nmsItemStack = invokeStatic(craftItemStackClass, "asNMSCopy", types(ItemStack.class), book);

                        if(!(NMS_VERSION.startsWith("v1_7") || NMS_VERSION.startsWith("v1_8"))) {
                            Class<?> enumHandClass = getNMSClass(
                                    is1_17 || is1_18 || is1_19
                                            ? "world.EnumHand"
                                            : "EnumHand"
                            );
                            Object mainHand = getStaticFieldValue(
                                    enumHandClass,
                                    is1_17 || is1_18 || is1_19
                                            ? "a"
                                            : "MAIN_HAND"
                            );

                            if(Bukkit.getVersion().contains("1.14.4")
                                    || NMS_VERSION.startsWith("v1_15")
                                    || NMS_VERSION.startsWith("v1_16")
                                    || is1_17
                                    || is1_18
                                    || is1_19) {
                                Class<?> itemWrittenBookClass = getNMSClass(
                                        is1_17 || is1_18 || is1_19
                                                ? "world.item.ItemWrittenBook"
                                                : "ItemWrittenBook"
                                );

                                if ((boolean) invokeStatic(
                                        itemWrittenBookClass,
                                        "a",
                                        types(
                                                getNMSClass(
                                                        is1_17 || is1_18 || is1_19
                                                                ? "world.item.ItemStack"
                                                                : "ItemStack"
                                                ),
                                                getNMSClass(
                                                        is1_17 || is1_18 || is1_19
                                                                ? "commands.CommandListenerWrapper"
                                                                : "CommandListenerWrapper"
                                                ),
                                                getNMSClass(
                                                        is1_17 || is1_18 || is1_19
                                                                ? "world.entity.player.EntityHuman"
                                                                : "EntityHuman")
                                        ),
                                        nmsItemStack,
                                        invoke(
                                                entityPlayer,
                                                NMS_VERSION.equals("v1_19_R3")
                                                        ? "cZ"
                                                        : NMS_VERSION.equals("v1_19_R2")
                                                                ? "cY"
                                                                : Bukkit.getVersion().contains("1.19.2")
                                                                        ? "cT"
                                                                        : is1_19
                                                                                ? "cU"
                                                                                : is1_18
                                                                                        ? "cQ"
                                                                                        : "getCommandListener"
                                        ),
                                        entityPlayer)) {
                                    Object activeContainer = getFieldValue(
                                            entityPlayer,
                                            is1_19
                                                    ? "bU"
                                                    : is1_17 || is1_18
                                                            ? "bV"
                                                            : "activeContainer"
                                    );

                                    invoke(activeContainer, "c");
                                }

                                sendPacketNMS(player, newInstance(
                                        getNMSClass(
                                                is1_17 || is1_18 || is1_19
                                                        ? "network.protocol.game.PacketPlayOutOpenBook"
                                                        : "PacketPlayOutOpenBook"),
                                        types(enumHandClass),
                                        mainHand
                                ));
                            } else
                                invoke(
                                        entityPlayer,
                                        "a",
                                        types(getNMSClass("ItemStack"), enumHandClass),
                                        nmsItemStack,
                                        mainHand
                                );
                        } else {
                            sendPacketNMS(
                                    player,
                                    NMS_VERSION.startsWith("v1_7")
                                            ? newInstance(
                                                    getNMSClass("PacketPlayOutCustomPayload"),
                                                    types(String.class, net.minecraft.util.io.netty.buffer.ByteBuf.class),
                                                    "MC|BOpen",
                                                    net.minecraft.util.io.netty.buffer.Unpooled.EMPTY_BUFFER)
                                            : newInstance(
                                                    getNMSClass("PacketPlayOutCustomPayload"),
                                                    types(String.class, getNMSClass("PacketDataSerializer")),
                                                    "MC|BOpen",
                                                    newInstance(
                                                            getNMSClass("PacketDataSerializer"),
                                                            types(ByteBuf.class),
                                                            Unpooled.EMPTY_BUFFER
                                                    )
                                            )
                            );
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        Bukkit.getScheduler().runTask(eazyNick, () -> {
                            if(noOffhand) {
                                try {
                                    invoke(playerInventory, "setItemInHand", types(ItemStack.class), hand);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            } else
                                playerInventory.setItemInMainHand(hand);
                        });
                    }
                }
            }, 1000L / 20).run();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
