package com.gmail.val59000mc.utils.equipment;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Objects;

public class Equipment {

    private final String id;
    private final boolean slotMutable;

    private final EquipmentSlot slot;
    private final ItemStack stack;

    public Equipment(String id, boolean slotMutable, EquipmentSlot slot, ItemStack stack) {
        this.id = id;
        this.slotMutable = slotMutable;

        this.slot = Objects.requireNonNull(slot, "Slot cannot be null");
        this.stack = Objects.requireNonNull(stack, "Stack cannot be null");
    }

    public String getId() {
        return id;
    }

    public boolean isSlotMutable() {
        return slotMutable;
    }

    public EquipmentSlot getSlot() {
        return slot;
    }

    public ItemStack getStack() {
        return stack;
    }

    public void equip(PlayerInventory inventory) {
        Objects.requireNonNull(inventory, "Inventory cannot be null");

        this.slot.equip(inventory, stack);
    }

}
