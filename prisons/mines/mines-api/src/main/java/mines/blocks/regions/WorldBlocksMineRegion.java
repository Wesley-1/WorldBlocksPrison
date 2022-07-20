package mines.blocks.regions;

import lombok.Getter;
import mines.blocks.registry.RegionRegistry;
import org.bukkit.Location;
import org.bukkit.World;
import org.codemc.worldguardwrapper.WorldGuardWrapper;
import org.codemc.worldguardwrapper.region.IWrappedRegion;

import java.util.Optional;

public record WorldBlocksMineRegion(World world, String regionIdentifier, String allowedBlock) {

    public Instance newInstance() {
        return new Instance(this, allowedBlock);
    }

    @Getter
    public static final class Instance {
        private final WorldBlocksMineRegion region;
        private final String block;
        private final Optional<IWrappedRegion> wgRegion;

        public Instance(WorldBlocksMineRegion region, String block) {
            this.region = region;
            this.block = block;
            WorldGuardWrapper worldGuardWrapper = WorldGuardWrapper.getInstance();
            this.wgRegion = worldGuardWrapper.getRegion(region.world(), region.regionIdentifier());
            RegionRegistry.get().getRegions().put(region.regionIdentifier(), this);
        }


        public boolean inRegion(Location location) {
            return wgRegion.map(iWrappedRegion -> iWrappedRegion.contains(location)).orElse(false);
        }
    }
}
