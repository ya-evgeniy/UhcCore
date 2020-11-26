package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.configuration.LobbyPvpConfiguration;
import com.gmail.val59000mc.events.UhcGameStateChangedEvent;
import com.gmail.val59000mc.events.UhcLobbyPlayerDamageByPlayerEvent;
import com.gmail.val59000mc.events.UhcLobbyPlayerDamageEvent;
import com.gmail.val59000mc.events.UhcLobbyPlayerKilledByPlayerEvent;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.lobby.pvp.LobbyPvpManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

public class LobbyPvpListener implements Listener {

    private GameManager gameManager;
    private LobbyPvpManager lobbyPvpManager;

    public LobbyPvpListener(GameManager gameManager, LobbyPvpManager lobbyPvpManager) {
        this.gameManager = gameManager;
        this.lobbyPvpManager = lobbyPvpManager;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void on(UhcLobbyPlayerDamageByPlayerEvent event) {
        Player player = event.getPlayer();
        Player damager = event.getDamager();

        boolean playerInZone = lobbyPvpManager.inZone(player.getUniqueId());
        boolean damagerInZone = lobbyPvpManager.inZone(damager.getUniqueId());

        if (!playerInZone || !damagerInZone) return;
        event.setPassOriginal(true);

        if (player.getHealth() - event.getFinalDamage() > 0.0) return;
        event.setCancelled(true);

        Bukkit.getPluginManager().callEvent(new UhcLobbyPlayerKilledByPlayerEvent(player, damager));
        LobbyPvpConfiguration configuration = gameManager.getLobbyPvpConfiguration();

        player.setHealth(20);
        damager.setHealth(20);

        if (configuration.isUseCustomRespawnLocation()) {
            World world = gameManager.getLobby().getLoc().getWorld();
            Location location = configuration.getCustomRespawnLocation();

            player.teleport(new Location(world, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch()));
        }
        else {
            player.teleport(gameManager.getLobby().getLoc());
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void on(UhcLobbyPlayerDamageEvent event) {
        Player player = event.getPlayer();
        boolean playerInZone = lobbyPvpManager.inZone(player.getUniqueId());

        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK
                && event.getCause() != EntityDamageEvent.DamageCause.PROJECTILE) return;

        if (!playerInZone) return;
        event.setPassOriginal(true);

        if (player.getHealth() - event.getFinalDamage() > 0.0) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void on(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            boolean playerInZone = lobbyPvpManager.inZone(player.getUniqueId());
            if (playerInZone) {
                GameState gameState = gameManager.getGameState();
                if (gameState != GameState.WAITING) return;

                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void on(PlayerFishEvent event) {
        if (!lobbyPvpManager.inZone(event.getPlayer().getUniqueId())) return;
        if (gameManager.getGameState() != GameState.WAITING) return;
        
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void on(PlayerMoveEvent event) {
        handleMove(event.getPlayer(), event.getFrom(), event.getTo(), true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void on(PlayerTeleportEvent event) {
        handleMove(event.getPlayer(), event.getFrom(), event.getTo(), true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void on(PlayerQuitEvent event) {
        if (gameManager.getGameState() == GameState.WAITING) lobbyPvpManager.removePlayer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void on(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        Location location = player.getLocation();

        GameMode gamemode = event.getNewGameMode();
        if (gamemode == GameMode.CREATIVE) {
            if (gameManager.getGameState() != GameState.WAITING) return;

            boolean inZone = lobbyPvpManager.inZone(player.getUniqueId());
            if (inZone) lobbyPvpManager.removePlayer(player);
        }
        else {
            handleMove(player, location, location, false);
        }
    }

    @EventHandler
    public void on(InventoryClickEvent event) {
        if (lobbyPvpManager.inZone(event.getWhoClicked().getUniqueId())) {
            if (gameManager.getGameState() == GameState.WAITING) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void on(PlayerDropItemEvent event) {
        if (lobbyPvpManager.inZone(event.getPlayer().getUniqueId())) {
            if (gameManager.getGameState() == GameState.WAITING) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void on(UhcGameStateChangedEvent event) {
        if (event.getNewGameState().ordinal() > GameState.WAITING.ordinal()) {
            HandlerList.unregisterAll(this);
        }
    }

    private void handleMove(Player player, Location from, Location to, boolean compareLocation) {
        if (player.getGameMode() == GameMode.CREATIVE) return;

        if (from == null || to == null) return;
        if (compareLocation && from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()) return;

        boolean inZone = lobbyPvpManager.inZone(player.getUniqueId());
        boolean nowInZone = lobbyPvpManager.nowInZone(to);

        if (!inZone && !nowInZone) return;

        if (nowInZone && !inZone) {
            lobbyPvpManager.addPlayer(player);
        }

        if (inZone && !nowInZone) {
            GameState gameState = gameManager.getGameState();
            if (gameState == GameState.WAITING) this.lobbyPvpManager.removePlayer(player);
        }
    }

}
