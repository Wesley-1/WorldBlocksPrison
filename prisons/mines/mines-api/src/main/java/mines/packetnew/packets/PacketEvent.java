package mines.packetnew.packets;

import net.minecraft.network.protocol.game.PacketPlayInBlockDig;
import net.minecraft.network.protocol.game.PacketPlayOutBlockBreak;
import net.minecraft.network.protocol.game.PacketPlayOutBlockBreakAnimation;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public abstract class PacketEvent<T> implements Cancellable {
    private boolean canceled = false;
    private Player player;
    private T packet;

    public PacketEvent(Player player, T packet) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        canceled = cancel;
    }

    public T getPacket() {
        return packet;
    }

    public void setPacket(T packet) {
        this.packet = packet;
    }

    public static PacketEvent<?> get(Player player, Object o) {
        //In packets.
        if (o instanceof PacketPlayOutBlockBreak) { return new PacketPlayOutBlockBreakImpl(player, (PacketPlayOutBlockBreak) o);
        } else if (o instanceof PacketPlayInBlockDig) { return new PacketPlayInBlockDigImpl(player, (PacketPlayInBlockDig) o); }

        return null;
    }
}