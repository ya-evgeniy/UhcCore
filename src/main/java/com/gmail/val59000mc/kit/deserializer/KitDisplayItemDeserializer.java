package com.gmail.val59000mc.kit.deserializer;

import com.gmail.val59000mc.kit.KitDisplayItem;
import com.gmail.val59000mc.kit.exception.KitParseException;
import com.google.gson.*;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;

import static com.gmail.val59000mc.kit.deserializer.stacktrace.KitDeserializeStacktraceManager.begin;
import static com.gmail.val59000mc.kit.deserializer.stacktrace.KitDeserializeStacktraceManager.end;

public class KitDisplayItemDeserializer implements JsonDeserializer<KitDisplayItem> {

    @Override
    public KitDisplayItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonObject()) throw new KitParseException("is not a object");
        JsonObject object = json.getAsJsonObject();

        ItemStack item;

        boolean hasTitle;
        String title;

        boolean hasDescription = false;
        Integer descriptionLength = null;
        String description = null;

        begin("item");
        {
            item = context.deserialize(object.get("item"), ItemStack.class);
            if (item == null) throw new KitParseException("cannot be null");
        }
        end();

        begin("title");
        {
            title = context.deserialize(object.get("title"), String.class);
            hasTitle = title != null;
        }
        end();

        begin("description");
        {
            JsonElement descriptionElement = object.get("description");
            if (descriptionElement != null && !descriptionElement.isJsonNull()) {
                if (!descriptionElement.isJsonObject()) throw new KitParseException("is not a object");
                JsonObject descriptionObject = descriptionElement.getAsJsonObject();

                begin("length");
                {
                    descriptionLength = context.deserialize(descriptionObject.get("length"), Integer.class);
                    if (descriptionLength == null) throw new KitParseException("cannot be null");
                }
                end();

                begin("text");
                {
                    description = context.deserialize(descriptionObject.get("text"), String.class);
                    if (description == null) throw new KitParseException("cannot be null");
                }
                end();

                hasDescription = true;
            }
        }
        end();

        return new KitDisplayItem(item, hasTitle, title, hasDescription, descriptionLength == null ? 0 : descriptionLength, description);
    }

}
