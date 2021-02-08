package com.gmail.val59000mc.kit.table.condition;

import com.gmail.val59000mc.kit.KitsManager;
import com.gmail.val59000mc.kit.upgrade.PlayerKitUpgrades;
import com.gmail.val59000mc.players.PlayersManager;
import com.gmail.val59000mc.players.UhcPlayer;
import com.google.gson.annotations.SerializedName;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class KitTableConditionUpgradeLevel implements KitTableCondition {

    public static final @NotNull String ID = "upgrades";

    private final @NotNull String upgradeId;
    private final int level;

    public KitTableConditionUpgradeLevel(@NotNull String upgradeId, int level) {
        this.upgradeId = upgradeId;
        this.level = level;
    }

    @Override
    public @NotNull String getId() {
        return ID;
    }

    @Override
    public boolean check(@NotNull Player player, @NotNull KitsManager manager, @NotNull Random random) {
        PlayersManager playersManager = manager.getGameManager().getPlayersManager();
        if (playersManager.doesPlayerExist(player)) {
            UhcPlayer uhcPlayer = playersManager.getUhcPlayer(player);
            PlayerKitUpgrades upgrades = uhcPlayer.getKitUpgrades();

            return this.level == upgrades.getLevel(upgradeId);
        }

        return false;
    }

    @Override
    public boolean checkForDisplay(@NotNull KitsManager manager, @NotNull UhcPlayer player, @NotNull PlayerKitUpgrades upgrades) {
        return this.level == upgrades.getLevel(this.upgradeId);
    }

}
