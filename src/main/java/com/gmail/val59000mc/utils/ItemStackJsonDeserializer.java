package com.gmail.val59000mc.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ItemStackJsonDeserializer {
    
    public static ItemStack deserializeItemStack(@NotNull JsonElement element) throws JsonParseException {
        if (!element.isJsonObject()) throw new JsonParseException("element is not a Object");
        JsonObject object = element.getAsJsonObject();

        JsonElement typeElement = object.get("type");
        if (typeElement == null || !typeElement.isJsonPrimitive()) throw new JsonParseException("type of item is not a Primitive");

        Material type = Material.matchMaterial(typeElement.getAsString());
        if (type == null) throw new JsonParseException("Unknown item type: " + typeElement.getAsString());

        ItemStack stack = new ItemStack(type);
        ItemMeta meta;

        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();

            switch (key) {
                case "type": continue;
                case "count":
                    if (!value.isJsonPrimitive()) throw new JsonParseException("item count is not a primitive");
                    stack.setAmount(value.getAsInt());
                    break;
                case "unbreakable":
                    if (!value.isJsonPrimitive()) throw new JsonParseException("item unbreakable is not a primitive");
                    meta = stack.getItemMeta();
                    if (meta != null) {
                        meta.setUnbreakable(value.getAsBoolean());
                        stack.setItemMeta(meta);
                    }
                    break;
                case "damage":
                    if (!value.isJsonPrimitive()) throw new JsonParseException("item durability is not a primitive");
                    meta = stack.getItemMeta();
                    if (meta instanceof Damageable) {
                        ((Damageable) meta).setDamage(value.getAsInt());
                        stack.setItemMeta(meta);
                    }
                    break;
                case "display":
                    if (!value.isJsonPrimitive()) throw new JsonParseException("item display is not a primitive");
                    meta = stack.getItemMeta();
                    if (meta != null) {
                        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', value.getAsString()));
                        stack.setItemMeta(meta);
                    }
                    break;
                case "enchantments":
                    Map<Enchantment, Integer> enchantments = deserializeEnchantments(value);
                    stack.addUnsafeEnchantments(enchantments);
                    break;
                case "potion_effects":
                    List<PotionEffect> potionEffects = deserializePotionEffects(value);
                    meta = stack.getItemMeta();
                    if (meta instanceof PotionMeta) {
                        PotionMeta potionMeta = (PotionMeta) meta;
                        for (PotionEffect effect : potionEffects) {
                            potionMeta.addCustomEffect(effect, true);
                        }
                        stack.setItemMeta(meta);
                    }
                    break;
                case "attribute_modifiers":
                    List<Pair<Attribute, AttributeModifier>> modifiers = deserializeAttributeModifiers(value);
                    meta = stack.getItemMeta();
                    if (meta != null) {
                        for (Pair<Attribute, AttributeModifier> modifier : modifiers) {
                            meta.addAttributeModifier(modifier.getKey(), modifier.getValue());
                        }
                        stack.setItemMeta(meta);
                    }
                    break;
                default:
                    throw new JsonParseException("Unknown item stack parameter: " + key);
            }
        }

        return stack;
    }

    private static Map<Enchantment, Integer> deserializeEnchantments(@NotNull JsonElement element) throws JsonParseException {
        if (!element.isJsonArray()) throw new JsonParseException("enchantments element is not a array");
        Map<Enchantment, Integer> enchantments = new HashMap<>();

        JsonArray array = element.getAsJsonArray();
        arr_for: for (JsonElement enchantmentElement : array) {
            if (!enchantmentElement.isJsonObject()) throw new JsonParseException("enchantment element is not a Object");
            JsonObject object = enchantmentElement.getAsJsonObject();

            JsonElement idElement = object.get("id");
            if (idElement == null || !idElement.isJsonPrimitive()) throw new JsonParseException("Enchantment id is not a primitive");

            JsonElement lvlElement = object.get("lvl");
            if (lvlElement == null || !lvlElement.isJsonPrimitive()) throw new JsonParseException("Enchantment lvl is not a primitive");

            String id = idElement.getAsString();
            int lvl = lvlElement.getAsInt();

            for (Enchantment enchantment : Enchantment.values()) {
                if (enchantment.getKey().toString().equals(id)) {
                    enchantments.put(enchantment, lvl);
                    continue arr_for;
                }
            }

            throw new JsonParseException("Unknown enchantment id: " + id);
        }

        return enchantments;
    }

    public static List<PotionEffect> deserializePotionEffects(@NotNull JsonElement element) throws JsonParseException {
        if (!element.isJsonArray()) throw new JsonParseException("Potion Effects element is not a array");
        JsonArray array = element.getAsJsonArray();

        List<PotionEffect> result = new ArrayList<>();

        for (JsonElement potionEffect : array) {
            if (!potionEffect.isJsonObject()) throw new JsonParseException("Potion is not a Object");
            JsonObject object = potionEffect.getAsJsonObject();

            JsonElement idElement = object.get("id");
            if (idElement == null || !idElement.isJsonPrimitive()) throw new JsonParseException("Potion effect id is not a primitive");

            JsonElement durationElement = object.get("duration");
            if (durationElement == null || !durationElement.isJsonPrimitive()) throw new JsonParseException("Potion effect duration is not a primitive");

            JsonElement lvlElement = object.get("lvl");
            if (lvlElement == null || !lvlElement.isJsonPrimitive()) throw new JsonParseException("Potion effect lvl is not a primitive");

            JsonElement particlesElement = object.get("particles");
            boolean particles = particlesElement != null && !particlesElement.isJsonPrimitive() && particlesElement.getAsBoolean();

            String id = idElement.getAsString();
            int duration = durationElement.getAsInt();
            int lvl = lvlElement.getAsInt();

            PotionEffectType type = PotionEffectType.getByName(id);
            if (type == null) type = PotionEffectType.getByName(id.substring("minecraft:".length()));
            if (type == null) throw new JsonParseException("Unknown potion id: " + id);

            PotionEffect effect = new PotionEffect(type, duration, lvl, true, particles);
            result.add(effect);
        }

        return result;
    }

    public static List<Pair<Attribute, AttributeModifier>> deserializeAttributeModifiers(@NotNull JsonElement element) throws JsonParseException {
        if (!element.isJsonArray()) throw new JsonParseException("Attribute modifiers element is not a array");
        JsonArray array = element.getAsJsonArray();

        List<Pair<Attribute, AttributeModifier>> result = new ArrayList<>();

        for (JsonElement attributeElement : array) {
            if (!attributeElement.isJsonObject()) throw new JsonParseException("Attribute element is not a Object");
            JsonObject object = attributeElement.getAsJsonObject();

            JsonElement idElement = object.get("id");
            if (idElement == null || !idElement.isJsonPrimitive()) throw new JsonParseException("Attribute id is not a primitive");

            JsonElement amountElement = object.get("amount");
            if (amountElement == null || !amountElement.isJsonPrimitive()) throw new JsonParseException("Attribute amount is not a primitive");

            JsonElement operationElement = object.get("operation");
            if (operationElement == null || !operationElement.isJsonPrimitive()) throw new JsonParseException("Attribute operation is not a primitive");

            JsonElement slotElement = object.get("slot");
            if (slotElement != null && !slotElement.isJsonPrimitive()) throw new JsonParseException("Attribute slotElement is not a primitive");

            String id = idElement.getAsString();
            int amount = amountElement.getAsInt();
            String operationId = operationElement.getAsString();

            Attribute attribute;
            try {
                attribute = Attribute.valueOf(id.replaceAll("\\.", "_").toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new JsonParseException("Unknown attribute id: " + id);
            }

            AttributeModifier.Operation operation;
            try {
                operation = AttributeModifier.Operation.valueOf(operationId.toUpperCase());
            }
            catch (IllegalArgumentException e) {
                throw new JsonParseException("Unknown attribute operation: " + operationId);
            }

            if (slotElement != null) {
                String slotId = slotElement.getAsString();
                EquipmentSlot equipmentSlot;
                try {
                    equipmentSlot = EquipmentSlot.valueOf(slotId.toUpperCase());
                }
                catch (IllegalArgumentException e) {
                    throw new JsonParseException("Unknown attribute slot: " + slotId);
                }
                result.add(new Pair<>(attribute, new AttributeModifier(UUID.randomUUID(), id, amount, operation, equipmentSlot)));
            }
            else {
                result.add(new Pair<>(attribute, new AttributeModifier(id, amount, operation)));
            }
        }

        return result;
    }

}
