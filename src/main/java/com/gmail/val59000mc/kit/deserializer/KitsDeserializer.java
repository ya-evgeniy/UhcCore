package com.gmail.val59000mc.kit.deserializer;

import com.gmail.val59000mc.kit.Kit;
import com.gmail.val59000mc.kit.KitDisplayItem;
import com.gmail.val59000mc.kit.KitGroup;
import com.gmail.val59000mc.kit.KitsManager;
import com.gmail.val59000mc.kit.deserializer.base.IntDeserializer;
import com.gmail.val59000mc.kit.deserializer.base.StringDeserializer;
import com.gmail.val59000mc.kit.deserializer.condition.KitTableConditionDeserializer;
import com.gmail.val59000mc.kit.deserializer.condition.KitTableConditionPermissionDeserializer;
import com.gmail.val59000mc.kit.deserializer.condition.KitTableConditionUpgradeLevelDeserializer;
import com.gmail.val59000mc.kit.deserializer.condition.KitTableConditionsDeserializer;
import com.gmail.val59000mc.kit.deserializer.entry.KitTableEntriesDeserializer;
import com.gmail.val59000mc.kit.deserializer.entry.KitTableEntryDeserializer;
import com.gmail.val59000mc.kit.deserializer.entry.KitTableEntryItemDeserializer;
import com.gmail.val59000mc.kit.deserializer.entry.KitTableEntrySetDeserializer;
import com.gmail.val59000mc.kit.table.condition.KitTableConditionPermission;
import com.gmail.val59000mc.kit.table.condition.KitTableConditionUpgradeLevel;
import com.gmail.val59000mc.kit.table.entry.KitTableEntries;
import com.gmail.val59000mc.kit.table.entry.KitTableEntry;
import com.gmail.val59000mc.kit.table.condition.KitTableCondition;
import com.gmail.val59000mc.kit.table.condition.KitTableConditions;
import com.gmail.val59000mc.kit.table.entry.KitTableEntryItem;
import com.gmail.val59000mc.kit.table.entry.KitTableEntrySet;
import com.gmail.val59000mc.kit.table.item.KitItem;
import com.gmail.val59000mc.kit.table.item.KitItemStack;
import com.gmail.val59000mc.kit.table.set.KitTablePool;
import com.gmail.val59000mc.kit.table.set.KitTablePools;
import com.gmail.val59000mc.kit.table.set.KitTableSet;
import com.gmail.val59000mc.kit.upgrade.KitUpgrade;
import com.gmail.val59000mc.kit.upgrade.KitUpgrades;
import com.gmail.val59000mc.utils.json.exclusion.IgnoreDeserializationExclusionStrategy;
import com.gmail.val59000mc.utils.json.exclusion.IgnoreSerializationExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class KitsDeserializer {

    private final Gson gson;

    public KitsDeserializer(@NotNull KitsManager manager) {
        GsonBuilder builder = new GsonBuilder();

        builder.addDeserializationExclusionStrategy(new IgnoreDeserializationExclusionStrategy());
        builder.addSerializationExclusionStrategy(new IgnoreSerializationExclusionStrategy());

        builder.registerTypeAdapter(Integer.class, new IntDeserializer());
        builder.registerTypeAdapter(String.class, new StringDeserializer());

        builder.registerTypeAdapter(Kit.class, new KitDeserializer());
        builder.registerTypeAdapter(KitDisplayItem.class, new KitDisplayItemDeserializer());
        builder.registerTypeAdapter(KitGroup.class, new KitGroupDeserializer(manager));
        builder.registerTypeAdapter(KitItem.class, new KitItemDeserializer());
        builder.registerTypeAdapter(ItemStack.class, new KitItemStackDeserializer());

        builder.registerTypeAdapter(KitTablePools.class, new KitTablePoolsDeserializer());
        builder.registerTypeAdapter(KitTablePool.class, new KitTablePoolDeserializer());

        builder.registerTypeAdapter(KitUpgrades.class, new KitUpgradesDeserializer());
        builder.registerTypeAdapter(KitUpgrade.class, new KitUpgradeDeserializer());

        builder.registerTypeAdapter(KitTableConditions.class, new KitTableConditionsDeserializer());
        builder.registerTypeAdapter(KitTableCondition.class, new KitTableConditionDeserializer(manager));
        builder.registerTypeAdapter(KitTableConditionUpgradeLevel.class, new KitTableConditionUpgradeLevelDeserializer());
        builder.registerTypeAdapter(KitTableConditionPermission.class, new KitTableConditionPermissionDeserializer());

        builder.registerTypeAdapter(KitTableEntries.class, new KitTableEntriesDeserializer());
        builder.registerTypeAdapter(KitTableEntry.class, new KitTableEntryDeserializer(manager));
        builder.registerTypeAdapter(KitTableEntryItem.class, new KitTableEntryItemDeserializer());
        builder.registerTypeAdapter(KitTableEntrySet.class, new KitTableEntrySetDeserializer(manager));

        builder.registerTypeAdapter(KitTableSet.class, new KitTableSetDeserializer());

        this.gson = builder.create();
    }

    public Gson getGson() {
        return gson;
    }

}
