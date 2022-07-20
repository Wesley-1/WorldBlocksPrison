package mines.blocks.listeners;

import com.mojang.datafixers.util.Pair;
import mines.Mines;
import mines.blocks.block.regeneration.BlockRegeneration;
import mines.blocks.events.WorldBlocksBreakEvent;
import mines.blocks.nms.packets.injector.WorldBlocksInjector;
import net.minecraft.network.protocol.game.PacketPlayOutBlockChange;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.level.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorldBlocksBreak implements Listener {

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

        final PacketPlayOutSpawnEntity packetPlayOutSpawnEntity = new PacketPlayOutSpawnEntity(entityArmorStand);
        final PacketPlayOutEntityEquipment packetPlayOutEntityEquipment = new PacketPlayOutEntityEquipment(entityArmorStand.getId(), Arrays.asList(Pair.of(EnumItemSlot.f, CraftItemStack.asNMSCopy(clonedDrops.get(0)))));
        final PacketPlayOutEntityMetadata packetPlayOutEntityMetadata = new PacketPlayOutEntityMetadata(entityArmorStand.getId(), entityArmorStand.getDataWatcher(), true);
        WorldBlocksInjector.sendPacket(player, packetPlayOutSpawnEntity);
        WorldBlocksInjector.sendPacket(player, packetPlayOutEntityEquipment);
        WorldBlocksInjector.sendPacket(player, packetPlayOutEntityMetadata);
    }
}
