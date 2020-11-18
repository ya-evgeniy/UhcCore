package com.gmail.val59000mc.configuration;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.kit.KitGroup;
import com.gmail.val59000mc.kit.KitsLoader;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.nio.file.Path;
import java.util.List;

public class KitsConfiguration {

    private final GameManager gameManager;

    private boolean enabled;

    private boolean databaseEnabled;
    private String url;

    private String ip;
    private int port;

    private String db;
    private String table;

    private String username;
    private String password;

    public KitsConfiguration(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public boolean load(JsonObject json) {
        if (json == null) return false;

        try {
            tryParseConfiguration(json);
            tryParseDatabase(json);
        }
        catch (Exception e) {
            e.printStackTrace();

            this.enabled = false;
            this.databaseEnabled = false;

            return false;
        }

        KitsLoader kitsLoader = new KitsLoader(gameManager.getKitsManager());
        try {
            Path pluginDirectoryPath = UhcCore.getPlugin().getDataFolder().toPath();
            kitsLoader.load(pluginDirectoryPath);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void tryParseConfiguration(JsonObject json) {
        this.enabled = json.get("enabled").getAsBoolean();
        if (!enabled) return;

        JsonElement groupsElement = json.get("groups");
        if (groupsElement == null || !groupsElement.isJsonArray()) throw new JsonParseException("");

        List<KitGroup> groups = new Gson().fromJson(groupsElement, new TypeToken<List<KitGroup>>() {}.getType());
        for (KitGroup group : groups) {
            gameManager.getKitsManager().registerGroup(group);
        }
    }

    private void tryParseDatabase(JsonObject json) {
        JsonElement databaseElement = json.get("database");
        if (databaseElement == null || !databaseElement.isJsonObject()) throw new JsonParseException("database is not a object");
        JsonObject object = databaseElement.getAsJsonObject();

        JsonElement enabledElement = object.get("enabled");
        if (enabledElement == null || !enabledElement.isJsonPrimitive()) throw new JsonParseException("database enabled is not a primitive");

        JsonElement urlElement = object.get("url");
        if (urlElement == null || !urlElement.isJsonPrimitive()) throw new JsonParseException("database url is not a primitive");

        JsonElement ipElement = object.get("ip");
        if (ipElement == null || !ipElement.isJsonPrimitive()) throw new JsonParseException("database ip is not a primitive");

        JsonElement portElement = object.get("port");
        if (portElement == null || !portElement.isJsonPrimitive()) throw new JsonParseException("database port is not a primitive");

        JsonElement dbElement = object.get("db");
        if (dbElement == null || !dbElement.isJsonPrimitive()) throw new JsonParseException("database db is not a primitive");

        JsonElement tableElement = object.get("table");
        if (tableElement == null || !tableElement.isJsonPrimitive()) throw new JsonParseException("database table is not a primitive");

        JsonElement usernameElement = object.get("username");
        if (usernameElement == null || !usernameElement.isJsonPrimitive()) throw new JsonParseException("database username is not a primitive");

        JsonElement passwordElement = object.get("password");
        if (passwordElement == null || !passwordElement.isJsonPrimitive()) throw new JsonParseException("database password is not a primitive");

        this.databaseEnabled = enabledElement.getAsBoolean();
        this.url = urlElement.getAsString();

        this.ip = ipElement.getAsString();
        this.port = portElement.getAsInt();

        this.db = dbElement.getAsString();
        this.table = tableElement.getAsString();

        this.username = usernameElement.getAsString();
        this.password = passwordElement.getAsString();
    }

    public boolean isEnabled() {
        return enabled;
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
