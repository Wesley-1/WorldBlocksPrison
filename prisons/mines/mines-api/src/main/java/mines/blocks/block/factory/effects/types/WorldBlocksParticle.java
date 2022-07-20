package mines.blocks.block.factory.effects.types;

import mines.blocks.block.factory.interfaces.EffectType;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class WorldBlocksParticle implements EffectType<Particle> {

    private final int val0;
    private Particle type = Particle.ASH;

    public WorldBlocksParticle(int val0) {
        this.val0 = val0;
    }

    @Override
    public Particle getType() {
        return type;
    }

    @Override
    public EffectType<Particle> setType(Particle type) {
        this.type = type;
        return this;
    }

    @Override
    public void handleEffectGlobal(World world, Location location) {
        world.spawnParticle(type, location, this.val0);
    }

    @Override
    public void handleEffectPlayer(World world, Player player) {
        world.spawnParticle(type, player.getLocation(), this.val0);
    }
}
