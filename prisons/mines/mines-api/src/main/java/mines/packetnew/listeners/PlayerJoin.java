package mines.packetnew.listeners;

import mines.packetnew.injector.WorldBlocksInjector;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectList;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffectType;

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
