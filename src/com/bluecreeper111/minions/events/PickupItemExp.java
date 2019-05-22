package com.bluecreeper111.minions.events;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.bluecreeper111.minions.utilities.Methods;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

public class PickupItemExp extends BukkitRunnable {
	
	public static HashMap<UUID, Inventory> minioninventory = new HashMap<>();
	public static HashMap<UUID, Integer> minionexp = new HashMap<>();
	
	// Handles picking up items/exp
	public void run() {
		try {
			MinionSpawn.data.getConfigurationSection("players").getKeys(false);
		} catch (NullPointerException e) {
			return;
		}
		for (String player : MinionSpawn.data.getConfigurationSection("players").getKeys(false)) {
			@SuppressWarnings("deprecation")
			OfflinePlayer p = Bukkit.getOfflinePlayer(player);
			NPC minion = CitizensAPI.getNPCRegistry().getByUniqueId(p.getUniqueId()) != null ? CitizensAPI.getNPCRegistry().getByUniqueId(p.getUniqueId()) : null;
			if (minion == null) continue;
			Collection<Entity> entities = minion.getStoredLocation().getWorld().getNearbyEntities(minion.getStoredLocation(), 2, 2, 2);
			for (Entity entity : entities) {
				if (entity.getType() == EntityType.DROPPED_ITEM) {
					Item ent = (Item) entity;
					entity.remove();
					ItemStack add = ent.getItemStack();
					Methods.addToInventory(minion.getUniqueId(), add);
			    } else if (entity.getType() == EntityType.EXPERIENCE_ORB) {
			    	ExperienceOrb exp = (ExperienceOrb) entity;
			    	entity.remove();
			    	int addxp = (minionexp.getOrDefault(minion.getUniqueId(), 0)) + exp.getExperience();
			    	minionexp.put(minion.getUniqueId(), addxp);
			    }
			}
		}
	}

}
