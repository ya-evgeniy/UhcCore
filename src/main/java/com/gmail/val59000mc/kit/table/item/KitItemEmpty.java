package com.gmail.val59000mc.kit.table.item;

import com.gmail.val59000mc.kit.KitsManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class KitItemEmpty implements KitItem {

    public static final KitItem INSTANCE = new KitItemEmpty();

    private KitItemEmpty() {
    }

    @Override
    public void give(@NotNull Player player, @NotNull KitsManager manager, @NotNull Random random) {
    }

    @Override
    public @NotNull KitItem getRandom(@NotNull KitsManager kitsManager, @NotNull Random random) {
        return this;
    }

    @Override
    public @NotNull KitItem combineWith(@NotNull KitItem item) {
        return item;
    }

    @Override
    public @NotNull List<KitItem> asList() {
        return Collections.emptyList();
    }

    @Override
    public @NotNull List<KitItem> asListSequence() {
        return Collections.emptyList();
    }

    @Override
    public @Nullable ItemStack getNext() {
        return null;
    }

}
