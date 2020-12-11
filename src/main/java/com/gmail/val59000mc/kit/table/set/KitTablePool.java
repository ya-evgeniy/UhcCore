package com.gmail.val59000mc.kit.table.set;

import com.gmail.val59000mc.inventory.kit.KitDisplayItems;
import com.gmail.val59000mc.kit.KitsManager;
import com.gmail.val59000mc.kit.table.entry.KitTableEntries;
import com.gmail.val59000mc.kit.table.entry.KitTableEntry;
import com.gmail.val59000mc.kit.table.item.KitItem;
import com.gmail.val59000mc.kit.table.item.KitItemGroup;
import com.gmail.val59000mc.kit.upgrade.PlayerKitUpgrades;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class KitTablePool {

    private final int rolls;
    private final @NotNull KitTableEntries entries;

    public KitTablePool(int rolls, @NotNull KitTableEntries entries) {
        this.rolls = rolls;
        this.entries = entries;
    }

    public int getRolls() {
        return rolls;
    }

    public @NotNull KitTableEntries getEntries() {
        return entries;
    }

    public void give(@NotNull Player player, @NotNull KitsManager manager, @NotNull Random random) {
        getItem(player, manager, random).give(player, manager, random);
    }

    public @NotNull KitItem getItem(@NotNull Player player, @NotNull KitsManager manager, @NotNull Random random) {
        KitItem result = KitItem.EMPTY;

        for (int i = 0; i < rolls; i++) {
            result = result.combineWith(entries.getRandomItem(player, manager, random));
        }

        return result;
    }

    public void appendDisplayItems(@NotNull KitDisplayItems items,
                                   @NotNull KitsManager manager,
                                   @NotNull UhcPlayer player,
                                   @NotNull PlayerKitUpgrades upgrades) {
        List<KitTableEntry> filteredEntries = entries.filteredEntriesForDisplay(manager, player, upgrades);
        if (filteredEntries.isEmpty()) return;

        if (filteredEntries.size() == 1) {
            filteredEntries.get(0).appendDisplayItems(items, manager, player, upgrades);
        }
        else {
            entries.appendDisplayItemsRandom(items, manager, player, upgrades);
        }
    }
}
