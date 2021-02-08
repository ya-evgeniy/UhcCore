package com.gmail.val59000mc.kit.deserializer;

import com.gmail.val59000mc.kit.exception.KitParseException;
import com.gmail.val59000mc.kit.table.entry.KitTableEntries;
import com.gmail.val59000mc.kit.table.set.KitTablePool;
import com.google.gson.*;

import java.lang.reflect.Type;

import static com.gmail.val59000mc.kit.deserializer.stacktrace.KitDeserializeStacktraceManager.begin;
import static com.gmail.val59000mc.kit.deserializer.stacktrace.KitDeserializeStacktraceManager.end;

public class KitTablePoolDeserializer implements JsonDeserializer<KitTablePool> {

    @Override
    public KitTablePool deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonObject()) throw new KitParseException("is not a object");
        JsonObject object = json.getAsJsonObject();

        Integer rolls;
        KitTableEntries entries;

        begin("rools");
        {
            rolls = context.deserialize(object.get("rools"), Integer.class);
            if (rolls == null) rolls = 1;
        }
        end();

        begin("entries");
        {
            entries = context.deserialize(object.get("entries"), KitTableEntries.class);
            if (entries == null) throw new KitParseException("cannot be null");
        }
        end();

        return new KitTablePool(rolls, entries);
    }

}
