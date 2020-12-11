package com.gmail.val59000mc.kit.table.item;

import com.gmail.val59000mc.kit.KitsManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class KitItemStack implements KitItem {

    private final @NotNull ItemStack stack;

    public KitItemStack(@NotNull ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public void give(@NotNull Player player, @NotNull KitsManager manager, @NotNull Random random) {
        player.getInventory().addItem(this.stack);
    }

    @Override
    public @NotNull KitItem getRandom(@NotNull KitsManager kitsManager, @NotNull Random random) {
        return this;
    }

    @Override
    public @NotNull KitItem combineWith(@NotNull KitItem item) {
        if (item == EMPTY) return this;
        return new KitItemGroup(Arrays.asList(this, item));
    }

    @Override
    public @NotNull List<KitItem> asList() {
        return Collections.singletonList(this);
    }

    @Override
    public @NotNull List<KitItem> asListSequence() {
        return Collections.singletonList(this);
    }

    @Override
    public @Nullable ItemStack getNext() {
        return this.stack;
    }

}
