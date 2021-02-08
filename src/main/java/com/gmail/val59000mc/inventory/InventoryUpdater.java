package com.gmail.val59000mc.inventory;

import java.util.Objects;

public class InventoryUpdater {

    private static int ID = 0;

    private static int nextId() {
        return ID++;
    }

    private final int id = nextId();

    private int period;
    private Runnable runnable;

    private int ticks = 0;

    public InventoryUpdater(Runnable runnable) {
        this(0, runnable);
    }

    public InventoryUpdater(int period, Runnable runnable) {
        this.period = period;
        this.runnable = runnable;
    }

    public void update() {
        ticks++;

        if (ticks >= period) {
            ticks = 0;
            runnable.run();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventoryUpdater that = (InventoryUpdater) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
