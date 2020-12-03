package com.gmail.val59000mc.kit.deserializer.entry;

import com.gmail.val59000mc.kit.KitsLoader;
import com.gmail.val59000mc.kit.KitsManager;
import com.gmail.val59000mc.kit.deserializer.KitDeserializeState;
import com.gmail.val59000mc.kit.exception.KitParseException;
import com.gmail.val59000mc.kit.table.condition.KitTableConditions;
import com.gmail.val59000mc.kit.table.entry.KitTableEntrySet;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.gmail.val59000mc.kit.deserializer.stacktrace.KitDeserializeStacktraceManager.begin;
import static com.gmail.val59000mc.kit.deserializer.stacktrace.KitDeserializeStacktraceManager.end;

public class KitTableEntrySetDeserializer implements JsonDeserializer<KitTableEntrySet> {

    private final KitsManager manager;

    public KitTableEntrySetDeserializer(KitsManager manager) {
        this.manager = manager;
    }

    @Override
    public KitTableEntrySet deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonObject()) throw new KitParseException("is not a object");
        JsonObject object = json.getAsJsonObject();

        Integer weight;
        List<String> references = new ArrayList<>();
        KitTableConditions conditions;

        begin("weight");
        {
            weight = context.deserialize(object.get("weight"), Integer.class);
            if (weight == null) weight = 1;
        }
        end();

        begin("references");
        {
            JsonElement referencesElement = object.get("references");
            if (referencesElement == null || !referencesElement.isJsonArray()) throw new KitParseException("is not a array");

            JsonArray referencesArray = referencesElement.getAsJsonArray();
            for (int i = 0; i < referencesArray.size(); i++) {
                begin(String.valueOf(i));
                {
                    String reference = context.deserialize(referencesArray.get(i), String.class);
                    if (reference == null) throw new KitParseException("cannot be null");

                    Path referencePath = KitsLoader.resolve(
                            KitDeserializeState.getWorkingDirectory(),
                            KitDeserializeState.getParentDirectoryPath(),
                            Paths.get(reference)
                    );

                    if (!KitsLoader.checkPathAccess(KitDeserializeState.getWorkingDirectory(), referencePath)) {
                        throw new KitParseException("access denied for reference '%s'", reference);
                    }

                    Path fixedReferencePath = KitDeserializeState.getWorkingDirectory().relativize(referencePath);
                    references.add(fixedReferencePath.toString());
                    manager.getRegistry().getSetReferences().add(fixedReferencePath);
                }
                end();
            }
        }
        end();

        begin("conditions");
        {
            conditions = context.deserialize(object.get("conditions"), KitTableConditions.class);
        }
        end();

        return new KitTableEntrySet(weight, references, conditions);
    }

}
