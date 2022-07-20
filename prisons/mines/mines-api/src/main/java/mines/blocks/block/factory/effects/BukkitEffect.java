package mines.blocks.block.factory.effects;

import mines.blocks.block.factory.interfaces.EffectType;
import mines.blocks.block.factory.interfaces.WorldBlocksEffects;

public class BukkitEffect implements WorldBlocksEffects {

    private EffectType<?> effectType;

    public BukkitEffect(EffectType<?> effectType) {
        this.effectType = effectType;
    }

    @Override
    public EffectType<?> getEffectType() {
        return this.effectType;
    }

    @Override
    public WorldBlocksEffects setEffectType(EffectType<?> type) {
        this.effectType = type;
        return this;
    }
}
