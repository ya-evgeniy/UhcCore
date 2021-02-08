package com.gmail.val59000mc.kit.deserializer;

import com.gmail.val59000mc.kit.exception.KitParseException;
import com.gmail.val59000mc.kit.table.set.KitTablePool;
import com.gmail.val59000mc.kit.table.set.KitTablePools;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.gmail.val59000mc.kit.deserializer.stacktrace.KitDeserializeStacktraceManager.begin;
import static com.gmail.val59000mc.kit.deserializer.stacktrace.KitDeserializeStacktraceManager.end;

public class KitTablePoolsDeserializer implements JsonDeserializer<KitTablePools> {

    @Override
    public KitTablePools deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonArray()) throw new KitParseException("is not a array");
        JsonArray array = json.getAsJsonArray();

        List<KitTablePool> pools = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            begin(String.valueOf(i));
            {
                KitTablePool pool = context.deserialize(array.get(i), KitTablePool.class);
                if (pool == null) throw new KitParseException("cannot be null");

                pools.add(pool);
            }
            end();
        }

        return new KitTablePools(pools);
    }

}
