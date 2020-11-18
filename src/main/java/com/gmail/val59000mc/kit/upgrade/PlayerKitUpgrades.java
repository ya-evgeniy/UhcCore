package com.gmail.val59000mc.kit.upgrade;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class PlayerKitUpgrades {

    private final Map<String, Integer> levelById = new HashMap<>();

    public int getLevel(@NotNull String id) {
        return this.levelById.getOrDefault(id, 0);
    }

    public int incrementLevel(@NotNull String id) {
        int newValue = getLevel(id) + 1;
        this.levelById.put(id, newValue);
        return newValue;
    }

    public void setLevel(@NotNull String id, int value) {
        this.levelById.put(id, value);
    }

}
