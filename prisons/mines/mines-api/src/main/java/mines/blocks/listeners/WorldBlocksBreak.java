package mines.blocks.listeners;

import com.flowpowered.math.vector.Vector3d;
import com.mojang.datafixers.util.Pair;
import me.lucko.helper.Schedulers;
import me.lucko.helper.scheduler.Task;
import mines.Mines;
import mines.blocks.block.regeneration.BlockRegeneration;
import mines.blocks.events.WorldBlocksBreakEvent;
import mines.blocks.nms.helper.BlockAnimHelper;
import mines.blocks.nms.packets.injector.WorldBlocksInjector;
import net.minecraft.network.protocol.game.*;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
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
import java.util.function.Consumer;

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
        entityArmorStand.setSmall(true);
        entityArmorStand.setInvisible(true);

        final PacketPlayOutSpawnEntity packetPlayOutSpawnEntity = new PacketPlayOutSpawnEntity(entityArmorStand);
        final PacketPlayOutEntityEquipment packetPlayOutEntityEquipment = new PacketPlayOutEntityEquipment(entityArmorStand.getId(), Arrays.asList(Pair.of(EnumItemSlot.f, CraftItemStack.asNMSCopy(clonedDrops.get(0)))));
        final PacketPlayOutEntityMetadata packetPlayOutEntityMetadata = new PacketPlayOutEntityMetadata(entityArmorStand.getId(), entityArmorStand.getDataWatcher(), true);

        WorldBlocksInjector.sendPacket(player, packetPlayOutSpawnEntity);
        WorldBlocksInjector.sendPacket(player, packetPlayOutEntityEquipment);
        WorldBlocksInjector.sendPacket(player, packetPlayOutEntityMetadata);

        final Location playerLocation = player.getLocation();
        Schedulers.async().runRepeating(new Consumer<>() {
            private int ticks = 0;
            private double difX = 0;
            private double difY = 0;
            private double difZ = 0;

            private static final int MOVEMENT_TICKS = 5;

            @Override
            public void accept(Task task) {
                short moveX = 0;
                short moveY = 0;
                short moveZ = 0;
                entityArmorStand.setYRot(entityArmorStand.getYRot() + (float) (Math.PI*3));
                final byte headRotation = (byte) (entityArmorStand.getYRot() * 256.0F / 360.0F);
                if (this.ticks == MOVEMENT_TICKS) {
                    this.difX = playerLocation.getX() - (block.getX() + 0.5);
                    this.difY = playerLocation.getY() - (block.getY() + 0.5);
                    this.difZ = playerLocation.getZ() - (block.getZ() + 0.5);
                }

                if (this.ticks > MOVEMENT_TICKS) {
                    final double prevX = entityArmorStand.locX();
                    final double prevY = entityArmorStand.locY();
                    final double prevZ = entityArmorStand.locZ();
                    final double curX = prevX + this.difX / 3;
                    final double curY = prevY + this.difY / 3;
                    final double curZ = prevZ + this.difZ / 3;
                    entityArmorStand.setPosition(curX, curY, curZ);

                    moveX = (short) ((curX * 32 - prevX * 32) * 128);
                    moveY = (short) ((curY * 32 - prevY * 32) * 128);
                    moveZ = (short) ((curZ * 32 - prevZ * 32) * 128);

                    if (this.ticks > 10) {
                        task.close();

                        final PacketPlayOutEntityDestroy packetPlayOutEntityDestroy = new PacketPlayOutEntityDestroy(entityArmorStand.getId());
                        WorldBlocksInjector.sendPacket(player, packetPlayOutEntityDestroy);
                        return;
                    }
                }

                final PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook packetPlayOutRelEntityMoveLook = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(
                    entityArmorStand.getId(),
                    moveX, moveY, moveZ,
                    headRotation, (byte) 0,
                    true);
                WorldBlocksInjector.sendPacket(player, packetPlayOutRelEntityMoveLook);
                this.ticks++;
            }
        }, 1, 1);

        new BlockAnimHelper(Mines.getPlugin(Mines.class)).fracture(block.getLocation(), 20);
    }
}
