package com.bluecreeper111.minions.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bluecreeper111.minions.events.MinionSpawn;
import com.bluecreeper111.minions.gui.GuiMain;
import com.bluecreeper111.minions.utilities.Methods;

public class MinionCMD implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
			if (!sender.hasPermission("minion.give")) {
				Methods.noPermission(sender);
				return true;
			}
			Player t = Bukkit.getPlayerExact(args[1]);
			if (t == null) {
				Methods.notFound(sender, args[1]);
				return true;
			}
			Methods.giveMinion(t);
			sender.sendMessage("§a[!] A minion has been given to the player §2" + t.getName());
			return true;
		} else {
			if ((sender instanceof Player) && MinionSpawn.data.getBoolean("players." + sender.getName() + ".minionplaced")) {
				GuiMain.openMainGui((Player)sender);
			} else {
				Methods.incorrectSyntax(sender, "/minion give <player>");
			}
		}
		return true;
	}
	
	

}
