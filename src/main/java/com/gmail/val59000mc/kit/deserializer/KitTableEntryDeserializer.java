package com.gmail.val59000mc.kit.deserializer;

import com.gmail.val59000mc.kit.KitsManager;
import com.gmail.val59000mc.kit.table.entry.KitTableEntry;
import com.google.gson.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

public class KitTableEntryDeserializer implements JsonDeserializer<KitTableEntry> {

    private final @NotNull KitsManager manager;

    public KitTableEntryDeserializer(@NotNull KitsManager manager) {
        this.manager = manager;
    }

    @Override
    public KitTableEntry deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonObject()) throw new JsonParseException("");
        JsonObject object = json.getAsJsonObject();

        JsonElement idElement = object.get("id");
        if (idElement == null || !idElement.isJsonPrimitive()) throw new JsonParseException("Entry id is not present.");
        String type = idElement.getAsString();

        Class<? extends KitTableEntry> entryImpl = manager.getTableRegistry().getEntryImpl(type);
        if (entryImpl == null) throw new JsonParseException(String.format("Implementation for entry with type '%s' not found", type));

        return context.deserialize(json, entryImpl);
    }

}
