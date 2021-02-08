package com.gmail.val59000mc.kit.table.condition;

import com.gmail.val59000mc.kit.KitsManager;
import com.gmail.val59000mc.kit.upgrade.PlayerKitUpgrades;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class KitTableConditionPermission implements KitTableCondition {

    public static final @NotNull String ID = "permission";

    private final @NotNull String permission;

    public KitTableConditionPermission(@NotNull String permission) {
        this.permission = permission;
    }

    @Override
    public @NotNull String getId() {
        return ID;
    }

    @Override
    public boolean check(@NotNull Player player, @NotNull KitsManager manager, @NotNull Random random) {
        return player.hasPermission(permission);
    }

    @Override
    public boolean checkForDisplay(@NotNull KitsManager manager, @NotNull UhcPlayer player, @NotNull PlayerKitUpgrades upgrades) {
        return true;
    }

}
