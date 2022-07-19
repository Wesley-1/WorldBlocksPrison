package mines.blocks.regions;

import lombok.Getter;
import mines.blocks.registry.RegionRegistry;
import org.bukkit.Location;
import org.codemc.worldguardwrapper.WorldGuardWrapper;
import org.codemc.worldguardwrapper.region.IWrappedRegion;

import java.util.Optional;

public record WorldBlocksMineRegion(String regionIdentifier, String allowedBlock) {

    public Instance newInstance(Location corner1, Location corner2, RegionRegistry registry) {
        return new Instance(this, registry, corner1, corner2, allowedBlock);
    }

    @Getter
    public final class Instance {
        private final WorldBlocksMineRegion region;
        private final RegionRegistry registry;
        private final Location corner1, corner2;
        private final String block;
        private final Optional<IWrappedRegion> wgRegion;

        public Instance(WorldBlocksMineRegion region, RegionRegistry registry, Location corner1, Location corner2, String block) {
            this.region = region;
            this.registry = registry;
            this.corner1 = corner1;
            this.corner2 = corner2;
            this.block = block;
            WorldGuardWrapper worldGuardWrapper = WorldGuardWrapper.getInstance();
            this.wgRegion = worldGuardWrapper.addCuboidRegion(regionIdentifier, corner1, corner2);
        }


        public boolean inRegion(Location location) {
            return wgRegion.map(iWrappedRegion -> iWrappedRegion.contains(location)).orElse(false);
        }
    }
}
