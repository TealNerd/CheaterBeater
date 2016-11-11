package com.biggestnerd.cheaterbeater;

import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;

public abstract class Config {

	private CheaterBeater plugin;
	private ConfigurationSection base;
	private String name;
	private boolean enabled;

	public Config(CheaterBeater plugin, ConfigurationSection base) {
		this.plugin = plugin;
		this.base = base;
		this.name = base.getString("name", base.getName());
		this.enabled = base.getBoolean("enabled", false);
		plugin.log(Level.INFO, "Config for {0}, enabled set to {1}, instance {2}", this.name, this.enabled, this.toString());
		this.wireup(base);
	}
	
	protected abstract void wireup(ConfigurationSection base);
	
	public ConfigurationSection getBase() {
		return base;
	}
	
	protected CheaterBeater plugin() {
		if(plugin == null) {
			plugin = CheaterBeater.instance();
		}
		return plugin;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public void set(String attr, Object value) {
		if(base != null && attr != null) {
			base.set(attr, value);
		}
	}
	
	public Object get(String attr) {
		if(base != null && attr != null) {
			return base.get(attr);
		}
		return null;
	}
}
