package com.gmail.val59000mc.commands.base;

import com.gmail.val59000mc.UhcCore;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public abstract class BaseCommand implements CommandExecutor, TabCompleter {

    protected @NotNull final String id;

    public BaseCommand(@NotNull String id) {
        this.id = id;
    }

    public void register() {
        UhcCore plugin = UhcCore.getPlugin();
        PluginCommand command = plugin.getCommand(this.id);
        if (command == null) {
            plugin.getLogger().warning(String.format("Command with id '%s' has no registered", id));
        }
        else {
            command.setExecutor(this);
            command.setTabCompleter(this);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] arguments) {
        if (sender instanceof Player) {
            onCommand((Player) sender, arguments);
        }
        return true;
    }

    protected abstract void onCommand(@NotNull Player sender, String[] arguments);

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] arguments) {
        if (sender instanceof Player) {
            List<String> result = onTabComplete((Player) sender, arguments);
            if (result == null) result = Collections.emptyList();
            return result;
        }
        return Collections.emptyList();
    }

    protected abstract @Nullable List<String> onTabComplete(@NotNull Player sender, String[] arguments);

}
