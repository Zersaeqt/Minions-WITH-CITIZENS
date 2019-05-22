package com.bluecreeper111.minions.gui;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import com.bluecreeper111.minions.Main;
import com.bluecreeper111.minions.events.MinionSpawn;
import com.bluecreeper111.minions.events.ModeChangeEvent;
import com.bluecreeper111.minions.events.PickupItemExp;
import com.bluecreeper111.minions.utilities.Methods;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCDamageEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;

@SuppressWarnings("deprecation")
public class GuiMain implements Listener {
	
	private Main plugin;
	
	public GuiMain(Main pl) {
		plugin = pl;
	}
	
	// Rename minion hashset
	public HashSet<UUID> rename = new HashSet<>();
	// Link chests
	public HashSet<UUID> link = new HashSet<>();
	// Handles linked chests
	public static HashMap<UUID, Block> linkedchest = new HashMap<>();
	
	// Cancels damage events
	@EventHandler
	public void dam(NPCDamageEvent e) {
		e.setCancelled(true);
	}
	// Tests for right-click
	@EventHandler
	public void interact(NPCRightClickEvent e) {
		if (e.getClicker().getUniqueId() != e.getNPC().getUniqueId()) return;
		openMainGui(e.getClicker());
	}
	// Opens GUI
	public static void openMainGui(Player p) {
		Inventory gui = Bukkit.createInventory(null, 27, "§7" + p.getName() + "\'s §4§lMinion");
		ItemStack deleteminion = Methods.createItem("§4§lDespawn", Methods.makeLore("§7Package your minion back,§7into its item state!"), Material.BARRIER, 1);
		gui.setItem(13, deleteminion);
		ItemStack teleporthere = Methods.createItem("§5§lTeleport", Methods.makeLore("§7Teleport your minion,§7to your location."), Material.ENDER_PEARL, 1);
		gui.setItem(12, teleporthere);
		ItemStack rename = Methods.createItem("§6§lRename", Methods.makeLore("§7Rename your minion."), Material.NAME_TAG, 1);
		gui.setItem(14, rename);
		ItemStack mode = null;
		ItemStack minioninventory = Methods.createItem("§6§lOpen Inventory", Methods.makeLore("§7Open your minion's inventory."), Material.CHEST, 1);
		gui.setItem(11, minioninventory);
		int exp = PickupItemExp.minionexp.getOrDefault(p.getUniqueId(), 0);
		ItemStack expitem = Methods.createItem("§a§lMinion Experience", Methods.makeLore("§7Current EXP: §a"+Integer.toString(exp)+",§9<> Click to Claim EXP <>"), Material.EXP_BOTTLE, 1);
		gui.setItem(15, expitem);
		ItemStack link = null;
		if (!linkedchest.containsKey(p.getUniqueId())) {
		    link = Methods.createItem("§e§lLink Chest", Methods.makeLore("§7Have your minion deposit,§7items into a chest,§8LINKED: §c§lX,§9<> Click to Link a Chest <>"), Material.LEASH, 1);
		} else {
			link = Methods.createItem("§e§lLink Chest", Methods.makeLore("§7Have your minion deposit,§7items into a chest,§8LINKED: §a§l\u2714,§9<> Click to Link a Chest <>"), Material.LEASH, 1);
		}
		gui.setItem(4, link);
		String modestring = MinionSpawn.data.getString("players." + p.getName() + ".mode") != null ? MinionSpawn.data.getString("players." + p.getName() + ".mode") : "default";
		switch(modestring) {
		case "miner":
			mode = Methods.createItem("§c§lChange Mode", Methods.makeLore("§7MODE: §6§lMINER,§7Click to change mode."), Material.IRON_PICKAXE, 1);
			break;
		case "idle":
			mode = Methods.createItem("§c§lChange Mode", Methods.makeLore("§7MODE: §c§lIDLE,§7Click to change mode."), Material.BED, 1);
			break;
		case "farmer":
			mode = Methods.createItem("§c§lChange Mode", Methods.makeLore("§7MODE: §e§lFARMER,§7Click to change mode."), Material.DIAMOND_HOE, 1);
			break;
		case "butcher":
			mode = Methods.createItem("§c§lChange Mode", Methods.makeLore("§7MODE: §8§lBUTCHER,§7Click to change mode."), Material.IRON_SWORD, 1);
			break;
		default:
			mode = Methods.createItem("§c§lChange Mode", Methods.makeLore("§7MODE: §c§lIDLE,§7Click to change mode."), Material.BED, 1);
		}
		for (int i = 0; i < 27; i++) {
			if (i == 13 || i == 12 || i == 14 || i == 11
					|| i == 15 || i == 4 || i == 22) continue;
			gui.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1));
		}
		gui.setItem(22, mode);
		p.openInventory(gui);
	}
	// Opens mode selector
	public void openModeSelector(Player p) {
		Inventory gui = Bukkit.createInventory(null, InventoryType.HOPPER, "§cSelect Mode");
		ItemStack mode1 = Methods.createItem("§6§lMiner", Methods.makeLore("§7Mines a predefined area,§7around the minion."), Material.IRON_PICKAXE, 1);
		mode1.getItemMeta().addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		ItemStack mode2 = Methods.createItem("§c§lIdle", Methods.makeLore("§7Idles mob spawners."), Material.BED, 1);
		ItemStack mode3 = Methods.createItem("§e§lFarmer", Methods.makeLore("§7Farms crops within a 9x9,§7area around the minion."), Material.DIAMOND_HOE, 1);
		ItemStack mode4 = Methods.createItem("§8§lButcher", Methods.makeLore("§7Kills mobs within a 9x9,§7area around the minion."), Material.IRON_SWORD, 1);
		ItemStack back = Methods.createItem("§4Go Back", Methods.makeLore("§7Go back to previous page."), Material.ARROW, 1);
		gui.setItem(1, mode1);
		gui.setItem(2, mode2);
		gui.setItem(3, mode3);
		gui.setItem(4, mode4);
		gui.setItem(0, back);
		p.openInventory(gui);
	}
	
	// Handles GUI Clicks (for Main GUI)
	@EventHandler
 	public void MainGUI(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
		if (e.getInventory().getName().equals("§7" + p.getName() + "\'s §4§lMinion")) {
			e.setCancelled(true);
			if (!e.getCurrentItem().hasItemMeta()) return;
			String clicked = e.getCurrentItem().getItemMeta().getDisplayName();
			NPC minion = CitizensAPI.getNPCRegistry().getByUniqueId(p.getUniqueId());
			switch(clicked) {
			case "§4§lDespawn":
				minion.destroy();
				MinionSpawn.data.set("players." + p.getName() + ".minionplaced", false);
				try {
					MinionSpawn.data.save(MinionSpawn.datafile);
				} catch (IOException ee) {
					ee.printStackTrace();
				}
				Methods.giveMinion(p);
				p.sendMessage("§a[!] Your minion has been given to you.");
				break;
			case "§5§lTeleport":
				minion.teleport(p.getLocation(), TeleportCause.PLUGIN);
				HashSet<Material> t = new HashSet<>();
				t.add(Material.AIR);
				minion.faceLocation(p.getTargetBlock(t, 200).getLocation());
				p.sendMessage("§a[!] Your minion has been teleported to your location.");
				break;
			case "§6§lRename":
				rename.add(p.getUniqueId());
				p.sendMessage("§6[!] Please type a new name for your minion.");
				break;
			case "§e§lLink Chest":
				link.add(p.getUniqueId());
				p.sendMessage("§6[!] Please left-click on a chest to link.");
				break;
			case "§c§lChange Mode":
				this.openModeSelector(p);
				return;
			case "§6§lOpen Inventory":
				Inventory minioninventory = PickupItemExp.minioninventory.getOrDefault(minion.getUniqueId(), Bukkit.createInventory(null, 27, "§6§lMinion Inventory"));
				p.openInventory(minioninventory);
				return;
			case "§a§lMinion Experience":
				int exp = PickupItemExp.minionexp.getOrDefault(minion.getUniqueId(), 0);
				if (exp == 0) {
					p.sendMessage("§c[!] Cannot withdraw 0 EXP!");
					break;
				}
				p.giveExp(exp);
				PickupItemExp.minionexp.put(minion.getUniqueId(), 0);
				p.sendMessage("§a[!] You have claimed §2" + exp + "§a EXP from your minion!");
				break;
			default:
				return;
			}
			p.closeInventory();
		}
	}
	// Handles GUI clicks in mode selector
	@EventHandler
	public void ModeSelector(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
		if (e.getInventory().getName().equals("§cSelect Mode")) {
			e.setCancelled(true);
			String name = e.getCurrentItem().getItemMeta().getDisplayName();
			switch(name) {
			case "§6§lMiner":
				MinionSpawn.data.set("players." + p.getName() + ".mode", "miner");
				Methods.saveFile(MinionSpawn.datafile, MinionSpawn.data);
				ModeChangeEvent eee = new ModeChangeEvent(p.getUniqueId(), "miner");
				plugin.getServer().getPluginManager().callEvent(eee);
				break;
			case "§c§lIdle":
				MinionSpawn.data.set("players." + p.getName() + ".mode", "idle");
				Methods.saveFile(MinionSpawn.datafile, MinionSpawn.data);
				ModeChangeEvent ee = new ModeChangeEvent(p.getUniqueId(), "idle");
				plugin.getServer().getPluginManager().callEvent(ee);
				p.sendMessage("§a[!] Mode has been set to §2Idle§a.");
				break;
			case "§e§lFarmer":
				MinionSpawn.data.set("players." + p.getName() + ".mode", "farmer");
				Methods.saveFile(MinionSpawn.datafile, MinionSpawn.data);
				ModeChangeEvent eeee = new ModeChangeEvent(p.getUniqueId(), "farmer");
				plugin.getServer().getPluginManager().callEvent(eeee);
				p.sendMessage("§a[!] Mode has been set to §2Farmer§a.");
				break;
			case "§8§lButcher":
				MinionSpawn.data.set("players." + p.getName() + ".mode", "butcher");
				Methods.saveFile(MinionSpawn.datafile, MinionSpawn.data);
				ModeChangeEvent eeeee = new ModeChangeEvent(p.getUniqueId(), "butcher");
				plugin.getServer().getPluginManager().callEvent(eeeee);
				p.sendMessage("§a[!] Mode has been set to §2Butcher§a.");
				break;
			case "§4Go Back":
				GuiMain.openMainGui(p);
				return;
			}
			p.closeInventory();
		}
	}
	
	// Handles renaming process
	@EventHandler
	public void rename(PlayerChatEvent e) {
		if (rename.contains(e.getPlayer().getUniqueId())) {
			if (CitizensAPI.getNPCRegistry().getByUniqueId(e.getPlayer().getUniqueId()) == null) {
				e.getPlayer().sendMessage("§c[!] Rename cancelled.");
				rename.remove(e.getPlayer().getUniqueId());
				return;
			}
			e.setCancelled(true);
			NPC minion = CitizensAPI.getNPCRegistry().getByUniqueId(e.getPlayer().getUniqueId());
			minion.setName(ChatColor.translateAlternateColorCodes('&', e.getMessage()));
			e.getPlayer().sendMessage("§a[!] Your minion has been renamed to §f" + e.getMessage().replaceAll("&", "§"));
			rename.remove(e.getPlayer().getUniqueId());
		}
	}
	// Handles linking chest
	@EventHandler
	public void click(PlayerInteractEvent e) {
		if (link.contains(e.getPlayer().getUniqueId())) {
			Player p = e.getPlayer();
			if (e.getAction() == Action.LEFT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.CHEST) {
				e.setCancelled(true);
				linkedchest.put(p.getUniqueId(), e.getClickedBlock());
				p.sendMessage("§a[!] You have linked a chest to your minion!");
				link.remove(p.getUniqueId());
			} else if (e.getAction() == Action.LEFT_CLICK_BLOCK && e.getClickedBlock().getType() != Material.CHEST) {
				p.sendMessage("§c[!] Link cancelled.");
				link.remove(p.getUniqueId());
				e.setCancelled(true);
			}
		}
	}

}
