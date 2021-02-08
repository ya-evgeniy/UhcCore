package com.gmail.val59000mc.inventory.util;

import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

public class InventoryRectangleRenderer {

    private final int width;
    private final int height;
    private final boolean clean;

    public InventoryRectangleRenderer(int width, int height, boolean clean) {
        this.width = width;
        this.height = height;
        this.clean = clean;
    }

    public <T> void render(int x, int y, @NotNull BiFunction<Integer, Integer, T> getter, SetItem<T> setter, SetItem<T> cleanSetter) {
        for (int dx = 0; dx < width; dx++) {
            for (int dy = 0; dy < height; dy++) {
                T item = getter.apply(dx, dy);
                if (item == null) {
                    if (clean) cleanSetter.set(dx, dy, null);
                }
                else {
                    setter.set(x + dx, y + dy, item);
                }
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isClean() {
        return clean;
    }

    public interface SetItem<T> {

        void set(int x, int y, T item);

    }

}
