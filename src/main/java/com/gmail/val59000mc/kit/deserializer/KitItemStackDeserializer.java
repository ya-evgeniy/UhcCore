package com.gmail.val59000mc.kit.deserializer;

import com.gmail.val59000mc.utils.ItemStackJsonDeserializer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;

public class KitItemStackDeserializer implements JsonDeserializer<ItemStack> {

    @Override
    public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return ItemStackJsonDeserializer.deserializeItemStack(json);
    }

}
