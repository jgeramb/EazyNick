package net.dev.nickplugin.utils.nickutils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.gmail.filoghost.coloredtags.ColoredTags;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import net.dev.nickplugin.main.Main;
import net.dev.nickplugin.utils.FileUtils;
import net.dev.nickplugin.utils.ReflectUtils;
import net.dev.nickplugin.utils.Utils;
import net.minecraft.server.v1_8_R1.PlayerList;
import net.minecraft.server.v1_9_R1.EntityPlayer;
import net.minecraft.server.v1_9_R1.MinecraftServer;
import net.minecraft.server.v1_9_R1.Packet;
import net.minecraft.server.v1_9_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_9_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_9_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_9_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_9_R1.PacketPlayOutRespawn;

public class NickManager_1_9_R1 {

	@SuppressWarnings({ "deprecation", "unchecked" })
	public static void changeSkin(CraftPlayer cp, String skinName) {
		if (Utils.health.containsKey(cp.getUniqueId())) {
			Utils.heldItemSlots.remove(cp.getUniqueId());
			Utils.armor.remove(cp.getUniqueId());
			Utils.items.remove(cp.getUniqueId());
			Utils.health.remove(cp.getUniqueId());
			Utils.food.remove(cp.getUniqueId());
			Utils.locations.remove(cp.getUniqueId());
			Utils.scoreBoards.remove(cp.getUniqueId());
		}
		
		Utils.heldItemSlots.put(cp.getUniqueId(), cp.getInventory().getHeldItemSlot());
		Utils.armor.put(cp.getUniqueId(), cp.getInventory().getArmorContents());
		Utils.items.put(cp.getUniqueId(), new HashMap<>());
		
		for (Integer i = 0; i < cp.getInventory().getSize(); i++) {
			Utils.items.get(cp.getUniqueId()).put(i, cp.getInventory().getItem(i.intValue()));
		}
		Utils.health.put(cp.getUniqueId(), Double.valueOf(cp.getHealth()));
		Utils.food.put(cp.getUniqueId(), Integer.valueOf(cp.getFoodLevel()));
		Utils.locations.put(cp.getUniqueId(), cp.getLocation().add(0.0D, 0.5D, 0.0D));
		Utils.scoreBoards.put(cp.getUniqueId(), cp.getScoreboard());
		
		GameProfile gp = cp.getProfile();
		
		try {
			gp = GameProfileBuilder.fetch(UUIDFetcher.getUUID(skinName));
		} catch (Exception ex) {
		}
		
		Collection<Property> props = gp.getProperties().get("textures");
		cp.getProfile().getProperties().removeAll("textures");
		cp.getProfile().getProperties().putAll("textures", props);
		
		destroyPlayer(cp);
		removePlayerFromTab(cp);
		
		if(FileUtils.cfg.getBoolean("SeeNickSelf") == true) {
			EntityPlayer ep = cp.getHandle();
			PacketPlayOutRespawn packet = new PacketPlayOutRespawn(cp.getPlayer().getWorld().getEnvironment().getId(), ep.getWorld().getDifficulty(), ep.getWorld().getWorldData().getType(), ep.playerInteractManager.getGameMode());
			ep.playerConnection.sendPacket(packet);
			ep.playerConnection.teleport(new Location(cp.getPlayer().getWorld(), ep.locX, ep.locY, ep.locZ, ep.yaw, ep.pitch));
			cp.getPlayer().updateInventory();
		}
		
		try {
			ReflectUtils.setField(cp.getProfile(), "id", UUID.fromString(cp.getUniqueId().toString()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), new Runnable() {
			
			@Override
			public void run() {
				addPlayerToTab(cp);
				spawnPlayer(cp);
			}
		}, 5);
		
		try {
			Field f = PlayerList.class.getDeclaredField("playersByName");
			f.setAccessible(true);
			Map<String, EntityPlayer> map = (Map<String, EntityPlayer>) f.get(MinecraftServer.getServer().getPlayerList());
			ArrayList<String> toRemove = new ArrayList<>();
			
			for (String name : map.keySet()) {
				if(map.get(name).getUniqueID().toString().equals(cp.getUniqueId().toString()))
					toRemove.add(name);
			}
			
			for (String string : toRemove) {
				map.remove(string);
			}
			
			map.put(cp.getName(), cp.getHandle());
			f.set(MinecraftServer.getServer().getPlayerList(), map);
			f.setAccessible(false);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
		}
		
		if (Utils.health.containsKey(cp.getUniqueId())) {
			cp.teleport(Utils.locations.get(cp.getUniqueId()));

			cp.getInventory().setArmorContents(Utils.armor.get(cp.getUniqueId()));

			for (int i = 0; i < cp.getInventory().getSize(); i++) {
				cp.getInventory().setItem(i, (Utils.items.get(cp.getUniqueId())).get(i));
			}
			
			cp.setHealth((Utils.health.get(cp.getUniqueId())));
			cp.setFoodLevel((Utils.food.get(cp.getUniqueId())));
			cp.setScoreboard(Utils.scoreBoards.get(cp.getUniqueId()));
			cp.getInventory().setHeldItemSlot(Utils.heldItemSlots.get(cp.getUniqueId()));
			
			if (Utils.health.containsKey(cp.getUniqueId())) {
				Utils.heldItemSlots.remove(cp.getUniqueId());
				Utils.armor.remove(cp.getUniqueId());
				Utils.items.remove(cp.getUniqueId());
				Utils.health.remove(cp.getUniqueId());
				Utils.food.remove(cp.getUniqueId());
				Utils.locations.remove(cp.getUniqueId());
				Utils.scoreBoards.remove(cp.getUniqueId());
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void refreshPlayer(CraftPlayer cp) {
		if (Utils.health.containsKey(cp.getUniqueId())) {
			Utils.heldItemSlots.remove(cp.getUniqueId());
			Utils.armor.remove(cp.getUniqueId());
			Utils.items.remove(cp.getUniqueId());
			Utils.health.remove(cp.getUniqueId());
			Utils.food.remove(cp.getUniqueId());
			Utils.locations.remove(cp.getUniqueId());
			Utils.scoreBoards.remove(cp.getUniqueId());
		}
		
		Utils.heldItemSlots.put(cp.getUniqueId(), cp.getInventory().getHeldItemSlot());
		Utils.armor.put(cp.getUniqueId(), cp.getInventory().getArmorContents());
		Utils.items.put(cp.getUniqueId(), new HashMap<>());
		
		for (Integer i = 0; i < cp.getInventory().getSize(); i++) {
			Utils.items.get(cp.getUniqueId()).put(i, cp.getInventory().getItem(i.intValue()));
		}
		Utils.health.put(cp.getUniqueId(), Double.valueOf(cp.getHealth()));
		Utils.food.put(cp.getUniqueId(), Integer.valueOf(cp.getFoodLevel()));
		Utils.locations.put(cp.getUniqueId(), cp.getLocation().add(0.0D, 0.75D, 0.0D));
		Utils.scoreBoards.put(cp.getUniqueId(), cp.getScoreboard());
		
		destroyPlayer(cp);
		removePlayerFromTab(cp);
		
		if(FileUtils.cfg.getBoolean("SeeNickSelf") == true) {
			EntityPlayer ep = cp.getHandle();
			PacketPlayOutRespawn packet = new PacketPlayOutRespawn(cp.getPlayer().getWorld().getEnvironment().getId(), ep.getWorld().getDifficulty(), ep.getWorld().getWorldData().getType(), ep.playerInteractManager.getGameMode());
			ep.playerConnection.sendPacket(packet);
			ep.playerConnection.teleport(new Location(cp.getPlayer().getWorld(), ep.locX, ep.locY, ep.locZ, ep.yaw, ep.pitch));
			cp.getPlayer().updateInventory();
		}
		
		if((FileUtils.cfg.getBoolean("DeathByNicking") == true) && !(FileUtils.cfg.getBoolean("SeeNickSelf") == true)) {
			cp.setHealth(0);
			cp.spigot().respawn();
		}
		
		try {
			ReflectUtils.setField(cp.getProfile(), "id", UUID.fromString(cp.getUniqueId().toString()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), new Runnable() {
			
			@Override
			public void run() {
				addPlayerToTab(cp);
				spawnPlayer(cp);
			}
		}, 2);
		
		if (Utils.health.containsKey(cp.getUniqueId())) {
			cp.teleport(Utils.locations.get(cp.getUniqueId()));

			cp.getInventory().setArmorContents(Utils.armor.get(cp.getUniqueId()));

			for (int i = 0; i < cp.getInventory().getSize(); i++) {
				cp.getInventory().setItem(i, (Utils.items.get(cp.getUniqueId())).get(i));
			}

			cp.setHealth((Utils.health.get(cp.getUniqueId())));
			cp.setFoodLevel((Utils.food.get(cp.getUniqueId())));
			cp.setScoreboard(Utils.scoreBoards.get(cp.getUniqueId()));
			cp.getInventory().setHeldItemSlot(Utils.heldItemSlots.get(cp.getUniqueId()));
			
			if (Utils.health.containsKey(cp.getUniqueId())) {
				Utils.heldItemSlots.remove(cp.getUniqueId());
				Utils.armor.remove(cp.getUniqueId());
				Utils.items.remove(cp.getUniqueId());
				Utils.health.remove(cp.getUniqueId());
				Utils.food.remove(cp.getUniqueId());
				Utils.locations.remove(cp.getUniqueId());
				Utils.scoreBoards.remove(cp.getUniqueId());
			}
		}
	}
	
	public static void setName(CraftPlayer cp, String nickName) {
		try {
			Utils.field.set(cp.getProfile(), nickName);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		if(Utils.coloredTagsStatus()) {
			ColoredTags.updateNametag(cp.getPlayer());
            ColoredTags.updateTab(cp.getPlayer());
		}
	}

	public static void destroyPlayer(CraftPlayer cp) {
		for(Player all : Bukkit.getOnlinePlayers()) {
			CraftPlayer cpAll = ((CraftPlayer)all);
			
			if(!(cpAll.getUniqueId().equals(cp.getUniqueId()))) {
				if(!(all.hasPermission("nick.bypass"))) {
					((CraftPlayer)all).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(cp.getEntityId()));
				}
			} else {
				if(FileUtils.cfg.getBoolean("SeeNickSelf") == true) {
					((CraftPlayer)all).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(cp.getEntityId()));
				}
			}
		}
	}

	public static void removePlayerFromTab(CraftPlayer cp) {
		for(Player all : Bukkit.getOnlinePlayers()) {
			CraftPlayer cpAll = ((CraftPlayer)all);
			
			if(!(cpAll.getUniqueId().equals(cp.getUniqueId()))) {
				if(!(all.hasPermission("nick.bypass"))) {
					sendPacket(cpAll, new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, cp.getHandle()));
				}
			} else {
				if(FileUtils.cfg.getBoolean("SeeNickSelf") == true) {
					sendPacket(cp, new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, cp.getHandle()));
				}
			}
		}
	}

	public static void spawnPlayer(CraftPlayer cp) {
		for(Player all : Bukkit.getOnlinePlayers()) {
			CraftPlayer cpAll = ((CraftPlayer)all);
			
			if(!(cpAll.getUniqueId().equals(cp.getUniqueId()))) {
				if(!(all.hasPermission("nick.bypass"))) {
					sendPacket(cpAll, new PacketPlayOutNamedEntitySpawn(cp.getHandle()));
				}
			}
		}
	}

	public static void addPlayerToTab(CraftPlayer cp) {
		for(Player all : Bukkit.getOnlinePlayers()) {
			CraftPlayer cpAll = ((CraftPlayer)all);
			
			if(!(cpAll.getUniqueId().equals(cp.getUniqueId()))) {
				if(!(all.hasPermission("nick.bypass"))) {
					sendPacket(cpAll, new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, cp.getHandle()));
				}
			} else {
				if(FileUtils.cfg.getBoolean("SeeNickSelf") == true) {
					sendPacket(cpAll, new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, cp.getHandle()));
				}
			}
		}
	}
	
	public static void sendPacket(CraftPlayer cp, Packet<?> packet) {
		cp.getHandle().playerConnection.sendPacket(packet);
	}
	
	public static Field getField(Class<?> clazz, String name) {
		Field field;
		
		try {
			field = clazz.getDeclaredField(name);
			field.setAccessible(true);
			
			if(Modifier.isFinal(field.getModifiers())) {
				Field modifierField = Field.class.getDeclaredField("modifiers");
				modifierField.setAccessible(true);
				
				modifierField.set(field, Integer.valueOf(field.getModifiers() & 0xFFFFFFEF));
			}
			
			return field;
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
			return null;
		}
	}
	
}
