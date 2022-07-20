package mines.blocks.registry;

import mines.blocks.block.factory.interfaces.WorldBlocksBlock;

import java.util.concurrent.ConcurrentHashMap;

public class BlockRegistry {

    public static BlockRegistry instance;

    private final ConcurrentHashMap<String, WorldBlocksBlock> blocks;

    public BlockRegistry() {
        blocks = new ConcurrentHashMap<>();
    }

    public ConcurrentHashMap<String, WorldBlocksBlock> getBlocks() {
        return blocks;
    }

    public static BlockRegistry get() {
        if (BlockRegistry.instance == null) {
            BlockRegistry.instance = new BlockRegistry();
        }
        return BlockRegistry.instance;
    }
}
