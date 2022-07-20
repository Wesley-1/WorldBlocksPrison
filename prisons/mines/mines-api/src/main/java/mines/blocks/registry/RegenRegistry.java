package mines.blocks.registry;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.block.Block;
import org.bukkit.Location;

import java.util.concurrent.ConcurrentHashMap;

public class RegenRegistry {
    private final ConcurrentHashMap<Location, org.bukkit.block.Block> regeneratingBlocks;
    public static RegenRegistry instance;

    public RegenRegistry() {
        this.regeneratingBlocks = new ConcurrentHashMap<>();
    }

    public ConcurrentHashMap<Location, org.bukkit.block.Block> getRegeneratingBlocks() {
        return regeneratingBlocks;
    }

    public static RegenRegistry get() {
        if (RegenRegistry.instance == null) {
            RegenRegistry.instance = new RegenRegistry();
        }
        return RegenRegistry.instance;
    }
}
