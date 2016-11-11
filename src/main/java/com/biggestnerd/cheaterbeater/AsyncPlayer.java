package com.biggestnerd.cheaterbeater;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class AsyncPlayer {
	
	private String world;
	private double x;
	private double y;
	private double z;
	private float yaw;
	private float pitch;
	private UUID id;
	private Map<PotionEffectType, Integer> potions;
	private ItemStack[] armor;
	private ItemStack mainHand;
	private ItemStack offHand;
	
	public AsyncPlayer(Player player) {
		Location loc = player.getLocation();
		world = loc.getWorld().getName();
		x = loc.getX();
		y = loc.getY();
		z = loc.getZ();
		yaw = loc.getYaw();
		pitch = loc.getPitch();
		id = player.getUniqueId();
		potions = new HashMap<PotionEffectType, Integer>();
		for(PotionEffect effect : player.getActivePotionEffects()) {
			potions.put(effect.getType(), effect.getAmplifier());
		}
		PlayerInventory inv = player.getInventory();
		if(inv.getArmorContents() != null) {
			armor = inv.getArmorContents().clone();
		}
		mainHand = inv.getItemInMainHand().clone();
		offHand = inv.getItemInOffHand().clone();
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Map<PotionEffectType, Integer> getPotions() {
		return potions;
	}

	public void setPotions(Map<PotionEffectType, Integer> potions) {
		this.potions = potions;
	}

	public ItemStack[] getArmor() {
		return armor;
	}

	public void setArmor(ItemStack[] armor) {
		this.armor = armor;
	}

	public ItemStack getMainHand() {
		return mainHand;
	}

	public void setMainHand(ItemStack mainHand) {
		this.mainHand = mainHand;
	}

	public ItemStack getOffHand() {
		return offHand;
	}

	public void setOffHand(ItemStack offHand) {
		this.offHand = offHand;
	}
	
	public void updateInventory(PlayerInventory inv) {
		if(inv.getArmorContents() != null) {
			armor = inv.getArmorContents().clone();
		}
	}
	
	public void updateLocation(Location loc) {
		world = loc.getWorld().getName();
		x = loc.getX();
		y = loc.getY();
		z = loc.getZ();
		pitch = loc.getPitch();
		yaw = loc.getYaw();
	}
	
	public double getAngle(AsyncPlayer other) {
		return 0;
	}
	
	public double getDistance(AsyncPlayer other) {
		return Math.sqrt(getDistanceSquared(other));
	}
	
	public double getDistanceSquared(AsyncPlayer other) {
		double dx = x - other.x;
		double dy = y - other.y;
		double dz = z - other.z;
		return dx * dx + dy * dy + dz * dz;
	}
	
	public Location getRealLocation() {
		return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
	}
}