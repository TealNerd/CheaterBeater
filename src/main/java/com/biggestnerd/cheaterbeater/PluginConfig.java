package com.biggestnerd.cheaterbeater;

import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;

public class PluginConfig {

	private static int expected_config_level = 1;
	
	private CheaterBeater plugin;
	private ConfigurationSection config;
	
	private boolean debug;
	private String broadcastPermission;
	
	public PluginConfig(ConfigurationSection root) {
		this(CheaterBeater.instance(), root);
	}
	
	public PluginConfig(CheaterBeater plugin, ConfigurationSection root) {
		this.plugin = plugin;
		this.config = root;
		
		int actual_config_level = config.getInt("configuration_file_version", -1);
		if(actual_config_level < 0 || actual_config_level > expected_config_level) {
			throw new InvalidConfigException("Invalid config file version");
		}
		
		debug = config.getBoolean("debug", false);
		if(debug) {
			plugin.log("Debug messages enabled");
		}
		
		broadcastPermission = config.getString("broadcast_permission", "blocker.broadcast");
		if(debug) {
			plugin.log(Level.INFO, "broadcast_permission set to {0}", broadcastPermission);
		}
	}
	
	public boolean isDebug() {
		return debug;
	}
	
	public void setDebug(boolean debug) {
		this.debug = debug;
		update("debug", debug);
	}
	
	public void update(String node, Object value) {
		config.set(node, value);
		plugin.saveConfig();
	}
	
	public String getBroadcastPermission() {
		return broadcastPermission;
	}
}
