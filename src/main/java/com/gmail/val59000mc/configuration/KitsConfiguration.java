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

    public KitsConfiguration(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public boolean load(JsonObject json) {
        if (json == null) return false;

        try {
            tryParseConfiguration(json);
        }
        catch (Exception e) {
            e.printStackTrace();
            this.enabled = false;
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

    public boolean isEnabled() {
        return enabled;
    }

}
