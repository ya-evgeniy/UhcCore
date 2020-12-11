package com.gmail.val59000mc.kit.deserializer.base;

import com.gmail.val59000mc.kit.exception.KitParseException;
import com.google.gson.*;

import java.lang.reflect.Type;

public class IntDeserializer implements JsonDeserializer<Integer> {

    @Override
    public Integer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonNull()) return null;

        if (json.isJsonPrimitive()) {
            JsonPrimitive jsonPrimitive = json.getAsJsonPrimitive();
            if (!jsonPrimitive.isNumber()) throw new KitParseException("is not a int");

            return jsonPrimitive.getAsInt();
        }
        else throw new KitParseException("is not a int");
    }

}
