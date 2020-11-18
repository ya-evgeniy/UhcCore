package com.gmail.val59000mc.inventory.util;

import com.gmail.val59000mc.inventory.UhcInventoryContent;
import com.gmail.val59000mc.inventory.UhcInventoryItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiFunction;

public class ScrollableRow {

    private final @NotNull List<UhcInventoryItem> slots;

    private int shift = 0;
    private final boolean buttons;

    private final int x;
    private final int y;

    private @Nullable UhcInventoryItem nextButton = null;
    private @Nullable UhcInventoryItem prevButton = null;

    private final InventoryRectangleRenderer rectangleRenderer;

    public ScrollableRow(@NotNull List<UhcInventoryItem> slots, int x, int y, int width, int height) {
        this.slots = slots;
        this.buttons = slots.size() > width;

        x += buttons ? 1 : 0;
        width -= buttons ? 2 : 0;

        this.x = x;
        this.y = y;

        rectangleRenderer = new InventoryRectangleRenderer(
                width, height, true
        );
    }

    public void render(@NotNull UhcInventoryContent inventory) {
        BiFunction<Integer, Integer, UhcInventoryItem> getter = (x, y) -> {
            int height = rectangleRenderer.getHeight();

            int index = (shift + x) * height + y;
            if (index > -1 && index < slots.size()) return slots.get(index);
            return null;
        };

        InventoryRectangleRenderer.SetItem<UhcInventoryItem> setter = inventory::setItem;

        rectangleRenderer.render(this.x, this.y, getter, setter, setter);

        if (buttons) renderButtons(inventory);
    }

    private void renderButtons(@NotNull UhcInventoryContent inventory) {
        int leftButtonIndex = this.x - 1;
        int rightButtonIndex = this.x + rectangleRenderer.getWidth();

        inventory.setItem(leftButtonIndex, this.prevButton);
        inventory.setItem(rightButtonIndex, this.nextButton);
    }

    public void setNextButton(UhcInventoryItem nextButton) {
        this.nextButton = nextButton;
    }

    public void setPrevButton(UhcInventoryItem prevButton) {
        this.prevButton = prevButton;
    }

    public void next() {
        int width = rectangleRenderer.getWidth();
        int height = rectangleRenderer.getHeight();

        int index = (shift + width) * height;

        boolean canRight = index < slots.size();
        if (canRight) shift++;
    }

    public void prev() {
        boolean canLeft = shift > 0;
        if (canLeft) shift--;
    }

}
