package mines.packets.packets;


import net.minecraft.network.protocol.game.PacketPlayOutBlockBreakAnimation;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment;
import org.bukkit.entity.Player;

public class PacketPlayOutBlockBreakAnimationImpl extends PacketEvent<PacketPlayOutBlockBreakAnimation> {

    private PacketPlayOutBlockBreakAnimation packet;

    public PacketPlayOutBlockBreakAnimationImpl(Player player, PacketPlayOutBlockBreakAnimation packet) {
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
