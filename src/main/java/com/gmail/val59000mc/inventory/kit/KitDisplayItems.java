package com.gmail.val59000mc.inventory.kit;

import com.gmail.val59000mc.kit.table.item.KitItem;
import com.gmail.val59000mc.kit.table.item.KitItemGroup;
import com.gmail.val59000mc.kit.table.item.KitItemStack;
import com.gmail.val59000mc.utils.stack.ItemStackUtil;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class KitDisplayItems {

    private final List<KitItem> items = new ArrayList<>();

    public void add(@NotNull KitItem item) {
        this.items.add(item);
    }

    public void addAll(@NotNull Collection<? extends KitItem> items) {
        this.items.addAll(items);
    }

    public @NotNull KitItem asKitItem() {
        return new KitItemGroup(this.items);
    }

    public void render(@NotNull KitUpgradeInventory inventory, int yIndex) {
        int xIndex = 2;
        int xIndexLast = 8;

        for (KitItem item : items) {
            if (item.asList().size() > 1) {
                inventory.setItem(xIndexLast--, yIndex, item.getNext());
            }
            else {
                inventory.setItem(xIndex++, yIndex, item.getNext());
            }
        }
    }

    public void setLore(@NotNull ItemMeta meta, @NotNull List<String> additionLore) {
        List<String> lore = new ArrayList<>();

        List<ItemStack> givenItems = new ArrayList<>();
        List<ItemStack> randomizedItems = new ArrayList<>();

        for (KitItem item : items) {
            List<KitItem> items = item.asList();
            if (items.size() == 1) {
                givenItems.add(items.get(0).getNext());
            }
            else {
                Deque<KitItem> deque = new LinkedList<>(items);
                while (!deque.isEmpty()) {
                    KitItem kitItem = deque.removeFirst();
                    if (kitItem instanceof KitItemStack) {
                        randomizedItems.add(kitItem.getNext());
                    }
                    else if (kitItem instanceof KitItemGroup) {
                        deque.addAll(kitItem.asList());
                    }
                }
            }
        }

        lore.add("[\"\"]");
        lore.add("[{\"text\":\"\",\"color\":\"white\",\"italic\":\"false\"},{\"text\":\"Предметы:\"}]");
        for (ItemStack stack : givenItems) {
            lore.add(
                    String.format("[{\"text\":\"\",\"color\":\"white\",\"italic\":\"false\"},{\"text\":\"- \"},{\"translate\":\"%s\",\"color\":\"white\"}]", ItemStackUtil.translatableKey(stack.getType()))
            );
        }

        if (!randomizedItems.isEmpty()) {
            lore.add("[{\"text\":\"\",\"color\":\"white\",\"italic\":\"false\"},{\"text\":\"- Случайный предмет:\"}]");
            for (ItemStack stack : randomizedItems) {
                lore.add(
                        String.format("[{\"text\":\"\",\"color\":\"white\",\"italic\":\"false\"},{\"text\":\"  - \"},{\"translate\":\"%s\",\"color\":\"white\"}]", ItemStackUtil.translatableKey(stack.getType()))
                );
            }
        }

        lore.addAll(additionLore);
        ItemStackUtil.setJsonLore(meta, lore);
    }

}
