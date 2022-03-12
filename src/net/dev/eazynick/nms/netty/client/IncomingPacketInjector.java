package net.dev.eazynick.nms.netty.client;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.nms.ReflectionHelper;
import net.dev.eazynick.nms.fakegui.sign.SignGUI;
import net.dev.eazynick.nms.fakegui.sign.SignGUI.EditCompleteEvent;

import io.netty.channel.*;

public class IncomingPacketInjector {

	private EazyNick eazyNick;
	
	private Channel channel;
	private String handlerName;
	
	public IncomingPacketInjector(Player player) {
		this.eazyNick = EazyNick.getInstance();
		
		ReflectionHelper reflectionHelper = eazyNick.getReflectionHelper();
		SignGUI signGUI = eazyNick.getSignGUI();
		
		try {
			String version = eazyNick.getVersion();
			boolean is17 = version.startsWith("1_17"), is18 = version.startsWith("1_18");
			Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
			Object playerConnection = entityPlayer.getClass().getField((is17 || is18) ? "b" : "playerConnection").get(entityPlayer);
			Object networkManager = playerConnection.getClass().getField((is17 || is18) ? "a" : "networkManager").get(playerConnection);
			
			//Get netty channel
			this.channel = (Channel) networkManager.getClass().getField((is17 || is18) ? (version.equals("1_18_R2") ? "m" : "k") : "channel").get(networkManager);
			this.handlerName = eazyNick.getDescription().getName().toLowerCase() + "_injector";
			
			unregister();
			
			//Add packet handler to netty channel
			channel.pipeline().addBefore("packet_handler", handlerName, new ChannelDuplexHandler() {
				
				@Override
				public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
					if(packet.getClass().getName().endsWith("PacketPlayInUpdateSign")) {
						if(signGUI.getEditCompleteListeners().containsKey(player)) {
							//Process SignGUI success
							Object[] rawLines = (Object[]) reflectionHelper.getField(packet.getClass(), (is17 || is18) ? "c" : "b").get(packet);
							
							Bukkit.getScheduler().runTask(eazyNick, () -> {
								try {
									String[] lines = new String[4];

									if(version.startsWith("1_8")) {
										int i = 0;
										
										for (Object obj : rawLines) {
											lines[i] = (String) obj.getClass().getMethod("getText").invoke(obj);
											
											i++;
										}
									} else
										lines = (String[]) rawLines;
									
									if (channel.pipeline().get("PacketInjector") != null)
										channel.pipeline().remove("PacketInjector");
									
									signGUI.getEditCompleteListeners().get(player).onEditComplete(new EditCompleteEvent(lines));
									signGUI.getBlocks().get(player).setType(signGUI.getOldTypes().get(player));
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							});
						}
					} else if(packet.getClass().getName().endsWith("PacketPlayInTabComplete")) {
						try {
							//Cache input text
							eazyNick.getUtils().getTextsToComplete().put(player, (String) reflectionHelper.getField(packet.getClass(), eazyNick.getUtils().isVersion13OrLater() ? "b" : "a").get(packet));
						} catch (IllegalArgumentException | IllegalAccessException ex) {
							ex.printStackTrace();
						}
					}
					
					super.channelRead(ctx, packet);
				}
				
			});
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void unregister() {
		try {
			if(channel.pipeline().get(handlerName) != null)
				channel.pipeline().remove(handlerName);
		} catch (Exception ignore) {
		}
	}
	
}