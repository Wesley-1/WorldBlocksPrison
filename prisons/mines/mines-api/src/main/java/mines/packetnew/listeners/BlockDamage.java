package mines.packetnew.listeners;

import mines.packetnew.injector.WorldBlocksInjector;
import mines.packetnew.registry.ProgressRegistry;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.PacketPlayOutBlockBreakAnimation;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Objects;

public class BlockDamage implements Listener {
    private final ProgressRegistry registry;

    public BlockDamage(ProgressRegistry registry) {
        this.registry = registry;
    }

    @EventHandler
    public void onBlockDamage(BlockBreakEvent event) {
        event.setCancelled(true);
    }
}
