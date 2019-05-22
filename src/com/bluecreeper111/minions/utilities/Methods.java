package com.bluecreeper111.minions.utilities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.bluecreeper111.minions.events.PickupItemExp;
import com.bluecreeper111.minions.gui.GuiMain;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

public class Methods {
	
	public static void notPlayer(CommandSender sender) {
		sender.sendMessage("§c[!] Only players can do that!");
	}
	public static void noPermission(CommandSender sender) {
		sender.sendMessage("§c[!] You do not have permission to do that!");
	}
	public static void notFound(CommandSender sender, String args) {
		sender.sendMessage("§c[!] Player §4" + args + "§c was not found!");
	}
	public static void incorrectSyntax(CommandSender sender, String syntax) {
		sender.sendMessage("§c[!] Incorrect syntax! Try using §4" + syntax);
	}
	public static void giveMinion(Player p) {
		ItemStack minion = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
		ItemMeta meta = minion.getItemMeta();
		meta.setDisplayName("§c§lMinion");
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		List<String> lore = new ArrayList<>();
		lore.add("§7Can be used to perform various tasks.");
		lore.add("§f<> Right Click To Place <>");
		meta.setLore(lore);
		minion.setItemMeta(meta);
		giveItem(p, minion);
	}
	public static ItemStack minionItem() {
		ItemStack minion = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
		ItemMeta meta = minion.getItemMeta();
		meta.setDisplayName("§c§lMinion");
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		List<String> lore = new ArrayList<>();
		lore.add("§7Can be used to perform various tasks.");
		lore.add("§f<> Right Click To Place <>");
		meta.setLore(lore);
		minion.setItemMeta(meta);
		return minion;
	}
	// Gives item
	public static void giveItem(Player p, ItemStack item) {
		if (p.getInventory().firstEmpty() == -1) {
			p.getWorld().dropItem(p.getLocation(), item);
		} else {
			p.getInventory().addItem(item);
		}
	}
	// Create custom item
	public static ItemStack createItem(String displayname, List<String> lore, Material type, Integer amount) {
		ItemStack give = new ItemStack(type, amount);
		ItemMeta meta = give.getItemMeta();
		meta.setDisplayName(displayname);
		meta.setLore(lore);
		give.setItemMeta(meta);
		return give;
	}
	// Creates lore, seperates lines with ","
	public static List<String> makeLore(String lore) {
	String[] l = lore.split(",");
	List<String> addlore = new ArrayList<>();
	for (String string : l) {
		addlore.add(string);
	}
	return addlore;
	}
	// Allows easy saving of files
	public static void saveFile(File file, YamlConfiguration config) {
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// Allows getting nearby blocks in a location
	public static List<Block> getNearbyBlocks(Location location, int radius) {
        List<Block> blocks = new ArrayList<Block>();
        for(int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for(int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
                for(int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                   blocks.add(location.getWorld().getBlockAt(x, y, z));
                }
            }
        }
        return blocks;
    }
	// Adds items to minions inventory
	public static void addToInventory(UUID id, ItemStack item) {
		NPC minion = CitizensAPI.getNPCRegistry().getByUniqueId(id);
		if (GuiMain.linkedchest.containsKey(minion.getUniqueId())) {
			Block chest = GuiMain.linkedchest.get(minion.getUniqueId());
			if (chest == null || chest.getType() != Material.CHEST) {
				GuiMain.linkedchest.remove(minion.getUniqueId());
			} else {
				Chest add = (Chest) chest.getState();
				if (add.getBlockInventory().firstEmpty() != -1) {
				add.getBlockInventory().addItem(item);
				return;
				}
			}
		}
		Inventory inv = PickupItemExp.minioninventory.getOrDefault(minion.getUniqueId(), Bukkit.createInventory(null, 27, "§6§lMinion Inventory"));
		inv.addItem(item);
		PickupItemExp.minioninventory.put(minion.getUniqueId(), inv);
	}

}
