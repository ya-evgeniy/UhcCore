package com.gmail.val59000mc.kit.deserializer;

import com.gmail.val59000mc.kit.KitsManager;
import com.gmail.val59000mc.kit.table.condition.KitTableCondition;
import com.gmail.val59000mc.kit.table.condition.KitTableConditions;
import com.google.gson.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class KitTableConditionsDeserializer implements JsonDeserializer<KitTableConditions> {

    private final @NotNull KitsManager manager;

    public KitTableConditionsDeserializer(@NotNull KitsManager manager) {
        this.manager = manager;
    }

    @Override
    public KitTableConditions deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonArray()) throw new JsonParseException("");
        JsonArray array = json.getAsJsonArray();

        List<KitTableCondition> conditions = new ArrayList<>();
        for (JsonElement element : array) {
            KitTableCondition condition = context.deserialize(element, KitTableCondition.class);
            if (condition == null) {
                throw new JsonParseException("");
            }
            conditions.add(condition);
        }

        return new KitTableConditions(conditions);
    }

}
