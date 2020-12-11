package com.gmail.val59000mc.kit.deserializer.condition;

import com.gmail.val59000mc.kit.exception.KitParseException;
import com.gmail.val59000mc.kit.table.condition.KitTableCondition;
import com.gmail.val59000mc.kit.table.condition.KitTableConditions;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.gmail.val59000mc.kit.deserializer.stacktrace.KitDeserializeStacktraceManager.begin;
import static com.gmail.val59000mc.kit.deserializer.stacktrace.KitDeserializeStacktraceManager.end;

public class KitTableConditionsDeserializer implements JsonDeserializer<KitTableConditions> {

    @Override
    public KitTableConditions deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonArray()) throw new KitParseException("is not a array");
        JsonArray array = json.getAsJsonArray();

        List<KitTableCondition> conditions = new ArrayList<>();

        for (int i = 0; i < array.size(); i++) {
            begin(String.valueOf(i));
            {
                KitTableCondition condition = context.deserialize(array.get(i), KitTableCondition.class);
                if (condition == null) throw new KitParseException("cannot be null");

                conditions.add(condition);
            }
            end();
        }

        return new KitTableConditions(conditions);
    }

}
