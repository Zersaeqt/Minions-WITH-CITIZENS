package com.bluecreeper111.minions.events;

import java.io.File;
import java.io.IOException;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.bluecreeper111.minions.Main;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

public class MinionSpawn implements Listener {

	private Main plugin;

	public MinionSpawn(Main pl) {
		plugin = pl;
	}

	public static File datafile = new File("plugins//Minions//playerdata.yml");
	public static YamlConfiguration data = YamlConfiguration.loadConfiguration(datafile);

	@SuppressWarnings("static-access")
	@EventHandler(ignoreCancelled = true)
	public void place(BlockPlaceEvent e) {
		String username = plugin.getConfig().getString("skinUsername");
		Player p = e.getPlayer();
		if (e.getItemInHand() == null || e.getItemInHand().getType() == Material.AIR || !e.getItemInHand().hasItemMeta()) return; 
		if (e.getItemInHand().getItemMeta().getDisplayName().equals("§c§lMinion")) {
			e.setCancelled(true);
			if (data.getBoolean("players." + p.getName() + ".minionplaced")) {
				p.sendMessage("§c[!] You already have a minion placed!");
				return;
			}
			if (p.getItemInHand().getAmount() == 1) {
				p.getInventory().setItemInHand(null);
			} else {
				p.getInventory().getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
			}
			data.set("players." + p.getName() + ".minionplaced", true);
			data.set("players." + p.getName() + ".mode", "idle");
			ModeChangeEvent ee = new ModeChangeEvent(p.getUniqueId(), "idle");
			plugin.getServer().getPluginManager().callEvent(ee);
			try {
				data.save(datafile);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			NPC minion = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, p.getUniqueId(), 0, "§4§lMinion");
			minion.data().setPersistent(minion.PLAYER_SKIN_UUID_METADATA, username);
			minion.data().setPersistent(minion.PLAYER_SKIN_USE_LATEST, false);
			minion.spawn(e.getBlock().getLocation());
			p.sendMessage("§a[!] You have spawned your minion!");
			return;
		}
	}

}
