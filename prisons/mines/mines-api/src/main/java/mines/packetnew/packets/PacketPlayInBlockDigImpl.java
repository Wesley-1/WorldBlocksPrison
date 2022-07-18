package mines.packetnew.packets;

import net.minecraft.network.protocol.game.PacketPlayInBlockDig;
import org.bukkit.entity.Player;

public class PacketPlayInBlockDigImpl extends PacketEvent<PacketPlayInBlockDig> {

    private PacketPlayInBlockDig packet;

    public PacketPlayInBlockDigImpl(Player player, PacketPlayInBlockDig packet) {
        super(player);
        this.packet = packet;
    }

    @Override
    public PacketPlayInBlockDig getPacket() {
        return this.packet;
    }

    @Override
    public void setPacket(PacketPlayInBlockDig packet) {
        this.packet = packet;
    }
}
