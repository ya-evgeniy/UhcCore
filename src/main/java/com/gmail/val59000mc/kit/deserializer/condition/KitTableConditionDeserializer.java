package com.gmail.val59000mc.kit.deserializer.condition;

import com.gmail.val59000mc.kit.KitsManager;
import com.gmail.val59000mc.kit.exception.KitParseException;
import com.gmail.val59000mc.kit.table.KitTableRegistry;
import com.gmail.val59000mc.kit.table.entry.KitTableEntry;
import com.gmail.val59000mc.kit.table.condition.KitTableCondition;
import com.google.gson.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

import static com.gmail.val59000mc.kit.deserializer.stacktrace.KitDeserializeStacktraceManager.begin;
import static com.gmail.val59000mc.kit.deserializer.stacktrace.KitDeserializeStacktraceManager.end;

public class KitTableConditionDeserializer implements JsonDeserializer<KitTableCondition> {

    private final @NotNull KitsManager manager;

    public KitTableConditionDeserializer(@NotNull KitsManager manager) {
        this.manager = manager;
    }

    @Override
    public KitTableCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonObject()) throw new KitParseException("is not a object");
        JsonObject object = json.getAsJsonObject();

        Class<? extends KitTableCondition> conditionImpl;

        begin("id");
        {
            String id = context.deserialize(object.get("id"), String.class);
            if (id == null) throw new KitParseException("cannot be null");

            KitTableRegistry tableRegistry = manager.getRegistry().getTableRegistry();
            conditionImpl = tableRegistry.getConditionImpl(id);
            if (conditionImpl == null) throw new KitParseException(
                    "unknown condition with id '%s'. Available ids: %s",
                    id, tableRegistry.getConditionTypes()
            );
        }
        end();

        return context.deserialize(json, conditionImpl);
    }

}
