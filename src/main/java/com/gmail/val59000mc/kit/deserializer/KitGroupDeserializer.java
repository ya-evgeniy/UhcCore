package com.gmail.val59000mc.kit.deserializer;

import com.gmail.val59000mc.kit.KitGroup;
import com.gmail.val59000mc.kit.KitsManager;
import com.gmail.val59000mc.kit.exception.KitParseException;
import com.google.gson.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

public class KitGroupDeserializer implements JsonDeserializer<KitGroup> {

    private final @NotNull KitsManager manager;

    public KitGroupDeserializer(@NotNull KitsManager manager) {
        this.manager = manager;
    }

    @Override
    public KitGroup deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonNull()) {
            return manager.getDefaultGroup();
        }
        else if (json.isJsonPrimitive()) {
            JsonPrimitive groupPrimitive = json.getAsJsonPrimitive();
            if (groupPrimitive.isString()) {
                String groupId = groupPrimitive.getAsString();
                KitGroup group = manager.getGroup(groupId);

                if (group == null) throw new KitParseException("group with id '%s' not found", groupId);
                return group;
            }
            else throw new KitParseException("is not a string");
        }
        else throw new KitParseException("is not a primitive");
    }

}
