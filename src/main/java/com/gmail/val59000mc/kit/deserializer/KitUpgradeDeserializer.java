package com.gmail.val59000mc.kit.deserializer;

import com.gmail.val59000mc.kit.exception.KitParseException;
import com.gmail.val59000mc.kit.upgrade.KitUpgrade;
import com.google.gson.*;

import java.lang.reflect.Type;

import static com.gmail.val59000mc.kit.deserializer.stacktrace.KitDeserializeStacktraceManager.begin;
import static com.gmail.val59000mc.kit.deserializer.stacktrace.KitDeserializeStacktraceManager.end;

public class KitUpgradeDeserializer implements JsonDeserializer<KitUpgrade> {

    @Override
    public KitUpgrade deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonObject()) throw new KitParseException("is not a object");
        JsonObject object = json.getAsJsonObject();

        Integer level;
        Integer cost;

        begin("level");
        {
            level = context.deserialize(object.get("level"), Integer.class);
            if (level == null) throw new KitParseException("cannot be null");
        }
        end();

        begin("cost");
        {
            cost = context.deserialize(object.get("cost"), Integer.class);
            if (cost == null) throw new KitParseException("cannot be null");
        }
        end();

        return new KitUpgrade(cost, level);
    }

}
