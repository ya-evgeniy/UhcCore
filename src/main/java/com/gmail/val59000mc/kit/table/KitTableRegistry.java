package com.gmail.val59000mc.kit.table;

import com.gmail.val59000mc.kit.table.condition.KitTableCondition;
import com.gmail.val59000mc.kit.table.condition.KitTableConditionPermission;
import com.gmail.val59000mc.kit.table.condition.KitTableConditionUpgradeLevel;
import com.gmail.val59000mc.kit.table.entry.KitTableEntry;
import com.gmail.val59000mc.kit.table.entry.KitTableEntryItem;
import com.gmail.val59000mc.kit.table.entry.KitTableEntrySet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KitTableRegistry {

    private final Map<String, Class<? extends KitTableEntry>> entryImplByType = new HashMap<>();
    private final Map<String, Class<? extends KitTableCondition>> conditionImplByType = new HashMap<>();

    public KitTableRegistry() {

        setEntryImpl(KitTableEntryItem.ID, KitTableEntryItem.class);
        setEntryImpl(KitTableEntrySet.ID, KitTableEntrySet.class);

        setConditionImpl(KitTableConditionPermission.ID, KitTableConditionPermission.class);
        setConditionImpl(KitTableConditionUpgradeLevel.ID, KitTableConditionUpgradeLevel.class);

    }

    public void setEntryImpl(@NotNull String type, @NotNull Class<? extends KitTableEntry> impl) {
        entryImplByType.put(type, impl);
    }

    public void setConditionImpl(@NotNull String type, @NotNull Class<? extends KitTableCondition> impl) {
        conditionImplByType.put(type, impl);
    }

    public @Nullable Class<? extends KitTableEntry> getEntryImpl(@NotNull String type) {
        return this.entryImplByType.get(type);
    }

    public @Nullable Class<? extends KitTableCondition> getConditionImpl(@NotNull String type) {
        return this.conditionImplByType.get(type);
    }

    public List<String> getEntryTypes() {
        return new ArrayList<>(this.entryImplByType.keySet());
    }

    public List<String> getConditionTypes() {
        return new ArrayList<>(this.conditionImplByType.keySet());
    }

}
