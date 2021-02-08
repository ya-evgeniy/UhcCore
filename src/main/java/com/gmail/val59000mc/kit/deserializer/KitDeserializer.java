package com.gmail.val59000mc.kit.deserializer;

import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.kit.Kit;
import com.gmail.val59000mc.kit.KitDisplayItem;
import com.gmail.val59000mc.kit.KitGroup;
import com.gmail.val59000mc.kit.KitsManager;
import com.gmail.val59000mc.kit.deserializer.stacktrace.KitDeserializeStacktraceManager;
import com.gmail.val59000mc.kit.exception.KitParseException;
import com.gmail.val59000mc.kit.table.entry.KitTableEntries;
import com.gmail.val59000mc.kit.upgrade.KitUpgrades;
import com.google.gson.*;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

import static com.gmail.val59000mc.kit.deserializer.stacktrace.KitDeserializeStacktraceManager.begin;
import static com.gmail.val59000mc.kit.deserializer.stacktrace.KitDeserializeStacktraceManager.end;

public class KitDeserializer implements JsonDeserializer<Kit> {

    @Override
    public Kit deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonObject()) throw new KitParseException("is not a object");
        JsonObject object = json.getAsJsonObject();

        String id = KitDeserializeState.getFullName();
        KitGroup group;
        KitDisplayItem display;
        @Nullable KitUpgrades upgrades;
        @Nullable KitTableEntries entries;

        begin("group");
        {
            group = context.deserialize(object.get("group"), KitGroup.class);
        }
        end();

        begin("display");
        {
            display = context.deserialize(object.get("display"), KitDisplayItem.class);
        }
        end();

        begin("upgrades");
        {
            upgrades = context.deserialize(object.get("upgrades"), KitUpgrades.class);
        }
        end();

        begin("entries");
        {
            entries = context.deserialize(object.get("entries"), KitTableEntries.class);
        }
        end();

        return new Kit(id, group, display, upgrades, entries);
    }

}
