package mines.blocks.block.factory;

import mines.blocks.block.factory.interfaces.WorldBlocksBlock;
import mines.blocks.block.factory.interfaces.WorldBlocksEffects;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorldBlock implements WorldBlocksBlock {

    private String name = "";
    private final List<WorldBlocksEffects> effectsList = new ArrayList<>();
    private final List<ItemStack> itemsList = new ArrayList<>();
    private int modelData = 0;
    private double regenTime = 0.0;
    private double hardnessMultiplier = 0.0;

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public WorldBlocksBlock setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public List<WorldBlocksEffects> getEffects() {
        return this.effectsList;
    }

    @Override
    public WorldBlocksBlock addEffect(WorldBlocksEffects effects) {
        this.effectsList.add(effects);
        return this;
    }

    @Override
    public WorldBlocksBlock removeEffect(WorldBlocksEffects effects) {
        this.effectsList.remove(effects);
        return this;
    }

    @Override
    public int getModelData() {
        return this.modelData;
    }

    @Override
    public WorldBlocksBlock setModelData(int modelData) {
        this.modelData = modelData;
        return this;
    }

    @Override
    public double getRegenTime() {
        return this.regenTime;
    }

    @Override
    public WorldBlocksBlock setRegenTime(double regenTime) {
        this.regenTime = regenTime;
        return this;
    }

    @Override
    public double getHardnessMultiplier() {
        return this.hardnessMultiplier;
    }

    @Override
    public WorldBlocksBlock setHardnessMultiplier(double hardnessMultiplier) {
        this.hardnessMultiplier = hardnessMultiplier;
        return this;
    }

    @Override
    public List<ItemStack> getDrops() {
        return this.itemsList;
    }

    @Override
    public WorldBlocksBlock addDrop(ItemStack itemStack) {
        this.itemsList.add(itemStack);
        return this;

    }


    @Override
    public WorldBlocksBlock removeDrop(ItemStack itemStack) {
        this.itemsList.remove(itemStack);
        return this;
    }
}
