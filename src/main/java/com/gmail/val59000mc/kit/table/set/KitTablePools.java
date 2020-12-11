package com.gmail.val59000mc.kit.table.set;

import com.gmail.val59000mc.inventory.kit.KitDisplayItems;
import com.gmail.val59000mc.kit.KitsManager;
import com.gmail.val59000mc.kit.table.item.KitItem;
import com.gmail.val59000mc.kit.upgrade.PlayerKitUpgrades;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class KitTablePools {

    private final @NotNull List<KitTablePool> pools;

    public KitTablePools(@NotNull List<KitTablePool> pools) {
        this.pools = pools;
    }

    public void give(@NotNull Player player, @NotNull KitsManager manager, @NotNull Random random) {
        getItem(player, manager, random).give(player, manager, random);
    }

    public @NotNull KitItem getItem(@NotNull Player player, @NotNull KitsManager manager, @NotNull Random random) {
        KitItem result = KitItem.EMPTY;
        for (KitTablePool pool : pools) {
            result = result.combineWith(pool.getItem(player, manager, random));
        }
        return result;
    }

    public void appendDisplayItems(@NotNull KitDisplayItems items,
                                   @NotNull KitsManager manager,
                                   @NotNull UhcPlayer player,
                                   @NotNull PlayerKitUpgrades upgrades) {
        if (pools.isEmpty()) return;

        for (KitTablePool pool : pools) {
            pool.appendDisplayItems(items, manager, player, upgrades);
        }
    }
}
