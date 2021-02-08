package com.gmail.val59000mc.inventory.kit.item;

import com.gmail.val59000mc.configuration.VaultManager;
import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.inventory.UhcInventoryItem;
import com.gmail.val59000mc.inventory.kit.KitUpgradeInventory;
import com.gmail.val59000mc.kit.Kit;
import com.gmail.val59000mc.kit.upgrade.KitUpgrade;
import com.gmail.val59000mc.kit.upgrade.KitUpgrades;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.utils.RomanNumber;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class KitLevelInventoryItem implements UhcInventoryItem {

    private final KitUpgradeInventory inventory;
    private final UhcPlayer player;
    private final Kit kit;
    private final KitUpgrade upgrade;
    private final int maxUpgradeLevel;

    public KitLevelInventoryItem(KitUpgradeInventory inventory,
                                 UhcPlayer player,
                                 Kit kit,
                                 KitUpgrade upgrade,
                                 int maxUpgradeLevel
    ) {
        this.inventory = inventory;
        this.player = player;
        this.kit = kit;
        this.upgrade = upgrade;
        this.maxUpgradeLevel = maxUpgradeLevel;
    }

    @Override
    public @Nullable ItemStack getDisplay() {
        Player bukkitPlayer = Bukkit.getPlayer(this.player.getUuid());
        if (bukkitPlayer == null) return null;

        KitUpgrades upgrades = kit.getUpgrades();
        if (upgrades == null) return null;

        int currentPlayerLevel = this.player.getKitUpgrades().getLevel(upgrades.getId());
        int nextPlayerLevel = currentPlayerLevel + 1;

        int upgradeDisplayLevel = upgrade.getLevel() + 1;

        ItemStack stack = new ItemStack(Material.AIR);

        List<String> lore = new ArrayList<>();
        if (upgrade.getCost() > 0) {
            lore.add(String.format("%s%sСтоимость: %s%d", ChatColor.RESET, ChatColor.GRAY, ChatColor.GOLD, upgrade.getCost()));
//            lore.add(
//                    String.format("[{\"text\":\"\",\"italic\":\"false\"},{\"text\":\"Стоимость: \",\"color\":\"gray\"},{\"text\":\"%d\",\"color\":\"gold\"}]", upgrade.getCost())
//            );
        }

        lore.add("");
        if (upgrade.getLevel() <= currentPlayerLevel) {
            stack.setType(Material.LIME_STAINED_GLASS_PANE);
            lore.add(String.format("%s%sПриобретено", ChatColor.RESET, ChatColor.GREEN));
//            lore.add("[{\"text\":\"\",\"italic\":\"false\"},{\"text\":\"Приобретено\",\"color\":\"green\"}]");
        }
        else if (nextPlayerLevel == upgrade.getLevel()) {
            if (upgrade.getCost() > VaultManager.getPlayerMoney(bukkitPlayer)) {
                stack.setType(Material.RED_STAINED_GLASS_PANE);
                lore.add(String.format("%s%sНедостаточно монет", ChatColor.RESET, ChatColor.RED));
//                lore.add("[{\"text\":\"\",\"italic\":\"false\"},{\"text\":\"Недостаточно монет\",\"color\":\"gray\"}]");
            }
            else {
                stack.setType(Material.YELLOW_STAINED_GLASS_PANE);
                lore.add(String.format("%s%sЛКМ %s- чтобы приобрести", ChatColor.RESET, ChatColor.GREEN, ChatColor.GRAY));
//                lore.add("[{\"text\":\"\",\"italic\":\"false\"},{\"text\":\"ЛКМ \",\"color\":\"green\"},{\"text\":\"- чтобы приобрести\",\"color\":\"gray\"}]");
            }
        }
        else {
            stack.setType(Material.RED_STAINED_GLASS_PANE);
            lore.add(String.format("%s%sПредыдущее улучшение не приобретено", ChatColor.RESET, ChatColor.RED));
//            lore.add("[{\"text\":\"\",\"italic\":\"false\"},{\"text\":\"Предыдущее улучшение\",\"color\":\"red\"},{\"text\":\" не приобретено\",\"color\":\"red\"}]");
        }

        boolean hasPermission = bukkitPlayer.hasPermission("uhccore.kit." + kit.getId());
        if (!hasPermission) {
            lore.add("");
            lore.add(String.format("%s%sДанное улучшение недоступно для тебя", ChatColor.RESET, ChatColor.RED));
//            lore.add("[\"\"]");
//            lore.add("[{\"text\":\"\",\"color\":\"white\",\"italic\":\"false\"},{\"text\":\"Данное улучшение недоступно для тебя\",\"color\":\"red\"}]");
        }

        String displayName = kit.getDisplay().hasTitle() ? kit.getDisplay().getTitle() : kit.getId();

        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(String.format("%s%s%s %s", ChatColor.RESET, ChatColor.GRAY, displayName, RomanNumber.fromDecimal(upgradeDisplayLevel)));
            meta.setLore(lore);
            stack.setItemMeta(meta);
        }

        return stack;
    }

    @Override
    public void on(@NotNull InventoryClickEvent event) {
        if (event.getAction() != InventoryAction.PICKUP_ALL) return;

        KitUpgrades upgrades = kit.getUpgrades();
        if (upgrades == null) return;

        Player bukkitPlayer = Bukkit.getPlayer(this.player.getUuid());
        if (bukkitPlayer == null) return;

        int currentPlayerLevel = this.player.getKitUpgrades().getLevel(upgrades.getId());
        int nextPlayerLevel = currentPlayerLevel + 1;

        if (nextPlayerLevel == upgrade.getLevel() && upgrade.getCost() <= VaultManager.getPlayerMoney(bukkitPlayer)) {

            boolean hasPermission = bukkitPlayer.hasPermission("uhccore.kit." + kit.getId());
            if (!hasPermission) {
                bukkitPlayer.playSound(bukkitPlayer.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                return;
            }

            int newLevel = this.player.getKitUpgrades().incrementLevel(upgrades.getId());
            VaultManager.removeMoney(bukkitPlayer, upgrade.getCost());
            inventory.updateUpgrades();

            GameManager.getGameManager().getKitsManager().getDbKitUpgrades().save(player, kit, newLevel);

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
