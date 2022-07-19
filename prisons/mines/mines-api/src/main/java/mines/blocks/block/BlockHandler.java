package mines.blocks.block;

import lombok.Getter;
import me.lucko.helper.item.ItemStackBuilder;
import mines.blocks.regions.WorldBlocksMineRegion;
import mines.blocks.registry.BlockRegistry;
import mines.blocks.registry.RegionRegistry;
import net.minecraft.core.BlockPosition;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

public class BlockHandler {

    public void setupConfiguration(BlockRegistry registry, YamlConfiguration section) {
        for (String string : section.getConfigurationSection("Blocks").getKeys(false)) {

            String blockName = section.getString("Blocks." + string + ".blockName");
            double regenTime = section.getDouble("Blocks." + string + ".regenTime");
            double hardnessMulti = section.getDouble("Blocks." + string +".hardnessMulti");
            int customModelData = section.getInt("Blocks." + string + ".modelData");

            BlockModel model = new BlockModel(blockName, regenTime, hardnessMulti, customModelData);
            for (String dropString : section.getConfigurationSection("Blocks." + string + ".drops").getKeys(false)) {
                String material = section.getString("Blocks." + string + ".drops." + dropString + ".material");
                String dropName = section.getString("Blocks." + string + ".drops." + dropString + ".itemName");
                List<String> lore = section.getStringList("Blocks." + string + ".drops." + dropString + ".lore");
                int data = section.getInt("Blocks." + string + ".drops." + dropString + ".data");
                boolean glowing = section.getBoolean("Blocks." + string + ".drops." + dropString + ".glowing");

                if (material == null || dropName == null) return;

                ItemStackBuilder builder = ItemStackBuilder.of(Material.valueOf(material)).name(dropName).lore(lore).data(data);

                if (glowing) {
                    builder.enchant(Enchantment.LURE);
                    builder.flag(ItemFlag.HIDE_ENCHANTS);
                }

                model.addCustomDrop(builder.build());

            }

            if (blockName == null) return;

            registry.getBlocks().put(blockName, model);
        }
    }

    public static BlockModel modelFromString(RegionRegistry regionRegistry, BlockRegistry blockRegistry, Block block) {
        for (WorldBlocksMineRegion.Instance region : regionRegistry.getRegions().values()) {
            if (region.getWgRegion().isPresent()) {
                if (region.getWgRegion().get().contains(block.getLocation())) {
                    return blockRegistry.getBlocks().get(region.getBlock());
                }
            }
        }
        return null;
    }

    @Getter
    public final class BlockModel {
        private final String blockName;
        private final double regenTime;
        private final double hardnessMulti;
        private final int customModelData;
        private Set<ItemStack> customDrops;

        public BlockModel(String blockName, double regenTime, double hardnessMulti, int customModelData) {
            this.blockName = blockName;
            this.regenTime = regenTime;
            this.hardnessMulti = hardnessMulti;
            this.customModelData = customModelData;
        }

        public void addCustomDrop(ItemStack itemStack) {
            this.customDrops.add(itemStack);
        }

        public Set<ItemStack> getCustomDrop() {
            return this.customDrops;
        }
    }
}
