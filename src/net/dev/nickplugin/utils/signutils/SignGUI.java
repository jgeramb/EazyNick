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
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.dev.nickplugin.NickPlugin;
import net.dev.nickplugin.utils.ReflectUtils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

public class SignGUI implements Listener {

	public static void open(Player p, String line1, String line2, String line3, String line4, EditCompleteListener listener) {
		Block b = p.getWorld().getBlockAt(p.getLocation()).getRelative(BlockFace.UP);
		b.setType(Material.getMaterial(NickPlugin.version.startsWith("1_14") ? "OAK_SIGN" : (NickPlugin.version.startsWith("1_13") ? "SIGN" : "SIGN_POST")));
		
		Sign sign = (Sign) b.getState();
		sign.setLine(0, line1);
		sign.setLine(1, line2);
		sign.setLine(2, line3);
		sign.setLine(3, line4);
		sign.update();
		
		Bukkit.getScheduler().runTaskLater(NickPlugin.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				try {
					boolean useCraftBlockEntityState = NickPlugin.version.startsWith("1_14") || NickPlugin.version.startsWith("1_13") || NickPlugin.version.startsWith("1_12");
					Object entityPlayer = p.getClass().getMethod("getHandle").invoke(p);
					Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);

					Field tileField = (useCraftBlockEntityState ? ReflectUtils.getCraftClass("block.CraftBlockEntityState") : sign.getClass()).getDeclaredField(useCraftBlockEntityState ? "tileEntity" : "sign");
					tileField.setAccessible(true);
					Object tileSign = tileField.get(sign);

					Field editable = tileSign.getClass().getDeclaredField("isEditable");
					editable.setAccessible(true);
					editable.set(tileSign, true);

					Field handler = tileSign.getClass().getDeclaredField(NickPlugin.version.startsWith("1_14") ? "j" : (NickPlugin.version.startsWith("1_13") ? "g" : "h"));
					handler.setAccessible(true);
					handler.set(tileSign, entityPlayer);

					playerConnection.getClass().getDeclaredMethod("sendPacket", ReflectUtils.getNMSClass("Packet")).invoke(playerConnection, ReflectUtils.getNMSClass("PacketPlayOutOpenSignEditor").getConstructor(ReflectUtils.getNMSClass("BlockPosition")).newInstance(ReflectUtils.getNMSClass("BlockPosition").getConstructor(double.class, double.class, double.class).newInstance(sign.getX(), sign.getY(), sign.getZ())));
		            
					Bukkit.getOnlinePlayers().stream().filter(all -> (all != p)).forEach(all -> all.sendBlockChange(b.getLocation(), Material.AIR, (byte) 0));
					
					Object networkManager = playerConnection.getClass().getDeclaredField("networkManager").get(playerConnection);
					Channel channel = (Channel) networkManager.getClass().getDeclaredField("channel").get(networkManager);
					
					Bukkit.getPluginManager().registerEvents(new Listener() {
						
						@EventHandler
						public void onQuit(PlayerQuitEvent e) {
							if(e.getPlayer() == p) {
								if (channel.pipeline().get("PacketInjector") != null)
									channel.pipeline().remove("PacketInjector");
							}
						}
						
						@EventHandler
						public void onKick(PlayerKickEvent e) {
							if(e.getPlayer() == p) {
								if (channel.pipeline().get("PacketInjector") != null)
									channel.pipeline().remove("PacketInjector");
							}
						}
						
					}, NickPlugin.getInstance());
					
					if (channel.pipeline().get("PacketInjector") == null) {
						channel.pipeline().addBefore("packet_handler", "PacketInjector", new ChannelDuplexHandler() {
							
							@Override
							public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
								if(packet.getClass().getName().endsWith("PacketPlayInUpdateSign")) {
									Object[] rawLines = (Object[]) ReflectUtils.getField(packet.getClass(), "b").get(packet);
									
									Bukkit.getScheduler().runTask(NickPlugin.getInstance(), new Runnable() {
										
										@Override
										public void run() {
											try {
												String[] lines = new String[4];

												if(NickPlugin.version.startsWith("1_8")) {
													int i = 0;
													
													for (Object obj : rawLines) {
														lines[i] = (String) obj.getClass().getMethod("getText").invoke(obj);
														
														i++;
													}
												} else
													lines = (String[]) rawLines;
												
												b.setType(Material.AIR);
												
												if (channel.pipeline().get("PacketInjector") != null)
													channel.pipeline().remove("PacketInjector");
												
												listener.onEditComplete(new EditCompleteEvent(lines));
											} catch (Exception ex) {
												ex.printStackTrace();
											}
										}
									});
								}
							}
							
						});
					}
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