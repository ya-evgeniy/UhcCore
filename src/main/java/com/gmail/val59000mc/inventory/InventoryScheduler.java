package com.gmail.val59000mc.inventory;

import com.gmail.val59000mc.UhcCore;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class InventoryScheduler {

    private static final InventoryScheduler INSTANCE = new InventoryScheduler();

    private List<InventoryUpdater> updaters = new ArrayList<>();

    public InventoryScheduler() {
        Bukkit.getScheduler().runTaskTimer(UhcCore.getPlugin(), this::run, 0, 0);
    }

    private void run() {
        this.updaters.forEach(InventoryUpdater::update);
    }

    public static void register(InventoryUpdater updater) {
        INSTANCE.updaters.add(updater);
    }

    public static void unregister(InventoryUpdater updater) {
        INSTANCE.updaters.remove(updater);
    }

}
