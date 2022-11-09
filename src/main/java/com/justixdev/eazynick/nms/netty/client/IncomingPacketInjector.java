package com.justixdev.eazynick.nms.netty.client;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.nms.ReflectionHelper;
import com.justixdev.eazynick.nms.fakegui.sign.SignGUI;
import com.justixdev.eazynick.nms.fakegui.sign.SignGUI.EditCompleteEvent;
import com.justixdev.eazynick.utilities.AsyncTask;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class IncomingPacketInjector {

    private final EazyNick eazyNick;

    private Channel channel;
    private String handlerName;

    public IncomingPacketInjector(Player player) {
        this.eazyNick = EazyNick.getInstance();

        ReflectionHelper reflectionHelper = eazyNick.getReflectionHelper();
        SignGUI signGUI = eazyNick.getSignGUI();

        try {
            String version = eazyNick.getVersion();
            boolean is1_17 = version.startsWith("1_17"), is1_18 = version.startsWith("1_18"), is1_19 = version.startsWith("1_19");
            Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = entityPlayer.getClass().getField(
                    (is1_17 || is1_18 || is1_19)
                            ? "b"
                            : "playerConnection"
            ).get(entityPlayer);
            Object networkManager = playerConnection.getClass().getField(
                    is1_19
                            ? "b"
                            : (is1_17 || is1_18)
                            ? "a"
                            : "networkManager"
            ).get(playerConnection);

            //Get netty channel
            this.channel = (Channel) networkManager.getClass().getField(
                    (is1_17 || is1_18 || is1_19)
                            ? (
                            (version.equals("1_18_R2") || is1_19)
                                    ? "m"
                                    : "k"
                    )
                            : "channel"
            ).get(networkManager);
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
                                if (packet.getClass().getSimpleName().equals("PacketPlayInUpdateSign")) {
                                    if (signGUI.getEditCompleteListeners().containsKey(player)) {
                                        //Process SignGUI success
                                        Object[] rawLines = (Object[]) reflectionHelper.getField(
                                                packet.getClass(),
                                                (is1_17 || is1_18 || is1_19)
                                                        ? "c"
                                                        : "b"
                                        ).get(packet);

                                        Bukkit.getScheduler().runTask(eazyNick, () -> {
                                            try {
                                                String[] lines = new String[4];

                                                if (version.startsWith("1_8")) {
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
                                } else if (packet.getClass().getName().endsWith("PacketPlayInTabComplete")) {
                                    //Cache input text
                                    eazyNick.getUtils().getTextsToComplete().put(
                                            player,
                                            (String) reflectionHelper.getField(
                                                    packet.getClass(),
                                                    eazyNick.getUtils().isVersion13OrLater()
                                                            ? "b"
                                                            : "a"
                                            ).get(packet)
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