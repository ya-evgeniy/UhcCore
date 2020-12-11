package com.gmail.val59000mc.kit.table.item;

import com.gmail.val59000mc.kit.KitsManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public interface KitItem {

    KitItem EMPTY = KitItemEmpty.INSTANCE;

    void give(@NotNull Player player, @NotNull KitsManager manager, @NotNull Random random);

    @NotNull KitItem getRandom(@NotNull KitsManager kitsManager, @NotNull Random random);

    @NotNull KitItem combineWith(@NotNull KitItem item);

    @NotNull List<KitItem> asList();

    @NotNull List<KitItem> asListSequence();

    @Nullable ItemStack getNext();

}
