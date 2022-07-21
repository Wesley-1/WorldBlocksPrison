package mines.blocks.block;

import me.lucko.helper.item.ItemStackBuilder;
import mines.blocks.block.factory.WorldBlockFactory;
import mines.blocks.block.factory.effects.BukkitEffectFactory;
import mines.blocks.block.factory.effects.types.WorldBlocksEffect;
import mines.blocks.block.factory.effects.types.WorldBlocksParticle;
import mines.blocks.block.factory.effects.types.WorldBlocksSound;
import mines.blocks.block.factory.interfaces.WorldBlocksBlock;
import mines.blocks.block.factory.interfaces.WorldBlocksEffects;
import mines.blocks.regions.WorldBlocksMineRegion;
import mines.blocks.registry.BlockRegistry;
import mines.blocks.registry.RegionRegistry;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.codemc.worldguardwrapper.region.IWrappedRegion;

import java.util.List;
import java.util.Objects;

public class BlockHandler {

    public void init(FileConfiguration configSection) {
        for (String blockKey : configSection.getConfigurationSection("Blocks").getKeys(false)) {
            String mainPath = "Blocks." + blockKey + ".";

            String blockName = configSection.getString(mainPath + "blockName");
            int customModelData = configSection.getInt(mainPath + "modelData");
            double hardnessMultiplier = configSection.getDouble(mainPath + "hardnessMultiplier");
            double regenTime = configSection.getDouble(mainPath + "regenTime");
            boolean hasParticles = configSection.getBoolean(mainPath + "hasParticle");
            boolean hasEffect = configSection.getBoolean(mainPath + "hasEffect");
            boolean hasSound = configSection.getBoolean(mainPath + "hasSound");

            WorldBlocksBlock worldBlock = new WorldBlockFactory()
                .newBlock()
                .setName(blockName)
                .setModelData(customModelData)
                .setHardnessMultiplier(hardnessMultiplier)
                .setRegenTime(regenTime);

            System.out.println(worldBlock.getName());
            System.out.println(worldBlock.getModelData());
            System.out.println(worldBlock.getHardnessMultiplier());

            if (hasParticles) {
                String particleType = configSection.getString(mainPath + "effects.particles.type");
                int amount = configSection.getInt(mainPath + "effects.particles.amount");

                WorldBlocksParticle worldBlocksParticles = new WorldBlocksParticle(amount);
                worldBlocksParticles.setType(Particle.valueOf(particleType));

                WorldBlocksEffects particleEffect = new BukkitEffectFactory().newEffect()
                    .setEffectType(worldBlocksParticles);

                worldBlock.addEffect(particleEffect);
            }

            if (hasEffect) {
                String effectType = configSection.getString(mainPath + "effects.effect.type");
                int data = configSection.getInt(mainPath + "effects.effect.data");
                int radius = configSection.getInt(mainPath + "effects.effect.radius");

                WorldBlocksEffect worldBlocksEffect = new WorldBlocksEffect(data, radius);

                worldBlocksEffect.setType(Effect.valueOf(effectType));

                WorldBlocksEffects effect = new BukkitEffectFactory().newEffect()
                    .setEffectType(worldBlocksEffect);

                worldBlock.addEffect(effect);
            }

            if (hasSound) {
                String soundType = configSection.getString(mainPath + "effects.sounds.type");
                int pitch = configSection.getInt(mainPath + "effects.sounds.pitch");
                int volume = configSection.getInt(mainPath + "effects.sounds.volume");

                WorldBlocksSound worldBlocksSound = new WorldBlocksSound(volume, pitch);
                // TODO fix the sound.valueOf(soundType) issue
                worldBlocksSound.setType(Sound.BLOCK_AMETHYST_BLOCK_BREAK);

                WorldBlocksEffects soundEffect = new BukkitEffectFactory().newEffect()
                    .setEffectType(worldBlocksSound);

                worldBlock.addEffect(soundEffect);
            }

            for (String dropKey : configSection.getConfigurationSection("Blocks." + blockKey + ".drops").getKeys(false)) {
                String dropPath = mainPath + "drops." + dropKey + ".";

                String material = configSection.getString(dropPath + "material");
                String dropName = configSection.getString(dropPath + "itemName");
                List<String> lore = configSection.getStringList(dropPath + "lore");
                int data = configSection.getInt(dropPath + "data");
                boolean glowing = configSection.getBoolean(dropPath + "glowing");

                if (material == null || dropName == null) return;

                ItemStackBuilder builder = ItemStackBuilder.of(Material.valueOf(material)).name(dropName).lore(lore).data(data);

                if (glowing) {
                    builder.enchant(Enchantment.LURE);
                    builder.flag(ItemFlag.HIDE_ENCHANTS);
                }

                System.out.println("Drops");
                worldBlock.addDrop(builder.build());
            }

            BlockRegistry.get().getBlocks().put(blockName, worldBlock);
        }
    }

    public static WorldBlocksBlock getBlock(Location location) {
        WorldBlocksMineRegion.Instance instance = RegionRegistry.get().getInstance(location);

        for (String blockKey : BlockRegistry.get().getBlocks().keySet()) {
            if (Objects.equals(instance.getBlock(), blockKey)) {
                return BlockRegistry.get().getBlocks().get(instance.getBlock());
            }
        }
        return null;
    }
}
