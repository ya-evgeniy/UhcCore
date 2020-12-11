package com.gmail.val59000mc.kit.deserializer.base;

import com.gmail.val59000mc.kit.exception.KitParseException;
import com.google.gson.*;

import java.lang.reflect.Type;

public class StringDeserializer implements JsonDeserializer<String> {

    @Override
    public String deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonNull()) return null;

        if (json.isJsonPrimitive()) {
            JsonPrimitive jsonPrimitive = json.getAsJsonPrimitive();
            if (!jsonPrimitive.isString()) throw new KitParseException("is not a string");

            return jsonPrimitive.getAsString();
        }
        else throw new KitParseException("is not a string");
    }

}
