package com.justixdev.eazynick.nms.netty.legacy;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.nms.netty.InjectorType;
import com.justixdev.eazynick.nms.netty.PacketInjector;
import net.minecraft.util.io.netty.channel.Channel;
import net.minecraft.util.io.netty.channel.ChannelDuplexHandler;
import net.minecraft.util.io.netty.channel.ChannelHandlerContext;
import net.minecraft.util.io.netty.channel.ChannelPromise;

import java.net.InetAddress;

public abstract class LegacyPacketInjector extends PacketInjector {

    protected Channel channel;

    public LegacyPacketInjector(InetAddress address, InjectorType type) {
        super(address, type);
    }

    public abstract void initChannel();

    @Override
    public void inject() {
        if (this.channel.pipeline().get(this.handlerName) != null)
            return;

        // Inject into to netty channel
        ChannelDuplexHandler handler = new ChannelDuplexHandler() {

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                if(!onPacketReceive(msg))
                    return;

                super.channelRead(ctx, msg);
            }

            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                Object newPacket = onPacketSend(msg);

                if(newPacket == null)
                    return;

                super.write(ctx, newPacket, promise);
            }

            @Override
            public void close(ChannelHandlerContext ctx, ChannelPromise future) throws Exception {
                EazyNick.getInstance().getPacketInjectorManager().remove(address);

                super.close(ctx, future);
            }
        };

        if(this.type.equals(InjectorType.INCOMING))
            this.channel.pipeline().addBefore(
                    "packet_handler",
                    this.handlerName,
                    handler
            );
        else
            this.channel.pipeline().addAfter(
                    "packet_handler",
                    this.handlerName,
                    handler
            );
    }

    @Override
    public void unregisterChannel() {
        this.channel.pipeline().remove(this.handlerName);
    }

}
