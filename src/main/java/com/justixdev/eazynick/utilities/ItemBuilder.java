package com.justixdev.eazynick.utilities;

import com.justixdev.eazynick.EazyNick;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class ItemBuilder {

    private ItemStack itemStack;
    private ItemMeta itemMeta;

    public ItemBuilder(int amount) {
        this(Material.getMaterial(
                        EazyNick.getInstance().getUtils().isVersion13OrLater()
                                ? "PLAYER_HEAD"
                                : "SKULL_ITEM"
                ),
                amount,
                EazyNick.getInstance().getUtils().isVersion13OrLater()
                        ? 0
                        : 3
        );
    }

    public ItemBuilder(Material mat) {
        this(mat, 1);
    }

    public ItemBuilder(Material mat, int amount) {
        this(mat, amount, 0);
    }

    public ItemBuilder(Material mat, int amount, int subID) {
        if(!(EazyNick.getInstance().getUtils().isVersion13OrLater())) {
            try {
                this.itemStack = ItemStack.class.getConstructor(
                        Material.class,
                        int.class,
                        short.class
                ).newInstance(
                        mat,
                        amount,
                        (short) subID
                );
            } catch (Exception ignore) {
                this.itemStack = new ItemStack(mat, amount);
            }
        } else
            this.itemStack = new ItemStack(mat, amount);

        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder setDurability(int durability) {
        if(!(EazyNick.getInstance().getUtils().isVersion13OrLater())) {
            try {
                itemStack.getClass().getMethod(
                        "setDurability",
                        short.class
                ).invoke(
                        itemStack,
                        (short) durability
                );
            } catch (Exception ignore) {
            }
        }

        return this;
    }

    public ItemBuilder setDisplayName(String displayName) {
        itemMeta.setDisplayName(displayName);

        return this;
    }

    public ItemBuilder setLore(String... lore) {
        itemMeta.setLore(
                (lore == null)
                        ? new ArrayList<>()
                        : Arrays.asList(lore)
        );

        return this;
    }

    public ItemBuilder setEnchanted(boolean value) {
        if(value) {
            itemMeta.addEnchant(Enchantment.DURABILITY, 1, false);

            if(!(EazyNick.getInstance().getVersion().equalsIgnoreCase("1_7_R4")))
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        return this;
    }

    public ItemBuilder setSkullOwner(String owner) {
        if(owner != null) {
            EazyNick eazyNick = EazyNick.getInstance();

            String value = "";

            try {
                if(eazyNick.getVersion().startsWith("1_7"))
                    value = eazyNick.getGameProfileBuilder_1_7()
                            .fetch(eazyNick.getUUIDFetcher_1_7().getUUID(owner))
                            .getProperties().get("textures")
                            .iterator()
                            .next()
                            .getValue();
                else if(eazyNick.getVersion().equals("1_8_R1"))
                    value = eazyNick.getGameProfileBuilder_1_8_R1()
                            .fetch(eazyNick.getUUIDFetcher_1_8_R1().getUUID(owner))
                            .getProperties().get("textures")
                            .iterator()
                            .next()
                            .getValue();
                else
                    value = eazyNick.getGameProfileBuilder()
                            .fetch(eazyNick.getUUIDFetcher().getUUID(owner))
                            .getProperties().get("textures")
                            .iterator()
                            .next()
                            .getValue();
            } catch (Exception ignore) {
            }

            return setSkullTextures(value);
        } else
            return this;
    }

    public ItemBuilder setSkullTextures(String value) {
        SkullMeta skullMeta = (SkullMeta) itemMeta;

        try {
            String version = EazyNick.getInstance().getVersion();
            Object profile;

            if(version.startsWith("1_7")) {
                net.minecraft.util.com.mojang.authlib.GameProfile gameProfile = new net.minecraft.util.com.mojang.authlib.GameProfile(
                        UUID.randomUUID(),
                        "MHF_Custom"
                );
                gameProfile.getProperties().removeAll("textures");
                gameProfile.getProperties().put("textures", new net.minecraft.util.com.mojang.authlib.properties.Property(
                        "textures",
                        value
                ));

                profile = gameProfile;
            } else {
                GameProfile gameProfile = new GameProfile(
                        UUID.randomUUID(),
                        "MHF_Custom"
                );
                gameProfile.getProperties().removeAll("textures");
                gameProfile.getProperties().put("textures", new Property(
                        "textures",
                        value
                ));

                profile = gameProfile;
            }

            Field profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, profile);
        } catch (Exception ignore) {
        }

        itemMeta = skullMeta;

        return this;
    }

    public ItemStack build() {
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

}