package com.gmail.val59000mc.inventory.kit.item;

import com.gmail.val59000mc.configuration.VaultManager;
import com.gmail.val59000mc.inventory.UhcInventoryItem;
import com.gmail.val59000mc.inventory.kit.KitUpgradeInventory;
import com.gmail.val59000mc.kit.upgrade.KitUpgrade;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.utils.RomanNumber;
import com.gmail.val59000mc.utils.stack.ItemStackUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class KitLevelInventoryItem implements UhcInventoryItem {

    private final KitUpgradeInventory inventory;
    private final UhcPlayer player;
    private final String upgradeId;
    private final KitUpgrade upgrade;
    private final String kitName;
    private final int maxUpgradeLevel;

    public KitLevelInventoryItem(KitUpgradeInventory inventory,
                                 UhcPlayer player,
                                 String kitName,
                                 String upgradeId,
                                 KitUpgrade upgrade,
                                 int maxUpgradeLevel
    ) {
        this.inventory = inventory;
        this.player = player;
        this.kitName = kitName;
        this.upgradeId = upgradeId;
        this.upgrade = upgrade;
        this.maxUpgradeLevel = maxUpgradeLevel;
    }

    @Override
    public @Nullable ItemStack getDisplay() {
        Player bukkitPlayer = Bukkit.getPlayer(this.player.getUuid());
        if (bukkitPlayer == null) return null;

        int currentPlayerLevel = this.player.getKitUpgrades().getLevel(upgradeId);
        int nextPlayerLevel = currentPlayerLevel + 1;

        int upgradeDisplayLevel = upgrade.getLevel() + 1;

        ItemStack stack = new ItemStack(Material.AIR);

        List<String> lore = new ArrayList<>();
//        lore.add("[\"\"]");
        if (upgrade.getCost() > 0) {
            lore.add(
                    String.format("[{\"text\":\"\",\"italic\":\"false\"},{\"text\":\"Стоимость: \",\"color\":\"gray\"},{\"text\":\"%d\",\"color\":\"gold\"}]", upgrade.getCost())
            );
        }

        lore.add("[\"\"]");
        if (upgrade.getLevel() <= currentPlayerLevel) {
            stack.setType(Material.LIME_STAINED_GLASS_PANE);
            lore.add("[{\"text\":\"\",\"italic\":\"false\"},{\"text\":\"Приобретено\",\"color\":\"green\"}]");
        }
        else if (nextPlayerLevel == upgrade.getLevel()) {
            if (upgrade.getCost() > VaultManager.getPlayerMoney(bukkitPlayer)) {
                stack.setType(Material.RED_STAINED_GLASS_PANE);
                lore.add("[{\"text\":\"\",\"italic\":\"false\"},{\"text\":\"Недостаточно монет\",\"color\":\"gray\"}]");
            }
            else {
                stack.setType(Material.YELLOW_STAINED_GLASS_PANE);
                lore.add("[{\"text\":\"\",\"italic\":\"false\"},{\"text\":\"ЛКМ \",\"color\":\"green\"},{\"text\":\"- чтобы приобрести\",\"color\":\"gray\"}]");
            }
        }
        else {
            stack.setType(Material.RED_STAINED_GLASS_PANE);
            lore.add("[{\"text\":\"\",\"italic\":\"false\"},{\"text\":\"Предыдущее улучшение\",\"color\":\"red\"},{\"text\":\" не приобретено\",\"color\":\"red\"}]");
        }

        ItemStackUtil.setDisplayName(stack, String.format("%s%s%s %s", ChatColor.RESET, ChatColor.GRAY, kitName, RomanNumber.fromDecimal(upgradeDisplayLevel)));
        ItemStackUtil.setJsonLore(stack, lore);

        return stack;
    }

    @Override
    public void on(@NotNull InventoryClickEvent event) {
        if (event.getAction() != InventoryAction.PICKUP_ALL) return;

        Player bukkitPlayer = Bukkit.getPlayer(this.player.getUuid());
        if (bukkitPlayer == null) return;

        int currentPlayerLevel = this.player.getKitUpgrades().getLevel(upgradeId);
        int nextPlayerLevel = currentPlayerLevel + 1;

        if (nextPlayerLevel == upgrade.getLevel() && upgrade.getCost() <= VaultManager.getPlayerMoney(bukkitPlayer)) {
            this.player.getKitUpgrades().incrementLevel(this.upgradeId);
            VaultManager.removeMoney(bukkitPlayer, upgrade.getCost());
            inventory.updateUpgrades();

            if (upgrade.getLevel() == maxUpgradeLevel) {
                bukkitPlayer.playSound(
                        bukkitPlayer.getLocation(),
                        Sound.UI_TOAST_CHALLENGE_COMPLETE,
                        0.5f, 2
                );
            }
            else {
                bukkitPlayer.playSound(
                        bukkitPlayer.getLocation(),
                        Sound.ITEM_TRIDENT_RETURN,
                        2, 2
                );
            }
        }
    }

}
