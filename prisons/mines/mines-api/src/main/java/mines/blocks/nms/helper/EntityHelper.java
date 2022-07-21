package mines.blocks.nms.helper;

import api.Instances;
import api.Tickable;
import me.lucko.helper.Schedulers;
import mines.blocks.nms.packets.injector.WorldBlocksInjector;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.world.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class EntityHelper {
    private final List<EntityWrapper> entities;

    public EntityHelper() {
        this.entities = new ArrayList<>();
    }

    public static void addEntity(EntityWrapper entity) {
        Instances.get(EntityHelper.class).entities.add(entity);
    }

    public static void removeEntity(EntityWrapper entityWrapper) {
        Instances.get(EntityHelper.class).entities.remove(entityWrapper);
    }

    public static abstract class EntityWrapper<T extends Entity> implements Tickable {
        private final T entity;
        private final String name;

        private boolean cancelled;
        private long ticksRan;

        public EntityWrapper(T entity, String name, boolean tickable, long delay, long period) {
            this.entity = entity;
            this.initEntity();
            this.name = name;

            this.cancelled = false;

            if (!tickable) {
                return;
            }

            Schedulers.async().runRepeating((task) -> {
                if (this.cancelled) {
                    task.stop();
                    this.removeEntity();
                    return;
                }

                this.tick();
                this.ticksRan++;
            }, delay, period);
        }

        public String getName() {
            return this.name;
        }

        public T getEntity() {
            return this.entity;
        }

        public long getTicksRan() {
            return this.ticksRan;
        }

        public void cancel() {
            this.cancelled = true;
        }

        public void removeEntity() {
            EntityHelper.removeEntity(this);
        }

        public void spawnEntity(Player... players) {
            final PacketPlayOutSpawnEntity packetPlayOutSpawnEntity = new PacketPlayOutSpawnEntity(this.entity);
            for (final Player player : players) {
                WorldBlocksInjector.sendPacket(player, packetPlayOutSpawnEntity);
            }
        }

        protected abstract void initEntity();
    }
}
