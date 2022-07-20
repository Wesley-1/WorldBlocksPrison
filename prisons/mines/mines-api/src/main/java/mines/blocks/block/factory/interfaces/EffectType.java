package mines.blocks.block.factory.interfaces;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public interface EffectType<T> {

    T getType();
    EffectType<T> setType(T type);

    void handleEffectGlobal(World world, Location location);

    void handleEffectPlayer(World world, Player player);

}
