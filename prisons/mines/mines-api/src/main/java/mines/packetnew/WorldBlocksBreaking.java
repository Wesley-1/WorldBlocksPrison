package mines.packetnew;

import me.lucko.helper.bossbar.BossBar;
import me.lucko.helper.bossbar.BossBarColor;
import me.lucko.helper.bossbar.BossBarStyle;
import me.lucko.helper.bossbar.BukkitBossBarFactory;
import mines.packetnew.events.WorldBlocksBreakEvent;
import mines.packetnew.injector.WorldBlocksInjector;
import mines.packetnew.packets.PacketPlayInBlockDigImpl;
import mines.packetnew.packets.PacketPlayOutBlockBreakImpl;
import mines.packetnew.registry.ProgressRegistry;
import mines.packetnew.subscription.EventSubscription;
import mines.packetnew.subscription.EventSubscriptions;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.PacketPlayInBlockDig;
import net.minecraft.network.protocol.game.PacketPlayOutBlockBreak;
import net.minecraft.network.protocol.game.PacketPlayOutBlockBreakAnimation;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEffect;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectList;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.util.Random;

public class WorldBlocksBreaking {

    private static Field blockPositionDigIn;
    private static Field blockPositionBlockBreakAnim;
    private static Field blockPositionBlockBreak;
    private static Field enumPlayerDigTypeInDig;
    private static Field inBool;
    private static Field dura;
    private static Field prog;
    private final ProgressRegistry registry;

    static {
        try {
            blockPositionDigIn = PacketPlayInBlockDig.class.getDeclaredField("a");
            enumPlayerDigTypeInDig = PacketPlayInBlockDig.class.getDeclaredField("c");

            blockPositionBlockBreak = PacketPlayOutBlockBreak.class.getDeclaredField("b");
            inBool = PacketPlayOutBlockBreak.class.getDeclaredField("e");

            dura = PacketPlayOutBlockBreakAnimation.class.getDeclaredField("a");
            prog = PacketPlayOutBlockBreakAnimation.class.getDeclaredField("c");
            blockPositionBlockBreakAnim = PacketPlayOutBlockBreakAnimation.class.getDeclaredField("b");

            blockPositionDigIn.setAccessible(true);
            enumPlayerDigTypeInDig.setAccessible(true);

            blockPositionBlockBreak.setAccessible(true);
            inBool.setAccessible(true);

            dura.setAccessible(true);
            prog.setAccessible(true);
            blockPositionBlockBreakAnim.setAccessible(true);

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
        BlockPosition blockPos = (BlockPosition) blockPositionDigIn.get(packet);

        PacketPlayOutEntityEffect entityEffect = new PacketPlayOutEntityEffect(event.getPlayer().getEntityId(), new MobEffect(MobEffectList.fromId(PotionEffectType.SLOW_DIGGING.getId()), Integer.MAX_VALUE, 255, true, false));
        WorldBlocksInjector.sendPacket(event.getPlayer(), entityEffect);

        if (registry.getBlocksBreaking().containsKey(blockPos)) {
            registry.getBlocksBreaking().remove(blockPos);
        }

        if (enumPlayerDigTypeInDig.get(packet) == PacketPlayInBlockDig.EnumPlayerDigType.a) {
            System.out.println(enumPlayerDigTypeInDig.get(packet));
            System.out.println("WERE IN THE START BREAKING ENUM!");
            registry.getBlocksBreaking().put(blockPos, true);
        } else if (enumPlayerDigTypeInDig.get(packet) == PacketPlayInBlockDig.EnumPlayerDigType.c || enumPlayerDigTypeInDig.get(packet) == PacketPlayInBlockDig.EnumPlayerDigType.b) {
            registry.getBlocksBreaking().remove(blockPos);
            registry.copyOldData(blockPos);
            System.out.println("NEW");
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
        System.out.println("Testing blockbreak");
        inBool.set(packet, true);
    }

    public void updatePacket() {

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            for (BlockPosition blockPosition : registry.getBlockProgress().keySet()) {

                double progress = registry.getBlockProgress().get(blockPosition);
                int progressInt = (int) progress;

                if (!registry.getRandomIntegers().containsKey(blockPosition)) {
                    Random random = new Random(System.currentTimeMillis());
                    registry.getRandomIntegers().put(blockPosition, random.nextInt(1000));
                }
                PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(registry.getRandomIntegers().get(blockPosition), blockPosition, (progressInt / 10));
                WorldBlocksInjector.sendPacket(player, packet);
            }
        }
    }

    private BossBar bossBar = null;

    public void progression(JavaPlugin mainClass) {
        if (bossBar == null) {
            bossBar = new BukkitBossBarFactory(Bukkit.getServer()).newBossBar().color(BossBarColor.GREEN).style(BossBarStyle.SOLID);
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player == null) return;
            try {
                Block block = player.getTargetBlockExact(5);

                if (block == null) return;

                double hardnessMultiplier = 1d / (300 / 100d);
                BlockPosition blockPosition = new BlockPosition(block.getX(), block.getY(), block.getZ());

                if (registry.getBlockBreak(blockPosition)) {
                    EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
                    World world = entityPlayer.getWorld();
                    IBlockData blockData = world.getType(blockPosition);

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

                    if (registry.getBlockProgress().containsKey(blockPosition)) {
                        registry.getBlockProgress().remove(blockPosition);
                    }

                    double oldProgress = 0;

                    if (registry.getOldBlockProgress().containsKey(blockPosition)) {
                        oldProgress = registry.getOldBlockProgress().get(blockPosition);
                    }

                    if (player.getGameMode() != GameMode.CREATIVE) {
                        registry.getBlockProgress().put(blockPosition, doubleProgress + oldProgress);
                    }

                    double blockProgress = registry.getBlockProgress().get(blockPosition);
                    double bossBarProgress = blockProgress / 100;

                    if (blockProgress < 100 && blockProgress > -1) {
                        bossBar.title("&a&lBlock Progress: &f&l" + (Math.round(blockProgress * 100) / 100.0) + "%");
                        bossBar.addPlayer(player);
                        bossBar.progress(bossBarProgress);
                        continue;
                    }

                    PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(registry.getRandomIntegers().get(blockPosition), blockPosition, 0);
                    WorldBlocksInjector.sendPacket(player, packet);

                    registry.getBlockProgress().remove(blockPosition);
                    registry.getOldBlockProgress().remove(blockPosition);

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