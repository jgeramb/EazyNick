package net.dev.eazynick.nms.fakegui.sign;

import java.lang.reflect.Field;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.nms.ReflectionHelper;
import net.dev.eazynick.utilities.Utils;

public class SignGUI implements Listener {

	private HashMap<Player, Block> blocks = new HashMap<>();
	private HashMap<Player, Material> oldTypes = new HashMap<>();
	private HashMap<Player, EditCompleteListener> editCompleteListeners = new HashMap<>();
	
	public void open(Player player, String line1, String line2, String line3, String line4, EditCompleteListener editCompleteListener) {
		EazyNick eazyNick = EazyNick.getInstance();
		ReflectionHelper reflectionHelper = eazyNick.getReflectionHelper();
		Utils utils = eazyNick.getUtils();
		
		Block block = player.getWorld().getBlockAt(player.getLocation().clone().add(0, 250 - player.getLocation().getBlockY(), 0));
		
		blocks.put(player, block);
		oldTypes.put(player, block.getType());
		editCompleteListeners.put(player, editCompleteListener);
		
		block.setType(Material.getMaterial((utils.isNewVersion() && !(eazyNick.getVersion().startsWith("1_13"))) ? "OAK_SIGN" : (eazyNick.getVersion().startsWith("1_13") ? "SIGN" : "SIGN_POST")));
		
		Sign sign = (Sign) block.getState();
		sign.setLine(0, line1);
		sign.setLine(1, line2);
		sign.setLine(2, line3);
		sign.setLine(3, line4);
		sign.update(false, false);
		
		Bukkit.getOnlinePlayers().stream().filter(currentPlayer -> (currentPlayer != player)).forEach(currentPlayer -> currentPlayer.sendBlockChange(block.getLocation(), Material.AIR, (byte) 0));
		
		Bukkit.getScheduler().runTaskLater(eazyNick, () -> {
			try {
				boolean useCraftBlockEntityState = utils.isNewVersion() || Bukkit.getVersion().contains("1.12.2") || Bukkit.getVersion().contains("1.12.1");
				Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
				Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);

				Field tileField = (useCraftBlockEntityState ? reflectionHelper.getCraftClass("block.CraftBlockEntityState") : sign.getClass()).getDeclaredField(useCraftBlockEntityState ? "tileEntity" : "sign");
				tileField.setAccessible(true);
				Object tileSign = tileField.get(sign);

				Field editable = tileSign.getClass().getDeclaredField("isEditable");
				editable.setAccessible(true);
				editable.set(tileSign, true);

				Field handler = tileSign.getClass().getDeclaredField((eazyNick.getVersion().startsWith("1_15") || eazyNick.getVersion().startsWith("1_16")) ? "c" : (eazyNick.getVersion().startsWith("1_14") ? "j" : (eazyNick.getVersion().startsWith("1_13") ? "g" : "h")));
				handler.setAccessible(true);
				handler.set(tileSign, entityPlayer);
				
				playerConnection.getClass().getDeclaredMethod("sendPacket", reflectionHelper.getNMSClass("Packet")).invoke(playerConnection, reflectionHelper.getNMSClass("PacketPlayOutOpenSignEditor").getConstructor(reflectionHelper.getNMSClass("BlockPosition")).newInstance(reflectionHelper.getNMSClass("BlockPosition").getConstructor(double.class, double.class, double.class).newInstance(sign.getX(), sign.getY(), sign.getZ())));
			} catch (Exception ex) {
	        	ex.printStackTrace();
	        }
		}, 3);
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