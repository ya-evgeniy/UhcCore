package com.gmail.val59000mc.utils.equipment.slot;

import com.gmail.val59000mc.utils.equipment.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Objects;

public class InventoryEquipmentSlot implements EquipmentSlot {

    private final int index;

    public InventoryEquipmentSlot(int index) {
        this.index = index;
    }

    @Override
    public void equip(PlayerInventory inventory, ItemStack stack) {
        Objects.requireNonNull(inventory, "Inventory cannot be null");
        Objects.requireNonNull(stack, "Stack cannot be null");

        if (index > -1 && index < inventory.getSize()) {
            inventory.setItem(index, stack);
        }
        else {
            inventory.addItem(stack);
        }
    }

    @Override
    public String buildId() {
        return "any";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventoryEquipmentSlot that = (InventoryEquipmentSlot) o;
        return index == that.index;
    }

    @Override
    public String toString() {
        return "InventoryEquipmentSlot{" +
                "index=" + index +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }
}
