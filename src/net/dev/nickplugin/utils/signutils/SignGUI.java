package net.dev.nickplugin.utils.signutils;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import net.dev.nickplugin.main.Main;
import net.dev.nickplugin.utils.ReflectUtils;

public class SignGUI implements Listener {

	public static void open(Player p, String line1, String line2, String line3, String line4, EditCompleteListener listener) {
		Block b = p.getWorld().getBlockAt(p.getLocation()).getRelative(BlockFace.UP);
		b.setType(Material.SIGN_POST);
		
		Bukkit.getOnlinePlayers().stream().forEach(all -> all.sendBlockChange(b.getLocation(), Material.AIR, (byte) 0));
		
		Sign sign = (Sign) b.getState();
		sign.setLine(0, line1);
		sign.setLine(1, line2);
		sign.setLine(2, line3);
		sign.setLine(3, line4);
		sign.update();
		
		Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				try {
					Object entityPlayer = p.getClass().getMethod("getHandle").invoke(p);
					Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);

					Field tileField = sign.getClass().getDeclaredField("sign");
					tileField.setAccessible(true);
					Object tileSign = tileField.get(sign);

					Field editable = tileSign.getClass().getDeclaredField("isEditable");
					editable.setAccessible(true);
					editable.set(tileSign, true);

					Field handler = tileSign.getClass().getDeclaredField("h");
					handler.setAccessible(true);
					handler.set(tileSign, entityPlayer);

					playerConnection.getClass().getDeclaredMethod("sendPacket", ReflectUtils.getNMSClass("Packet")).invoke(playerConnection, ReflectUtils.getNMSClass("PacketPlayOutOpenSignEditor").getConstructor(ReflectUtils.getNMSClass("BlockPosition")).newInstance(ReflectUtils.getNMSClass("BlockPosition").getConstructor(double.class, double.class, double.class).newInstance(sign.getX(), sign.getY(), sign.getZ())));
		            
		            Bukkit.getPluginManager().registerEvents(new Listener() {
		            	
		            	@EventHandler
		            	public void onSignChange(SignChangeEvent e) {
		            		if(e.getPlayer().getUniqueId().equals(p.getUniqueId())) {
		            			listener.onEditComplete(new EditCompleteEvent(e.getLines()));
		            			
		            			e.getBlock().setType(Material.AIR);
		            		}
		            	}
		            	
					}, Main.getInstance());
				} catch (Exception ex) {
		        	ex.printStackTrace();
		        }
			}
		}, 5);
	}
	
	public interface EditCompleteListener {
		
		void onEditComplete(EditCompleteEvent e);
		
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