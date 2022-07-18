package mines.regions;

import me.lucko.helper.serialize.Position;
import me.lucko.helper.serialize.Region;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public record WorldBlocksRegion(String regionName, int customModelData, int maxEnchantLevelArmor, int maxEnchantLevelTools) {

    public Instance createRegionInstance(Location cornerOne, Location cornerTwo) {
        return new Instance(cornerOne, cornerTwo);
    }

    public class Instance {
        private final Region region;
        private final Location cornerOne;
        private final Location cornerTwo;

        public Instance(Location cornerOne, Location cornerTwo) {
            this.cornerOne = cornerOne;
            this.cornerTwo = cornerTwo;
            this.region = Region.of(Position.of(cornerOne), Position.of(cornerTwo));
        }
    }
}
