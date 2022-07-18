package mines.packets.packets;

import net.minecraft.network.protocol.game.PacketPlayInBlockDig;
import net.minecraft.network.protocol.game.PacketPlayOutBlockBreak;
import net.minecraft.network.protocol.game.PacketPlayOutBlockBreakAnimation;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import java.lang.reflect.Field;

public abstract class PacketEvent<T> implements Cancellable {
    private boolean canceled = false;
    private Player player;

    public PacketEvent(Player player) {
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

    public abstract T getPacket();
    public abstract void setPacket(T packet);

    public <T> T getField(int index, Class<?> type) {
        //Get the index-th field of type.
        for (Field f : getPacket().getClass().getDeclaredFields()) {
            if (type.isAssignableFrom(f.getType())) {
                try {
                    f.setAccessible(true);
                    return (T) f.get(getPacket());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static PacketEvent<?> get(Player player, Object o) {
        //In packets.
        if (o instanceof PacketPlayOutBlockBreakAnimation) { return new PacketPlayOutBlockBreakAnimationImpl(player, (PacketPlayOutBlockBreakAnimation) o);
        } else if (o instanceof PacketPlayOutBlockBreak) { return new PacketPlayOutBlockBreakImpl(player, (PacketPlayOutBlockBreak) o);
        } else if (o instanceof PacketPlayInBlockDig) { return new PacketPlayInBlockDigImpl(player, (PacketPlayInBlockDig) o); }

        return null;
    }
}