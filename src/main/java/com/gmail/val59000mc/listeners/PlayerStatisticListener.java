package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.players.PlayerState;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerStatisticListener implements Listener {

    private final @NotNull GameManager gameManager;

    public PlayerStatisticListener(@NotNull GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void on(PlayerStatisticIncrementEvent event) {
        try {
            final UhcPlayer uhcPlayer = gameManager.getPlayersManager().getUhcPlayer(event.getPlayer());
            if (uhcPlayer.getState() != PlayerState.PLAYING) {
                event.setCancelled(true);
            }
        } catch (Exception ignore) {
        }
    }

}
