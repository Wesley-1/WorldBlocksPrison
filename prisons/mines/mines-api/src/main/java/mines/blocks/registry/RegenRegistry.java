package mines.blocks.registry;

import api.Instances;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.block.Block;
import org.bukkit.Location;

import java.util.concurrent.ConcurrentHashMap;

public class RegenRegistry {
    private final ConcurrentHashMap<Location, org.bukkit.block.Block> regeneratingBlocks;

    public RegenRegistry() {
        this.regeneratingBlocks = new ConcurrentHashMap<>();
    }

    public static ConcurrentHashMap<Location, org.bukkit.block.Block> getRegeneratingBlocks() {
        return Instances.get(RegenRegistry.class).regeneratingBlocks;
    }
}
