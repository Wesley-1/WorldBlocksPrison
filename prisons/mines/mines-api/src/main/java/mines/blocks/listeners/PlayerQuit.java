package mines.blocks.listeners;

import mines.blocks.nms.packets.injector.WorldBlocksInjector;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;

public class PlayerQuit implements Listener {

    private final WorldBlocksInjector injector;

    public PlayerQuit(WorldBlocksInjector injector) {
        this.injector = injector;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.getPlayer().removePotionEffect(PotionEffectType.SLOW_DIGGING);
        injector.unInject(event.getPlayer());
        System.out.println("Uninjecting");
    }
}
