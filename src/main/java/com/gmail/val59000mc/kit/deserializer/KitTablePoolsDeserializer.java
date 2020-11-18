package com.gmail.val59000mc.kit.deserializer;

import com.gmail.val59000mc.kit.table.set.KitTablePool;
import com.gmail.val59000mc.kit.table.set.KitTablePools;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class KitTablePoolsDeserializer implements JsonDeserializer<KitTablePools> {

    private static final Type LIST_POOLS_TYPE = new TypeToken<List<KitTablePool>>() {}.getType();

    @Override
    public KitTablePools deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        List<KitTablePool> pools = context.deserialize(json, LIST_POOLS_TYPE);
        return new KitTablePools(pools);
    }

}
