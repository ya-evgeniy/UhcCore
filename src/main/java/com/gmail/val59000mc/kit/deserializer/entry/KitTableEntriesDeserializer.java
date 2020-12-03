package com.gmail.val59000mc.kit.deserializer.entry;

import com.gmail.val59000mc.kit.exception.KitParseException;
import com.gmail.val59000mc.kit.table.entry.KitTableEntries;
import com.gmail.val59000mc.kit.table.entry.KitTableEntry;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.gmail.val59000mc.kit.deserializer.stacktrace.KitDeserializeStacktraceManager.begin;
import static com.gmail.val59000mc.kit.deserializer.stacktrace.KitDeserializeStacktraceManager.end;

public class KitTableEntriesDeserializer implements JsonDeserializer<KitTableEntries> {

    @Override
    public KitTableEntries deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonArray()) throw new KitParseException("is not a array");
        JsonArray array = json.getAsJsonArray();

        List<KitTableEntry> entries = new ArrayList<>();

        for (int i = 0; i < array.size(); i++) {
            begin(String.valueOf(i));
            {
                KitTableEntry entry = context.deserialize(array.get(i), KitTableEntry.class);
                if (entry == null) throw new KitParseException("cannot be null");

                entries.add(entry);
            }
            end();
        }

        return new KitTableEntries(entries);
    }

}
