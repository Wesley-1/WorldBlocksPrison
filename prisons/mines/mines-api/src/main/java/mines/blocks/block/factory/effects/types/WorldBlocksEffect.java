package mines.blocks.block.factory.effects.types;

import mines.blocks.block.factory.interfaces.EffectType;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class WorldBlocksEffect implements EffectType<Effect> {

    private final int val0;
    private final int val1;
    private Effect type = Effect.ANVIL_BREAK;

    public WorldBlocksEffect(int val0, int val1) {
        this.val0 = val0;
        this.val1 = val1;
    }

    @Override
    public Effect getType() {
        return type;
    }

    @Override
    public EffectType<Effect> setType(Effect type) {
        this.type = type;
        return this;
    }

    @Override
    public void handleEffectGlobal(World world, Location location) {
        if (type == null) return;
        world.playEffect(location, type, this.val0, this.val1);
    }

    @Override
    public void handleEffectPlayer(World world, Player player) {
        if (type == null) return;
        world.playEffect(player.getLocation(), type, this.val0, this.val1);
    }
}
