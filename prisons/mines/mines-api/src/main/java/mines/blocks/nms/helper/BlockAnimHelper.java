package mines.blocks.nms.helper;

import api.Instances;
import io.netty.util.Constant;
import me.lucko.helper.bossbar.BossBar;
import me.lucko.helper.bossbar.BossBarColor;
import me.lucko.helper.bossbar.BossBarStyle;
import me.lucko.helper.bossbar.BukkitBossBarFactory;
import mines.Constants;
import mines.Mines;
import mines.blocks.block.BlockHandler;
import mines.blocks.block.factory.interfaces.WorldBlocksBlock;
import mines.blocks.block.regeneration.BlockRegeneration;
import mines.blocks.events.WorldBlocksBreakEvent;
import mines.blocks.nms.packets.injector.WorldBlocksInjector;
import mines.blocks.registry.BlockRegistry;
import mines.blocks.registry.ProgressRegistry;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.network.protocol.game.PacketPlayInBlockDig;
import net.minecraft.network.protocol.game.PacketPlayOutBlockBreak;
import net.minecraft.network.protocol.game.PacketPlayOutBlockBreakAnimation;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_17_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.*;

public class BlockAnimHelper {
    public static class BlockAnimation {
        private Field currentDigTickField, lastDigTickField;
        private final EntityPlayer entityPlayer;
        private final Player player;
        private final Block block;
        private WorldBlocksBlock worldBlocksBlock;

        private final float damageDoneToBlock;
        private int currentInt, lastInt;

        public BlockAnimation(Player player, Block block, float damage) {
            this.player = player;
            this.entityPlayer = ((CraftPlayer) player).getHandle();
            this.block = block;
            this.damageDoneToBlock = damage;

            if (BlockHandler.getBlock(block.getLocation()) != null) {
                this.worldBlocksBlock = BlockHandler.getBlock(block.getLocation());
            }


        }

        public void handleAnimation() throws NoSuchFieldException, IllegalAccessException {
            BlockPosition blockPos = ((CraftBlock) this.block).getPosition();

            if (!isCreative(this.player)) {
                ProgressRegistry.get().getBlockProgress().put(blockPos, getDamageDoneToBlock());
            }

            completeBreak(blockPos);

            updatePacket();
        }

        public void updatePacket() {

            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                for (BlockPosition blockPosition : ProgressRegistry.get().getBlockProgress().keySet()) {

                    double progress = ProgressRegistry.get().getBlockProgress().get(blockPosition);
                    int progressInt = (int) progress;

                    if (!ProgressRegistry.get().getRandomIntegers().containsKey(blockPosition)) {
                        Random random = new Random(System.currentTimeMillis());
                        ProgressRegistry.get().getRandomIntegers().put(blockPosition, random.nextInt(1000));
                    }

                    PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(ProgressRegistry.get().getRandomIntegers().get(blockPosition), blockPosition, (progressInt / 10));
                    WorldBlocksInjector.sendPacket(player, packet);
                }
            }
        }

        public double getDamageDoneToBlock() throws NoSuchFieldException, IllegalAccessException {
            BlockPosition blockPos = ((CraftBlock) this.block).getPosition();

            this.currentDigTickField = entityPlayer.d.getClass().getDeclaredField("i");
            this.lastDigTickField = entityPlayer.d.getClass().getDeclaredField("g");

            currentDigTickField.setAccessible(true);
            lastDigTickField.setAccessible(true);

            this.currentInt = currentDigTickField.getInt(entityPlayer.d);
            this.lastInt = lastDigTickField.getInt(entityPlayer.d);

            int newDigTick = currentInt - lastInt;

            float newDamage = damageDoneToBlock * (float) (newDigTick + 1);
            double doubleProgress = (newDamage * 100f) * this.worldBlocksBlock.getHardnessMultiplier();

            ProgressRegistry.get().getBlockProgress().remove(blockPos);

            double oldProgress = 0;

            if (ProgressRegistry.get().getOldBlockProgress().containsKey(blockPos)) {
                oldProgress = ProgressRegistry.get().getOldBlockProgress().get(blockPos);
            }

            return doubleProgress + oldProgress;
        }

        private void completeBreak(BlockPosition blockPos) {
            double blockProgress = ProgressRegistry.get().getBlockProgress().get(blockPos);
            double bossBarProgress = blockProgress / 100;

            updateBars(blockPos, blockProgress, bossBarProgress);
        }

        public void updateBars(BlockPosition blockPos, double blockProgress, double bossBarProgress) {
            if (blockProgress < 100 && blockProgress > -1) {
                Constants.progressBossBar.title("&a&lBlock Progress: &f&l" + (Math.round(blockProgress * 100) / 100) + "%");
                Constants.progressBossBar.addPlayer(this.player);
                Constants.progressBossBar.progress(bossBarProgress);
            } else {
                ProgressRegistry.get().getBlocksBreaking().remove(blockPos);
                ProgressRegistry.get().getBlockProgress().remove(blockPos);
                ProgressRegistry.get().getOldBlockProgress().remove(blockPos);

                new BlockRegeneration(Instances.get(Mines.class), this.player, this.block.getLocation(), this.block, (long) this.worldBlocksBlock.getRegenTime());

                Bukkit.getPluginManager().callEvent(new WorldBlocksBreakEvent(this.block, this.player));

                Constants.progressBossBar.close();
            }
        }

        private boolean isCreative(Player player) {
            return player.getGameMode() == GameMode.CREATIVE;
        }
    }

    public void fractureBlock(Player player, Block block, int radius) throws NoSuchFieldException, IllegalAccessException {
        for (Block nearBlock : getNearbyBlocks(block.getLocation(), radius)) {
            BlockAnimation anim = new BlockAnimation(player, nearBlock, 0.2f);
            anim.handleAnimation();
        }
    }

    public List<Block> getNearbyBlocks(Location location, int radius) {
        List<Block> blocks = new ArrayList<Block>();
        for(int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for(int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
                for(int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                    blocks.add(location.getWorld().getBlockAt(x, y, z));
                }
            }
        }
        return blocks;
}
