package com.gmail.val59000mc.kit.deserializer.condition;

import com.gmail.val59000mc.kit.exception.KitParseException;
import com.gmail.val59000mc.kit.table.condition.KitTableConditionUpgradeLevel;
import com.google.gson.*;

import java.lang.reflect.Type;

import static com.gmail.val59000mc.kit.deserializer.stacktrace.KitDeserializeStacktraceManager.begin;
import static com.gmail.val59000mc.kit.deserializer.stacktrace.KitDeserializeStacktraceManager.end;

public class KitTableConditionUpgradeLevelDeserializer implements JsonDeserializer<KitTableConditionUpgradeLevel> {

    @Override
    public KitTableConditionUpgradeLevel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonObject()) throw new KitParseException("is not a object");
        JsonObject object = json.getAsJsonObject();

        String upgradeId;
        Integer level;

        begin("upgrade_id");
        {
            upgradeId = context.deserialize(object.get("upgrade_id"), String.class);
            if (upgradeId == null) throw new KitParseException("cannot be null");
        }
        end();

        begin("upgrade_level");
        {
            level = context.deserialize(object.get("upgrade_level"), Integer.class);
            if (level == null) throw new KitParseException("cannot be null");
        }
        end();

        return new KitTableConditionUpgradeLevel(upgradeId, level);
    }

}
