package com.gmail.val59000mc.kit.table.condition;

import com.gmail.val59000mc.kit.KitsManager;
import com.gmail.val59000mc.kit.upgrade.PlayerKitUpgrades;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public interface KitTableCondition {

    @NotNull String getId();

    boolean check(@NotNull Player player, @NotNull KitsManager manager, @NotNull Random random);

    boolean checkForDisplay(@NotNull KitsManager manager, @NotNull UhcPlayer player, @NotNull PlayerKitUpgrades upgrades);

}
