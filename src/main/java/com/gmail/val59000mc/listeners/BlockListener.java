package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.configuration.BlockLootConfiguration;
import com.gmail.val59000mc.configuration.MainConfiguration;
import com.gmail.val59000mc.customitems.UhcItems;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.utils.RandomUtils;
import com.gmail.val59000mc.utils.UniversalMaterial;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;

public class BlockListener implements Listener{

	private final MainConfiguration configuration;
	private final Map<Material, BlockLootConfiguration> blockLoots;
	private final int maxBuildingHeight;
	
	public BlockListener(MainConfiguration configuration){
		this.configuration = configuration;
		blockLoots = configuration.getEnableBlockLoots() ? configuration.getBlockLoots() : new HashMap<>();
		maxBuildingHeight = configuration.getMaxBuildingHeight();
	}
	@EventHandler
	public void onBlockBreak(final BlockBreakEvent event){
		handleBlockLoot(event);
		handleShearedLeaves(event);
	}
	@EventHandler
	public void onBlockPlace(final BlockPlaceEvent event){
		handleMaxBuildingHeight(event);
	}

	@EventHandler
	public void onLeavesDecay(LeavesDecayEvent event){
		handleAppleDrops(event);
	}

	private void handleMaxBuildingHeight(BlockPlaceEvent e){
		if (maxBuildingHeight < 0 || e.getPlayer().getGameMode() != GameMode.SURVIVAL) return;

		if (e.getBlock().getY() > maxBuildingHeight){
			e.setCancelled(true);
			e.getPlayer().sendMessage(Lang.PLAYERS_BUILD_HEIGHT);
		}
	}

	private void handleBlockLoot(BlockBreakEvent event) {
		Block breakingBlock = event.getBlock();
		Material material = breakingBlock.getType();

		BlockLootConfiguration lootConf = this.blockLoots.get(material);
		if (lootConf == null) return;

		event.setDropItems(false);

		ItemStack mainHandItem = event.getPlayer().getInventory().getItemInMainHand();
		if (!lootConf.getMiningTools().isEmpty() && !lootConf.getMiningTools().contains(mainHandItem.getType())) return;

		Location centerBlockLocation = breakingBlock.getLocation().add(0.5, 0.5, 0.5);
		event.setExpToDrop(lootConf.getAddXp());

		World world = centerBlockLocation.getWorld();
		if (world != null) world.dropItemNaturally(centerBlockLocation, lootConf.getLoot().clone());

		if (lootConf.getAddXp() > 0) UhcItems.spawnExtraXp(centerBlockLocation, lootConf.getAddXp());
	}

	private void handleShearedLeaves(BlockBreakEvent e){
		if (!configuration.getAppleDropsFromShearing()){
			return;
		}

		if (!UniversalMaterial.isLeaves(e.getBlock().getType())){
			return;
		}

		if (e.getPlayer().getItemInHand().getType() == Material.SHEARS){
			Bukkit.getPluginManager().callEvent(new LeavesDecayEvent(e.getBlock()));
		}
	}

	private void handleAppleDrops(LeavesDecayEvent e){
		Block block = e.getBlock();
		Material type = block.getType();
		boolean isOak;

		if (configuration.getAppleDropsFromAllTrees()){
			if (type != UniversalMaterial.OAK_LEAVES.getType()) {
				e.getBlock().setType(UniversalMaterial.OAK_LEAVES.getType());
			}
			isOak = true;
		}else {
			isOak = type == UniversalMaterial.OAK_LEAVES.getType() || type == UniversalMaterial.DARK_OAK_LEAVES.getType();
		}

		if (!isOak){
			return; // Will never drop apples so drops don't need to increase
		}

		double percentage = configuration.getAppleDropPercentage()-0.5;

		if (percentage <= 0){
			return; // No added drops
		}

		// Number 0-100
		double random = RandomUtils.randomInteger(0, 200)/2D;

		if (random > percentage){
			return; // Number above percentage so no extra apples.
		}

		// Add apple to drops
		Bukkit.getScheduler().runTask(UhcCore.getPlugin(), () -> block.getWorld().dropItem(block.getLocation().add(.5, .5, .5), new ItemStack(Material.APPLE)));
	}

}