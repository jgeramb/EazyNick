package com.justixdev.eazynick.utilities;

import com.justixdev.eazynick.utilities.mojang.MojangAPI;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static com.justixdev.eazynick.nms.ReflectionHelper.*;

public class ItemBuilder {

    private ItemStack itemStack;
    private ItemMeta itemMeta;

    public ItemBuilder(int amount) {
        this(
                Material.getMaterial(VERSION_13_OR_LATER ? "PLAYER_HEAD" : "SKULL_ITEM"),
                amount,
                VERSION_13_OR_LATER ? 0 : 3
        );
    }

    public ItemBuilder(Material mat) {
        this(mat, 1);
    }

    public ItemBuilder(Material mat, int amount) {
        this(mat, amount, 0);
    }

    public ItemBuilder(Material mat, int amount, int subID) {
        if(VERSION_13_OR_LATER)
            this.itemStack = new ItemStack(mat, amount);
        else {
            try {
                this.itemStack = (ItemStack) newInstance(
                        ItemStack.class,
                        new Class<?>[] {
                                Material.class,
                                int.class,
                                short.class
                        },
                        mat,
                        amount,
                        (short) subID
                );
            } catch (Exception ignore) {
                this.itemStack = new ItemStack(mat, amount);
            }
        }

        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder setDurability(int durability) {
        if(!VERSION_13_OR_LATER) {
            try {
                invoke(itemStack, "setDurability", types(short.class), durability);
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

            if(!NMS_VERSION.equalsIgnoreCase("v1_7_R4"))
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        return this;
    }

    public ItemBuilder setSkullOwner(String owner) {
        if(owner != null) {
            try {
                Object gameProfile = MojangAPI.getGameProfile(owner);

                if(gameProfile != null) {
                    return setSkullTextures(
                            (String) invoke(invoke(invoke(invoke(
                                    gameProfile,
                                    "getProperties"),
                                    "get", types(Object.class), "textures"),
                                    "iterator"),
                                    "next")
                    );
                }
            } catch (Exception ignore) {
            }
        }

        return this;
    }

    public ItemBuilder setSkullTextures(String value) {
        SkullMeta skullMeta = (SkullMeta) this.itemMeta;

        try {
            Object profile;

            if(NMS_VERSION.startsWith("v1_7")) {
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
                com.mojang.authlib.GameProfile gameProfile = new com.mojang.authlib.GameProfile(
                        UUID.randomUUID(),
                        "MHF_Custom"
                );
                gameProfile.getProperties().removeAll("textures");
                gameProfile.getProperties().put("textures", new com.mojang.authlib.properties.Property(
                        "textures",
                        value
                ));

                profile = gameProfile;
            }

            setField(skullMeta, "profile", profile);
        } catch (Exception ignore) {
        }

        this.itemMeta = skullMeta;

        return this;
    }

    public ItemStack build() {
        this.itemStack.setItemMeta(this.itemMeta);

        return this.itemStack;
    }

}