package mines.blocks.registry;

import mines.blocks.regions.WorldBlocksMineRegion;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.codemc.worldguardwrapper.WorldGuardWrapper;
import org.codemc.worldguardwrapper.region.IWrappedRegion;

import java.util.concurrent.ConcurrentHashMap;

public class RegionRegistry {
    private final ConcurrentHashMap<String, WorldBlocksMineRegion.Instance> regions;
    public static RegionRegistry instance;

    public RegionRegistry() {
        regions = new ConcurrentHashMap<>();
    }

    public ConcurrentHashMap<String, WorldBlocksMineRegion.Instance> getRegions() {
        return regions;
    }

    public WorldBlocksMineRegion.Instance getInstance(Location location) {
        for (IWrappedRegion region : WorldGuardWrapper.getInstance().getRegions(location)) {
            if (getRegions().containsKey(region.getId())) {
                return getRegions().get(region.getId());
            }
        }

        return null;
    }

    public static RegionRegistry get() {
        if (RegionRegistry.instance == null) {
            RegionRegistry.instance = new RegionRegistry();
        }
        return RegionRegistry.instance;
    }
}
