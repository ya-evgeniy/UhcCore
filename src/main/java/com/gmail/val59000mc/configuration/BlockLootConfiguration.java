package com.gmail.val59000mc.configuration;

import com.gmail.val59000mc.exceptions.ParseException;
import com.gmail.val59000mc.utils.JsonItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class BlockLootConfiguration {

	private Material material;
	private ItemStack loot;
	private int addXp;

	private boolean additionalXpEnabled = false;
	private int additionalXpRangeMin = 0;
	private int additionalXpRangeMax = 0;

	private List<FortuneAdditionalItems> fortuneItems = new ArrayList<>();

	private List<Material> miningTools = new ArrayList<>();
	
	public BlockLootConfiguration() {
		this.material = Material.AIR;
		this.loot = new ItemStack(material);
		this.addXp = 0;
	}
	
	public boolean parseConfiguration(ConfigurationSection section){
		if (section == null){
			return false;
		}

		try{
			material = Material.valueOf(section.getName());
		}catch(IllegalArgumentException e){
			Bukkit.getLogger().warning("[UhcCore] Couldn't parse section '"+section.getName()+"' in block-loot. This is not an existing block type. Ignoring it.");
			return false;
		}
		
		String itemStr = section.getString("loot");

		if (itemStr == null){
			Bukkit.getLogger().warning("[UhcCore] Couldn't parse section '"+section.getName()+"' in block-loot. Missing loot item.");
			return false;
		}

		try {
			loot = JsonItemUtils.getItemFromJson(itemStr);
		}catch (ParseException ex){
			Bukkit.getLogger().warning("[UhcCore] Couldn't parse loot '"+material.toString()+"' in block-loot.");
			ex.printStackTrace();
			return false;
		}

		List<String> miningToolNames = section.getStringList("mining-tools");
		for (String miningToolName : miningToolNames) {
			Material miningTool = Material.matchMaterial(miningToolName);
			if (miningTool != null) this.miningTools.add(miningTool);
		}

		addXp = section.getInt("add-xp",0);

		final ConfigurationSection additionalXpSection = section.getConfigurationSection("additional-xp-range");
		if (additionalXpSection != null) {
			additionalXpEnabled = true;
			additionalXpRangeMin = additionalXpSection.getInt("min", 0);
			additionalXpRangeMax = additionalXpSection.getInt("max", 0);
		}

		final List<LinkedHashMap<String, Object>> fortune = (List<LinkedHashMap<String, Object>>) section.getList("fortune");
		if (fortune != null) {
			for (LinkedHashMap<String, Object> element : fortune) {
				final Object level = element.get("level");
				final Object additionalCount = element.get("additional-count");
				final Object chance = element.get("chance");

				if (level == null || additionalCount == null || chance == null) continue;

				final FortuneAdditionalItems items = new FortuneAdditionalItems(((Integer) level), ((Integer) additionalCount), ((Integer) chance));
				this.fortuneItems.add(items);
			}
		}

		return true;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public ItemStack getLoot() {
		return loot;
	}
	
	public int getAddXp() {
		return addXp;
	}

	public boolean isAdditionalXpEnabled() {
		return additionalXpEnabled;
	}

	public int getAdditionalXpRangeMin() {
		return additionalXpRangeMin;
	}

	public int getAdditionalXpRangeMax() {
		return additionalXpRangeMax;
	}

	public List<FortuneAdditionalItems> getFortuneItems() {
		return fortuneItems;
	}

	public List<Material> getMiningTools() {
		return miningTools;
	}

	public static class FortuneAdditionalItems {

		private int level = 0;
		private int additionalCount = 0;
		private int chance = 0;

		public FortuneAdditionalItems(int level, int additionalCount, int chance) {
			this.level = level;
			this.additionalCount = additionalCount;
			this.chance = max(0, min(100, chance));
		}

		public int getLevel() {
			return level;
		}

		public int getAdditionalCount() {
			return additionalCount;
		}

		public int getChance() {
			return chance;
		}

	}

}