package com.gmail.val59000mc.kit.deserializer.entry;

import com.gmail.val59000mc.kit.KitsManager;
import com.gmail.val59000mc.kit.exception.KitParseException;
import com.gmail.val59000mc.kit.table.KitTableRegistry;
import com.gmail.val59000mc.kit.table.entry.KitTableEntry;
import com.google.gson.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

import static com.gmail.val59000mc.kit.deserializer.stacktrace.KitDeserializeStacktraceManager.begin;
import static com.gmail.val59000mc.kit.deserializer.stacktrace.KitDeserializeStacktraceManager.end;

public class KitTableEntryDeserializer implements JsonDeserializer<KitTableEntry> {

    private final @NotNull KitsManager manager;

    public KitTableEntryDeserializer(@NotNull KitsManager manager) {
        this.manager = manager;
    }

    @Override
    public KitTableEntry deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonObject()) throw new KitParseException("is not a object");
        JsonObject object = json.getAsJsonObject();

        Class<? extends KitTableEntry> entryImpl;

        begin("id");
        {
            String id = context.deserialize(object.get("id"), String.class);
            if (id == null) throw new KitParseException("cannot be null");

            KitTableRegistry tableRegistry = manager.getRegistry().getTableRegistry();
            entryImpl = tableRegistry.getEntryImpl(id);
            if (entryImpl == null) throw new KitParseException(
                    "unknown entry with id '%s'. Available ids: %s",
                    id, tableRegistry.getEntryTypes()
            );
        }
        end();

        return context.deserialize(json, entryImpl);
    }

}
