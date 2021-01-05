package net.dev.eazynick.utils;

import java.lang.reflect.Field;
import java.util.*;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import net.dev.eazynick.EazyNick;

public class ItemBuilder {

	private ItemStack itemStack;
	private ItemMeta itemMeta;
	
	public ItemBuilder(int amount) {
		this(Material.getMaterial(EazyNick.getInstance().getUtils().isNewVersion() ? "PLAYER_HEAD" : "SKULL_ITEM"), amount, EazyNick.getInstance().getUtils().isNewVersion() ? 0 : 3);
	}
	
	public ItemBuilder(Material mat) {
		this(mat, 1);
	}
	
	public ItemBuilder(Material mat, int amount) {
		this(mat, amount, 0);
	}
	
	public ItemBuilder(Material mat, int amount, int subID) {
		this.itemStack = new ItemStack(mat, amount, (short) subID);
		this.itemMeta = itemStack.getItemMeta();
	}
	
	public ItemBuilder(ItemStack originalItemStack) {
		this.itemStack = originalItemStack;
		this.itemMeta = originalItemStack.getItemMeta();
	}

	public ItemBuilder setAmount(int amount) {
		itemStack.setAmount(amount);
		
		return this;
	}
	
	public ItemBuilder setDisplayName(String displayName) {
		itemMeta.setDisplayName(displayName);
		
		return this;
	}
	
	public ItemBuilder setLore(String... lore) {
		itemMeta.setLore((lore == null) ? new ArrayList() : Arrays.asList(lore));
		
		return this;
	}
	
	public ItemBuilder addEnchant(Enchantment ench, int amplifier) {
		itemMeta.addEnchant(ench, amplifier, false);
		
		return this;
	}
	
	public ItemBuilder addItemFlags(ItemFlag... flags) {
		itemMeta.addItemFlags(flags);
		
		return this;
	}
	
	public ItemBuilder setEnchanted(boolean value) {
		if(value) {
			addEnchant(Enchantment.DURABILITY, 1);
			
			if(!(EazyNick.getInstance().getVersion().equalsIgnoreCase("1_7_R4")))
				addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		
		return this;
	}
	
	public ItemBuilder setSkullOwner(String owner) {
		if(owner != null) {
			EazyNick eazyNick = EazyNick.getInstance();
			
			String value = "";
			
			try {
				if(eazyNick.getVersion().startsWith("1_7"))
					value = eazyNick.getGameProfileBuilder_1_7().fetch(eazyNick.getUUIDFetcher_1_7().getUUID(owner)).getProperties().get("textures").iterator().next().getValue();
				else if(eazyNick.getVersion().equals("1_8_R1"))
					value = eazyNick.getGameProfileBuilder_1_8_R1().fetch(eazyNick.getUUIDFetcher_1_8_R1().getUUID(owner)).getProperties().get("textures").iterator().next().getValue();
				else
					value = eazyNick.getGameProfileBuilder().fetch(eazyNick.getUUIDFetcher().getUUID(owner)).getProperties().get("textures").iterator().next().getValue();
			} catch (Exception ex) {
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
	        	net.minecraft.util.com.mojang.authlib.GameProfile gameProfile = new net.minecraft.util.com.mojang.authlib.GameProfile(UUID.randomUUID(), "MHF_Custom");
	            gameProfile.getProperties().removeAll("textures");
	            gameProfile.getProperties().put("textures", new net.minecraft.util.com.mojang.authlib.properties.Property("textures", value));
	            
	            profile = gameProfile;
        	} else {
	        	GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "MHF_Custom");
	            gameProfile.getProperties().removeAll("textures");
	            gameProfile.getProperties().put("textures", new Property("textures", value));
	            
	            profile = gameProfile;
        	}
        	
            Field profileField = null;
            profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, profile);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        itemMeta = skullMeta;

        return this;
    }
	
	public ItemStack build() {
		itemStack.setItemMeta(itemMeta);
		
		return itemStack;
	}

}