package com.gmail.val59000mc.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UhcInventoryContent extends UhcInventory {

    protected UhcInventoryItem[] content;

    public UhcInventoryContent(int lines, @NotNull String title) {
        super(lines, title);
        this.content = new UhcInventoryItem[this.inventory.getSize()];
    }

    public void setItem(int x, int y, @Nullable UhcInventoryItem item) {
        setItem(y * 9 + x, item);
    }

    public void setItem(int index, @Nullable UhcInventoryItem item) {
        if (index > -1 && index < content.length) {
            this.content[index] = item;
            this.inventory.setItem(index, item == null ? null : item.getDisplay());
//            ReflectionInventory.setItem(inventory, index, item.getDisplay(), false, true);
        }
    }

    public @Nullable UhcInventoryItem getItem(int x, int y) {
        return getItem(y * 9 + x);
    }

    public @Nullable UhcInventoryItem getItem(int index) {
        if (index > -1 && index < content.length) {
            return this.content[index];
        }
        return null;
    }

    @Override
    public void on(@NotNull InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;
        int slot = event.getSlot();

        if (slot > -1 && slot < content.length) {
            UhcInventoryItem item = this.content[slot];
            if (item != null) {
                item.on(event);
            }
        }
    }

}
