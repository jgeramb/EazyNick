package net.dev.eazynick.nms.fakegui.sign;

import java.lang.reflect.Field;
import java.util.HashMap;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.nms.ReflectionHelper;
import net.dev.eazynick.utilities.AsyncTask;
import net.dev.eazynick.utilities.AsyncTask.AsyncRunnable;
import net.dev.eazynick.utilities.Utils;

public class SignGUI implements Listener {

	private EazyNick eazyNick = EazyNick.getInstance();
	private ReflectionHelper reflectionHelper;
	private Utils utils;
	
	private HashMap<Player, Block> blocks;
	private HashMap<Player, Material> oldTypes;
	private HashMap<Player, EditCompleteListener> editCompleteListeners;
	
	public SignGUI(EazyNick eazyNick) {
		this.eazyNick = eazyNick;
		this.reflectionHelper = eazyNick.getReflectionHelper();
		this.utils = eazyNick.getUtils();
		
		this.blocks = new HashMap<>();
		this.oldTypes = new HashMap<>();
		this.editCompleteListeners = new HashMap<>();
	}
	
	public void open(Player player, String line1, String line2, String line3, String line4, EditCompleteListener editCompleteListener) {
		String version = eazyNick.getVersion();
		boolean is17 = version.startsWith("1_17"), is18 = version.startsWith("1_18");
		Block block = player.getWorld().getBlockAt(player.getLocation().clone().add(0, 250 - player.getLocation().getBlockY(), 0));
		
		blocks.put(player, block);
		oldTypes.put(player, block.getType());
		editCompleteListeners.put(player, editCompleteListener);
		
		block.setType(Material.getMaterial((utils.isVersion13OrLater() && !(version.startsWith("1_13"))) ? "OAK_SIGN" : (version.startsWith("1_13") ? "SIGN" : "SIGN_POST")));
		
		Sign sign = (Sign) block.getState();
		sign.setLine(0, line1);
		sign.setLine(1, line2);
		sign.setLine(2, line3);
		sign.setLine(3, line4);
		sign.update(false, false);
		
		Bukkit.getOnlinePlayers().stream().filter(currentPlayer -> (currentPlayer != player)).forEach(currentPlayer -> {
			if(is18) {
				currentPlayer.sendBlockChange(block.getLocation(), Material.AIR.createBlockData());
			} else {
				try {
					currentPlayer.getClass().getMethod("sendBlockChange", Location.class, Material.class, byte.class).invoke(currentPlayer, block.getLocation(), Material.AIR, (byte) 0);
				} catch (Exception ignore) {
				}
			}
		});
		
		new AsyncTask(new AsyncRunnable() {
			
			@Override
			public void run() {
				try {
					boolean useCraftBlockEntityState = utils.isVersion13OrLater() || Bukkit.getVersion().contains("1.12.2") || Bukkit.getVersion().contains("1.12.1");
					Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
					Object playerConnection = entityPlayer.getClass().getField((is17 || is18) ? "b" : "playerConnection").get(entityPlayer);
	
					Field tileField = (useCraftBlockEntityState ? reflectionHelper.getCraftClass("block.CraftBlockEntityState") : sign.getClass()).getDeclaredField(useCraftBlockEntityState ? "tileEntity" : "sign");
					tileField.setAccessible(true);
					Object tileSign = tileField.get(sign);
	
					Field editable = tileSign.getClass().getDeclaredField((is17 || is18) ? "f" : "isEditable");
					editable.setAccessible(true);
					editable.set(tileSign, true);
					
					Field handler = tileSign.getClass().getDeclaredField((version.startsWith("1_15") || version.startsWith("1_16")) ? "c" : (version.startsWith("1_14") ? "j" : (version.startsWith("1_13") ? "g" : ((is17 || is18) ? "g" : "h"))));
					handler.setAccessible(true);
					handler.set(tileSign, (is17 || is18) ? player.getUniqueId() : entityPlayer);
					
					playerConnection.getClass().getDeclaredMethod(is18 ? "a" : "sendPacket", reflectionHelper.getNMSClass((is17 || is18) ? "network.protocol.Packet" : "Packet")).invoke(playerConnection, reflectionHelper.getNMSClass((is17 || is18) ? "network.protocol.game.PacketPlayOutOpenSignEditor" : "PacketPlayOutOpenSignEditor").getConstructor(reflectionHelper.getNMSClass((is17 || is18) ? "core.BlockPosition" : "BlockPosition")).newInstance(reflectionHelper.getNMSClass((is17 || is18) ? "core.BlockPosition" : "BlockPosition").getConstructor(double.class, double.class, double.class).newInstance(sign.getX(), sign.getY(), sign.getZ())));
				} catch (Exception ex) {
		        	ex.printStackTrace();
		        }
			}
		}, 50L * 3).run();
	}
	
	public HashMap<Player, Block> getBlocks() {
		return blocks;
	}
	
	public HashMap<Player, Material> getOldTypes() {
		return oldTypes;
	}
	
	public HashMap<Player, EditCompleteListener> getEditCompleteListeners() {
		return editCompleteListeners;
	}
	
	public static interface EditCompleteListener {
		
		void onEditComplete(EditCompleteEvent event);
		
	}
	
	public static class EditCompleteEvent {
		
		private String[] lines;
		
		public EditCompleteEvent(String[] lines) {
			this.lines = lines;
		}
		
		public String[] getLines() {
			return lines;
		}
		
	}
    
}