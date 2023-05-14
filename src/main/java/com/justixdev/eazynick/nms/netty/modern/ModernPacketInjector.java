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
        if (this.channel.pipeline().get(this.handlerName) != null)
            return;

        // Inject into to netty channel
        ChannelDuplexHandler handler = new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                if(!onPacketReceive(msg)) {
                    //System.out.println("cancelled incoming packet " + msg.getClass().getSimpleName());
                    return;
                }

                switch (msg.getClass().getSimpleName()) {
                    case "PacketPlayInFlying":
                    case "PacketPlayInPosition":
                    case "PacketPlayOutEntityEquipment":
                    case "PacketPlayInSettings":
                    case "PacketPlayInPositionLook":
                    case "PacketPlayInCustomPayload":
                    case "PacketPlayInKeepAlive":
                    case "PacketPlayInEntityAction":
                    case "PacketPlayInLook":
                    case "PacketPlayInAbilities":
                    case "PacketPlayInSetCreativeSlot":
                    case "PacketPlayInArmAnimation":
                    case "PacketPlayInBlockPlace":
                        break;
                    default:
                        //System.out.println("allowed incoming packet " + msg.getClass().getSimpleName());
                        break;
                }

                super.channelRead(ctx, msg);
            }

            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                Object newPacket = onPacketSend(msg);

                if(newPacket == null) {
                    //System.out.println("cancelled outgoing packet " + msg.getClass().getSimpleName());
                    return;
                }

                switch (msg.getClass().getSimpleName()) {
                    case "PacketPlayOutRelEntityMove":
                    case "PacketPlayOutRelEntityMoveLook":
                    case "PacketPlayOutEntityHeadRotation":
                    case "PacketPlayOutEntityVelocity":
                    case "PacketPlayOutKeepAlive":
                    case "PacketPlayOutUpdateTime":
                    case "PacketPlayOutEntityLook":
                    case "PacketPlayOutEntityMetadata":
                    case "PacketPlayOutNamedSoundEffect":
                    case "PacketPlayOutEntityEquipment":
                    case "PacketPlayOutUpdateAttributes":
                    case "PacketPlayOutMapChunkBulk":
                    case "PacketPlayOutTileEntityData":
                    case "PacketPlayOutGameStateChange":
                    case "PacketPlayOutWorldBorder":
                    case "PacketPlayOutSetSlot":
                    case "PacketPlayOutWindowItems":
                    case "PacketPlayOutPosition":
                    case "PacketPlayOutExperience":
                    case "PacketPlayOutUpdateHealth":
                    case "PacketPlayOutMapChunk":
                    case "PacketPlayOutWorldParticles":
                    case "PacketPlayOutBlockChange":
                    case "PacketPlayOutEntityTeleport":
                        break;
                    default:
                        //System.out.println("allowed outgoing packet " + msg.getClass().getSimpleName());
                        break;
                }

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
