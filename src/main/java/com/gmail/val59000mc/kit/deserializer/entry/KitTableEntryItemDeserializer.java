package com.gmail.val59000mc.kit.deserializer.entry;

import com.gmail.val59000mc.kit.exception.KitParseException;
import com.gmail.val59000mc.kit.table.condition.KitTableConditions;
import com.gmail.val59000mc.kit.table.entry.KitTableEntryItem;
import com.gmail.val59000mc.kit.table.item.KitItem;
import com.google.gson.*;

import java.lang.reflect.Type;

import static com.gmail.val59000mc.kit.deserializer.stacktrace.KitDeserializeStacktraceManager.begin;
import static com.gmail.val59000mc.kit.deserializer.stacktrace.KitDeserializeStacktraceManager.end;

public class KitTableEntryItemDeserializer implements JsonDeserializer<KitTableEntryItem> {

    @Override
    public KitTableEntryItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonObject()) throw new KitParseException("is not a object");
        JsonObject object = json.getAsJsonObject();

        Integer weight;
        KitItem item;
        KitTableConditions conditions;

        begin("weight");
        {
            weight = context.deserialize(object.get("weight"), Integer.class);
            if (weight == null) weight = 1;
        }
        end();

        begin("item");
        {
            item = context.deserialize(object.get("item"), KitItem.class);
            if (item == null) throw new KitParseException("cannot be null");
        }
        end();

        begin("conditions");
        {
            conditions = context.deserialize(object.get("conditions"), KitTableConditions.class);
        }
        end();

        return new KitTableEntryItem(weight, item, conditions);
    }

}
