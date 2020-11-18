package com.gmail.val59000mc.kit.deserializer;

import com.gmail.val59000mc.kit.KitsManager;
import com.gmail.val59000mc.kit.table.entry.KitTableEntry;
import com.gmail.val59000mc.kit.table.condition.KitTableCondition;
import com.google.gson.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

public class KitTableConditionDeserializer implements JsonDeserializer<KitTableCondition> {

    private final @NotNull KitsManager manager;

    public KitTableConditionDeserializer(@NotNull KitsManager manager) {
        this.manager = manager;
    }

    @Override
    public KitTableCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonObject()) throw new JsonParseException("");
        JsonObject object = json.getAsJsonObject();

        JsonElement idElement = object.get("id");
        if (idElement == null || !idElement.isJsonPrimitive()) throw new JsonParseException("Condition id is not a primitive");
        String type = idElement.getAsString();

        Class<? extends KitTableCondition> conditionImpl = manager.getTableRegistry().getConditionImpl(type);
        if (conditionImpl == null) throw new JsonParseException("");

        return context.deserialize(json, conditionImpl);
    }

}
