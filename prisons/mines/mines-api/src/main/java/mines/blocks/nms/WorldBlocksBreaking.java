package mines.blocks.nms;

import me.lucko.helper.bossbar.BossBar;
import me.lucko.helper.bossbar.BossBarColor;
import me.lucko.helper.bossbar.BossBarStyle;
import me.lucko.helper.bossbar.BukkitBossBarFactory;
import mines.blocks.block.BlockHandler;
import mines.blocks.block.regeneration.BlockRegeneration;
import mines.blocks.events.WorldBlocksBreakEvent;
import mines.blocks.nms.packets.injector.WorldBlocksInjector;
import mines.blocks.nms.packets.packets.type.PacketPlayInBlockDigImpl;
import mines.blocks.nms.packets.packets.type.PacketPlayOutBlockBreakImpl;
import mines.blocks.nms.packets.subscription.EventSubscription;
import mines.blocks.nms.packets.subscription.EventSubscriptions;
import mines.blocks.registry.BlockRegistry;
import mines.blocks.registry.ProgressRegistry;
import mines.blocks.registry.RegenRegistry;
import mines.blocks.registry.RegionRegistry;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectList;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.BlockLadder;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.properties.IBlockState;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.util.Random;

public class WorldBlocksBreaking {

    private static Field blockPositionDigIn;
    private static Field enumPlayerDigTypeInDig;
    private static Field blockPositionBlockBreak;
    private static Field inBool;
    private final ProgressRegistry progressRegistry;
    private final RegenRegistry regenRegistry;
    private final RegionRegistry regionRegistry;
    private final BlockRegistry blockRegistry;

    static {
        try {
            blockPositionDigIn = PacketPlayInBlockDig.class.getDeclaredField("a");
            enumPlayerDigTypeInDig = PacketPlayInBlockDig.class.getDeclaredField("c");
            inBool = PacketPlayOutBlockBreak.class.getDeclaredField("e");
            blockPositionBlockBreak = PacketPlayOutBlockBreak.class.getDeclaredField("b");

            blockPositionDigIn.setAccessible(true);
            enumPlayerDigTypeInDig.setAccessible(true);
            inBool.setAccessible(true);
            blockPositionBlockBreak.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public WorldBlocksBreaking(ProgressRegistry registry, RegenRegistry regenRegistry, RegionRegistry regionRegistry, BlockRegistry blockRegistry) {
        this.regenRegistry = regenRegistry;
        this.regionRegistry = regionRegistry;
        this.blockRegistry = blockRegistry;
        EventSubscriptions.instance.subscribe(this, getClass());
        this.progressRegistry = registry;
    }

    @EventSubscription
    private void onPacketPlayInDig(PacketPlayInBlockDigImpl event) throws IllegalAccessException {
        PacketPlayInBlockDig packet = event.getPacket();
        BlockPosition blockPos = (BlockPosition) blockPositionDigIn.get(packet);

        PacketPlayOutEntityEffect entityEffect = new PacketPlayOutEntityEffect(event.getPlayer().getEntityId(), new MobEffect(MobEffectList.fromId(PotionEffectType.SLOW_DIGGING.getId()), Integer.MAX_VALUE, 255, true, false));
        WorldBlocksInjector.sendPacket(event.getPlayer(), entityEffect);

        progressRegistry.getBlocksBreaking().remove(blockPos);

        if (enumPlayerDigTypeInDig.get(packet) == PacketPlayInBlockDig.EnumPlayerDigType.a) {
            System.out.println(enumPlayerDigTypeInDig.get(packet));
            progressRegistry.getBlocksBreaking().put(blockPos, true);

        } else if (enumPlayerDigTypeInDig.get(packet) == PacketPlayInBlockDig.EnumPlayerDigType.c || enumPlayerDigTypeInDig.get(packet) == PacketPlayInBlockDig.EnumPlayerDigType.b) {
            progressRegistry.getBlocksBreaking().remove(blockPos);
            progressRegistry.copyOldData(blockPos);
        }

        // if isCustomBlock() && enum == c then return
    }

    @EventSubscription
    private void onPacketBlockBreak(PacketPlayOutBlockBreakImpl event) throws IllegalAccessException {
        PacketPlayOutBlockBreak packet = event.getPacket();
        BlockPosition blockPos = (BlockPosition) blockPositionBlockBreak.get(packet);

        /*
        TODO customBlockHandling
        if (isCustomBlock(blockPos)) {
            inBool.set(packet, true);
        }

         */


        inBool.set(packet, true);
    }

    public void updatePacket() {

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            for (BlockPosition blockPosition : progressRegistry.getBlockProgress().keySet()) {

                double progress = progressRegistry.getBlockProgress().get(blockPosition);
                int progressInt = (int) progress;

                if (!progressRegistry.getRandomIntegers().containsKey(blockPosition)) {
                    Random random = new Random(System.currentTimeMillis());
                    progressRegistry.getRandomIntegers().put(blockPosition, random.nextInt(1000));
                }

                PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(progressRegistry.getRandomIntegers().get(blockPosition), blockPosition, (progressInt / 10));
                WorldBlocksInjector.sendPacket(player, packet);
            }
        }
    }

    private BossBar bossBar = null;

    public void progression(JavaPlugin main) {

        if (bossBar == null) {
            bossBar = new BukkitBossBarFactory(Bukkit.getServer())
                .newBossBar()
                .color(BossBarColor.GREEN)
                .style(BossBarStyle.SOLID);
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player == null) return;

            try {
                Block block = player.getTargetBlockExact(5);

                if (block == null) return;

                if (!block.getType().equals(Material.NOTE_BLOCK)) return;

                BlockPosition blockPosition = new BlockPosition(block.getX(), block.getY(), block.getZ());

                BlockHandler.BlockModel model = BlockHandler.modelFromString(regionRegistry, blockRegistry, block);

                double hardnessMultiplier;
                double regenTime;

                if (model == null) {
                    hardnessMultiplier = 1d / (300 / 100d);
                    regenTime = System.currentTimeMillis() + 10 * 1000;
                } else {
                    hardnessMultiplier = 1d / (model.getHardnessMulti() / 100d);
                    regenTime = System.currentTimeMillis() + model.getRegenTime() * 1000;
                }

                if (progressRegistry.getBlockBreak(blockPosition)) {
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

                    progressRegistry.getBlockProgress().remove(blockPosition);

                    double oldProgress = 0;

                    if (progressRegistry.getOldBlockProgress().containsKey(blockPosition)) {
                        oldProgress = progressRegistry.getOldBlockProgress().get(blockPosition);
                    }

                    if (player.getGameMode() != GameMode.CREATIVE) {
                        progressRegistry.getBlockProgress().put(blockPosition, doubleProgress + oldProgress);
                    }

                    double blockProgress = progressRegistry.getBlockProgress().get(blockPosition);
                    double bossBarProgress = blockProgress / 100;

                    if (blockProgress < 100 && blockProgress > -1) {
                        bossBar.title("&a&lBlock Progress: &f&l" + (Math.round(blockProgress * 100) / 100.0) + "%");
                        bossBar.addPlayer(player);
                        bossBar.progress(bossBarProgress);
                        continue;
                    }


                    PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(progressRegistry.getRandomIntegers().get(blockPosition), blockPosition, 0);

                    Material material = Material.STONE;
                    player.sendBlockChange(block.getLocation(), material.createBlockData());

                    WorldBlocksInjector.sendPacket(player, packet);

                    progressRegistry.getBlockProgress().remove(blockPosition);
                    progressRegistry.getOldBlockProgress().remove(blockPosition);

                    new BlockRegeneration(
                        regenRegistry,
                        main,
                        block.getLocation(),
                        block,
                        System.currentTimeMillis(),
                        (long) regenTime, player);

                    Bukkit.getPluginManager().callEvent(new WorldBlocksBreakEvent(block, player));
                    bossBar.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        updatePacket();
    }
}

