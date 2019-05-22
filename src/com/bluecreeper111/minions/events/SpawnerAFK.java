package com.bluecreeper111.minions.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import com.bluecreeper111.minions.utilities.Methods;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

public class SpawnerAFK extends BukkitRunnable {
	
	public static HashMap<UUID, List<Block>> afkspawners = new HashMap<>();
	
	public void run() {
		try {
			MinionSpawn.data.getConfigurationSection("players").getKeys(false);
		} catch (NullPointerException e) {
			return;
		}
		for (String player : MinionSpawn.data.getConfigurationSection("players").getKeys(false)) {
			@SuppressWarnings("deprecation")
			OfflinePlayer p = Bukkit.getOfflinePlayer(player);
			String mode = MinionSpawn.data.getString("players." + player + ".mode") != null ? MinionSpawn.data.getString("players." + player + ".mode") : "notfound";
			if (!mode.equals("idle")) continue;
			NPC minion = CitizensAPI.getNPCRegistry().getByUniqueId(p.getUniqueId()) != null ? CitizensAPI.getNPCRegistry().getByUniqueId(p.getUniqueId()) : null;
			if (minion == null) continue;
			List<Block> blocks = Methods.getNearbyBlocks(minion.getStoredLocation(), 16);
			List<Block> removespawners = new ArrayList<Block>();
			minion.getStoredLocation().getChunk().load();
			for (Block b : blocks) {
				if (b.getType() == Material.MOB_SPAWNER) {
					List<Block> bl = afkspawners.getOrDefault(minion.getUniqueId(), new ArrayList<Block>());
					removespawners.add(b);
					if (bl.contains(b)) continue;
					bl.add(b);
					afkspawners.put(minion.getUniqueId(), bl);
					int x = b.getLocation().getBlockX();
					int y = b.getLocation().getBlockY();
					int z = b.getLocation().getBlockZ();
					boolean wasopped = p.isOp();
					p.setOp(true);
					Bukkit.dispatchCommand(p.getPlayer(), "blockdata " + x + " " + y + " " + z + " {RequiredPlayerRange:1000000}");
					if (!wasopped) {
						p.setOp(false);
					}
				}
			}
			List<Block> spawners = afkspawners.getOrDefault(minion.getUniqueId(), new ArrayList<Block>());
			for (Iterator<Block> iterator = spawners.iterator(); iterator.hasNext();) {
				Block sp = iterator.next();
				if (!removespawners.contains(sp)) {
					iterator.remove();
					int x = sp.getLocation().getBlockX();
					int y = sp.getLocation().getBlockY();
					int z = sp.getLocation().getBlockZ();
					boolean wasopped = p.isOp();
					p.setOp(true);
					Bukkit.dispatchCommand(p.getPlayer(), "blockdata " + x + " " + y + " " + z + " {RequiredPlayerRange:16}");
					if (!wasopped) {
						p.setOp(false);
					}
				}
			}
			afkspawners.put(minion.getUniqueId(), spawners);
		}
	}

}
