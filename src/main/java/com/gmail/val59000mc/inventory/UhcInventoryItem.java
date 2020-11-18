package com.gmail.val59000mc.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface UhcInventoryItem {

    @Nullable ItemStack getDisplay();

    default void on(@NotNull InventoryClickEvent event) {  };

}
