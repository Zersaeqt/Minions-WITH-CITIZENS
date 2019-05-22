package com.bluecreeper111.minions.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Crops;
import org.bukkit.scheduler.BukkitRunnable;

import com.bluecreeper111.minions.Main;
import com.bluecreeper111.minions.events.MinionSpawn;
import com.bluecreeper111.minions.events.ModeChangeEvent;
import com.bluecreeper111.minions.events.ModeChangeListener;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Equipment.EquipmentSlot;

public class Farm extends BukkitRunnable {
	
	private Main plugin;
	
	public Farm(Main pl) {
		plugin = pl;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		List<UUID> remove = new ArrayList<>();
		for (UUID id : ModeChangeListener.farming) {
			NPC minion = CitizensAPI.getNPCRegistry().getByUniqueId(id) != null ? CitizensAPI.getNPCRegistry().getByUniqueId(id) : null;
			if (minion == null) {
				remove.add(id);
				continue;
			}
			Equipment equip = minion.getTrait(Equipment.class);
			equip.set(EquipmentSlot.HAND, new ItemStack(Material.DIAMOND_HOE, 1));
			for (Block b : Methods.getNearbyBlocks(minion.getStoredLocation(), 9)) {
				if (b.getState().getData() instanceof Crops && ((Crops)b.getState().getData()).getState() == CropState.RIPE) {
					boolean cleared = false;
					for (ItemStack drop : b.getDrops()) {
						if (drop.getType() == Material.SEEDS && cleared == false) {
							drop.setAmount(drop.getAmount() - 1);
							cleared = true;
						} else if (drop.getType() == Material.MELON_SEEDS && cleared == false) {
							drop.setAmount(drop.getAmount() - 1);
							cleared = true;
						} else if (drop.getType() == Material.PUMPKIN_SEEDS && cleared == false) {
							drop.setAmount(drop.getAmount() - 1);
							cleared = true;
						}
						Methods.addToInventory(minion.getUniqueId(), drop);
					}
					b.getDrops().clear();
					b.setData(CropState.SEEDED.getData());
					b.getState().update();
				}
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
