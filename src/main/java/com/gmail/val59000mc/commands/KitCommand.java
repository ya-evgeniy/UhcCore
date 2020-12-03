package com.gmail.val59000mc.commands;

import com.gmail.val59000mc.commands.base.BaseCommand;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.kit.Kit;
import com.gmail.val59000mc.kit.upgrade.KitUpgrades;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class KitCommand extends BaseCommand {

    public static final String ID = "kit";

    private final GameManager manager;

    // kit give <kit_id> <player_name>
    // kit level <player_name> <kit_id> <level>

    public KitCommand(GameManager manager) {
        super(ID);
        this.manager = manager;
    }

    @Override
    protected void onCommand(@NotNull Player sender, String[] arguments) {
        if (!sender.hasPermission("uhccore.kit-commands")) {
            return;
        }

        if (arguments.length == 0) {
            sender.sendMessage(ChatColor.RED + "Недостаточно агрументов");
            return;
        }

        String argument = arguments[0];
        switch (argument) {
            case "give":
                onCommandGive(sender, arguments);
                break;
            case "level":
                onCommandLevel(sender, arguments);
                break;
            default:
                sender.sendMessage(String.format("%sНеизвестный агрумент '%s'", ChatColor.RED, argument));
                break;
        }
    }

    private void onCommandGive(Player sender, String[] arguments) {
        if (arguments.length < 3) {
            sender.sendMessage(ChatColor.RED + "Недостаточно агрументов");
            return;
        }

        String playerName = arguments[1];
        String kitId = arguments[2];

        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            sender.sendMessage(String.format("%sИгрок с ником '%s' не найден", ChatColor.RED, playerName));
            return;
        }

        Kit kit = manager.getKitsManager().getKit(kitId.replace("/", File.separator));
        if (kit == null) {
            sender.sendMessage(String.format("%sКит с ид '%s' не найден", ChatColor.RED, kitId));
            return;
        }

        if (arguments.length > 3) {
            int settingLevel;
            try {
                settingLevel = Integer.parseInt(arguments[3]);
            } catch (NumberFormatException e) {
                sender.sendMessage(String.format("%sУровень кита '%s' не является числом", ChatColor.RED, kitId));
                return;
            }

            UhcPlayer uhcPlayer = manager.getPlayersManager().getUhcPlayer(player);
            if (uhcPlayer == null) {
                sender.sendMessage(String.format("%sИгрок с ником '%s' не найден", ChatColor.RED, playerName));
                return;
            }

            KitUpgrades upgrades = kit.getUpgrades();
            if (upgrades == null) {
                sender.sendMessage(String.format("%sКит '%s' не имеет улучшений", ChatColor.RED, kitId));
                return;
            }

            int upgradeLevel = uhcPlayer.getKitUpgrades().getLevel(upgrades.getId()); // FIXME
            uhcPlayer.getKitUpgrades().setLevel(upgrades.getId(), settingLevel);
            manager.getKitsManager().giveKit(kit, player);
            uhcPlayer.getKitUpgrades().setLevel(upgrades.getId(), upgradeLevel);
        }
        else {
            manager.getKitsManager().giveKit(kit, player);
        }
        sender.sendMessage(String.format("%sКит с ид '%s' успешно выдан '%s'", ChatColor.GREEN, kitId, player.getName()));
    }

    private void onCommandLevel(Player sender, String[] arguments) {
        if (arguments.length < 4) {
            sender.sendMessage(ChatColor.RED + "Недостаточно агрументов");
            return;
        }

        String playerName = arguments[1];
        String kitId = arguments[2];
        String level = arguments[3];

        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            sender.sendMessage(String.format("%sИгрок с ником '%s' не найден", ChatColor.RED, playerName));
            return;
        }

        Kit kit = manager.getKitsManager().getKit(kitId.replace("/", File.separator));
        if (kit == null) {
            sender.sendMessage(String.format("%sКит '%s' не найден", ChatColor.RED, kitId));
            return;
        }

        KitUpgrades upgrades = kit.getUpgrades();
        if (upgrades == null) {
            sender.sendMessage(String.format("%sКит '%s' не имеет улучшений", ChatColor.RED, kitId));
            return;
        }

        int settingLevel = 0;
        try {
            settingLevel = Integer.parseInt(level);
        } catch (NumberFormatException e) {
            sender.sendMessage(String.format("%sУровень кита '%s' не является числом", ChatColor.RED, kitId));
            return;
        }

        UhcPlayer uhcPlayer = manager.getPlayersManager().getUhcPlayer(player);
        if (uhcPlayer == null) {
            sender.sendMessage(String.format("%sИгрок с ником '%s' не найден", ChatColor.RED, playerName));
            return;
        }

        int maxLevel = upgrades.getLevels().size();
        int currentLevel = uhcPlayer.getKitUpgrades().getLevel(upgrades.getId());

        if (settingLevel < 0 || settingLevel > maxLevel) {
            sender.sendMessage(String.format("%sУровень кита '%s' должен быть в диапазоне от '%s' до '%s'", ChatColor.RED, settingLevel, 0, maxLevel));
            return;
        }

        if (currentLevel == settingLevel) {
            sender.sendMessage(String.format("%sУровень кита '%s' уже имеется у игрока", ChatColor.RED, settingLevel));
            return;
        }

        uhcPlayer.getKitUpgrades().setLevel(upgrades.getId(), settingLevel);
        manager.getKitsManager().getDbKitUpgrades().save(uhcPlayer, kit, settingLevel);
        sender.sendMessage(String.format("%sИгроку '%s' установлен уровень '%s' для кита '%s'", ChatColor.GREEN, player.getName(), settingLevel, kit.getFormattedId()));
    }

    @Override
    protected @Nullable List<String> onTabComplete(@NotNull Player sender, String[] arguments) {
        if (!sender.hasPermission("uhccore.kit-commands")) {
            return null;
        }

        if (arguments.length == 0) return null;

        if (arguments.length == 1) {
            String argument = arguments[0];

            List<String> result = new ArrayList<>();
            if ("give".startsWith(argument)) result.add("give");
            if ("level".startsWith(argument)) result.add("level");

            return result;
        }

        String argument = arguments[0];
        switch (argument) {
            case "give":
                return onTabCompleteGive(sender, arguments);
            case "level":
                return onTabCompleteLevel(sender, arguments);
        }

        return null;
    }

    private List<String> onTabCompleteGive(Player sender, String[] arguments) {

        if (arguments.length == 2) {
            String playerArgument = arguments[1].toLowerCase();

            List<String> result = new ArrayList<>();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.getName().toLowerCase().contains(playerArgument)) {
                    result.add(onlinePlayer.getName());
                }
            }

            return result;
        }

        if (arguments.length == 3) {
            String kitArgument = arguments[2];
            List<String> result = new ArrayList<>();
            for (Kit kit : manager.getKitsManager().getKits()) {
                if (kit.getFormattedId().contains(" ")) continue;
                if (kit.getFormattedId().contains(kitArgument)) {
                    result.add(kit.getFormattedId());
                }
            }

            return result;
        }

        if (arguments.length == 4) {
            String playerArgument = arguments[1];
            String kitArgument = arguments[2];
            String level = arguments[3];

            Kit kit = manager.getKitsManager().getKit(kitArgument.replace("/", File.separator));
            if (kit == null) return null;

            Player player = Bukkit.getPlayer(playerArgument);
            if (player == null) return null;

            List<String> result = new ArrayList<>();
            appendKitLevels(result, player, kit, level);
            return result;
        }

        return null;
    }

    private List<String> onTabCompleteLevel(Player sender, String[] arguments) {

        if (arguments.length == 2) {
            String playerArgument = arguments[1].toLowerCase();

            List<String> result = new ArrayList<>();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.getName().toLowerCase().contains(playerArgument)) {
                    result.add(onlinePlayer.getName());
                }
            }

            return result;
        }

        String playerArgument = arguments[1];

        if (arguments.length == 3) {
            String kitArgument = arguments[2];

            List<String> result = new ArrayList<>();
            for (Kit kit : manager.getKitsManager().getKits()) {
                if (kit.getFormattedId().contains(" ")) continue;
                if (kit.getFormattedId().contains(kitArgument)) {
                    result.add(kit.getFormattedId());
                }
            }

            return result;
        }

        String kitArgument = arguments[2];

        if (arguments.length == 4) {
            String level = arguments[3];

            Kit kit = manager.getKitsManager().getKit(kitArgument.replace("/", File.separator));
            if (kit == null) return null;

            Player player = Bukkit.getPlayer(playerArgument);
            if (player == null) return null;

            List<String> result = new ArrayList<>();
            appendKitLevels(result, player, kit, level);
            return result;
        }

        return null;
    }

    private void appendKitLevels(List<String> arguments, Player player, Kit kit, String currentInput) {
        KitUpgrades upgrades = kit.getUpgrades();
        if (upgrades == null) return;

        int maxLevel = upgrades.getLevels().size();

        UhcPlayer uhcPlayer = manager.getPlayersManager().getUhcPlayer(player);
        if (uhcPlayer == null) return;

        int currentLevel = uhcPlayer.getKitUpgrades().getLevel(upgrades.getId());

        for (int i = 0; i <= maxLevel; i++) {
            String levelValue = String.valueOf(i);
            if (currentLevel != i && levelValue.contains(currentInput)) arguments.add(levelValue);
        }
    }

}
