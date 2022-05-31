package com.justixdev.eazynick.nms.fakegui.sign;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.nms.ReflectionHelper;
import com.justixdev.eazynick.utilities.AsyncTask;
import com.justixdev.eazynick.utilities.AsyncTask.AsyncRunnable;
import com.justixdev.eazynick.utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class SignGUI implements Listener {

	private final EazyNick eazyNick;
	private final ReflectionHelper reflectionHelper;
	private final Utils utils;
	
	private final Map<Player, Block> blocks;
	private final Map<Player, Material> oldTypes;
	private final Map<Player, EditCompleteListener> editCompleteListeners;
	
	public SignGUI(EazyNick eazyNick) {
		this.eazyNick = eazyNick;
		this.reflectionHelper = eazyNick.getReflectionHelper();
		this.utils = eazyNick.getUtils();
		
		this.blocks = new HashMap<>();
		this.oldTypes = new HashMap<>();
		this.editCompleteListeners = new HashMap<>();
	}
	
	@SuppressWarnings("ConstantConditions")
	public void open(Player player, String line1, String line2, String line3, String line4, EditCompleteListener editCompleteListener) {
		String version = eazyNick.getVersion();
		boolean is17 = version.startsWith("1_17"), is18 = version.startsWith("1_18");
		Block block = player.getWorld().getBlockAt(player.getLocation().clone().add(0, 250 - player.getLocation().getBlockY(), 0));
		
		blocks.put(player, block);
		oldTypes.put(player, block.getType());
		editCompleteListeners.put(player, editCompleteListener);
		
		block.setType(Material.getMaterial(
				(utils.isVersion13OrLater() && !(version.startsWith("1_13")))
						? "OAK_SIGN"
						: (
								version.startsWith("1_13")
										? "SIGN"
										: "SIGN_POST"
						)
		));

		Bukkit.getScheduler().runTask(eazyNick, () -> {
			Sign sign = (Sign) block.getState();
			sign.setLine(0, line1);
			sign.setLine(1, line2);
			sign.setLine(2, line3);
			sign.setLine(3, line4);
			sign.update(false, false);

			Bukkit.getOnlinePlayers()
					.stream()
					.filter(currentPlayer -> (currentPlayer != player))
					.forEach(currentPlayer -> {
				if(is18) {
					currentPlayer.sendBlockChange(block.getLocation(), Material.AIR.createBlockData());
				} else {
					try {
						currentPlayer.getClass().getMethod(
								"sendBlockChange",
								Location.class,
								Material.class,
								byte.class
						).invoke(
								currentPlayer,
								block.getLocation(),
								Material.AIR,
								(byte) 0
						);
					} catch (Exception ignore) {
					}
				}
			});

			new AsyncTask(new AsyncRunnable() {

				@Override
				public void run() {
					try {
						boolean useCraftBlockEntityState = utils.isVersion13OrLater()
								|| Bukkit.getVersion().contains("1.12.2")
								|| Bukkit.getVersion().contains("1.12.1");
						Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
						Object playerConnection = entityPlayer.getClass().getField(
								(is17 || is18)
										? "b"
										: "playerConnection"
						).get(entityPlayer);

						Field tileField = (useCraftBlockEntityState
								? reflectionHelper.getCraftClass("block.CraftBlockEntityState")
								: sign.getClass()
						).getDeclaredField(useCraftBlockEntityState ? "tileEntity" : "sign");
						tileField.setAccessible(true);
						Object tileSign = tileField.get(sign);

						Field editable = tileSign.getClass().getDeclaredField((is17 || is18) ? "f" : "isEditable");
						editable.setAccessible(true);
						editable.set(tileSign, true);

						Field handler = tileSign.getClass().getDeclaredField(
								(version.startsWith("1_15") || version.startsWith("1_16"))
										? "c" : (
												version.startsWith("1_14")
														? "j"
														: (
																version.startsWith("1_13")
																		? "g"
																		: (
																				(is17 || is18)
																						? "g"
																						: "h"
																		)
														)
										)
						);
						handler.setAccessible(true);
						handler.set(
								tileSign,
								(is17 || is18)
										? player.getUniqueId()
										: entityPlayer
						);

						playerConnection.getClass().getDeclaredMethod(
								is18
										? "a" :
										"sendPacket",
								reflectionHelper.getNMSClass(
										(is17 || is18)
												? "network.protocol.Packet"
												: "Packet"
								)).invoke(
										playerConnection,
								reflectionHelper.getNMSClass(
										(is17 || is18)
												? "network.protocol.game.PacketPlayOutOpenSignEditor"
												: "PacketPlayOutOpenSignEditor"
								).getConstructor(reflectionHelper.getNMSClass(
										(is17 || is18)
												? "core.BlockPosition"
												: "BlockPosition"
								)).newInstance(reflectionHelper.getNMSClass(
										(is17 || is18)
												? "core.BlockPosition"
												: "BlockPosition"
								).getConstructor(
										double.class,
										double.class,
										double.class
								).newInstance(
										sign.getX(),
										sign.getY(),
										sign.getZ()
								)));
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}, 50L * 3).run();
		});
	}
	
	public Map<Player, Block> getBlocks() {
		return blocks;
	}
	
	public Map<Player, Material> getOldTypes() {
		return oldTypes;
	}
	
	public Map<Player, EditCompleteListener> getEditCompleteListeners() {
		return editCompleteListeners;
	}
	
	public interface EditCompleteListener {
		
		void onEditComplete(EditCompleteEvent event);
		
	}
	
	public static class EditCompleteEvent {
		
		private final String[] lines;
		
		public EditCompleteEvent(String[] lines) {
			this.lines = lines;
		}
		
		public String[] getLines() {
			return lines;
		}
		
	}
    
}