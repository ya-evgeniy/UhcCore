package com.gmail.val59000mc.events.kit;

import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerKitUpgradesLoaded extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final @NotNull UhcPlayer player;

    public PlayerKitUpgradesLoaded(@NotNull UhcPlayer player) {
        this.player = player;
    }

    public UhcPlayer getPlayer() {
        return player;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
