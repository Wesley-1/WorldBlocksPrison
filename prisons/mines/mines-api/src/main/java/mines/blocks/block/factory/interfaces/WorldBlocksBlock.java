package mines.blocks.block.factory.interfaces;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface WorldBlocksBlock {

    String getName();
    WorldBlocksBlock setName(String name);

    List<WorldBlocksEffects> getEffects();

    WorldBlocksBlock addEffect(WorldBlocksEffects effectFactory);
    WorldBlocksBlock removeEffect(WorldBlocksEffects effectFactory);

    int getModelData();
    WorldBlocksBlock setModelData(int modelData);

    double getRegenTime();
    WorldBlocksBlock setRegenTime(double regenTime);

    double getHardnessMultiplier();
    WorldBlocksBlock setHardnessMultiplier(double hardnessMultiplier);

    List<ItemStack> getDrops();

    WorldBlocksBlock addDrop(ItemStack itemStack);
    WorldBlocksBlock removeDrop(ItemStack itemStack);

}

