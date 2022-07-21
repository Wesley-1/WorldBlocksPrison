package mines.blocks.listeners;

import com.flowpowered.math.vector.Vector3d;
import com.mojang.datafixers.util.Pair;
import me.lucko.helper.Schedulers;
import me.lucko.helper.scheduler.Task;
import mines.Mines;
import mines.blocks.block.BlockHandler;
import mines.blocks.block.factory.interfaces.WorldBlocksBlock;
import mines.blocks.block.regeneration.BlockRegeneration;
import mines.blocks.events.WorldBlocksBreakEvent;
import mines.blocks.nms.helper.BlockAnimHelper;
import mines.blocks.nms.helper.EntityHelper;
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
        final WorldBlocksBlock worldBlocksBlock = BlockHandler.getBlock(block.getLocation());
        System.out.println("1");
        if (worldBlocksBlock == null) {
            return;
        }

        System.out.println("2");
        new BlockEntity(player, nmsWorld, block, worldBlocksBlock.getDrops().get(0));

        new BlockAnimHelper(Mines.getPlugin(Mines.class)).fracture(block.getLocation(), 20);
    }

    private static class BlockEntity extends EntityHelper.EntityWrapper<EntityArmorStand> {

        private static final int MOVEMENT_TICKS = 5;

        private final Player owner;
        private final Block block;
        private final ItemStack item;

        private double difX = 0;
        private double difY = 0;
        private double difZ = 0;

        public BlockEntity(Player owner, World world, Block block, ItemStack item) {
            super(
                new EntityArmorStand(world, block.getX() + 0.5, block.getY() + 0.5, block.getZ() + 0.5),
                "block-entity",
                true,
                1,
                1
            );
            System.out.println("3");
            this.owner = owner;
            this.block = block;
            this.item = item;
        }

        @Override
        public void tick() {
            System.out.println("4");
            if (this.getTicksRan() == 0) {
                System.out.println("5");
                this.spawnEntity(this.owner);

                final PacketPlayOutEntityEquipment packetPlayOutEntityEquipment = new PacketPlayOutEntityEquipment(this.getEntity().getId(), Arrays.asList(Pair.of(EnumItemSlot.f, CraftItemStack.asNMSCopy(this.item))));
                final PacketPlayOutEntityMetadata packetPlayOutEntityMetadata = new PacketPlayOutEntityMetadata(this.getEntity().getId(), this.getEntity().getDataWatcher(), true);
                WorldBlocksInjector.sendPacket(this.owner, packetPlayOutEntityEquipment);
                WorldBlocksInjector.sendPacket(this.owner, packetPlayOutEntityMetadata);
            }

            System.out.println("6");
            short moveX = 0;
            short moveY = 0;
            short moveZ = 0;
            this.getEntity().setYRot(this.getEntity().getYRot() + (float) (Math.PI*3));
            final byte headRotation = (byte) (this.getEntity().getYRot() * 256.0F / 360.0F);
            if (this.getTicksRan() == MOVEMENT_TICKS) {
                final Location playerLocation = this.owner.getLocation();
                this.difX = playerLocation.getX() - (block.getX() + 0.5);
                this.difY = playerLocation.getY() - (block.getY() + 0.5);
                this.difZ = playerLocation.getZ() - (block.getZ() + 0.5);
            }

            if (this.getTicksRan() > MOVEMENT_TICKS) {
                final double prevX = this.getEntity().locX();
                final double prevY = this.getEntity().locY();
                final double prevZ = this.getEntity().locZ();
                final double curX = prevX + this.difX / 3;
                final double curY = prevY + this.difY / 3;
                final double curZ = prevZ + this.difZ / 3;
                this.getEntity().setPosition(curX, curY, curZ);

                moveX = (short) ((curX * 32 - prevX * 32) * 128);
                moveY = (short) ((curY * 32 - prevY * 32) * 128);
                moveZ = (short) ((curZ * 32 - prevZ * 32) * 128);

                if (this.getTicksRan() > 10) {
                    this.cancel();

                    final PacketPlayOutEntityDestroy packetPlayOutEntityDestroy = new PacketPlayOutEntityDestroy(this.getEntity().getId());
                    WorldBlocksInjector.sendPacket(this.owner, packetPlayOutEntityDestroy);
                    return;
                }
            }

            final PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook packetPlayOutRelEntityMoveLook = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(
                this.getEntity().getId(),
                moveX, moveY, moveZ,
                headRotation, (byte) 0,
                true);
            WorldBlocksInjector.sendPacket(this.owner, packetPlayOutRelEntityMoveLook);
        }

        @Override
        protected void initEntity() {
            final EntityArmorStand entityArmorStand = this.getEntity();
            entityArmorStand.setInvisible(true);
            entityArmorStand.setSmall(true);
            entityArmorStand.setInvisible(true);
        }
    }
}
