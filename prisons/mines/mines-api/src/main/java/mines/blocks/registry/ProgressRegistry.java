package mines.blocks.registry;

import net.minecraft.core.BlockPosition;

import java.util.concurrent.ConcurrentHashMap;

public class ProgressRegistry {

    private final ConcurrentHashMap<BlockPosition, Integer> randomIntegers;
    private final ConcurrentHashMap<BlockPosition, Boolean> blocksBreaking;
    private final ConcurrentHashMap<BlockPosition, Double> blockProgress;
    private final ConcurrentHashMap<BlockPosition, Double> oldBlockProgress;

    public ProgressRegistry() {
        this.randomIntegers = new ConcurrentHashMap<>();
        this.blocksBreaking = new ConcurrentHashMap<>();
        this.blockProgress = new ConcurrentHashMap<>();
        this.oldBlockProgress = new ConcurrentHashMap<>();
    }

    public boolean getBlockBreak(BlockPosition pos) {
        return blocksBreaking.containsKey(pos);
    }

    public void copyOldData(BlockPosition pos) {
        if (!blockProgress.containsKey(pos)) {
            return;
        }

        if (oldBlockProgress.containsKey(pos)) {
            oldBlockProgress.remove(pos);
        }

        oldBlockProgress.put(pos, blockProgress.get(pos));
    }

    public ConcurrentHashMap<BlockPosition, Double> getBlockProgress() {
        return blockProgress;
    }

    public ConcurrentHashMap<BlockPosition, Double> getOldBlockProgress() {
        return oldBlockProgress;
    }

    public ConcurrentHashMap<BlockPosition, Boolean> getBlocksBreaking() {
        return blocksBreaking;
    }

    public ConcurrentHashMap<BlockPosition, Integer> getRandomIntegers() {
        return randomIntegers;
    }

}
