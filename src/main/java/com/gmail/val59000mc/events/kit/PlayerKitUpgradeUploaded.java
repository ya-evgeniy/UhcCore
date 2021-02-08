package com.gmail.val59000mc.events.kit;

import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerKitUpgradeUploaded extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final @NotNull UhcPlayer player;
    private final @NotNull String upgradeId;
    private final int level;

    public PlayerKitUpgradeUploaded(@NotNull UhcPlayer player, @NotNull String upgradeId, int level) {
        this.player = player;
        this.upgradeId = upgradeId;
        this.level = level;
    }

    public UhcPlayer getPlayer() {
        return player;
    }

    public String getUpgradeId() {
        return upgradeId;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
    
}
