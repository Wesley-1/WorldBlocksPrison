package mines.packetnew.packets;

import net.minecraft.network.protocol.game.PacketPlayInBlockDig;
import org.bukkit.entity.Player;

public class PacketPlayInBlockDigImpl extends PacketEvent<PacketPlayInBlockDig> {

    public PacketPlayInBlockDigImpl(Player player, PacketPlayInBlockDig packet) {
        super(player, packet);
    }
}
