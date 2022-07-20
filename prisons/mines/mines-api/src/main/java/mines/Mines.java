package mines;

import lombok.Getter;
import mines.blocks.block.BlockHandler;
import mines.blocks.listeners.PlayerJoin;
import mines.blocks.listeners.PlayerQuit;
import mines.blocks.listeners.WorldBlocksBreak;
import mines.blocks.nms.WorldBlocksBreaking;
import mines.blocks.nms.packets.injector.WorldBlocksInjector;
import mines.blocks.nms.packets.subscription.EventSubscriptions;
import mines.blocks.registry.BlockRegistry;
import mines.blocks.registry.ProgressRegistry;
import mines.blocks.registry.RegenRegistry;
import mines.blocks.registry.RegionRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.plugin.java.JavaPlugin;

import javax.swing.plaf.synth.Region;

public class Mines extends JavaPlugin {

    /*
    TODO make a better way for the block breaking class to handle block breaking in large quantities.
    TODO make a method for shattering and fracturing blocks.
    TODO clean up some of the code such as listeners, handlers, etc.
    TODO make an ArmorStandHelper class for making the Armor Stand animation in block break. (ignPurple or Zlurpy job)
    TODO finish the animation for ArmorStand (ignPurple job)
    TODO make the regeneration class into an event and just call the event.
    TODO make some commands, maybe add some more details to the regions.
     */

    private WorldBlocksBreaking blockBreaking;
    private WorldBlocksInjector injector;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        setupPackets();

        BlockHandler blockHandler = new BlockHandler();

        registerEvents();

        blockHandler.init(getConfig());

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            blockBreaking.progression(this);
        }, 2, 2);
    }

    @Override
    public void onDisable() {

    }

    public void setupPackets() {
        new EventSubscriptions(this);
        this.injector = new WorldBlocksInjector();
        this.blockBreaking = new WorldBlocksBreaking();
    }

    public void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(injector), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuit(injector), this);
        Bukkit.getPluginManager().registerEvents(new WorldBlocksBreak(this), this);
    }

    public void registerCommands() {
        /*
        TODO region command
         */
    }
}
