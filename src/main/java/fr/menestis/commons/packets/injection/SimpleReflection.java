package fr.menestis.commons.packets.injection;

import net.minecraft.server.v1_8_R3.Scoreboard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Contains useful reflection utils. <p>
 * It is not nms version dependent
 */
public class SimpleReflection {

    /**
     * @param classname The class name, if nms, use "{nms}"
     * @return The class you want
     */
    public static Class<?> getClass(final String classname) {
        try {
            final String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
            final String path = classname.replace("{nms}", "net.minecraft.server." + version)
                    .replace("{nm}", "net.minecraft." + version)
                    .replace("{cb}", "org.bukkit.craftbukkit.." + version);
            return Class.forName(path);
        } catch (final Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    /**
     * @param p The Bukkit player
     * @return NmsPlayer
     * @throws Exception if error in reflection
     */
    public static Object getNmsPlayer(final Player p) throws Exception {
        final Method getHandle = p.getClass().getMethod("getHandle");
        return getHandle.invoke(p);
    }

    /**
     * @param s The Bukkit Scoreboard
     * @return NmsScoreboard
     * @throws Exception if error in reflection
     */
    public static Object getNmsScoreboard(final Scoreboard s) throws Exception {
        final Method getHandle = s.getClass().getMethod("getHandle");
        return getHandle.invoke(s);
    }

    /**
     * @param instance  Object instance
     * @param fieldName Object fields name
     * @return Field Value
     * @throws Exception if error in reflection
     */
    public static Object getFieldValue(final Object instance, final String fieldName) throws Exception {
        final Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(instance);
    }

    /**
     * @param field The class field
     * @param obj   The object instance
     * @param <T>   The return type
     * @return The field value
     */
    public static <T> T getFieldValue(final Field field, final Object obj) {
        try {
            //noinspection unchecked
            return (T) field.get(obj);
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param clazz     The Class
     * @param fieldName The field name
     * @return The class field
     * @throws Exception if error in reflection
     */
    public static Field getField(final Class<?> clazz, final String fieldName) throws Exception {
        final Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }

    /**
     * @param instance The object instance
     * @param field    The class field
     * @param value    The value you want to place
     */
    public static void setValue(final Object instance, final String field, final Object value) {
        try {
            final Field f = instance.getClass().getDeclaredField(field);
            f.setAccessible(true);
            f.set(instance, value);
        } catch (final Throwable t) {
            t.printStackTrace();
        }
    }
}
