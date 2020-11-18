package com.gmail.val59000mc.kit;

import com.gmail.val59000mc.inventory.kit.KitDisplayItems;
import com.gmail.val59000mc.kit.table.entry.KitTableEntries;
import com.gmail.val59000mc.kit.table.item.KitItem;
import com.gmail.val59000mc.kit.upgrade.KitUpgrades;
import com.gmail.val59000mc.kit.upgrade.PlayerKitUpgrades;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.utils.json.annotation.IgnoreDeserialization;
import com.gmail.val59000mc.utils.json.annotation.IgnoreSerialization;
import com.google.gson.annotations.Expose;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Kit {

    private final @NotNull String id;
    private final @NotNull KitGroup group;

    private final @NotNull ItemStack display;

    private final @Nullable KitUpgrades upgrades;
    private final @Nullable KitTableEntries entries;

//    @IgnoreSerialization @IgnoreDeserialization
//    private @Nullable KitDisplayItems displayItems;

    public Kit(@NotNull String id,
               @NotNull KitGroup group,
               @NotNull ItemStack display,
               @Nullable KitUpgrades upgrades,
               @Nullable KitTableEntries entries) {
        this.id = id;
        this.group = group;
        this.display = display;
        this.upgrades = upgrades;
        this.entries = entries;
    }

    public @NotNull String getId() {
        return id;
    }

    public @NotNull KitGroup getGroup() {
        return group;
    }

    public @NotNull ItemStack getDisplayItems() {
        return display;
    }

    public @NotNull String getDisplayName() {
        ItemMeta meta = this.display.getItemMeta();
        if (meta == null) return this.id + ":null";
        return meta.getDisplayName();
    }

    public @Nullable KitUpgrades getUpgrades() {
        return upgrades;
    }

    public @Nullable KitTableEntries getEntries() {
        return entries;
    }

    public void give(@NotNull Player player, @NotNull KitsManager manager, @NotNull Random random) {
        getItem(player, manager, random).give(player, manager, random);
    }

    public @NotNull KitItem getItem(@NotNull Player player, @NotNull KitsManager manager, @NotNull Random random) {
        if (entries == null) return KitItem.EMPTY;
        return entries.getItem(player, manager, random);
    }

    public @NotNull KitDisplayItems getDisplayItems(@NotNull KitsManager manager, @NotNull UhcPlayer player, @NotNull PlayerKitUpgrades upgrades) {
        KitDisplayItems result = new KitDisplayItems();
        if (this.entries != null) this.entries.appendDisplayItems(result, manager, player, upgrades);
        return result;
    }

}
