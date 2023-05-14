package com.justixdev.eazynick.nms.netty.modern;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.nms.netty.InjectorType;
import com.justixdev.eazynick.nms.netty.PacketInjector;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.net.InetAddress;

public abstract class ModernPacketInjector extends PacketInjector {

    protected Channel channel;

    public ModernPacketInjector(InetAddress address, InjectorType type) {
        super(address, type);
    }

    public abstract void initChannel();

    @Override
    public void inject() {
        if (this.channel.pipeline().get(this.handlerName) != null) {
            System.err.println("Skipping " + this.type.name() + " handler for " + this.address.getHostAddress() + "!");
            return;
        }

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
            public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
                EazyNick.getInstance().getPacketInjectorManager().remove(address);

                super.close(ctx, promise);
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
