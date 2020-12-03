package com.gmail.val59000mc.kit.deserializer;

import com.gmail.val59000mc.kit.exception.KitParseException;
import com.gmail.val59000mc.kit.table.item.KitItem;
import com.gmail.val59000mc.kit.table.item.KitItemGroup;
import com.gmail.val59000mc.kit.table.item.KitItemStack;
import com.google.gson.*;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.gmail.val59000mc.kit.deserializer.stacktrace.KitDeserializeStacktraceManager.begin;
import static com.gmail.val59000mc.kit.deserializer.stacktrace.KitDeserializeStacktraceManager.end;

public class KitItemDeserializer implements JsonDeserializer<KitItem> {

    @Override
    public KitItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonObject()) {
            return deserializeItemStack(json.getAsJsonObject(), context);
        }
        else if (json.isJsonArray()) {
            return deserializeItemGroup(json.getAsJsonArray(), context);
        }
        else if (json.isJsonNull()) {
            return KitItem.EMPTY;
        }

        throw new KitParseException("unsupported element type. Available types: [object, array, null]");
    }

    private KitItem deserializeItemStack(JsonObject object, JsonDeserializationContext context) {
        ItemStack stack = context.deserialize(object, ItemStack.class);
        if (stack == null) throw new KitParseException("cannot be null");

        return new KitItemStack(stack);
    }

    private KitItem deserializeItemGroup(JsonArray array, JsonDeserializationContext context) {
        List<KitItem> items = new ArrayList<>();

        for (int i = 0; i < array.size(); i++) {
            begin(String.valueOf(i));
            {
                KitItem item = context.deserialize(array.get(i), KitItem.class);
                if (item == null) throw new KitParseException("cannot be null");

                items.add(item);
            }
            end();
        }

        return new KitItemGroup(items);
    }

}
