package mines.blocks.nms.packets.injector;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.DefaultChannelPromise;
import mines.blocks.nms.packets.handler.WorldChannelDuplexHandler;
import mines.blocks.nms.packets.subscription.EventSubscriptions;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.concurrent.ConcurrentHashMap;

public class WorldBlocksInjector {

    public static ConcurrentHashMap<Player, WorldChannelDuplexHandler> playerHandler;

    static {
        playerHandler = new ConcurrentHashMap<>();
    }

    public WorldBlocksInjector() {
        EventSubscriptions.instance.subscribe(this, getClass());
    }

    public WorldChannelDuplexHandler inject(Player player) {
        System.out.println("Starting Injection");
        if (!playerHandler.containsKey(player)) {
            System.out.println("PlayerHandler in injection");
            WorldChannelDuplexHandler handler = new WorldChannelDuplexHandler(player);
            System.out.println("Duplex handler created");
            startListen(player, handler);
            System.out.println("Starting to listen");
            playerHandler.put(player, handler);
            System.out.println("Putting into handler!");
            return handler;
        } else {
            System.out.println("Were in else for some reason");
            return playerHandler.get(player);
        }
    }

    public void unInject(Player player) {
        if (playerHandler.containsKey(player)) {
            System.out.println("Uninjecting");
            stopListen(player);
            System.out.println("Stopped listening");
            playerHandler.remove(player);
            System.out.println("Removed");
        }

    }

    public static void sendPacket(Player player, Object packet) {
        WorldChannelDuplexHandler handler = playerHandler.get(player);
        if (handler == null) {
            return;
        }

        try {
            handler.write(handler.getCtx(), packet, new DefaultChannelPromise(handler.getPromise().channel()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startListen(Player player, WorldChannelDuplexHandler duplexHandler) {
        ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().b.a.k.pipeline();
        pipeline.addBefore("packet_handler", player.getName(), duplexHandler);
    }

    private void stopListen(Player player) {
        Channel channel = ((CraftPlayer) player).getHandle().b.a.k;

        if (channel == null) return;

        channel.eventLoop().submit(() -> {
            channel.pipeline().remove(player.getName());
            System.out.println("Just removed");
        });
    }

}
