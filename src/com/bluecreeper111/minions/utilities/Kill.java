package com.bluecreeper111.minions.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
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

public class Kill extends BukkitRunnable implements Listener {
	
	private Main plugin;
	
	public Kill(Main pl) {
		plugin = pl;
	}

	@Override
	public void run() {
		List<UUID> remove = new ArrayList<>();
		for (UUID id : ModeChangeListener.butchering) {
			NPC minion = CitizensAPI.getNPCRegistry().getByUniqueId(id) != null ? CitizensAPI.getNPCRegistry().getByUniqueId(id) : null;
			if (minion == null) {
				remove.add(id);
				continue;
			}
			Equipment equip = minion.getTrait(Equipment.class);
			equip.set(EquipmentSlot.HAND, new ItemStack(Material.IRON_SWORD, 1));
			for (Entity ent : minion.getStoredLocation().getWorld().getNearbyEntities(minion.getStoredLocation(), 9, 9, 9)) {
				if (ent instanceof LivingEntity && ent.getType() != EntityType.PLAYER) {
					LivingEntity entity = (LivingEntity) ent;
					entity.damage(6.0);
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
