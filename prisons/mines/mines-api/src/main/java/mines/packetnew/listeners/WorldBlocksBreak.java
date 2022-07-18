package mines.packetnew.listeners;

import me.lucko.helper.scheduler.Scheduler;
import me.lucko.shadow.bukkit.BukkitShadowFactory;
import mines.Mines;
import mines.packetnew.events.WorldBlocksBreakEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class WorldBlocksBreak implements Listener {

    private final Mines main;
    public WorldBlocksBreak(Mines main) {
        this.main = main;
    }

    @EventHandler
    public void onBlockBreak(WorldBlocksBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        block.getDrops().clear();


    }
}
