package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.configuration.LobbyPvpConfiguration;
import com.gmail.val59000mc.events.UhcGameStateChangedEvent;
import com.gmail.val59000mc.events.UhcLobbyPlayerDamageByPlayerEvent;
import com.gmail.val59000mc.events.UhcLobbyPlayerDamageEvent;
import com.gmail.val59000mc.events.UhcLobbyPlayerKilledByPlayerEvent;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.lobby.pvp.LobbyPvpManager;
import com.gmail.val59000mc.lobby.pvp.PlayerLobbyPvpInventory;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.utils.equipment.Equipment;
import com.gmail.val59000mc.utils.equipment.EquipmentContainer;
import com.gmail.val59000mc.utils.equipment.EquipmentSlot;
import com.gmail.val59000mc.utils.equipment.EquipmentUtils;
import com.gmail.val59000mc.utils.equipment.slot.OffhandEquipmentSlot;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
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
    public void on(InventoryDragEvent event) {
        if (lobbyPvpManager.inZone(event.getWhoClicked().getUniqueId())) {
            if (gameManager.getGameState() == GameState.WAITING) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(InventoryClickEvent event) {
        if (lobbyPvpManager.inZone(event.getWhoClicked().getUniqueId())) {
            if (gameManager.getGameState() == GameState.WAITING) {
                if (event.getAction() != InventoryAction.PICKUP_ALL && event.getAction() != InventoryAction.PLACE_ALL && event.getAction() != InventoryAction.SWAP_WITH_CURSOR) {
                    event.setCancelled(true);
                    return;
                }

                try {
                    final Player player = (Player) event.getWhoClicked();
                    final UhcPlayer uhcPlayer = gameManager.getPlayersManager().getUhcPlayer(player);
                    final PlayerLobbyPvpInventory inventory = uhcPlayer.getLobbyPvpInventory();

                    final EquipmentContainer equipmentContainer = gameManager.getLobbyPvpConfiguration().getEquipmentContainer();

                    final int targetIndex = event.getSlot();
                    final EquipmentSlot targetSlot = EquipmentUtils.from(targetIndex);
                    final String targetId = inventory.getItemId(equipmentContainer, targetSlot);

                    if (targetSlot != null && targetId != null) {
                        final Equipment equipment = equipmentContainer.getEquipmentByItemId(targetId);
                        if (equipment != null && !equipment.isSlotMutable()) {
                            event.setCancelled(true);
                            return;
                        }
                    }

                    if (inventory.getCursorId() == null) {

                        if (targetSlot == null || targetId == null) {
                            event.setCancelled(true);
                            return;
                        }

                        final ItemStack item = player.getInventory().getItem(event.getSlot());
                        if (item == null || item.getType().equals(Material.AIR)) {
                            event.setCancelled(true);
                            return;
                        }

                        inventory.setSourceIndex(targetIndex);
                        inventory.setCursorId(targetId);
                        inventory.setCursorSlot(targetSlot);

                        return;
                    }


                    if (targetSlot == null) {
                        event.setCancelled(true);
                        return;
                    }

                    inventory.getPlayerEquipment().put(inventory.getCursorId(), targetSlot);
                    inventory.setChanged(true);

                    final ItemStack item = player.getInventory().getItem(event.getSlot());
                    if (item == null || item.getType().equals(Material.AIR)) {
                        if (targetId != null) {
                            inventory.getPlayerEquipment().put(targetId, inventory.getCursorSlot());
                        }
                    }

                    if (targetId == null) {
                        inventory.setSourceIndex(-1);
                        inventory.setCursorId(null);
                        inventory.setCursorSlot(null);
                    }
                    else {
                        inventory.getPlayerEquipment().remove(targetId);
                        inventory.setSourceIndex(targetIndex);
                        inventory.setCursorId(targetId);
                        inventory.setCursorSlot(targetSlot);
                    }
                } catch (Exception e) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void on(PlayerSwapHandItemsEvent event) {
        final Player player = event.getPlayer();
        if (lobbyPvpManager.inZone(player.getUniqueId())) {
            if (gameManager.getGameState() == GameState.WAITING) {
                final int selectedSlot = player.getInventory().getHeldItemSlot();

                final EquipmentSlot sourceSlot = EquipmentUtils.from(selectedSlot);
                final EquipmentSlot targetSlot = new OffhandEquipmentSlot();

                try {
                    final UhcPlayer uhcPlayer = gameManager.getPlayersManager().getUhcPlayer(player);
                    final PlayerLobbyPvpInventory inventory = uhcPlayer.getLobbyPvpInventory();

                    final EquipmentContainer equipmentContainer = gameManager.getLobbyPvpConfiguration().getEquipmentContainer();

                    final String sourceId = inventory.getItemId(equipmentContainer, sourceSlot);
                    final String targetId = inventory.getItemId(equipmentContainer, targetSlot);

                    if (sourceId == null && targetId == null) {
                        return;
                    }

                    final Equipment sourceEquipment = equipmentContainer.getEquipmentByItemId(sourceId);
                    final Equipment targetEquipment = equipmentContainer.getEquipmentByItemId(targetId);

                    if (sourceEquipment != null && !sourceEquipment.isSlotMutable()) {
                        event.setCancelled(true);
                        return;
                    }

                    if (targetEquipment != null && !targetEquipment.isSlotMutable()) {
                        event.setCancelled(true);
                        return;
                    }

                    if (sourceId != null) {
                        inventory.getPlayerEquipment().put(sourceId, targetSlot);
                        inventory.setChanged(true);
                    }

                    if (targetId != null) {
                        inventory.getPlayerEquipment().put(targetId, sourceSlot);
                        inventory.setChanged(true);
                    }
                }
                catch (Exception e) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void on(PlayerDropItemEvent event) {
        if (lobbyPvpManager.inZone(event.getPlayer().getUniqueId())) {
            if (gameManager.getGameState() == GameState.WAITING) {
                try {
                    final Player player = event.getPlayer();

                    final UhcPlayer uhcPlayer = gameManager.getPlayersManager().getUhcPlayer(player);
                    final PlayerLobbyPvpInventory inventory = uhcPlayer.getLobbyPvpInventory();

                    final EquipmentContainer equipmentContainer = gameManager.getLobbyPvpConfiguration().getEquipmentContainer();

                    if (inventory.getCursorId() == null) {
                        event.setCancelled(true);
                        return;
                    }

                    String targetId = inventory.getItemId(equipmentContainer, inventory.getCursorSlot());
                    if (targetId == null || targetId.equals(inventory.getCursorId())) {
                        inventory.getCursorSlot().equip(player.getInventory(), event.getItemDrop().getItemStack());

                        inventory.getPlayerEquipment().put(inventory.getCursorId(), inventory.getCursorSlot());
                        inventory.setChanged(true);
                        inventory.setSourceIndex(-1);
                        inventory.setCursorId(null);
                        inventory.setCursorSlot(null);

                        event.getItemDrop().remove();
                        return;
                    }

                    for (int i = 0; i < 41; i++) {
                        final EquipmentSlot targetSlot = EquipmentUtils.from(i);
                        if (targetSlot == null) continue;

                        targetId = inventory.getItemId(equipmentContainer, targetSlot);
                        if (targetId == null) {
                            targetSlot.equip(player.getInventory(), event.getItemDrop().getItemStack());

                            inventory.getPlayerEquipment().put(inventory.getCursorId(), targetSlot);
                            inventory.setChanged(true);
                            inventory.setSourceIndex(-1);
                            inventory.setCursorId(null);
                            inventory.setCursorSlot(null);

                            event.getItemDrop().remove();
                            return;
                        }
                    }

                    event.getItemDrop().remove();
                }
                catch (Exception e) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void on(UhcGameStateChangedEvent event) {
        if (event.getNewGameState().ordinal() > GameState.WAITING.ordinal()) {
            HandlerList.unregisterAll(this);

            List<Player> playersInZone = lobbyPvpManager.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toList());
            for (Player player : playersInZone) {

                try {
                    final UhcPlayer uhcPlayer = gameManager.getPlayersManager().getUhcPlayer(player);
                    uhcPlayer.getLobbyPvpInventory().endChanging(gameManager.getLobbyPvpConfiguration().getEquipmentContainer());
                    if (uhcPlayer.getLobbyPvpInventory().isChanged()) {
                        lobbyPvpManager.getDbLobbyPvp().save(uhcPlayer);
                        uhcPlayer.getLobbyPvpInventory().setChanged(false);
                    }
                }
                catch (Exception ignore) {}

                lobbyPvpManager.justRemove(player.getUniqueId());
                player.getInventory().clear();
                player.setFoodLevel(20);
                player.setSaturation(20);
                player.setHealth(20.0);
            }
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

            try {
                final UhcPlayer uhcPlayer = gameManager.getPlayersManager().getUhcPlayer(player);
                if (uhcPlayer.getLobbyPvpInventory().isChanged()) {
                    lobbyPvpManager.getDbLobbyPvp().save(uhcPlayer);
                    uhcPlayer.getLobbyPvpInventory().setChanged(false);
                }
            }
            catch (Exception ignore) {}
        }
    }

}
