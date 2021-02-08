package com.gmail.val59000mc.kit.deserializer.condition;

import com.gmail.val59000mc.kit.exception.KitParseException;
import com.gmail.val59000mc.kit.table.condition.KitTableConditionPermission;
import com.gmail.val59000mc.kit.table.condition.KitTableConditionUpgradeLevel;
import com.google.gson.*;

import java.lang.reflect.Type;

import static com.gmail.val59000mc.kit.deserializer.stacktrace.KitDeserializeStacktraceManager.begin;
import static com.gmail.val59000mc.kit.deserializer.stacktrace.KitDeserializeStacktraceManager.end;

public class KitTableConditionPermissionDeserializer implements JsonDeserializer<KitTableConditionPermission> {

    @Override
    public KitTableConditionPermission deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonObject()) throw new KitParseException("is not a object");
        JsonObject object = json.getAsJsonObject();

        String permission;

        begin("permission");
        {
            permission = context.deserialize(object.get("permission"), String.class);
            if (permission == null) throw new KitParseException("cannot be null");
        }
        end();

        return new KitTableConditionPermission(permission);
    }

}
