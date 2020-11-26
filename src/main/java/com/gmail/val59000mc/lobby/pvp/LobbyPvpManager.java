package com.gmail.val59000mc.lobby.pvp;

import com.gmail.val59000mc.configuration.LobbyPvpConfiguration;
import com.gmail.val59000mc.customitems.UhcItems;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.lobby.pvp.zone.Zone;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.stream.Stream;

public class LobbyPvpManager {

    private final GameManager gameManager;

    private Set<UUID> playersInZone = new HashSet<>();

    public LobbyPvpManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void addPlayer(Player player) {
        player.setFoodLevel(7);
        this.playersInZone.add(player.getUniqueId());

        LobbyPvpConfiguration conf = this.gameManager.getLobbyPvpConfiguration();
        conf.getEquipmentContainer().equip(player, true);

        for (PotionEffect effect : player.getActivePotionEffects()) player.removePotionEffect(effect.getType());
        for (PotionEffect effect : conf.getEffects()) effect.apply(player);
    }

    public void removePlayer(Player player) {
        this.playersInZone.remove(player.getUniqueId());

        player.getInventory().clear();
        for (PotionEffect effect : player.getActivePotionEffects()) player.removePotionEffect(effect.getType());

        player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 99999999, 0, true, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 99999999, 0, true, false));
        player.setHealth(20);
        player.setExhaustion(20);
        player.setFoodLevel(20);
        player.setExp(0);

        UhcItems.giveLobbyItemsTo(player);
    }

    public void justRemove(UUID uniqueId) {
        this.playersInZone.remove(uniqueId);
    }

    public boolean inZone(UUID uniqueId) {
        return playersInZone.contains(uniqueId);
    }

    public boolean nowInZone(Location location) {
        List<Zone> zones = this.gameManager.getLobbyPvpConfiguration().getZones();

        Location lobbyLocation = gameManager.getLobby().getLoc();
        if (lobbyLocation == null) return false;
        if (!Objects.equals(location.getWorld(), lobbyLocation.getWorld())) return false;

        return zones.stream().anyMatch(zone -> zone.inZone(
                location.getX(),
                location.getY(),
                location.getZ()
        ));
    }

    public Stream<UUID> stream() {
        return this.playersInZone.stream();
    }

}