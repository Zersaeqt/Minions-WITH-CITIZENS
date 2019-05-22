package com.bluecreeper111.minions.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.bluecreeper111.minions.Main;
import com.bluecreeper111.minions.events.MinionSpawn;
import com.bluecreeper111.minions.events.ModeChangeEvent;
import com.bluecreeper111.minions.events.ModeChangeListener;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Equipment.EquipmentSlot;

public class Mine extends BukkitRunnable {
	
	private Main plugin;
	
	public Mine(Main pl) {
		plugin = pl;
	}

	public void run() {
		List<UUID> remove = new ArrayList<>();
		for (UUID uuid : ModeChangeListener.miningblocks.keySet()) {
			List<Block> blocks = ModeChangeListener.miningblocks.get(uuid);
			NPC minion = CitizensAPI.getNPCRegistry().getByUniqueId(uuid) != null ? CitizensAPI.getNPCRegistry().getByUniqueId(uuid) : null;
			if (minion == null) {
				remove.add(uuid);
				continue;
			}
			Equipment equip = minion.getTrait(Equipment.class);
			equip.set(EquipmentSlot.HAND, new ItemStack(Material.IRON_PICKAXE, 1));
			for (Block block : blocks) {
				if (block == null || block.getType() == Material.AIR || block.isLiquid()) continue;
				for (ItemStack drop : block.getDrops()) {
					Methods.addToInventory(minion.getUniqueId(), drop);
				}
				block.setType(Material.AIR);
			}
		}
		for (UUID id : remove) {
			OfflinePlayer p = Bukkit.getOfflinePlayer(id);
			MinionSpawn.data.set("players." + p.getName() + ".mode", "idle");
			Methods.saveFile(MinionSpawn.datafile, MinionSpawn.data);
			ModeChangeEvent ee = new ModeChangeEvent(p.getUniqueId(), "idle");
			plugin.getServer().getPluginManager().callEvent(ee);
		}
	}

}
