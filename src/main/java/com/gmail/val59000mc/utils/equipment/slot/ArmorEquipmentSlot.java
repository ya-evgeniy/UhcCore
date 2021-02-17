package com.gmail.val59000mc.utils.equipment.slot;

import com.gmail.val59000mc.utils.equipment.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Objects;

public class ArmorEquipmentSlot implements EquipmentSlot {

    private final SlotType type;

    public ArmorEquipmentSlot(SlotType type) {
        this.type = Objects.requireNonNull(type, "Slot type cannot be null");
    }

    public SlotType getType() {
        return type;
    }

    @Override
    public void equip(PlayerInventory inventory, ItemStack stack) {
        Objects.requireNonNull(inventory, "Inventory cannot be null");
        Objects.requireNonNull(stack, "Stack cannot be null");

        switch (type) {
            case HELMET:
                inventory.setHelmet(stack);
                break;
            case CHESTPLATE:
                inventory.setChestplate(stack);
                break;
            case LEGGINGS:
                inventory.setLeggings(stack);
                break;
            case BOOTS:
                inventory.setBoots(stack);
                break;
        }
    }

    @Override
    public String buildId() {
        switch (type) {
            case HELMET:
                return "helmet";
            case CHESTPLATE:
                return "chestplate";
            case LEGGINGS:
                return "leggings";
            case BOOTS:
                return "boots";
        }
        return "any";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArmorEquipmentSlot that = (ArmorEquipmentSlot) o;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    @Override
    public String toString() {
        return "ArmorEquipmentSlot{" +
                "type=" + type +
                '}';
    }

    public enum SlotType {

        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS

    }

}
