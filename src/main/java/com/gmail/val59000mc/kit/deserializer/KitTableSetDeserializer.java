package com.gmail.val59000mc.kit.deserializer;

import com.gmail.val59000mc.kit.exception.KitParseException;
import com.gmail.val59000mc.kit.table.set.KitTablePools;
import com.gmail.val59000mc.kit.table.set.KitTableSet;
import com.google.gson.*;

import java.lang.reflect.Type;

import static com.gmail.val59000mc.kit.deserializer.stacktrace.KitDeserializeStacktraceManager.begin;
import static com.gmail.val59000mc.kit.deserializer.stacktrace.KitDeserializeStacktraceManager.end;

public class KitTableSetDeserializer implements JsonDeserializer<KitTableSet> {

    @Override
    public KitTableSet deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonObject()) throw new KitParseException("is not a object");
        JsonObject object = json.getAsJsonObject();

        KitTablePools pools;

        begin("pools");
        {
            pools = context.deserialize(object.get("pools"), KitTablePools.class);
            if (pools == null) throw new KitParseException("cannot be null");
        }
        end();

        return new KitTableSet(KitDeserializeState.getFullName(), pools);
    }

}
