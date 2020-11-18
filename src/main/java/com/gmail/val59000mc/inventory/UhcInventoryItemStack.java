package com.gmail.val59000mc.inventory;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class UhcInventoryItemStack implements UhcInventoryItem {

    protected final ItemStack stack;

    public UhcInventoryItemStack(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public final @Nullable ItemStack getDisplay() {
        return this.stack;
    }

}
