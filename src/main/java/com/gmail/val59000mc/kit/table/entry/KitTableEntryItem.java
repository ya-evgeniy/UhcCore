package com.gmail.val59000mc.kit.table.entry;

import com.gmail.val59000mc.inventory.kit.KitDisplayItems;
import com.gmail.val59000mc.kit.KitsManager;
import com.gmail.val59000mc.kit.table.item.KitItem;
import com.gmail.val59000mc.kit.table.condition.KitTableConditions;
import com.gmail.val59000mc.kit.upgrade.PlayerKitUpgrades;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class KitTableEntryItem implements KitTableEntry {

    public static final @NotNull String ID = "item";

    private final int weight;
    private final @NotNull KitItem item;
    private final @Nullable KitTableConditions conditions;

    public KitTableEntryItem(int weight, @NotNull KitItem item, @Nullable KitTableConditions conditions) {
        this.weight = weight;
        this.item = item;
        this.conditions = conditions;
    }

    @Override
    public @NotNull String getId() {
        return ID;
    }

    @Override
    public int getWeight() {
        return this.weight == 0 ? 1 : this.weight;
    }

    @Override
    public @Nullable KitTableConditions getConditions() {
        return conditions;
    }

    @Override
    public void give(@NotNull Player player, @NotNull KitsManager manager, @NotNull Random random) {
        getItem(player, manager, random).give(player, manager, random);
    }

    @Override
    public @NotNull KitItem getItem(@NotNull Player player, @NotNull KitsManager manager, @NotNull Random random) {
        return this.item;
    }

    @Override
    public boolean checkCondition(@NotNull Player player, @NotNull KitsManager manager, @NotNull Random random) {
        if (conditions == null) return true;
        return conditions.check(player, manager, random);
    }

    @Override
    public void appendDisplayItems(@NotNull KitDisplayItems items, @NotNull KitsManager manager, @NotNull UhcPlayer player, @NotNull PlayerKitUpgrades upgrades) {
        if (this.conditions != null && !this.conditions.checkForDisplay(manager, player, upgrades)) return;
        items.addAll(this.item.asListSequence());
    }

    @Override
    public boolean checkConditionForDisplay(@NotNull KitsManager manager, @NotNull UhcPlayer player, @NotNull PlayerKitUpgrades upgrades) {
        if (conditions == null) return true;
        return conditions.checkForDisplay(manager, player, upgrades);
    }

    public @NotNull KitItem getItem() {
        return item;
    }

}
