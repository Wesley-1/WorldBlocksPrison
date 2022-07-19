package mines.blocks.registry;

import mines.blocks.block.BlockHandler;

import java.util.concurrent.ConcurrentHashMap;

public class BlockRegistry {
    private final ConcurrentHashMap<String, BlockHandler.BlockModel> blocks;

    public BlockRegistry() {
        blocks = new ConcurrentHashMap<>();
    }

    public ConcurrentHashMap<String, BlockHandler.BlockModel> getBlocks() {
        return blocks;
    }
}
