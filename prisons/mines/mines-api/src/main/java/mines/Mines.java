package mines;

import lombok.Getter;
import mines.packetnew.WorldBlocksBreaking;
import mines.packetnew.injector.WorldBlocksInjector;
import mines.packetnew.listeners.PlayerJoin;
import mines.packetnew.listeners.PlayerQuit;
import mines.packetnew.listeners.WorldBlocksBreak;
import mines.packetnew.registry.ProgressRegistry;
import mines.packetnew.subscription.EventSubscriptions;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Mines extends JavaPlugin {

    private WorldBlocksInjector injector;
    @Getter private WorldBlocksBreaking blockBreaking;
    private EventSubscriptions subscriptions;
    private ProgressRegistry registry;

    @Override
    public void onEnable() {
        this.registry = new ProgressRegistry();
        this.subscriptions = new EventSubscriptions(this);
        this.blockBreaking = new WorldBlocksBreaking(registry);
        this.injector = new WorldBlocksInjector();

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
