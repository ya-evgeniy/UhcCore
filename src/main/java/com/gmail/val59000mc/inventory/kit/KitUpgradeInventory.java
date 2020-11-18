package com.gmail.val59000mc.inventory.kit;

import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.inventory.InventoryScheduler;
import com.gmail.val59000mc.inventory.InventoryUpdater;
import com.gmail.val59000mc.inventory.UhcInventoryContent;
import com.gmail.val59000mc.inventory.UhcInventoryItem;
import com.gmail.val59000mc.inventory.kit.item.KitLevelInventoryItem;
import com.gmail.val59000mc.kit.Kit;
import com.gmail.val59000mc.kit.KitsManager;
import com.gmail.val59000mc.kit.upgrade.KitUpgrade;
import com.gmail.val59000mc.kit.upgrade.KitUpgrades;
import com.gmail.val59000mc.kit.upgrade.PlayerKitUpgrades;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class KitUpgradeInventory extends UhcInventoryContent {

    private final KitsManager kitsManager;

    private final UhcPlayer player;
    private final Kit kit;

    private InventoryUpdater updater;
    private KitDisplayItems[] displayItems;

    public KitUpgradeInventory(@NotNull KitsManager kitsManager, @NotNull UhcPlayer player, @NotNull Kit kit) {
        super(5, "Upgrade inventory");

        this.kitsManager = kitsManager;
        this.player = player;
        this.kit = kit;

        this.updater = new InventoryUpdater(10, this::update);
        this.generateContent();
    }

    private void generateContent() {
        KitUpgrades upgrades = kit.getUpgrades();
        if (upgrades != null) {
            String displayName = kit.getDisplayName().replaceAll("&[0-9a-f]", "");
            drawUpgradeLevels(displayName, upgrades);

            displayItems = new KitDisplayItems[5];

            int yIndex = 0;
            PlayerKitUpgrades playerKitUpgrades = new PlayerKitUpgrades();
            playerKitUpgrades.setLevel(upgrades.getId(), 0);
            displayItems[yIndex++] = kit.getDisplayItems(GameManager.getGameManager().getKitsManager(), player, playerKitUpgrades);

            for (KitUpgrade level : upgrades.getLevels()) {
                playerKitUpgrades = new PlayerKitUpgrades();
                playerKitUpgrades.setLevel(upgrades.getId(), level.getLevel());
                displayItems[yIndex++] = kit.getDisplayItems(GameManager.getGameManager().getKitsManager(), player, playerKitUpgrades);
            }
        }

        this.update();
    }

    private void drawUpgradeLevels(String displayName, KitUpgrades upgrades) {
        String id = upgrades.getId();

        List<KitUpgrade> levels = new ArrayList<>(upgrades.getLevels());
        levels.sort(Comparator.comparingInt(KitUpgrade::getLevel));

        int yIndex = 5 / 2 - (levels.size() + 1) / 2;

        this.setItem(0, yIndex++, new KitLevelInventoryItem(this, player, displayName, id, new KitUpgrade(0, 0), levels.size()));
        for (KitUpgrade level : levels) {
            this.setItem(0, yIndex++, new KitLevelInventoryItem(this, player, displayName, id, level, levels.size()));
        }
    }

    public void update() {
        for (int i = 0; i < this.displayItems.length; i++) {
            this.displayItems[i].render(this, i);
        }
    }

    public void updateUpgrades() {
        for (int i = 0; i < inventory.getSize(); i += 9) {
            setItem(i, getItem(i));
        }
    }

    @Override
    public void on(@NotNull InventoryOpenEvent event) {
        InventoryScheduler.register(this.updater);
    }

    @Override
    public void on(@NotNull InventoryCloseEvent event) {
        InventoryScheduler.unregister(this.updater);
    }

}
