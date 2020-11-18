package com.gmail.val59000mc.kit.deserializer;

import com.gmail.val59000mc.kit.KitsManager;
import com.gmail.val59000mc.kit.table.entry.KitTableEntries;
import com.gmail.val59000mc.kit.table.entry.KitTableEntry;
import com.google.gson.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class KitTableEntriesDeserializer implements JsonDeserializer<KitTableEntries> {

    private final @NotNull KitsManager manager;

    public KitTableEntriesDeserializer(@NotNull KitsManager manager) {
        this.manager = manager;
    }

    @Override
    public KitTableEntries deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonArray()) throw new JsonParseException("");
        JsonArray array = json.getAsJsonArray();

        List<KitTableEntry> entries = new ArrayList<>();
        for (JsonElement element : array) {
            KitTableEntry entry = context.deserialize(element, KitTableEntry.class);
            if (entry == null) {
                throw new JsonParseException("");
            }
            entries.add(entry);
        }

        return new KitTableEntries(entries);
    }

}
