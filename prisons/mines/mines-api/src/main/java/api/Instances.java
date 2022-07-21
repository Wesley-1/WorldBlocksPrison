package api;

import org.jetbrains.annotations.NotNull;

import java.util.IdentityHashMap;
import java.util.Map;

public class Instances {
    private static final Map<Class<?>, Object> INSTANCES = new IdentityHashMap<>();

    @NotNull
    public static <T> T get(Class<T> clazz) {
        try {
            if (INSTANCES.containsKey(clazz)) {
                return (T) INSTANCES.get(clazz);
            }

            final T object = clazz.newInstance();
            INSTANCES.put(clazz, object);
            return object;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return clazz.cast(new Object());
    }

    public static void register(Object object) {
        INSTANCES.put(object.getClass(), object);
    }
}
