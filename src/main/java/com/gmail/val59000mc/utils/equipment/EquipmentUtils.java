package com.gmail.val59000mc.utils.equipment;

import com.gmail.val59000mc.utils.equipment.slot.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EquipmentUtils {

    public static @NotNull EquipmentSlot from(@NotNull String name) {
        switch (name) {
            case "helmet":
                return new ArmorEquipmentSlot(ArmorEquipmentSlot.SlotType.HELMET);
            case "chestplate":
                return new ArmorEquipmentSlot(ArmorEquipmentSlot.SlotType.CHESTPLATE);
            case "leggings":
                return new ArmorEquipmentSlot(ArmorEquipmentSlot.SlotType.LEGGINGS);
            case "boots":
                return new ArmorEquipmentSlot(ArmorEquipmentSlot.SlotType.BOOTS);
            case "offhand":
                return new OffhandEquipmentSlot();
            default:
                int i = name.indexOf(".");
                if (i != -1) {
                    try {
                        i = Integer.parseInt(name.substring(i + 1));
                    } catch (NumberFormatException ignore) {
                        i = -1;
                    }
                }

                if (name.startsWith("hotbar")) {
                    return new HotbarEquipmentSlot(i);
                }
                else if (name.startsWith("storage")) {
                    return new StorageEquipmentSlot(i + 9);
                }
                else {
                    return new InventoryEquipmentSlot(i);
                }
        }
    }

    public static @Nullable EquipmentSlot from(int index) {
        if (index > -1 && index < 9) {
            return new HotbarEquipmentSlot(index);
        }
        if (index > 8 && index < 36) {
            return new StorageEquipmentSlot(index);
        }
        if (index == 40) {
            return new OffhandEquipmentSlot();
        }
        if (index == 36) {
            return new ArmorEquipmentSlot(ArmorEquipmentSlot.SlotType.BOOTS);
        }
        if (index == 37) {
            return new ArmorEquipmentSlot(ArmorEquipmentSlot.SlotType.LEGGINGS);
        }
        if (index == 38) {
            return new ArmorEquipmentSlot(ArmorEquipmentSlot.SlotType.CHESTPLATE);
        }
        if (index == 39) {
            return new ArmorEquipmentSlot(ArmorEquipmentSlot.SlotType.HELMET);
        }

        return null;
    }

}
