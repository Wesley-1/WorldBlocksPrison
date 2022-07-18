package mines.packets.injector;

import mines.packets.handlers.WorldBlocksPacketHandler;
import mines.packets.subscription.EventSubscription;
import mines.packets.subscription.EventSubscriptions;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class WorldBlocksInjector {

    private final String CHANNEL = "worldblocks_packet_listener";


    public WorldBlocksInjector() {
        EventSubscriptions.instance.subscribe(this, getClass());
    }

    @EventSubscription
    private void onJoin(PlayerJoinEvent event) {
        inject(event.getPlayer());
    }

    @EventSubscription
    private void onLeave(PlayerQuitEvent event) {
        unInject(event.getPlayer());
    }

    private void inject(Player player) {
        //Inject a packet listener into the player's connection.
        CraftPlayer craftPlayer = (CraftPlayer) player;
        PlayerConnection connection = craftPlayer.getHandle().b;
        connection.a.k.eventLoop().submit(() -> {
            if (connection.a.k.pipeline().get(CHANNEL) != null) {
                //Remove it.
                connection.a.k.pipeline().remove(CHANNEL);

            }
            //Add it.
            connection.a.k.pipeline().addBefore("packet_handler", CHANNEL, new WorldBlocksPacketHandler(player));
        });
    }

    private void unInject(Player player) {
        //Remove the packet listener from the player's connection.
        CraftPlayer craftPlayer = (CraftPlayer) player;
        PlayerConnection connection = craftPlayer.getHandle().b;
        connection.a.k.eventLoop().submit(() -> {
            if (connection.a.k.pipeline().get(CHANNEL) != null) {
                //Remove it.
                connection.a.k.pipeline().remove(CHANNEL);
            }
        });
    }
}

