package com.gmail.val59000mc.kit.table.entry;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.inventory.kit.KitDisplayItems;
import com.gmail.val59000mc.kit.KitsManager;
import com.gmail.val59000mc.kit.table.condition.KitTableConditions;
import com.gmail.val59000mc.kit.table.item.KitItem;
import com.gmail.val59000mc.kit.table.set.KitTableSet;
import com.gmail.val59000mc.kit.upgrade.PlayerKitUpgrades;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class KitTableEntrySet implements KitTableEntry {

    public static final @NotNull String ID = "set";

    private final int weight;
    private final @NotNull List<String> name;
    private final @Nullable KitTableConditions conditions;

    public KitTableEntrySet(int weight, @NotNull List<String> name, @Nullable KitTableConditions conditions) {
        this.weight = weight;
        this.name = name;
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
        return this.conditions;
    }

    @Override
    public void give(@NotNull Player player, @NotNull KitsManager manager, @NotNull Random random) {
        for (String setId : this.name) {
            KitTableSet set = manager.getSet(setId);
            if (set == null) {
                UhcCore.getPlugin().getLogger().warning(String.format("Set with id '%s' not found", setId));
                continue;
            }
            set.give(player, manager, random);
        }
    }

    @Override
    public @NotNull KitItem getItem(@NotNull Player player, @NotNull KitsManager manager, @NotNull Random random) {
        if (!checkCondition(player, manager, random)) {
            return KitItem.EMPTY;
        }

        KitItem result = KitItem.EMPTY;
        for (String setId : this.name) {
            KitTableSet set = manager.getSet(setId);
            if (set == null) {
                UhcCore.getPlugin().getLogger().warning(String.format("Set with id '%s' not found", setId));
                continue;
            }
            result = result.combineWith(set.getItem(player, manager, random));
        }

        return result;
    }

    @Override
    public boolean checkCondition(@NotNull Player player, @NotNull KitsManager manager, @NotNull Random random) {
        if (this.conditions == null) return true;
        return this.conditions.check(player, manager, random);
    }

    @Override
    public void appendDisplayItems(@NotNull KitDisplayItems items,
                                   @NotNull KitsManager manager,
                                   @NotNull UhcPlayer player,
                                   @NotNull PlayerKitUpgrades upgrades) {
        if (conditions != null && !conditions.checkForDisplay(manager, player, upgrades)) return;

        for (String setId : this.name) {
            KitTableSet set = manager.getSet(setId);
            if (set == null) {
                UhcCore.getPlugin().getLogger().warning(String.format("Set with id '%s' not found", setId));
                continue;
            }
            set.appendDisplayItems(items, manager, player, upgrades);
        }
    }

    @Override
    public boolean checkConditionForDisplay(@NotNull KitsManager manager, @NotNull UhcPlayer player, @NotNull PlayerKitUpgrades upgrades) {
        if (this.conditions == null) return true;
        return this.conditions.checkForDisplay(manager, player, upgrades);
    }

}
