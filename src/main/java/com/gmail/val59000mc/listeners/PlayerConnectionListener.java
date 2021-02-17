package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.exceptions.UhcPlayerJoinException;
import com.gmail.val59000mc.exceptions.UhcTeamException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.players.PlayerState;
import com.gmail.val59000mc.players.PlayersManager;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.threads.KillDisconnectedPlayerThread;
import com.gmail.val59000mc.threads.TeleportPlayersThread;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener{

	private final GameManager gameManager;
	private final PlayersManager playersManager;

	public PlayerConnectionListener(GameManager gameManager, PlayersManager playersManager){
		this.gameManager = gameManager;
		this.playersManager = playersManager;
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerLogin(PlayerLoginEvent event){
		// Player is not allowed to join so don't create UhcPlayer. (Server full, whitelist, ban, ...)
		if (event.getResult() != Result.ALLOWED){
			return;
		}
		
		try{
			boolean allowedToJoin = playersManager.isPlayerAllowedToJoin(event.getPlayer());

			if (allowedToJoin){
				// Create player if not existent.
				playersManager.getOrCreateUhcPlayer(event.getPlayer());
			}else{
				throw new UhcPlayerJoinException("An unexpected error as occured.");
			}
		}catch(final UhcPlayerJoinException e){
			event.setKickMessage(e.getMessage());
			event.setResult(Result.KICK_OTHER);
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerJoin(final PlayerJoinEvent event){
		Player player = event.getPlayer();
		UhcPlayer uhcPlayer = playersManager.getUhcPlayer(player);

		GameState gameState = gameManager.getGameState();
		if (gameState == GameState.STARTING || gameState == GameState.PLAYING) {
			if (uhcPlayer.getState().equals(PlayerState.PLAYING) && uhcPlayer.isNeedInitialize()) {
				TeleportPlayersThread.teleportPlayer(uhcPlayer);
				if (gameState == GameState.PLAYING) playersManager.initializePlayer(player);
			}
		}

		Bukkit.getScheduler().runTaskLater(UhcCore.getPlugin(), () -> playersManager.playerJoinsTheGame(event.getPlayer()), 1);
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerDisconnect(PlayerQuitEvent event){
		Player player = event.getPlayer();
		UhcPlayer uhcPlayer = playersManager.getUhcPlayer(player);

		final GameState gameState = gameManager.getGameState();
		if (gameState.equals(GameState.WAITING)) {
			try {
				uhcPlayer.getTeam().leave(uhcPlayer);
			}
			catch (UhcTeamException ignore) {
			}
		}

		if(gameState.equals(GameState.STARTING) && !uhcPlayer.getState().equals(PlayerState.PLAYING)) {
			playersManager.setPlayerSpectateAtLobby(uhcPlayer);
			gameManager.broadcastInfoMessage(uhcPlayer.getName()+" has left while the game was starting and has been killed.");
			playersManager.strikeLightning(uhcPlayer);

			playersManager.getPlayersList().remove(uhcPlayer);
		}

		if(gameState.equals(GameState.PLAYING) || gameState.equals(GameState.STARTING) || gameState.equals(GameState.DEATHMATCH)){
			if(gameManager.getConfiguration().getEnableKillDisconnectedPlayers() && uhcPlayer.getState().equals(PlayerState.PLAYING)){

				KillDisconnectedPlayerThread killDisconnectedPlayerThread = new KillDisconnectedPlayerThread(
						player.getUniqueId(),
						gameManager.getConfiguration().getMaxDisconnectPlayersTime()
				);

				Bukkit.getScheduler().runTaskLaterAsynchronously(UhcCore.getPlugin(), killDisconnectedPlayerThread,1);
			}
			if (gameManager.getConfiguration().getSpawnOfflinePlayers() && gameState.equals(GameState.PLAYING) && uhcPlayer.getState().equals(PlayerState.PLAYING)) {
				if (!uhcPlayer.isNeedInitialize()) playersManager.spawnOfflineZombieFor(player);
			}
			playersManager.checkIfRemainingPlayers();
		}
	}

}