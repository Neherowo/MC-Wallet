package pl.nehorowo.wallet.database.helper;

import lombok.SneakyThrows;
import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;

public class ReflectionHelper {

    public static String getVersion(){
        return Bukkit.getServer().getClass().getPackage().getName().substring(23);
    }

    public static Class<?> getCraftBukkitClass(String classPath){
        try {
            return Class.forName("org.bukkit.craftbukkit." + getVersion() + "." + classPath);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static Field getField(Class<?> clazz, String fieldName){
        for (Field declaredField : clazz.getDeclaredFields()) {
            declaredField.setAccessible(true);
            if(declaredField.getName().equals(fieldName)) return declaredField;
        }
        return null;
    }

    private static final Class<?> CraftInventoryClass = getCraftBukkitClass("inventory.CraftInventory");
    private static final Class<?> MinecraftInventoryClass = getCraftBukkitClass("inventory.CraftInventoryCustom").getDeclaredClasses()[0];

    private static Field IInventoryField;
    private static Field titleField;

    static {
        IInventoryField = getField(CraftInventoryClass, "inventory");
        titleField = getField(MinecraftInventoryClass, "title");
    }

    public static <T> Constructor<?> getConstructor(Class<T> clazz, Class<?>... args) {
        for (Constructor<?> constructor : clazz.getConstructors()) {
            constructor.setAccessible(true);
            if(Arrays.equals(constructor.getParameterTypes(), args)){
                return constructor;
            }
        }
        return null;
    }

    @SneakyThrows
    public static <T> T newInstance(Constructor<T> constructor, Object... args) {
        return constructor.newInstance(args);
    }
}
