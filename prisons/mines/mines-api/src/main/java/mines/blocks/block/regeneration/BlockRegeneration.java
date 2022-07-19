package mines.blocks.block.regeneration;

import mines.blocks.nms.packets.injector.WorldBlocksInjector;
import mines.blocks.registry.RegenRegistry;
import net.minecraft.network.protocol.game.PacketPlayOutBlockChange;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class BlockRegeneration {

    private final JavaPlugin instance;
    private final Location blockPosition;
    private final Block block;
    private final long start;
    private final long end;
    private final Player player;

    public BlockRegeneration(RegenRegistry registry, JavaPlugin instance, Location blockPosition, Block block, long start, long end, Player player) {
        this.instance = instance;
        this.blockPosition = blockPosition;
        this.block = block;
        this.start = start;
        this.end = end;
        this.player = player;
        makeRegen(registry);
    }

    public void makeRegen(RegenRegistry registry) {
        new BukkitRunnable() {

            @Override
            public void run() {
                if (!registry.getRegeneratingBlocks().containsKey(blockPosition)) return;
                if (!(System.currentTimeMillis() >= end)) return;

                player.sendBlockChange(blockPosition, block.getBlockData());
                registry.getRegeneratingBlocks().remove(blockPosition);
            }

        }.runTask(this.instance);
    }

    public static void cancelAllRegens(RegenRegistry registry) {
        for (Player user : Bukkit.getOnlinePlayers()) {
            for (Map.Entry<Location, Block> blocks : registry.getRegeneratingBlocks().entrySet()) {
                Location blockKey = blocks.getKey();
                Block blockVal = blocks.getValue();

                user.sendBlockChange(blockKey, blockVal.getBlockData());
                registry.getRegeneratingBlocks().remove(blockKey);
            }
        }
    }
}
