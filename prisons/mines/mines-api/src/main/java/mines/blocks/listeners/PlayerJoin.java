package mines.blocks.listeners;

import mines.blocks.nms.packets.injector.WorldBlocksInjector;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {

    private final WorldBlocksInjector injector;

    public PlayerJoin(WorldBlocksInjector injector) {
        this.injector = injector;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        injector.inject(event.getPlayer());
        System.out.println("Injecting");
    }
}
