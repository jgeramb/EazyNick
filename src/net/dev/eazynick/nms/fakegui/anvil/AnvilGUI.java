package net.dev.eazynick.nms.fakegui.anvil;

import java.lang.reflect.*;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.nms.ReflectionHelper;

public class AnvilGUI {
	
	private EazyNick eazyNick;
	private Player player;
	public AnvilClickEventHandler handler;
	private HashMap<AnvilSlot, ItemStack> items = new HashMap<AnvilSlot, ItemStack>();
	private Class<?> blockPositionClass, containerAnvil, entityHuman;
	private Inventory inv;
	private Listener listener;

	public AnvilGUI(final Player player, final AnvilClickEventHandler handler) {
		eazyNick = EazyNick.getInstance();
		ReflectionHelper reflectionHelper = eazyNick.getReflectionHelper();
		
		boolean is17 = eazyNick.getVersion().startsWith("1_17");
		
		blockPositionClass = reflectionHelper.getNMSClass(is17 ? "core.BlockPosition" : "BlockPosition");
		containerAnvil = reflectionHelper.getNMSClass(is17 ? "world.inventory.ContainerAnvil" : "ContainerAnvil");
		entityHuman = reflectionHelper.getNMSClass(is17 ? "world.entity.player.EntityHuman" : "EntityHuman");
		
		this.player = player;
		this.handler = handler;

		this.listener = new Listener() {
			
			@EventHandler
			public void onInventoryClick(InventoryClickEvent event) {
				if (event.getWhoClicked() instanceof Player) {
					if (event.getInventory().equals(inv)) {
						event.setCancelled(true);

						ItemStack item = event.getCurrentItem();
						int slot = event.getRawSlot();
						String name = "";

						if (item != null) {
							if (item.hasItemMeta()) {
								ItemMeta meta = item.getItemMeta();

								if (meta.hasDisplayName())
									name = meta.getDisplayName();
							}
						}

						AnvilClickEvent clickEvent = new AnvilClickEvent(AnvilSlot.bySlot(slot), name);

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
					
					player.setLevel(player.getLevel() - 1);
					
					if (inv.equals(AnvilGUI.this.inv)) {
						inv.clear();
						
						destroy();
					}
				}
			}

			@EventHandler
			public void onPlayerQuit(PlayerQuitEvent event) {
				if (event.getPlayer().equals(getPlayer())) {
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

	public void open() throws IllegalAccessException, InvocationTargetException, InstantiationException {
		ReflectionHelper reflectionHelper = eazyNick.getReflectionHelper();
		
		player.setLevel(player.getLevel() + 1);
		player.closeInventory();

		try {
			String version = eazyNick.getVersion();
			boolean is17 = version.startsWith("1_17");
			Class<?> playerInventoryClass = reflectionHelper.getNMSClass(is17 ? "world.entity.player.PlayerInventory" : "PlayerInventory"), worldClass = reflectionHelper.getNMSClass(is17 ? "world.level.World" : "World");
			Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player), playerInventory = reflectionHelper.getField(reflectionHelper.getNMSClass(is17 ? "world.entity.player.EntityHuman" : "EntityHuman"), is17 ? "co" : "inventory").get(entityPlayer), world = reflectionHelper.getField(reflectionHelper.getNMSClass(is17 ? "world.entity.Entity" : "Entity"), is17 ? "t" : "world").get(entityPlayer), blockPosition = blockPositionClass.getConstructor(int.class, int.class, int.class).newInstance(0, 0, 0);
			int c = (int) invokeMethod("nextContainerCounter", entityPlayer);
			//TODO: Upgrade to 1.17
			Object container = (is17 || version.startsWith("1_16") || version.startsWith("1_15") || version.startsWith("1_14")) ? containerAnvil.getConstructor(int.class, playerInventoryClass, reflectionHelper.getNMSClass(is17 ? "world.inventory.ContainerAccess" : "ContainerAccess")).newInstance(c, playerInventory, reflectionHelper.getNMSClass(is17 ? "world.inventory.ContainerAccess" : "ContainerAccess").getMethod("at", worldClass, blockPositionClass).invoke(null, world, blockPosition)) : containerAnvil.getConstructor(playerInventoryClass, worldClass, blockPositionClass, entityHuman).newInstance(playerInventory, world, blockPosition, entityPlayer);
			
			reflectionHelper.getField(reflectionHelper.getNMSClass(is17 ? "world.inventory.Container" : "Container"), "checkReachable").set(container, false);

			Object bukkitView = invokeMethod("getBukkitView", container);
			inv = (Inventory) invokeMethod("getTopInventory", bukkitView);
			
			for (AnvilSlot slot : items.keySet())
				inv.setItem(slot.getSlot(), items.get(slot));
			
			Constructor<?> chatMessageConstructor = reflectionHelper.getNMSClass(is17 ? "network.chat.ChatMessage" : "ChatMessage").getConstructor(String.class, Object[].class);
			Object playerConnection = reflectionHelper.getField(reflectionHelper.getNMSClass(is17 ? "server.level.EntityPlayer" : "EntityPlayer"), is17 ? "b" : "playerConnection").get(entityPlayer), chatMessage = chatMessageConstructor.newInstance("Repairing", new Object[] {});
			Object packet = (is17 || version.startsWith("1_16")) ? reflectionHelper.getNMSClass(is17 ? "network.protocol.game.PacketPlayOutOpenWindow" : "PacketPlayOutOpenWindow").getConstructor(int.class, reflectionHelper.getNMSClass(is17 ? "world.inventory.Containers" : "Containers"), reflectionHelper.getNMSClass(is17 ? "network.chat.IChatBaseComponent" : "IChatBaseComponent")).newInstance(c, reflectionHelper.getNMSClass(is17 ? "world.inventory.Containers" : "Containers").getDeclaredField(is17 ? "h" : "ANVIL").get(null), chatMessage) : reflectionHelper.getNMSClass("PacketPlayOutOpenWindow").getConstructor(int.class, String.class, reflectionHelper.getNMSClass("IChatBaseComponent"), int.class).newInstance(c, "minecraft:anvil", chatMessage, 0);

			Method sendPacket = getMethod("sendPacket", playerConnection.getClass(), reflectionHelper.getNMSClass(is17 ? "network.protocol.Packet" : "Packet"));
			sendPacket.invoke(playerConnection, packet);

			Field activeContainerField = reflectionHelper.getField(entityHuman, is17 ? "bV" : "activeContainer");
			activeContainerField.set(entityPlayer, container);

			reflectionHelper.getField(reflectionHelper.getNMSClass(is17 ? "world.inventory.ContainerAnvil" : "ContainerAnvil"), is17 ? "v" : ((version.startsWith("1_1") && !(version.startsWith("1_10") || version.startsWith("1_11"))) ? "renameText" : "l")).set(activeContainerField.get(entityPlayer), "Type in some text");
			reflectionHelper.getField(reflectionHelper.getNMSClass(is17 ? "world.inventory.Container" : "Container"), is17 ? "j" : "windowId").set(activeContainerField.get(entityPlayer), c);

			activeContainerField.get(entityPlayer).getClass().getMethod("addSlotListener", reflectionHelper.getNMSClass(is17 ? "world.inventory.ICrafting" : "ICrafting")).invoke(activeContainerField.get(entityPlayer), is17 ? reflectionHelper.getField(entityPlayer.getClass(), "cX").get(entityPlayer) : entityPlayer);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private Method getMethod(String name, Class<?> clazz, Class<?>... args) {
		try {
			Method method = clazz.getMethod(name, args);
			
			return method;
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

		private int slot;

		private AnvilSlot(int slot) {
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

	public class AnvilClickEvent {
		
		private AnvilSlot slot;
		private String name;
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