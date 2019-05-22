package com.bluecreeper111.minions.events;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

public class ModeChangeEvent extends Event {
	
	private UUID uuid;
	private String mode;
	
	public ModeChangeEvent(UUID uuid, String mode) {
		this.uuid = uuid;
		this.mode = mode;
	}
	
	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
		return handlers;
	}
	public static HandlerList getHandlerList() {
		return handlers;
	}
	public Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}
	public String getMode() {
		return mode;
	}
	public NPC getMinion() {
		return CitizensAPI.getNPCRegistry().getByUniqueId(uuid);
	}
	

}
