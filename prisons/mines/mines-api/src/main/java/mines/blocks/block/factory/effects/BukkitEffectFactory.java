package mines.blocks.block.factory.effects;

import mines.blocks.block.factory.interfaces.EffectFactory;
import mines.blocks.block.factory.interfaces.WorldBlocksEffects;

public class BukkitEffectFactory implements EffectFactory {

    @Override
    public WorldBlocksEffects newEffect() {
        return new BukkitEffect(null);
    }

}
