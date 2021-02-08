package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.events.UhcLobbyPlayerDamageByPlayerEvent;
import com.gmail.val59000mc.events.UhcLobbyPlayerDamageEvent;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.PlayerState;
import com.gmail.val59000mc.players.PlayersManager;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.projectiles.ProjectileSource;

public class PlayerDamageListener implements Listener{

	private final GameManager gameManager;
	private final boolean friendlyFire;

	public PlayerDamageListener(GameManager gameManager){
		this.gameManager = gameManager;
		friendlyFire = gameManager.getConfiguration().getEnableFriendlyFire();
	}
	
	@EventHandler(priority=EventPriority.NORMAL)
	public void onPlayerDamage(EntityDamageByEntityEvent event){
		boolean skip = handleLobbyEntityDamageByEntityEvent(event);
		if (skip) return;

		handlePvpAndFriendlyFire(event);
		handleLightningStrike(event);
		handleArrow(event);
	}

	@EventHandler(priority=EventPriority.NORMAL)
	public void onPlayerDamage(EntityDamageEvent event){
		handleAnyDamage(event);
	}
	
	///////////////////////
	// EntityDamageEvent //
	///////////////////////

	private void handleAnyDamage(EntityDamageEvent event){
		if(event.getEntity() instanceof Player){
			Player player = (Player) event.getEntity();

			if (gameManager.getGameState() == GameState.WAITING) {
				UhcLobbyPlayerDamageEvent damageEvent = new UhcLobbyPlayerDamageEvent(player, event.getFinalDamage(), event.getCause());
				Bukkit.getPluginManager().callEvent(damageEvent);

				event.setCancelled(damageEvent.isCancelled());
				if (damageEvent.isPassOriginal()) return;
			}

			if (gameManager.getGameState() == GameState.STARTING) {
				event.setCancelled(true);
				return;
			}

			PlayersManager pm = gameManager.getPlayersManager();
			UhcPlayer uhcPlayer = pm.getUhcPlayer(player);

			PlayerState uhcPlayerState = uhcPlayer.getState();
			if(uhcPlayerState.equals(PlayerState.WAITING) || uhcPlayerState.equals(PlayerState.DEAD)){
				event.setCancelled(true);
				return;
			}

			if (uhcPlayer.isFrozen()){
				event.setCancelled(true);
			}
		}
	}
	
	///////////////////////////////
	// EntityDamageByEntityEvent //
	///////////////////////////////
	
	private void handlePvpAndFriendlyFire(EntityDamageByEntityEvent event){

		PlayersManager pm = gameManager.getPlayersManager();
		
		
		if(event.getDamager() instanceof Player && event.getEntity() instanceof Player){
			if(!gameManager.getPvp()){
				event.setCancelled(true);
				return;
			}
			
			Player damager = (Player) event.getDamager();
			Player damaged = (Player) event.getEntity();
			UhcPlayer uhcDamager = pm.getUhcPlayer(damager);
			UhcPlayer uhcDamaged = pm.getUhcPlayer(damaged);

			if(!friendlyFire && uhcDamager.getState().equals(PlayerState.PLAYING) && uhcDamager.isInTeamWith(uhcDamaged)){
				damager.sendMessage(Lang.PLAYERS_FF_OFF);
				event.setCancelled(true);
			}
		}
	}
	
	private void handleLightningStrike(EntityDamageByEntityEvent event){
		if(event.getDamager() instanceof LightningStrike && event.getEntity() instanceof Player){
			event.setCancelled(true);
		}
	}
	
	private void handleArrow(EntityDamageByEntityEvent event){

		PlayersManager pm = gameManager.getPlayersManager();
		
		if(event.getEntity() instanceof Player && event.getDamager() instanceof Arrow){
			Projectile arrow = (Projectile) event.getDamager();
			final Player shot = (Player) event.getEntity();
			if(arrow.getShooter() instanceof Player){
				
				if(!gameManager.getPvp()){
					event.setCancelled(true);
					return;
				}

				UhcPlayer uhcDamager = pm.getUhcPlayer((Player) arrow.getShooter());
				UhcPlayer uhcDamaged = pm.getUhcPlayer(shot);

				if(!friendlyFire && uhcDamager.getState().equals(PlayerState.PLAYING) && uhcDamager.isInTeamWith(uhcDamaged)){
					uhcDamager.sendMessage(Lang.PLAYERS_FF_OFF);
					event.setCancelled(true);
				}
			}
		}
	}

	///////////////////////////////
	// Lobby Pvp Listener        //
	///////////////////////////////

	private boolean handleLobbyEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		if (event.getEntity().getType() != EntityType.PLAYER) return false;
		Player player = (Player) event.getEntity();

		if (player.getGameMode() == GameMode.CREATIVE) return false;

		Player damager;
		if (event.getDamager() instanceof Player) {
			damager = (Player) event.getDamager();
		}
		else if (event.getDamager() instanceof Projectile) {
			Projectile projectile = (Projectile) event.getDamager();
			ProjectileSource shooter = projectile.getShooter();
			if (shooter instanceof Player) {
				damager = (Player) shooter;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}

		if (gameManager.getPvp()) return false;

		GameState gameState = gameManager.getGameState();
		if (gameState != GameState.WAITING) return false;

		UhcLobbyPlayerDamageByPlayerEvent damageEvent = new UhcLobbyPlayerDamageByPlayerEvent(player, damager, event.getFinalDamage());
		Bukkit.getPluginManager().callEvent(damageEvent);

		event.setCancelled(damageEvent.isCancelled());
		return damageEvent.isPassOriginal();
	}

}