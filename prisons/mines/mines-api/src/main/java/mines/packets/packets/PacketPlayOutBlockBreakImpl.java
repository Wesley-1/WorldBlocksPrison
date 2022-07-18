package mines.packets.packets;

import net.minecraft.network.protocol.game.PacketPlayInBlockDig;
import net.minecraft.network.protocol.game.PacketPlayOutBlockBreak;
import org.bukkit.entity.Player;

public class PacketPlayOutBlockBreakImpl extends PacketEvent<PacketPlayOutBlockBreak> {

    private PacketPlayOutBlockBreak packet;

    public PacketPlayOutBlockBreakImpl(Player player, PacketPlayOutBlockBreak packet) {
        super(player);
        this.packet = packet;
    }

    @Override
    public PacketPlayOutBlockBreak getPacket() {
        return this.packet;
    }

    @Override
    public void setPacket(PacketPlayOutBlockBreak packet) {
        this.packet = packet;
    }
}


