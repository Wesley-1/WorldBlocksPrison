package mines.packetnew.packets;

import net.minecraft.network.protocol.game.PacketPlayOutBlockBreak;
import org.bukkit.entity.Player;

public class PacketPlayOutBlockBreakImpl extends PacketEvent<PacketPlayOutBlockBreak> {

    public PacketPlayOutBlockBreakImpl(Player player, PacketPlayOutBlockBreak packet) {
        super(player, packet);
    }
}


