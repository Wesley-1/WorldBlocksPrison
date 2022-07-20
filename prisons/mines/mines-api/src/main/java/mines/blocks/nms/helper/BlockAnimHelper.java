package mines.blocks.nms.helper;

import io.netty.util.Constant;
import me.lucko.helper.bossbar.BossBarColor;
import me.lucko.helper.bossbar.BossBarStyle;
import me.lucko.helper.bossbar.BukkitBossBarFactory;
import mines.Constants;
import mines.blocks.block.BlockHandler;
import mines.blocks.block.factory.interfaces.WorldBlocksBlock;
import mines.blocks.block.regeneration.BlockRegeneration;
import mines.blocks.events.WorldBlocksBreakEvent;
import mines.blocks.nms.packets.injector.WorldBlocksInjector;
import mines.blocks.registry.ProgressRegistry;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.network.protocol.game.PacketPlayInBlockDig;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockAnimHelper {

    private final JavaPlugin instance;

    public BlockAnimHelper(JavaPlugin instance) {
        this.instance = instance;
    }

    public void fracture(Location loc, int radius) {
        for (Player player: Bukkit.getOnlinePlayers()) {
            for (Block block : getNearbyBlocks(loc, radius)) {
                if (!(block.getType() == Material.NOTE_BLOCK)) return;
                // TODO I need to make it so it does some damage to the blocks around the player, this is an enchant on cosmic
            }
        }
    }

    public void shatter() {
        // TODO I need to make it so I'm able to insta break like 10 - 20 of these blocks.
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

    public void progression() {

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player == null) return;

            try {
                Block block = player.getTargetBlockExact(5);

                if (block == null) return;

                if (!block.getType().equals(Material.NOTE_BLOCK)) return;

                BlockPosition blockPosition = new BlockPosition(block.getX(), block.getY(), block.getZ());

                WorldBlocksBlock worldBlocksBlock = BlockHandler.getBlock(player.getLocation());

                double hardnessMultiplier;
                double regenTime;

                if (worldBlocksBlock == null) {
                    hardnessMultiplier = 1d / (300 / 100d);
                    regenTime = System.currentTimeMillis() + 10 * 1000;
                } else {
                    hardnessMultiplier = 1d / (worldBlocksBlock.getHardnessMultiplier() / 100d);
                    regenTime = System.currentTimeMillis() + worldBlocksBlock.getRegenTime() * 1000;
                }

                if (ProgressRegistry.get().getBlockBreak(blockPosition)) {
                    EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

                    Field currentDigTickField = entityPlayer.d.getClass().getDeclaredField("i");
                    Field lastDigTickField = entityPlayer.d.getClass().getDeclaredField("g");

                    currentDigTickField.setAccessible(true);
                    lastDigTickField.setAccessible(true);

                    int currentDigTick = currentDigTickField.getInt(entityPlayer.d);
                    int lastDigTick = lastDigTickField.getInt(entityPlayer.d);

                    int newDigTick = currentDigTick - lastDigTick;

                    float itemDamage = 0.055555556f;
                    float newDamage = itemDamage * (float) (newDigTick + 1);
                    double doubleProgress = (newDamage * 100f) * hardnessMultiplier;

                    ProgressRegistry.get().getBlockProgress().remove(blockPosition);

                    double oldProgress = 0;

                    if (ProgressRegistry.get().getOldBlockProgress().containsKey(blockPosition)) {
                        oldProgress = ProgressRegistry.get().getOldBlockProgress().get(blockPosition);
                    }

                    if (player.getGameMode() != GameMode.CREATIVE) {
                        ProgressRegistry.get().getBlockProgress().put(blockPosition, doubleProgress + oldProgress);
                    }

                    double blockProgress = ProgressRegistry.get().getBlockProgress().get(blockPosition);
                    double bossBarProgress = blockProgress / 100;

                    if (blockProgress < 100 && blockProgress > -1) {
                        Constants.progressBossBar.title("&a&lBlock Progress: &f&l" + (Math.round(blockProgress * 100) / 100.0) + "%");
                        Constants.progressBossBar.addPlayer(player);
                        Constants.progressBossBar.progress(bossBarProgress);
                        continue;
                    }

                    PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(
                        ProgressRegistry.get().getRandomIntegers().get(blockPosition), blockPosition, 0);

                    Material material = Material.STONE;
                    player.sendBlockChange(block.getLocation(), material.createBlockData());

                    WorldBlocksInjector.sendPacket(player, packet);

                    ProgressRegistry.get().getBlockProgress().remove(blockPosition);
                    ProgressRegistry.get().getOldBlockProgress().remove(blockPosition);

                    new BlockRegeneration(instance, player, block.getLocation(), block, (long) regenTime);
                //TODO fix the update for blocks because it doesn't properly update them.
                    Bukkit.getPluginManager().callEvent(new WorldBlocksBreakEvent(block, player));
                    Constants.progressBossBar.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        updatePacket();
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
}
