package mines.packetnew.packets;

import net.minecraft.network.protocol.game.PacketPlayOutBlockBreak;
import net.minecraft.network.protocol.game.PacketPlayOutBlockBreakAnimation;
import org.bukkit.entity.Player;

public class PacketPlayOutBlockBreakAnimImpl extends PacketEvent<PacketPlayOutBlockBreakAnimation> {

    private PacketPlayOutBlockBreakAnimation packet;

    public PacketPlayOutBlockBreakAnimImpl(Player player, PacketPlayOutBlockBreakAnimation packet) {
        super(player);
        this.packet = packet;
    }

    @Override
    public PacketPlayOutBlockBreakAnimation getPacket() {
        return this.packet;
    }

    @Override
    public void setPacket(PacketPlayOutBlockBreakAnimation packet) {
        this.packet = packet;
    }
}
