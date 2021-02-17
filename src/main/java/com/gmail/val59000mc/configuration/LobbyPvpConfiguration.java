package com.gmail.val59000mc.configuration;

import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.lobby.pvp.zone.RectangleZone;
import com.gmail.val59000mc.lobby.pvp.zone.SphereZone;
import com.gmail.val59000mc.lobby.pvp.zone.Zone;
import com.gmail.val59000mc.utils.ItemStackJsonDeserializer;
import com.gmail.val59000mc.utils.equipment.Equipment;
import com.gmail.val59000mc.utils.equipment.EquipmentContainer;
import com.gmail.val59000mc.utils.equipment.EquipmentSlot;
import com.gmail.val59000mc.utils.equipment.EquipmentUtils;
import com.gmail.val59000mc.utils.equipment.slot.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

public class LobbyPvpConfiguration {

    private final GameManager gameManager;

    private boolean enabled;
    private List<Zone> zones;

    private EquipmentContainer equipmentContainer;

    private boolean useCustomRespawnLocation = false;
    private Location customRespawnLocation;

    private List<PotionEffect> effects = new ArrayList<>();

    private boolean databaseEnabled;
    private String url;
    private String ip;
    private int port;
    private String db;
    private String table;
    private String username;
    private String password;

    public LobbyPvpConfiguration(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public boolean load(JsonObject json) {
        if (json == null) return false;

        try {
            tryParseConfiguration(json);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            this.enabled = false;
            return false;
        }
    }

    private void tryParseConfiguration(JsonObject json) {
        this.enabled = json.get("enabled").getAsBoolean();
        if (!this.enabled) return;

        tryParseZones(json.get("zones"));
        tryParseEquipment(json.get("equipment"));

        useCustomRespawnLocation = json.get("use_custom_respawn_location").getAsBoolean();
        if (useCustomRespawnLocation) {
            JsonObject customRespawnLocationObject = json.get("custom_respawn_location").getAsJsonObject();

            double x = customRespawnLocationObject.get("x").getAsDouble() + .5;
            double y = customRespawnLocationObject.get("y").getAsDouble();
            double z = customRespawnLocationObject.get("z").getAsDouble() + .5;

            float pitch = 0f;
            float yaw = 0f;

            try {
                pitch = customRespawnLocationObject.get("pitch").getAsFloat();
                yaw = customRespawnLocationObject.get("yaw").getAsFloat();
            }
            catch (Exception ignore) {
            }

            customRespawnLocation = new Location(null, x, y, z, yaw, pitch);
        }

        JsonElement effectsElement = json.get("effects");
        try {
            if (effectsElement != null) this.effects = ItemStackJsonDeserializer.deserializePotionEffects(effectsElement);
        }
        catch (JsonParseException e) {
            e.printStackTrace();
        }

        final JsonElement databaseElement = json.get("database");
        if (databaseElement.isJsonObject()) {
            final JsonObject databaseObject = databaseElement.getAsJsonObject();

            this.databaseEnabled = databaseObject.get("enabled").getAsBoolean();
            if (!this.databaseEnabled) return;

            this.url = databaseObject.get("url").getAsString();
            this.ip = databaseObject.get("ip").getAsString();
            this.port = databaseObject.get("port").getAsInt();
            this.db = databaseObject.get("db").getAsString();
            this.table = databaseObject.get("table").getAsString();
            this.username = databaseObject.get("username").getAsString();
            this.password = databaseObject.get("password").getAsString();
        }
    }

    private void tryParseZones(JsonElement element) {
        zones = new ArrayList<>();

        JsonArray zones = element.getAsJsonArray();
        for (JsonElement zoneElement : zones) {
            JsonObject zoneObject = zoneElement.getAsJsonObject();

            String type = zoneObject.get("type").getAsString();
            JsonObject parameters = zoneObject.get("parameters").getAsJsonObject();

            switch (type) {
                case "rectangle":
                    int x1 = parameters.get("x1").getAsInt();
                    int y1 = parameters.get("y1").getAsInt();
                    int z1 = parameters.get("z1").getAsInt();

                    int x2 = parameters.get("x2").getAsInt();
                    int y2 = parameters.get("y2").getAsInt();
                    int z2 = parameters.get("z2").getAsInt();

                    this.zones.add(new RectangleZone(
                            x1, y1, z1,
                            x2, y2, z2
                    ));
                    break;
                case "sphere":
                    int x = parameters.get("x").getAsInt();
                    int y = parameters.get("y").getAsInt();
                    int z = parameters.get("z").getAsInt();

                    int r = parameters.get("r").getAsInt();

                    this.zones.add(new SphereZone(
                            x, y, z,
                            r
                    ));
                    break;
            }
        }
    }

    private void tryParseEquipment(JsonElement element) {
        List<Equipment> equipments = new ArrayList<>();

        JsonArray equipment = element.getAsJsonArray();
        for (JsonElement equipmentElement : equipment) {
            JsonObject equipmentObject = equipmentElement.getAsJsonObject();

            final String id = equipmentObject.get("id").getAsString();
            final boolean slotMutable = equipmentObject.get("slot_mutable").getAsBoolean();

            String slot = equipmentObject.get("slot").getAsString();
            EquipmentSlot equipmentSlot = EquipmentUtils.from(slot);

            JsonObject itemObject = equipmentObject.get("item").getAsJsonObject();
            try {
                ItemStack stack = ItemStackJsonDeserializer.deserializeItemStack(itemObject);
                equipments.add(new Equipment(
                        id, slotMutable, equipmentSlot, stack
                ));
            }
            catch (JsonParseException e) {
                e.printStackTrace();
            }
        }

        equipmentContainer = new EquipmentContainer(equipments);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public List<Zone> getZones() {
        return zones;
    }

    public EquipmentContainer getEquipmentContainer() {
        return equipmentContainer;
    }

    public boolean isUseCustomRespawnLocation() {
        return useCustomRespawnLocation;
    }

    public Location getCustomRespawnLocation() {
        return customRespawnLocation;
    }

    public List<PotionEffect> getEffects() {
        return effects;
    }

    public boolean isDatabaseEnabled() {
        return databaseEnabled;
    }

    public String getUrl() {
        return url;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getDb() {
        return db;
    }

    public String getTable() {
        return table;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
