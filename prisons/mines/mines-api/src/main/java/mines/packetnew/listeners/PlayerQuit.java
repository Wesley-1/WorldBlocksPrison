package mines.packetnew.listeners;

import mines.packetnew.injector.WorldBlocksInjector;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {

    private final WorldBlocksInjector injector;

    public PlayerQuit(WorldBlocksInjector injector) {
        this.injector = injector;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        injector.unInject(event.getPlayer());
        System.out.println("Uninjecting");
    }
}
