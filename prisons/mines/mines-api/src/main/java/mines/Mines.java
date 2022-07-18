package mines;

import mines.packets.WorldBlocksBreaking;
import mines.packets.injector.WorldBlocksInjector;
import mines.packets.registry.ProgressRegistry;
import mines.packets.subscription.EventSubscriptions;
import org.bukkit.plugin.java.JavaPlugin;

public class Mines extends JavaPlugin {

    private WorldBlocksInjector injector;
    private WorldBlocksBreaking blockBreaking;
    private EventSubscriptions subscriptions;
    private ProgressRegistry registry;

    @Override
    public void onEnable() {
        this.registry = new ProgressRegistry();
        this.subscriptions = new EventSubscriptions(this);
        this.blockBreaking = new WorldBlocksBreaking(registry);
        this.injector = new WorldBlocksInjector();
    }
}
