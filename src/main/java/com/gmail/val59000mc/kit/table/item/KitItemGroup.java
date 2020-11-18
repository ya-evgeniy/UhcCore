package com.gmail.val59000mc.kit.table.item;

import com.gmail.val59000mc.kit.KitsManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class KitItemGroup implements KitItem {

    private final List<KitItem> items;

    private int index;

    public KitItemGroup(List<KitItem> items) {
        this.items = items;
    }

    @Override
    public void give(@NotNull Player player, @NotNull KitsManager manager, @NotNull Random random) {
        for (KitItem item : items) {
            item.give(player, manager, random);
        }
    }

    @Override
    public @NotNull KitItem getRandom(@NotNull KitsManager kitsManager, @NotNull Random random) {
        if (items.isEmpty()) return KitItem.EMPTY;
        return this.items.get(random.nextInt(this.items.size()));
    }

    @Override
    public @NotNull KitItem combineWith(@NotNull KitItem item) {
        if (item == EMPTY) return this;
        return new KitItemGroup(Arrays.asList(this, item));
    }

    @Override
    public @NotNull List<KitItem> asList() {
        if (items.isEmpty()) return Collections.emptyList();
        return new ArrayList<>(this.items);
    }

    @Override
    public @NotNull List<KitItem> asListSequence() {
        List<KitItem> result = new ArrayList<>();
        for (KitItem item : this.items) {
            result.addAll(item.asListSequence());
        }
        return result;
    }

    @Override
    public @Nullable ItemStack getNext() {
        if (items.isEmpty()) return null;
        if (index >= items.size()) index = 0;

        return items.get(index++).getNext();
    }

}
