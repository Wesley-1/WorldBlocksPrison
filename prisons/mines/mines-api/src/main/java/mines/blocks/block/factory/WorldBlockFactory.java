package mines.blocks.block.factory;

import mines.blocks.block.factory.interfaces.WorldBlocksBlock;
import mines.blocks.block.factory.interfaces.BlockFactory;

public class WorldBlockFactory implements BlockFactory {

    @Override
    public WorldBlocksBlock newBlock() {
        return new WorldBlock();
    }
}
