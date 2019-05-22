package com.bluecreeper111.minions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import com.bluecreeper111.minions.commands.MinionCMD;
import com.bluecreeper111.minions.events.MinionSpawn;
import com.bluecreeper111.minions.events.ModeChangeListener;
import com.bluecreeper111.minions.events.PickupItemExp;
import com.bluecreeper111.minions.events.SpawnerAFK;
import com.bluecreeper111.minions.gui.GuiMain;
import com.bluecreeper111.minions.utilities.Farm;
import com.bluecreeper111.minions.utilities.Kill;
import com.bluecreeper111.minions.utilities.Methods;
import com.bluecreeper111.minions.utilities.Mine;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

public class Main extends JavaPlugin {
	
	public void onEnable() {
		registerCommands();
		registerEvents();
		configuration();
		loadInventories();
	}
	
	@SuppressWarnings("deprecation")
	public void onDisable() {
		// Giving all players minions back
		for (String player : MinionSpawn.data.getConfigurationSection("players").getKeys(false)) {
			if (MinionSpawn.data.getBoolean("players." + player + ".minionplaced")) {
				MinionSpawn.data.set("players." + player + ".minionplaced", false);
				try {
					MinionSpawn.data.save(MinionSpawn.datafile);
				} catch (IOException e) {
					e.printStackTrace();
				}
				NPC npc = CitizensAPI.getNPCRegistry().getByUniqueId(Bukkit.getOfflinePlayer(player).getUniqueId());
				try {
					Methods.giveMinion(Bukkit.getPlayer(player));
				} catch (Exception e) {
					npc.getStoredLocation().getWorld().dropItem(npc.getStoredLocation(), Methods.minionItem());
				}
				npc.destroy();
			}
		}
		for (List<Block> list : SpawnerAFK.afkspawners.values()) {
			for (Block sp : list) {
				int x = sp.getLocation().getBlockX();
				int y = sp.getLocation().getBlockY();
				int z = sp.getLocation().getBlockZ();
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "blockdata " + x + " " + y + " " + z + " {RequiredPlayerRange:16}");
			}
		}
		saveInventories();
	}
	
	private void registerCommands() {
		getCommand("minion").setExecutor(new MinionCMD());
	}
	
	private void registerEvents() {
		PluginManager pm = Bukkit.getPluginManager();
		new PickupItemExp().runTaskTimer(this, 60L, 60L);
		new SpawnerAFK().runTaskTimer(this, 600L, 600L);
		new Mine(this).runTaskTimer(this, 20L, 20L);
		new Kill(this).runTaskTimer(this, 20L, 20L);
		new Farm(this).runTaskTimer(this, 20L, 20L);
		pm.registerEvents(new MinionSpawn(this), this);
		pm.registerEvents(new GuiMain(this), this);
		pm.registerEvents(new ModeChangeListener(), this);
	}
	private void configuration() {
		if (!new File(this.getDataFolder(), "config.yml").exists()) {
			saveResource("config.yml", false);
		}
		if (!MinionSpawn.datafile.exists()) {
			try {
				MinionSpawn.datafile.createNewFile();
				MinionSpawn.data.save(MinionSpawn.datafile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	// Saving minion inventories/exp
	private void saveInventories() {
		for (UUID id : PickupItemExp.minioninventory.keySet()) {
			Inventory inv = PickupItemExp.minioninventory.getOrDefault(id, Bukkit.createInventory(null, 27, "§6§lMinion Inventory"));
			ItemStack[] items = inv.getContents();
			MinionSpawn.data.set("minions." + id.toString() + ".items", items);
		}
		for (UUID id : PickupItemExp.minionexp.keySet()) {
			int amount = PickupItemExp.minionexp.getOrDefault(id, 0);
			MinionSpawn.data.set("minions." + id.toString() + ".exp", amount);
			
		}
		Methods.saveFile(MinionSpawn.datafile, MinionSpawn.data);
	}
	// Loading minion inventories/exp
	private void loadInventories() {
		try {
			MinionSpawn.data.getConfigurationSection("minions").getKeys(false);
		} catch (NullPointerException e) {
			return;
		}
		for (Iterator<String> iterator = MinionSpawn.data.getConfigurationSection("minions").getKeys(false).iterator(); iterator.hasNext();) {
			String id = iterator.next();
			UUID uuid = UUID.fromString(id);
			@SuppressWarnings("unchecked")
			ArrayList<ItemStack> iteme = (ArrayList<ItemStack>) MinionSpawn.data.get("minions." + id + ".items");
			ItemStack[] items = iteme.toArray(new ItemStack[iteme.size()]);
			Inventory inv = Bukkit.createInventory(null, 27, "§6§lMinion Inventory");
			for (ItemStack item : items) {
				if (item == null || item.getType() == Material.AIR) continue;
				inv.addItem(item);
			}
			PickupItemExp.minioninventory.put(uuid, inv);
			int exp = MinionSpawn.data.getInt("minions." + id + ".items");
			PickupItemExp.minionexp.put(uuid, exp);
			iterator.remove();
		}
	}

}
