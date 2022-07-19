package mines.packetnew.listeners;

import me.lucko.helper.scheduler.Scheduler;
import me.lucko.shadow.bukkit.BukkitShadowFactory;
import mines.Mines;
import mines.packetnew.events.WorldBlocksBreakEvent;
import mines.packetnew.injector.WorldBlocksInjector;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.level.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
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
        final List<ItemStack> clonedDrops = new ArrayList<>(block.getDrops());
        if (clonedDrops.isEmpty()) {
            return;
        }

        block.getDrops().clear();

        final World nmsWorld = ((CraftWorld) block.getWorld()).getHandle();
        final EntityArmorStand entityArmorStand = new EntityArmorStand(nmsWorld, block.getX() + 0.5, block.getY() + 0.5, block.getZ() + 0.5);
        entityArmorStand.setInvisible(true);
        entityArmorStand.setSlot(EnumItemSlot.f, CraftItemStack.asNMSCopy(clonedDrops.get(0)));

        final PacketPlayOutSpawnEntity packetPlayOutSpawnEntity = new PacketPlayOutSpawnEntity(entityArmorStand);
        final PacketPlayOutEntityMetadata packetPlayOutEntityMetadata = new PacketPlayOutEntityMetadata(entityArmorStand.getId(), entityArmorStand.getDataWatcher(), true);
        WorldBlocksInjector.sendPacket(player, packetPlayOutSpawnEntity);
        WorldBlocksInjector.sendPacket(player, packetPlayOutEntityMetadata);
    }
}
