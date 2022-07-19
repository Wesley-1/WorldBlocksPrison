package mines.blocks.registry;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.block.Block;
import org.bukkit.Location;

import java.util.concurrent.ConcurrentHashMap;

public class RegenRegistry {
    private final ConcurrentHashMap<Location, org.bukkit.block.Block> regeneratingBlocks;

    public RegenRegistry() {
        this.regeneratingBlocks = new ConcurrentHashMap<>();
    }

    public ConcurrentHashMap<Location, org.bukkit.block.Block> getRegeneratingBlocks() {
        return regeneratingBlocks;
    }
}
