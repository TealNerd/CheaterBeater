package com.biggestnerd.cheaterbeater;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class AsyncPlayerDataListener implements Listener {
	
	private Map<UUID, AsyncPlayer> data;

	public AsyncPlayerDataListener(CheaterBeater plugin) {
		data = new ConcurrentHashMap<UUID, AsyncPlayer>();
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		data.put(event.getPlayer().getUniqueId(), new AsyncPlayer(event.getPlayer()));
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		data.remove(event.getPlayer().getUniqueId());
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		UUID id = event.getPlayer().getUniqueId();
		AsyncPlayer player = data.get(id);
		Location loc = event.getPlayer().getLocation();
		player.setX(loc.getX());
		player.setY(loc.getY());
		player.setZ(loc.getZ());
		player.setPitch(loc.getPitch());
		player.setYaw(loc.getYaw());
	}
	
	@EventHandler
	public void onInventoryInteract(InventoryInteractEvent event) {
		HumanEntity whoClicked = event.getWhoClicked();
		AsyncPlayer player = data.get(whoClicked.getUniqueId());
		player.updateInventory(whoClicked.getInventory());
	}
	
	public AsyncPlayer getPlayerData(UUID id) {
		return data.get(id);
	}
}
