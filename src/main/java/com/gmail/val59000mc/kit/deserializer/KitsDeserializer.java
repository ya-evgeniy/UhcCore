package com.gmail.val59000mc.kit.deserializer;

import com.gmail.val59000mc.kit.KitGroup;
import com.gmail.val59000mc.kit.KitsManager;
import com.gmail.val59000mc.kit.table.entry.KitTableEntries;
import com.gmail.val59000mc.kit.table.entry.KitTableEntry;
import com.gmail.val59000mc.kit.table.condition.KitTableCondition;
import com.gmail.val59000mc.kit.table.condition.KitTableConditions;
import com.gmail.val59000mc.kit.table.item.KitItem;
import com.gmail.val59000mc.kit.table.set.KitTablePools;
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

        builder.registerTypeAdapter(KitGroup.class, new KitGroupDeserializer(manager));
        builder.registerTypeAdapter(ItemStack.class, new KitItemStackDeserializer());
        builder.registerTypeAdapter(KitTableEntries.class, new KitTableEntriesDeserializer(manager));
        builder.registerTypeAdapter(KitTableEntry.class, new KitTableEntryDeserializer(manager));
        builder.registerTypeAdapter(KitTableConditions.class, new KitTableConditionsDeserializer(manager));
        builder.registerTypeAdapter(KitTableCondition.class, new KitTableConditionDeserializer(manager));

        builder.registerTypeAdapter(KitTablePools.class, new KitTablePoolsDeserializer());

        builder.registerTypeAdapter(KitItem.class, new KitItemDeserializer());

        this.gson = builder.create();
    }

    public Gson getGson() {
        return gson;
    }

}
