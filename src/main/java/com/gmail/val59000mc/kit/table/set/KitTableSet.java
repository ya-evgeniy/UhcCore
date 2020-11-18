package com.gmail.val59000mc.kit.table.set;

import com.gmail.val59000mc.inventory.kit.KitDisplayItems;
import com.gmail.val59000mc.kit.KitsManager;
import com.gmail.val59000mc.kit.table.item.KitItem;
import com.gmail.val59000mc.kit.upgrade.PlayerKitUpgrades;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class KitTableSet {

    private final @NotNull String id;
    private final @NotNull KitTablePools pools;

    public KitTableSet(@NotNull String id, @NotNull KitTablePools pools) {
        this.id = id;
        this.pools = pools;
    }

    public @NotNull String getId() {
        return id;
    }

    public @NotNull KitTablePools getPools() {
        return pools;
    }

    public void give(@NotNull Player player, @NotNull KitsManager manager, @NotNull Random random) {
        pools.give(player, manager, random);
    }

    public @NotNull KitItem getItem(@NotNull Player player, @NotNull KitsManager manager, @NotNull Random random) {
        return pools.getItem(player, manager, random);
    }

    public void appendDisplayItems(@NotNull KitDisplayItems items,
                                   @NotNull KitsManager manager,
                                   @NotNull UhcPlayer player,
                                   @NotNull PlayerKitUpgrades upgrades) {
        pools.appendDisplayItems(items, manager, player, upgrades);
    }
}
