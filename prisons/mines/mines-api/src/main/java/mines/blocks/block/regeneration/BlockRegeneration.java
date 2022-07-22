package mines.blocks.block.regeneration;

import api.Instances;
import mines.blocks.nms.packets.injector.WorldBlocksInjector;
import mines.blocks.registry.RegenRegistry;
import net.minecraft.network.protocol.game.PacketPlayOutBlockChange;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class BlockRegeneration {

    private final JavaPlugin instance;
    private final Location blockPosition;
    private final Block block;
    private final long end;
    private final Player player;

    public BlockRegeneration(JavaPlugin instance, Player player, Location blockPosition, Block block, long end) {
        this.instance = instance;
        this.blockPosition = blockPosition;
        this.block = block;
        this.end = end;
        this.player = player;
        makeRegen();
    }

    public void makeRegen() {
        this.block.setType(Material.STONE);
        RegenRegistry.getRegeneratingBlocks().put(this.blockPosition, this.block);

        new BukkitRunnable() {

            @Override
            public void run() {
                if (!RegenRegistry.getRegeneratingBlocks().containsKey(blockPosition)) return;
                if (!(System.currentTimeMillis() >= end)) return;

                player.sendBlockChange(blockPosition, block.getBlockData());
                RegenRegistry.getRegeneratingBlocks().remove(blockPosition);
            }

        }.runTask(this.instance);
    }

    public static void cancelRegenerations() {
        for (Player user : Bukkit.getOnlinePlayers()) {
            for (Map.Entry<Location, Block> blocks : RegenRegistry.getRegeneratingBlocks().entrySet()) {
                Location blockKey = blocks.getKey();
                Block blockVal = blocks.getValue();

                user.sendBlockChange(blockKey, blockVal.getBlockData());
                RegenRegistry.getRegeneratingBlocks().remove(blockKey);
            }
        }
    }
}
