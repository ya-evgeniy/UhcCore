package com.gmail.val59000mc.kit.deserializer;

import com.gmail.val59000mc.kit.table.item.KitItem;
import com.gmail.val59000mc.kit.table.item.KitItemGroup;
import com.gmail.val59000mc.kit.table.item.KitItemStack;
import com.google.gson.*;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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

        throw new JsonParseException("");
    }

    private KitItem deserializeItemStack(JsonObject object, JsonDeserializationContext context) {
        ItemStack stack = context.deserialize(object, ItemStack.class);
        return new KitItemStack(stack);
    }

    private KitItem deserializeItemGroup(JsonArray array, JsonDeserializationContext context) {
        List<KitItem> items = new ArrayList<>();
        for (JsonElement element : array) {
            KitItem item = context.deserialize(element, KitItem.class);
            items.add(item);
        }
        return new KitItemGroup(items);
    }

}
