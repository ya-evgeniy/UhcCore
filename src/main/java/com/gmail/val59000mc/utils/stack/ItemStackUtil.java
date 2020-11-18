package com.gmail.val59000mc.utils.stack;

import com.gmail.val59000mc.utils.SafeNmsReflection;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemStackUtil {

    private static final Field f_CraftMetaItem_lore;

    private static final Method m_IChatBaseComponent_ChatSerialize_a;

    static {

        Class<?> c_CraftMetaItem = SafeNmsReflection.getBukkitClass("inventory.CraftMetaItem");
        f_CraftMetaItem_lore = SafeNmsReflection.getField(c_CraftMetaItem, "lore");

        Class<?> c_IChatMutableComponent = SafeNmsReflection.getMinecraftClass("IChatMutableComponent");
        Class<?> c_IChatBaseComponent_ChatSerializer = SafeNmsReflection.getMinecraftClass("IChatBaseComponent$ChatSerializer");

        m_IChatBaseComponent_ChatSerialize_a = SafeNmsReflection.getMethod(c_IChatBaseComponent_ChatSerializer, "a", String.class);
    }

    public static void setDisplayName(@Nullable ItemStack stack, @Nullable String title) {
        if (stack == null) return;

        ItemMeta meta = stack.getItemMeta();
        setDisplayName(meta, title);
        stack.setItemMeta(meta);
    }

    public static void setDisplayName(@Nullable ItemMeta meta, @Nullable String title) {
        if (meta != null) meta.setDisplayName(title);
    }

    public static void setLore(@Nullable ItemStack stack, @Nullable List<String> lore) {
        if (stack == null) return;

        ItemMeta meta = stack.getItemMeta();
        setLore(meta, lore);
        stack.setItemMeta(meta);
    }

    public static void setLore(@Nullable ItemMeta meta, @Nullable List<String> lore) {
        if (meta != null) meta.setLore(lore);
    }

    public static void setJsonLore(@Nullable ItemStack stack, @Nullable List<String> lore) {
        if (stack == null) return;

        ItemMeta meta = stack.getItemMeta();
        setJsonLore(meta, lore);
        stack.setItemMeta(meta);
    }

    public static void setJsonLore(@Nullable ItemMeta meta, @Nullable List<String> lore) {
        if (meta == null) return;
        if (lore == null) {
            meta.setLore(null);
            return;
        }

        SafeNmsReflection.setFieldValue(meta, f_CraftMetaItem_lore, toIChatBaseComponent(lore));
    }

    public static @Nullable Object toIChatBaseComponent(@Nullable String json) {
        if (json == null) return null;
        return SafeNmsReflection.invokeField(null, m_IChatBaseComponent_ChatSerialize_a, json);
    }

    public static @NotNull List<Object> toIChatBaseComponent(@Nullable List<String> json) {
        if (json == null) return Collections.emptyList();

        ArrayList<Object> result = new ArrayList<>();
        for (String s : json) {
            result.add(toIChatBaseComponent(s));
        }
        return result;
    }

    public static @NotNull String translatableKey(@NotNull Material type) {
        String format;
        if (type.isBlock()) {
            format = "block.%s.%s";
        }
        else if (type.isItem()) {
            format = "item.%s.%s";
        }
        else {
            format = "%s.%s";
        }
        NamespacedKey key = type.getKey();
        return String.format(format, key.getNamespace(), key.getKey());
    }

}
