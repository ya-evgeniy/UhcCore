package com.gmail.val59000mc.kit;

import org.bukkit.inventory.ItemStack;

public class KitDisplayItem {

    private ItemStack item;

    private boolean hasTitle;
    private String title;

    private boolean hasDescription;
    private int length;
    private String text;

    public KitDisplayItem(ItemStack item, boolean hasTitle, String title, boolean hasDescription, int length, String text) {
        this.item = item;
        this.hasTitle = hasTitle;
        this.title = title;
        this.hasDescription = hasDescription;
        this.length = length;
        this.text = text;
    }

    public ItemStack getItem() {
        return item;
    }

    public boolean hasTitle() {
        return hasTitle;
    }

    public String getTitle() {
        return title;
    }

    public boolean hasDescription() {
        return hasDescription;
    }

    public int getLength() {
        return length;
    }

    public String getText() {
        return text;
    }

}
