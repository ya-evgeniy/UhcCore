package com.gmail.val59000mc.kit.deserializer;

import com.gmail.val59000mc.kit.exception.KitParseException;
import com.gmail.val59000mc.kit.upgrade.KitUpgrade;
import com.gmail.val59000mc.kit.upgrade.KitUpgrades;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.gmail.val59000mc.kit.deserializer.stacktrace.KitDeserializeStacktraceManager.begin;
import static com.gmail.val59000mc.kit.deserializer.stacktrace.KitDeserializeStacktraceManager.end;

public class KitUpgradesDeserializer implements JsonDeserializer<KitUpgrades> {

    @Override
    public KitUpgrades deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonObject()) throw new KitParseException("is not a object");
        JsonObject object = json.getAsJsonObject();

        String id;
        List<KitUpgrade> upgrades = new ArrayList<>();

        begin("id");
        {
            id = context.deserialize(object.get("id"), String.class);
            if (id == null) throw new KitParseException("cannot be null");
        }
        end();

        begin("levels");
        {
            JsonElement levels = object.get("levels");

            if (!levels.isJsonArray()) throw new KitParseException("is not a array");
            JsonArray levelsArray = levels.getAsJsonArray();

            for (int i = 0; i < levelsArray.size(); i++) {
                begin(String.valueOf(i));
                {
                    KitUpgrade upgrade = context.deserialize(levelsArray.get(i), KitUpgrade.class);
                    upgrades.add(upgrade);
                }
                end();
            }
        }
        end();

        return new KitUpgrades(id, upgrades);
    }

}
