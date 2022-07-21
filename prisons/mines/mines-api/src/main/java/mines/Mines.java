package mines;

import lombok.Getter;
import me.lucko.helper.Commands;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.lucko.helper.text3.Text;
import mines.blocks.block.BlockHandler;
import mines.blocks.listeners.PlayerJoin;
import mines.blocks.listeners.PlayerQuit;
import mines.blocks.listeners.WorldBlocksBreak;
import mines.blocks.nms.WorldBlocksBreaking;
import mines.blocks.nms.packets.injector.WorldBlocksInjector;
import mines.blocks.nms.packets.subscription.EventSubscriptions;
import mines.blocks.regions.WorldBlocksMineRegion;
import mines.blocks.registry.BlockRegistry;
import mines.blocks.registry.ProgressRegistry;
import mines.blocks.registry.RegenRegistry;
import mines.blocks.registry.RegionRegistry;
import mines.config.ConfigValue;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.plugin.java.JavaPlugin;
import org.codemc.worldguardwrapper.WorldGuardWrapper;

import javax.swing.plaf.synth.Region;
import java.util.function.Predicate;

public class Mines extends ExtendedJavaPlugin {

    /*
    TODO make a better way for the block breaking class to handle block breaking in large quantities.
    TODO make a method for shattering and fracturing blocks.
    TODO clean up some of the code such as listeners, handlers, etc.
    TODO make an ArmorStandHelper class for making the Armor Stand animation in block break. (ignPurple or Zlurpy job)
    TODO finish the animation for ArmorStand (ignPurple job)
    TODO make the regeneration class into an event and just call the event.
    TODO make some commands, maybe add some more details to the regions.
    TODO fix the update for blocks because it doesn't properly update them.
     */

    private WorldBlocksBreaking blockBreaking;
    private WorldBlocksInjector injector;

    @Override
    public void enable() {
        saveDefaultConfig();
        setupPackets();

        BlockHandler blockHandler = new BlockHandler();

        registerEvents();
        blockHandler.init(getConfig());

        registerCommands();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            blockBreaking.progression(this);
        }, 2, 2);

    }

    @Override
    public void disable() {

    }

    public void setupPackets() {
        new EventSubscriptions(this);
        this.injector = new WorldBlocksInjector();
        this.blockBreaking = new WorldBlocksBreaking();
    }

    public void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(injector), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuit(injector), this);
        Bukkit.getPluginManager().registerEvents(new WorldBlocksBreak(), this);
    }

    public void registerCommands() {
        Commands.create()
            .assertPermission("worldblocks.prisons.regions.creation")
            .assertPlayer()
            .assertUsage(ConfigValue.get().getRegionCommandUsageMessage())
            .handler(handler -> {

                if (handler.arg(0).parseOrFail(String.class).equals("register")) {
                    String regionName = handler.arg(1).parseOrFail(String.class);
                    String blockName = handler.arg(2).parseOrFail(String.class);

                    if (!BlockRegistry.get().getBlocks().containsKey(blockName)) {
                        System.out.println(BlockRegistry.get().getBlocks().keySet());
                        handler.sender().sendMessage(ConfigValue.get().getBlockNotFoundMessage());
                        return;
                    }

                    if (!WorldGuardWrapper.getInstance().getRegions(handler.sender().getWorld()).containsKey(regionName)) {
                        handler.sender().sendMessage(ConfigValue.get().getWorldGuardRegionNotFoundMessage());
                        return;
                    }

                    WorldBlocksMineRegion region = new WorldBlocksMineRegion(handler.sender().getWorld(), regionName, blockName);
                    RegionRegistry.get().getRegions().put(regionName, region.newInstance());

                    handler.sender().sendMessage(ConfigValue.get().getRegionRegisteredCommandMessage());
                }
            }).register("wbregion");
    }
}
