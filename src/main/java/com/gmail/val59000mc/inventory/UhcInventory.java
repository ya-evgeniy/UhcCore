package com.gmail.val59000mc.inventory;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UhcInventory implements InventoryHolder {

    protected final Inventory inventory;

    public UhcInventory(int lines, @NotNull String title) {
        this.inventory = Bukkit.createInventory(this, 9 * Math.max(Math.min(lines, 6), 1), title);
    }

    @Override
    public final @NotNull Inventory getInventory() {
        return this.inventory;
    }

    public final void openFor(@NotNull HumanEntity entity) {
        entity.openInventory(this.inventory);
    }

    public void setItem(int x, int y, @Nullable ItemStack stack) {
        if (x > 9) return;
        setItem(y * 9 + x, stack);
    }

    public void setItem(int index, @Nullable ItemStack stack) {
        if (index > -1 && index < inventory.getSize()) {
            inventory.setItem(index, stack);
        }
    }

    public void on(@NotNull InventoryClickEvent event) {
    }

    public void on(@NotNull InventoryDragEvent event) {
    }

    public void on(@NotNull InventoryOpenEvent event) {
    }

    public void on(@NotNull InventoryCloseEvent event) {
    }

}
