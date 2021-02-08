package com.gmail.val59000mc.utils;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

public class SafeNmsReflection {

    private static final String VERSION;
    private static final String BUKKIT_PACKAGE;
    private static final String MINECRAFT_PACKAGE;

    static {
        String finalVersion = null;
        try {
            finalVersion = Bukkit.getServer().getClass().toString().split("\\.")[3];
        } catch (IndexOutOfBoundsException ignore) { }
        VERSION = finalVersion;

        BUKKIT_PACKAGE = "org.bukkit.craftbukkit." + VERSION + ".";
        MINECRAFT_PACKAGE = "net.minecraft.server." + VERSION + ".";
    }

    public static @Nullable Class<?> getBukkitClass(@NotNull String name) {
        try {
            return Class.forName(BUKKIT_PACKAGE + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static @Nullable Class<?> getMinecraftClass(@NotNull String name) {
        try {
            return Class.forName(MINECRAFT_PACKAGE + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static @Nullable Constructor<?> getConstructor(@Nullable Class<?> clazz, @Nullable Class<?>... args) {
        if (clazz == null) return null;
        if (Arrays.stream(args).anyMatch(Objects::isNull)) return null;
        try {
            return clazz.getConstructor(args);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static @Nullable Object newInstance(@Nullable Constructor<?> constructor, @Nullable Object... args) {
        if (constructor == null) return null;
        try {
            return constructor.newInstance(args);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            return null;
        }
    }

    public static @Nullable Field getField(@Nullable Class<?> clazz, @NotNull String fieldName) {
        if (clazz == null) return null;
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static @Nullable Method getMethod(@Nullable Class<?> clazz, @NotNull String name, @Nullable Class<?>... args) {
        if (clazz == null) return null;
        if (Arrays.stream(args).anyMatch(Objects::isNull)) return null;
        try {
            return clazz.getDeclaredMethod(name, args);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setFieldValue(@Nullable Object instance, @Nullable Field field, @Nullable Object value) {
        if (instance == null || field == null) return;

        try {
            field.setAccessible(true);
            field.set(instance, value);
        }
        catch (IllegalAccessException | SecurityException e) {
            e.printStackTrace();
        }
    }

    public static @Nullable Object getFieldValue(@Nullable Object instance, @Nullable Field field) {
        if (field == null) return instance;

        try {
            field.setAccessible(true);
            return field.get(instance);
        }
        catch (IllegalAccessException | SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static @Nullable Object invokeMethod(@Nullable Object instance, @Nullable Method method, @Nullable Object... args) {
        if (method == null) return null;
        try {
            method.setAccessible(true);
            return method.invoke(instance, args);
        } catch (IllegalAccessException | InvocationTargetException | SecurityException e) {
            e.printStackTrace();
            return null;
        }
    }

}
