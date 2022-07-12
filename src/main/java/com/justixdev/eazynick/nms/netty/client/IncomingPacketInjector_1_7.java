package com.justixdev.eazynick.nms.netty.client;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.nms.ReflectionHelper;
import com.justixdev.eazynick.nms.fakegui.sign.SignGUI;
import com.justixdev.eazynick.nms.fakegui.sign.SignGUI.EditCompleteEvent;
import com.justixdev.eazynick.utilities.AsyncTask;
import net.minecraft.util.io.netty.channel.Channel;
import net.minecraft.util.io.netty.channel.ChannelDuplexHandler;
import net.minecraft.util.io.netty.channel.ChannelHandlerContext;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class IncomingPacketInjector_1_7 {

	private final EazyNick eazyNick;
	
	private Channel channel;
	private String handlerName;
	
	public IncomingPacketInjector_1_7(Player player) {
		this.eazyNick = EazyNick.getInstance();
		
		ReflectionHelper reflectionHelper = eazyNick.getReflectionHelper();
		SignGUI signGUI = eazyNick.getSignGUI();
		
		try {
			Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
			Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
			Object networkManager = playerConnection.getClass().getDeclaredField("networkManager").get(playerConnection);
			
			//Get netty channel
			this.channel = (Channel) networkManager.getClass().getDeclaredField("m").get(networkManager);
			this.handlerName = eazyNick.getDescription().getName().toLowerCase() + "_injector";
			
			unregister();
			
			if (channel.pipeline().get(handlerName) != null) return;

			//Add packet handler to netty channel
			channel.pipeline().addBefore(
					"packet_handler",
					handlerName,
					new ChannelDuplexHandler() {

						@Override
						public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
							try {
								if(packet.getClass().getName().endsWith("PacketPlayInUpdateSign")) {
									if(!(signGUI.getEditCompleteListeners().containsKey(player))) return;

									//Process SignGUI success
									Object[] rawLines = (Object[]) reflectionHelper.getField(packet.getClass(), "b").get(packet);

									Bukkit.getScheduler().runTask(eazyNick, () -> {
										try {
											String[] lines = new String[4];

											if(eazyNick.getVersion().startsWith("1_8")) {
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
								} else if(packet.getClass().getName().endsWith("PacketPlayInTabComplete")) {
									//Cache input text
									eazyNick.getUtils().getTextsToComplete().put(
											player,
											(String) reflectionHelper.getField(packet.getClass(), "a").get(packet)
									);
								}
							} catch (Exception ex) {
								ex.printStackTrace();
							}

							super.channelRead(ctx, packet);
						}
					}
			);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void unregister() {
		Thread killThread = new Thread(() -> {
			try {
				channel.pipeline().remove(handlerName);
			} catch (Exception ignore) {
			}
		});
		
		killThread.start();
		
		new AsyncTask(new AsyncTask.AsyncRunnable() {
			
			@Override
			public void run() {
				killThread.interrupt();
			}
		}, 50).run();
	}
	
}