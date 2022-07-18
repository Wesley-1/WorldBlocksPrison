package mines.packets;

import mines.packets.packets.PacketPlayInBlockDigImpl;
import mines.packets.packets.PacketPlayOutBlockBreakAnimationImpl;
import mines.packets.packets.PacketPlayOutBlockBreakImpl;
import mines.packets.registry.ProgressRegistry;
import mines.packets.subscription.EventSubscription;
import mines.packets.subscription.EventSubscriptions;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.PacketPlayInBlockDig;
import net.minecraft.network.protocol.game.PacketPlayOutBlockBreak;
import net.minecraft.network.protocol.game.PacketPlayOutBlockBreakAnimation;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Random;

public class WorldBlocksBreaking {

    private static Field blockPosition, enumPlayerDigTypeInDig, inBool, currentTick, lastDigTick, dura, prog;
    private final ProgressRegistry registry;

    static {
        try {
            blockPosition = PacketPlayInBlockDig.class.getDeclaredField("a");
            enumPlayerDigTypeInDig = PacketPlayInBlockDig.class.getDeclaredField("c");

            blockPosition = PacketPlayOutBlockBreak.class.getDeclaredField("b");
            inBool = PacketPlayOutBlockBreak.class.getDeclaredField("e");

            dura = PacketPlayOutBlockBreakAnimation.class.getDeclaredField("a");
            prog = PacketPlayOutBlockBreakAnimation.class.getDeclaredField("c");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public WorldBlocksBreaking(ProgressRegistry registry) {
        EventSubscriptions.instance.subscribe(this, getClass());
        this.registry = registry;
    }

    @EventSubscription
    private void onPacketPlayInDig(PacketPlayInBlockDigImpl event) throws IllegalAccessException {
        PacketPlayInBlockDig packet = event.getPacket();
        BlockPosition blockPos = (BlockPosition) blockPosition.get(packet);

        if (registry.getBlocksBreaking().containsKey(blockPos)) {
            registry.getBlocksBreaking().remove(blockPos);
        }

        if (enumPlayerDigTypeInDig.get(packet) == PacketPlayInBlockDig.EnumPlayerDigType.a) {
            registry.getBlocksBreaking().put(blockPos, true);

        } else if (enumPlayerDigTypeInDig.get(packet) == PacketPlayInBlockDig.EnumPlayerDigType.c || enumPlayerDigTypeInDig.get(packet) == PacketPlayInBlockDig.EnumPlayerDigType.b) {
            registry.getBlocksBreaking().put(blockPos, false);
            registry.copyOldData(blockPos);
        }

        // if isCustomBlock() && enum == c then return
    }

    @EventSubscription
    private void onPacketBlockBreak(PacketPlayOutBlockBreakImpl event) throws IllegalAccessException {
        PacketPlayOutBlockBreak packet = event.getPacket();
        BlockPosition blockPos = (BlockPosition) blockPosition.get(packet);

        /*
        TODO customBlockHandling
        if (isCustomBlock(blockPos)) {
            inBool.set(packet, true);
        }

         */

        inBool.set(packet, true);
    }

    @EventSubscription
    private void onPacketBlockBreakAnimation(PacketPlayOutBlockBreakAnimationImpl event) throws IllegalAccessException, NoSuchFieldException {
        PacketPlayOutBlockBreakAnimation packet = event.getPacket();

        BlockPosition blockPos = (BlockPosition) blockPosition.get(packet);
        Player player = event.getPlayer();
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        World world = entityPlayer.getWorld();

        double hardness = 300;
        double hardnessMulti = 1 / (hardness / 100);

        if (registry.getBlockBreak(blockPos)) {
            IBlockData blockData = world.getType(blockPos);

            int currentInt, lastInt;
            double oldPosValue;

            currentTick = entityPlayer.d.getClass().getDeclaredField("currentTick");
            lastDigTick = entityPlayer.d.getClass().getDeclaredField("lastDigTick");

            currentInt = lastDigTick.getInt(entityPlayer.d);
            lastInt = currentTick.getInt(entityPlayer.d);

            int k = currentInt - lastInt;

            float blockDamage = blockData.getDamage(entityPlayer, world, blockPos) * (k + 1);
            double progress = (blockDamage * 100) * hardnessMulti;

            if (registry.getBlockProgress().containsKey(blockPos)) {
                registry.getBlockProgress().remove(blockPos);
            }

            if (player.getGameMode() != GameMode.CREATIVE && registry.getOldBlockProgress().containsKey(blockPos)) {
                oldPosValue = registry.getOldBlockProgress().get(blockPos);
                registry.getBlockProgress().put(blockPos, progress + oldPosValue);
            }
        }

        if (!registry.getRandomIntegers().containsKey(blockPos)) {
            Random random = new Random(System.currentTimeMillis());
            registry.getRandomIntegers().put(blockPos, random.nextInt(1000));
        }

        dura.set(packet, registry.getRandomIntegers().get(blockPos));
        blockPosition.set(packet, blockPos);
        prog.set(packet, registry.getBlockProgress().get(blockPos) / 10);
    }

    public boolean isCustomBlock(BlockPosition pos) {
        return true;
    }
}
