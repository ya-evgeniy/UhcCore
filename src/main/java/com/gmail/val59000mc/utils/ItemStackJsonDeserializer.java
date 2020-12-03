package com.gmail.val59000mc.utils;

import com.google.gson.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffectTypeWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemStackJsonDeserializer {

    public static final Map<String, PotionEffectType> POTION_EFFECT_TYPE_BY_MC_NAME;
    public static final Map<String, Enchantment> ENCHANTMENT_BY_MC_NAME;

    public static final Pattern DURATION_PATTERN = Pattern.compile("(((?<d>\\d+)[d])?((?<h>\\d+)[h])?((?<m>\\d+)[m])?((?<s>\\d+)[s])?((?<t>\\d+)[t])?)");

    static {
        Map<String, PotionEffectType> potions = new HashMap<>();
        potions.put("speed", PotionEffectType.SPEED);
        potions.put("slowness", PotionEffectType.SLOW);
        potions.put("haste", PotionEffectType.FAST_DIGGING);
        potions.put("mining_fatigue", PotionEffectType.SLOW_DIGGING);
        potions.put("strength", PotionEffectType.INCREASE_DAMAGE);
        potions.put("instant_health", PotionEffectType.HEAL);
        potions.put("instant_damage", PotionEffectType.HARM);
        potions.put("jump_boost", PotionEffectType.JUMP);
        potions.put("nausea", PotionEffectType.CONFUSION);
        potions.put("regeneration", PotionEffectType.REGENERATION);
        potions.put("resistance", PotionEffectType.DAMAGE_RESISTANCE);
        potions.put("fire_resistance", PotionEffectType.FIRE_RESISTANCE);
        potions.put("water_breathing", PotionEffectType.WATER_BREATHING);
        potions.put("invisibility", PotionEffectType.INVISIBILITY);
        potions.put("blindness", PotionEffectType.BLINDNESS);
        potions.put("night_vision", PotionEffectType.NIGHT_VISION);
        potions.put("hunger", PotionEffectType.HUNGER);
        potions.put("weakness", PotionEffectType.WEAKNESS);
        potions.put("poison", PotionEffectType.POISON);
        potions.put("wither", PotionEffectType.WITHER);
        potions.put("health_boost", PotionEffectType.HEALTH_BOOST);
        potions.put("absorption", PotionEffectType.ABSORPTION);
        potions.put("saturation", PotionEffectType.SATURATION);
        potions.put("glowing", PotionEffectType.GLOWING);
        potions.put("levitation", PotionEffectType.LEVITATION);
        potions.put("luck", PotionEffectType.LUCK);
        potions.put("unluck", PotionEffectType.UNLUCK);
        potions.put("slow_falling", PotionEffectType.SLOW_FALLING);
        potions.put("conduit_power", PotionEffectType.CONDUIT_POWER);
        potions.put("dolphins_grace", PotionEffectType.DOLPHINS_GRACE);
        potions.put("bad_omen", PotionEffectType.BAD_OMEN);
        potions.put("hero_of_the_village", PotionEffectType.HERO_OF_THE_VILLAGE);
        POTION_EFFECT_TYPE_BY_MC_NAME = Collections.unmodifiableMap(potions);

        Map<String, Enchantment> enchantments = new HashMap<>();
        for (Enchantment enchantment : Enchantment.values()) {
            enchantments.put(enchantment.getKey().getKey(), enchantment);
        }
        ENCHANTMENT_BY_MC_NAME = Collections.unmodifiableMap(enchantments);
    }

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
                    meta = stack.getItemMeta();
                    if (meta instanceof EnchantmentStorageMeta) {
                        EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) meta;
                        for (Map.Entry<Enchantment, Integer> enchantmentEntry : enchantments.entrySet()) {
                            enchantmentStorageMeta.addStoredEnchant(enchantmentEntry.getKey(), enchantmentEntry.getValue(), true);
                        }
                        stack.setItemMeta(meta);
                    }
                    else {
                        stack.addUnsafeEnchantments(enchantments);
                    }
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
                case "effects":
                    List<PotionEffect> effects = deserializeEffects(value);
                    meta = stack.getItemMeta();
                    if (meta instanceof SuspiciousStewMeta) {
                        SuspiciousStewMeta suspiciousStewMeta = (SuspiciousStewMeta) meta;
                        for (PotionEffect effect : effects) suspiciousStewMeta.addCustomEffect(effect, true);
                        stack.setItemMeta(suspiciousStewMeta);
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
        for (JsonElement enchantmentElement : array) {
            if (!enchantmentElement.isJsonObject()) throw new JsonParseException("enchantment element is not a Object");
            JsonObject object = enchantmentElement.getAsJsonObject();

            JsonElement idElement = object.get("id");
            if (idElement == null || !idElement.isJsonPrimitive())
                throw new JsonParseException("Enchantment id is not a primitive");

            JsonElement lvlElement = object.get("lvl");
            if (lvlElement == null || !lvlElement.isJsonPrimitive())
                throw new JsonParseException("Enchantment lvl is not a primitive");

            String id = idElement.getAsString();
            int lvl = lvlElement.getAsInt();

            String modifiedId = id;
            if (modifiedId.startsWith("minecraft:")) modifiedId = modifiedId.substring("minecraft:".length());

            Enchantment enchantment = ENCHANTMENT_BY_MC_NAME.get(modifiedId);
            if (enchantment == null)
                throw new JsonParseException("Unknown enchantment id: '" + id + "'. Available enchantments: " + ENCHANTMENT_BY_MC_NAME.keySet());

            enchantments.put(enchantment, lvl);
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

            JsonElement particlesElement = object.get("show_particles");
            boolean particles = particlesElement == null || particlesElement.isJsonPrimitive() && particlesElement.getAsBoolean();

            String id = idElement.getAsString();
            int duration = deserializeDuration(durationElement.getAsJsonPrimitive());
            int lvl = lvlElement.getAsInt();

            String modifiedId = id;
            if (modifiedId.startsWith("minecraft:")) modifiedId = modifiedId.substring("minecraft:".length());

            PotionEffectType type = POTION_EFFECT_TYPE_BY_MC_NAME.get(modifiedId);
            if (type == null) throw new JsonParseException("Unknown potion id: '" + id + "'. Available effects: " + POTION_EFFECT_TYPE_BY_MC_NAME.keySet());

            PotionEffect effect = new PotionEffect(type, duration, lvl, true, particles);
            result.add(effect);
        }

        return result;
    }

    public static List<PotionEffect> deserializeEffects(@NotNull JsonElement element) throws JsonParseException {
        if (!element.isJsonArray()) throw new JsonParseException("Effects element is not a array");
        JsonArray array = element.getAsJsonArray();

        List<PotionEffect> result = new ArrayList<>();

        for (JsonElement potionEffect : array) {
            if (!potionEffect.isJsonObject()) throw new JsonParseException("Potion is not a Object");
            JsonObject object = potionEffect.getAsJsonObject();

            JsonElement idElement = object.get("id");
            if (idElement == null || !idElement.isJsonPrimitive()) throw new JsonParseException("Effect id is not a primitive");

            JsonElement durationElement = object.get("duration");
            if (durationElement == null || !durationElement.isJsonPrimitive()) throw new JsonParseException("Effect duration is not a primitive");

            String id = idElement.getAsString();
            int duration = deserializeDuration(durationElement.getAsJsonPrimitive());

            PotionEffectType type = POTION_EFFECT_TYPE_BY_MC_NAME.get(id);
            if (type == null) type = POTION_EFFECT_TYPE_BY_MC_NAME.get(id.substring("minecraft:".length()));
            if (type == null) throw new JsonParseException("Unknown potion id: " + id);

            PotionEffect effect = new PotionEffect(type, duration, 1, true, true);
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

    private static int deserializeDuration(JsonPrimitive primitive) {
        if (primitive.isNumber()) return primitive.getAsInt();
        String stringDuration = primitive.getAsString();
        Matcher durationMatcher = DURATION_PATTERN.matcher(stringDuration);
        if (durationMatcher.find()) {
            String strDays = durationMatcher.group("d");
            String strHours = durationMatcher.group("h");
            String strMinutes = durationMatcher.group("m");
            String strSeconds = durationMatcher.group("s");
            String strTicks = durationMatcher.group("t");

            int days = strDays == null ? 0 : Integer.parseInt(strDays);
            int hours = strHours == null ? 0 : Integer.parseInt(strHours);
            int minutes = strMinutes == null ? 0 : Integer.parseInt(strMinutes);
            int seconds = strSeconds == null ? 0 : Integer.parseInt(strSeconds);
            int ticks = strTicks == null ? 0 : Integer.parseInt(strTicks);

            return ((((days * 24 + hours) * 60 + minutes) * 60 + seconds) * 20 + ticks);
        }

        throw new JsonParseException(String.format("duration incorrect pattern: '%s'. Use [<days>d][<hours>h][<minutes>m][<seconds>s][<ticks>t]", stringDuration));
    }

}
