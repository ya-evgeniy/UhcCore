package com.gmail.val59000mc.kit.table.entry;

import com.gmail.val59000mc.inventory.kit.KitDisplayItems;
import com.gmail.val59000mc.kit.KitsManager;
import com.gmail.val59000mc.kit.table.condition.KitTableConditions;
import com.gmail.val59000mc.kit.table.item.KitItem;
import com.gmail.val59000mc.kit.upgrade.PlayerKitUpgrades;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Random;

public interface KitTableEntry {

    @NotNull String getId();

    int getWeight();

    @Nullable KitTableConditions getConditions();

    void give(@NotNull Player player, @NotNull KitsManager manager, @NotNull Random random);

    @NotNull KitItem getItem(@NotNull Player player, @NotNull KitsManager manager, @NotNull Random random);

    boolean checkCondition(@NotNull Player player, @NotNull KitsManager manager, @NotNull Random random);

    void appendDisplayItems(@NotNull KitDisplayItems items,
                            @NotNull KitsManager manager,
                            @NotNull UhcPlayer player,
                            @NotNull PlayerKitUpgrades upgrades);

    boolean checkConditionForDisplay(@NotNull KitsManager manager,
                                     @NotNull UhcPlayer player,
                                     @NotNull PlayerKitUpgrades upgrades);

}
