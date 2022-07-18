package mines.packets.handlers;

import mines.packets.registry.ProgressRegistry;
import net.minecraft.core.BlockPosition;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class ProgressHandler extends BukkitRunnable {

    private final ProgressRegistry registry;

    public ProgressHandler(ProgressRegistry registry) {
        this.registry = registry;
    }
    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (BlockPosition position : registry.getBlockProgress().keySet()) {
                double progressDouble = registry.getBlockProgress().get(position);
                int progress = (int) progressDouble;


            }
        }
    }

}
