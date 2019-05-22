package com.bluecreeper111.minions.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Equipment.EquipmentSlot;

public class ModeChangeListener implements Listener {
	
	public static HashMap<UUID, List<Block>> selectingblocks = new HashMap<>();
	public static HashMap<UUID, List<Block>> miningblocks = new HashMap<>();
	public static HashSet<UUID> butchering = new HashSet<>();
	public static HashSet<UUID> farming = new HashSet<>();
	
	// Responsible for handling  mode changes
	@EventHandler
	public void switchm(ModeChangeEvent e) {
		selectingblocks.remove(e.getPlayer().getUniqueId());
		miningblocks.remove(e.getPlayer().getUniqueId());
		butchering.remove(e.getPlayer().getUniqueId());
		farming.remove(e.getPlayer().getUniqueId());
		if (e.getMinion() != null) {
			e.getMinion().getTrait(Equipment.class).set(EquipmentSlot.HAND, null);
		}
		switch(e.getMode()) {
		case "miner":
			e.getPlayer().sendMessage("§6[!] Please left-click up to §e3 §6blocks to mine. When you are done selecting blocks, please crouch.");
			selectingblocks.put(e.getPlayer().getUniqueId(), new ArrayList<Block>());
			break;
		case "butcher":
			butchering.add(e.getPlayer().getUniqueId());
			break;
		case "farmer":
			farming.add(e.getPlayer().getUniqueId());
			break;
		}
	
	}
	// Handles selecting mine blocks
	@EventHandler
	public void selectb(PlayerInteractEvent e) {
		if (selectingblocks.containsKey(e.getPlayer().getUniqueId()) && e.getAction() == Action.LEFT_CLICK_BLOCK) {
			e.setCancelled(true);
			List<Block> blocks = selectingblocks.get(e.getPlayer().getUniqueId());
			if (blocks.size() == 3) {
				e.getPlayer().sendMessage("§c[!] Max block number reached (3). Crouch to finish selection.");
				return;
			}
			BlockBreakEvent event = new BlockBreakEvent(e.getClickedBlock(), e.getPlayer());
			if (event.isCancelled()) {
				e.getPlayer().sendMessage("§c[!] Cannot select that block!");
				return;
			}
			blocks.add(e.getClickedBlock());
			e.getPlayer().sendMessage("§6[!] Block selected. You can choose up to §e" + Integer.toString(3 - blocks.size()) + "§6 more blocks.");
			selectingblocks.put(e.getPlayer().getUniqueId(), blocks);
		}
	}
	// Handles finishing selection process
	@EventHandler
	public void finishb(PlayerToggleSneakEvent e) {
		if (e.getPlayer().isSneaking() && selectingblocks.containsKey(e.getPlayer().getUniqueId())) {
			if (selectingblocks.get(e.getPlayer().getUniqueId()).size() == 0) {
				e.getPlayer().sendMessage("§c[!] You must select at least one block!");
				return;
			}
			List<Block> add = selectingblocks.get(e.getPlayer().getUniqueId());
			selectingblocks.remove(e.getPlayer().getUniqueId());
			e.getPlayer().sendMessage("§a[!] Blocks selected. Mining mode enabled.");
			miningblocks.put(e.getPlayer().getUniqueId(), add);
		}
		
	}

}
