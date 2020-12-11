package com.gmail.val59000mc.kit.table.condition;

import com.gmail.val59000mc.kit.KitsManager;
import com.gmail.val59000mc.kit.upgrade.PlayerKitUpgrades;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class KitTableConditions {

    private final @NotNull List<KitTableCondition> conditions;

    public KitTableConditions(@NotNull List<KitTableCondition> conditions) {
        this.conditions = conditions;
    }

    public boolean check(@NotNull Player player, @NotNull KitsManager manager, @NotNull Random random) {
        if (conditions.isEmpty()) return true;
        return conditions.stream().allMatch(condition -> condition.check(player, manager, random));
    }

    public boolean checkForDisplay(@NotNull KitsManager manager, @NotNull UhcPlayer player, @NotNull PlayerKitUpgrades upgrades) {
        if (conditions.isEmpty()) return true;
        return conditions.stream().allMatch(condition -> condition.checkForDisplay(manager, player, upgrades));
    }

}
