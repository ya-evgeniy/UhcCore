package com.gmail.val59000mc.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

public class UhcLobbyPlayerDamageEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Player player;
    private final double finalDamage;
    private final EntityDamageEvent.DamageCause cause;

    private boolean canceled;
    private boolean passOriginal;

    public UhcLobbyPlayerDamageEvent(Player player, double finalDamage, EntityDamageEvent.@NotNull DamageCause cause) {
        this.player = player;

        this.finalDamage = finalDamage;
        this.cause = cause;
    }

    public Player getPlayer() {
        return player;
    }

    public double getFinalDamage() {
        return finalDamage;
    }

    public EntityDamageEvent.DamageCause getCause() {
        return cause;
    }

    @Override
    public boolean isCancelled() {
        return this.canceled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.canceled = cancel;
    }

    public boolean isPassOriginal() {
        return passOriginal;
    }

    public void setPassOriginal(boolean passOriginal) {
        this.passOriginal = passOriginal;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
