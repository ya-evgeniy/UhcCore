package com.gmail.val59000mc.threads;

import com.gmail.val59000mc.customitems.GameItem;
import com.gmail.val59000mc.customitems.UhcItems;
import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.kit.Kit;
import com.gmail.val59000mc.kit.KitsManager;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.players.UhcTeam;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class TeleportPlayersThread implements Runnable{

	private final GameManager gameManager;
	private final UhcTeam team;
	
	public TeleportPlayersThread(GameManager gameManager, UhcTeam team) {
		this.gameManager = gameManager;
		this.team = team;
	}

	@Override
	public void run() {
		for(UhcPlayer uhcPlayer : team.getMembers()) teleportPlayer(uhcPlayer);
	}

	public static void teleportPlayer(UhcPlayer uhcPlayer) {
		UhcTeam team = uhcPlayer.getTeam();
		if (team == null) return;

		Player player;
		try {
			player = uhcPlayer.getPlayer();
		}catch (UhcPlayerNotOnlineException ex){
			return;
		}

		Bukkit.getLogger().info("[UhcCore] Teleporting "+player.getName());

		uhcPlayer.freezePlayer(team.getStartingLocation());
		player.teleport(team.getStartingLocation());
		player.setFireTicks(0);

		uhcPlayer.setHasBeenTeleportedToLocation(true);
	}

}