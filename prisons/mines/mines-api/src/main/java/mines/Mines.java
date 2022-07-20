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
import org.bukkit.plugin.java.JavaPlugin;

import javax.swing.plaf.synth.Region;

public class Mines extends JavaPlugin {

    private WorldBlocksInjector injector;
    @Getter private WorldBlocksBreaking blockBreaking;
    private EventSubscriptions subscriptions;
    private BlockHandler blockHandler;


    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.subscriptions = new EventSubscriptions(this);
        this.blockBreaking = new WorldBlocksBreaking();
        this.injector = new WorldBlocksInjector();
        this.blockHandler = new BlockHandler();

        this.blockHandler.init(getConfig());
        System.out.println("Starting register events!");
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(injector), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuit(injector), this);
        Bukkit.getPluginManager().registerEvents(new WorldBlocksBreak(this), this);
        System.out.println("Finished Registering!");

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            blockBreaking.progression(this);
        }, 2, 2);
    }

    @Override
    public void onDisable() {

    }
}
