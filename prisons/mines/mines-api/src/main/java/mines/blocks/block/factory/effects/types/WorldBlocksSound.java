package mines.blocks.block.factory.effects.types;

import mines.blocks.block.factory.interfaces.EffectType;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class WorldBlocksSound implements EffectType<Sound> {

    private final float val0;
    private final float val1;
    private Sound type = Sound.BLOCK_AMETHYST_BLOCK_BREAK;

    public WorldBlocksSound(float val0, float val1) {
        this.val0 = val0;
        this.val1 = val1;
    }

    @Override
    public Sound getType() {
        return type;
    }

    @Override
    public EffectType<Sound> setType(Sound type) {
        this.type = type;
        return this;
    }

    @Override
    public void handleEffectGlobal(World world, Location location) {
        if (type == null) return;
        world.playSound(location, type, this.val0, this.val1);
    }

    @Override
    public void handleEffectPlayer(World world, Player player) {
        if (type == null) return;
        world.playSound(player.getLocation(), type, this.val0, this.val1);
    }
}
