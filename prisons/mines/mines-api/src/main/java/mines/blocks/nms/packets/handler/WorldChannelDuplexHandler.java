package mines.blocks.nms.packets.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.Getter;
import mines.blocks.nms.packets.packets.PacketEvent;
import mines.blocks.nms.packets.subscription.EventSubscriptions;
import org.bukkit.entity.Player;

public class WorldChannelDuplexHandler extends ChannelDuplexHandler {
    private final Player player;

    @Getter private ChannelHandlerContext ctx = null;
    @Getter private ChannelPromise promise = null;

    public WorldChannelDuplexHandler(Player player) {
        this.player = player;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        PacketEvent<?> event = PacketEvent.get(player, msg);
        if (event == null) {
            super.channelRead(ctx, msg);
            return;
        }
        EventSubscriptions.instance.publish(event);
        if (!event.isCancelled()) {
            super.channelRead(ctx, msg);
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        this.ctx = ctx;
        this.promise = promise;

        PacketEvent<?> event = PacketEvent.get(player, msg);
        if (event == null) {
            super.write(ctx, msg, promise);
            return;
        }
        EventSubscriptions.instance.publish(event);
        if (!event.isCancelled()) {
            super.write(ctx, event.getPacket(), promise);
        }
    }
}
