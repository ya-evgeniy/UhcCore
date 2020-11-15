package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.configuration.MainConfiguration;
import com.gmail.val59000mc.players.PlayerState;
import com.gmail.val59000mc.players.PlayersManager;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMovementListener implements Listener{

    private final PlayersManager playersManager;
    private final MainConfiguration configuration;

    public PlayerMovementListener(PlayersManager playersManager, MainConfiguration configuration){
        this.playersManager = playersManager;
        this.configuration = configuration;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        handleLobbyPlayers(event);
        handleFrozenPlayers(event);
    }

    private void handleLobbyPlayers(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        UhcPlayer uhcPlayer = playersManager.getUhcPlayer(player);
        if (uhcPlayer.getState().equals(PlayerState.WAITING) && player.getLocation().getY() < 0) {
            e.getPlayer().teleport(configuration.getLobbySpawnLocation());
        }
    }

    private void handleFrozenPlayers(PlayerMoveEvent e){
        Player player = e.getPlayer();
        UhcPlayer uhcPlayer = playersManager.getUhcPlayer(player);
        if (uhcPlayer.isFrozen()){
            Location freezeLoc = uhcPlayer.getFreezeLocation();
            Location toLoc = e.getTo();

            if (toLoc.getBlockX() != freezeLoc.getBlockX() || toLoc.getBlockZ() != freezeLoc.getBlockZ()){
                Location newLoc = toLoc.clone();
                newLoc.setX(freezeLoc.getBlockX() + .5);
                newLoc.setZ(freezeLoc.getBlockZ() + .5);

                player.teleport(newLoc);
            }
        }
    }

}