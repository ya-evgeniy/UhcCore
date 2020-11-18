package com.gmail.val59000mc.kit.deserializer;

import com.gmail.val59000mc.kit.KitGroup;
import com.gmail.val59000mc.kit.KitsManager;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

public class KitGroupDeserializer implements JsonDeserializer<KitGroup> {

    private final @NotNull KitsManager manager;

    public KitGroupDeserializer(@NotNull KitsManager manager) {
        this.manager = manager;
    }

    @Override
    public KitGroup deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonPrimitive()) throw new JsonParseException("kit group expected string");
        String groupId = json.getAsString();

        KitGroup group = manager.getGroup(groupId);
        if (group == null) {
            throw new JsonParseException(String.format("kit group with id '%s' not found", groupId));
        }

        return group;
    }

}
