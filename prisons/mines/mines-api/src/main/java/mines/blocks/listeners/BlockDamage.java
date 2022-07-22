package mines.blocks.listeners;

import mines.blocks.nms.helper.BlockAnimHelper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;

public class BlockDamage implements Listener {

    @EventHandler
    public void onBlockDamage(BlockDamageEvent event) throws NoSuchFieldException, IllegalAccessException {
        BlockAnimHelper.BlockAnimation blockAnimation = new BlockAnimHelper.BlockAnimation(event.getPlayer(), event.getBlock(), 0.05f);
        blockAnimation.handleAnimation();

        BlockAnimHelper animHelper = new BlockAnimHelper();
        animHelper.fractureBlock(event.getPlayer(), event.getBlock(), 10);
    }
}
