package com.gmail.val59000mc.lobby.pvp;

import com.gmail.val59000mc.utils.equipment.Equipment;
import com.gmail.val59000mc.utils.equipment.EquipmentContainer;
import com.gmail.val59000mc.utils.equipment.EquipmentSlot;
import com.gmail.val59000mc.utils.equipment.EquipmentUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerLobbyPvpInventory {

    private int sourceIndex;

    private String cursorId;
    private EquipmentSlot cursorSlot;

    private boolean loaded = true;
    private final Map<String, EquipmentSlot> playerEquipment = new HashMap<>();
    private boolean changed = false;

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public Map<String, EquipmentSlot> getPlayerEquipment() {
        return playerEquipment;
    }

    public void equip(EquipmentContainer equipmentContainer, Player player) {
        player.getInventory().clear();

        final List<Equipment> equipments = equipmentContainer.getEquipments();
        for (Equipment equipment : equipments) {
            final String id = equipment.getId();
            final EquipmentSlot equipmentSlot = playerEquipment.get(id);

            if (!isLoaded() || equipmentSlot == null) {
                equipment.equip(player.getInventory());
            }
            else {
                equipmentSlot.equip(player.getInventory(), equipment.getStack());
            }
        }
    }

    public @Nullable String getItemId(EquipmentContainer equipmentContainer, @Nullable EquipmentSlot from) {
        if (from == null) return null;

        for (Map.Entry<String, EquipmentSlot> entry : this.playerEquipment.entrySet()) {
            if (entry.getValue().equals(from)) {
                return entry.getKey();
            }
        }

        for (Equipment equipment : equipmentContainer.getEquipments()) {
            if (equipment.getSlot().equals(from)) {
                if (this.playerEquipment.get(equipment.getId()) == null) return equipment.getId();
                else return null;
            }
        }

        return null;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public int getSourceIndex() {
        return sourceIndex;
    }

    public void setSourceIndex(int sourceIndex) {
        this.sourceIndex = sourceIndex;
    }

    public String getCursorId() {
        return cursorId;
    }

    public void setCursorId(String cursorId) {
        this.cursorId = cursorId;
    }

    public EquipmentSlot getCursorSlot() {
        return cursorSlot;
    }

    public void setCursorSlot(EquipmentSlot cursorSlot) {
        this.cursorSlot = cursorSlot;
    }

    public void endChanging(EquipmentContainer equipmentContainer) {
        if (getCursorId() == null) {
            return;
        }

        String targetId = getItemId(equipmentContainer, getCursorSlot());
        if (targetId == null || targetId.equals(getCursorId())) {
            getPlayerEquipment().put(getCursorId(), getCursorSlot());
            setChanged(true);
            setSourceIndex(-1);
            setCursorId(null);
            setCursorSlot(null);
            return;
        }

        for (int i = 0; i < 41; i++) {
            final EquipmentSlot targetSlot = EquipmentUtils.from(i);
            if (targetSlot == null) continue;

            targetId = getItemId(equipmentContainer, targetSlot);
            if (targetId == null) {
                getPlayerEquipment().put(getCursorId(), targetSlot);
                setChanged(true);
                setSourceIndex(-1);
                setCursorId(null);
                setCursorSlot(null);
                return;
            }
        }
    }
}
