package com.gmail.val59000mc.kit;

import com.gmail.val59000mc.kit.upgrade.PlayerKitUpgrades;
import org.jetbrains.annotations.NotNull;

public class PlayerKit {

    private PlayerKitUpgrades upgrades = new PlayerKitUpgrades();
    private boolean loaded = false;

    public @NotNull PlayerKitUpgrades getUpgrades() {
        return upgrades;
    }

    public boolean isLoaded() {
        return loaded;
    }

}
