package com.gmail.val59000mc.utils.equipment;

import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class EquipmentContainer {

    private final List<Equipment> equipments;

    public EquipmentContainer(List<Equipment> equipments) {
        this.equipments = Collections.unmodifiableList(Objects.requireNonNull(equipments, "Equipments cannot be null"));
    }

    public List<Equipment> getEquipments() {
        return equipments;
    }

    public void equip(Player player) {
        equip(player, true);
    }

    public void equip(Player player, boolean clearInventory) {
        Objects.requireNonNull(player, "Player cannot be null");
        PlayerInventory inventory = player.getInventory();

        if (clearInventory) inventory.clear();
        for (Equipment equipment : this.equipments) equipment.equip(inventory);
    }

    public @Nullable Equipment getEquipmentByItemId(String itemId) {
        if (itemId == null) return null;

        for (Equipment equipment : equipments) {
            if (equipment.getId().equals(itemId)) {
                return equipment;
            }
        }
        return null;
    }
}
