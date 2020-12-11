package com.gmail.val59000mc.kit.upgrade;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class KitUpgrades {

    private final @NotNull String id;
    private final @NotNull List<KitUpgrade> levels;

    public KitUpgrades(@NotNull String id, @NotNull List<KitUpgrade> levels) {
        this.id = id;
        this.levels = levels;
    }

    public String getId() {
        return id;
    }

    public List<KitUpgrade> getLevels() {
        return levels;
    }

}
