package com.gmail.val59000mc.kit.table.entry;

import com.gmail.val59000mc.inventory.kit.KitDisplayItems;
import com.gmail.val59000mc.kit.KitsManager;
import com.gmail.val59000mc.kit.table.item.KitItem;
import com.gmail.val59000mc.kit.upgrade.PlayerKitUpgrades;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class KitTableEntries {

    private final @NotNull List<KitTableEntry> entries;

    public KitTableEntries(@NotNull List<KitTableEntry> entries) {
        this.entries = entries;
    }

    public List<KitTableEntry> asList() {
        return entries;
    }

    public void give(@NotNull Player player, @NotNull KitsManager manager, @NotNull Random random) {
        getItem(player, manager, random).give(player, manager, random);
    }

    public @NotNull KitItem getItem(@NotNull Player player, @NotNull KitsManager manager, @NotNull Random random) {
        KitItem result = KitItem.EMPTY;
        for (KitTableEntry entry : entries) {
            result = result.combineWith(entry.getItem(player, manager, random));
        }
        return result;
    }

    public @NotNull KitItem getRandomItem(@NotNull Player player, @NotNull KitsManager manager, @NotNull Random random) {
        List<KitTableEntry> entries = filteredEntries(player, manager, random);

        int totalWeight = entries.stream().mapToInt(KitTableEntry::getWeight).sum();
        int rand = random.nextInt(totalWeight);

        for (KitTableEntry entry : entries) {
            rand -= entry.getWeight();
            if (rand < 0) return entry.getItem(player, manager, random);
        }

        return KitItem.EMPTY;
    }

    public @NotNull List<KitTableEntry> filteredEntries(@NotNull Player player, @NotNull KitsManager manager, @NotNull Random random) {
        return this.entries.stream().filter(entry -> entry.checkCondition(player, manager, random)).collect(Collectors.toList());
    }

    public @NotNull List<KitTableEntry> filteredEntriesForDisplay(@NotNull KitsManager manager,
                                                                  @NotNull UhcPlayer player,
                                                                  @NotNull PlayerKitUpgrades upgrades) {
        return this.entries.stream().filter(entry -> entry.checkConditionForDisplay(manager, player, upgrades)).collect(Collectors.toList());
    }

    public void appendDisplayItems(@NotNull KitDisplayItems items,
                                   @NotNull KitsManager manager,
                                   @NotNull UhcPlayer player,
                                   @NotNull PlayerKitUpgrades upgrades) {
        for (KitTableEntry entry : this.entries) {
            entry.appendDisplayItems(items, manager, player, upgrades);
        }
    }

    public void appendDisplayItemsRandom(@NotNull KitDisplayItems items,
                                         @NotNull KitsManager manager,
                                         @NotNull UhcPlayer player,
                                         @NotNull PlayerKitUpgrades upgrades) {
        KitDisplayItems result = new KitDisplayItems();
        for (KitTableEntry entry : this.entries) {
            entry.appendDisplayItems(result, manager, player, upgrades);
        }
        items.add(result.asKitItem());
    }

}
