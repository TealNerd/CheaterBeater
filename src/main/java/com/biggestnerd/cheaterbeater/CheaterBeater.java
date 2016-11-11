package com.biggestnerd.cheaterbeater;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.google.common.reflect.ClassPath;

public class CheaterBeater extends JavaPlugin {

	private static CheaterBeater instance;
	private PluginConfig config;
	private List<Beater<?>> beaters;
	private AsyncPlayerDataListener playerData;
	
	public void onEnable() {
		instance = this;
		beaters = new LinkedList<Beater<?>>();
		
		saveDefaultConfig();
		reloadConfig();
		FileConfiguration conf = getConfig();
		try {
			config = new PluginConfig(this, conf);
		} catch (InvalidConfigException e) {
			log(Level.SEVERE, "Failed to load config, disabling plugin.", e);
			setEnabled(false);
			return;
		}
		
		playerData = new AsyncPlayerDataListener(this);
		registerListener(playerData);
		
		ConfigurationSection beaterConfigs = getConfig().getConfigurationSection("beaters");
		try {
			ClassPath getSamplersPath = ClassPath.from(getClassLoader());
			
			for(ClassPath.ClassInfo info : getSamplersPath.getTopLevelClasses("com.biggestnerd.beater.beaters")) {
				try {
					Class<?> clazz = info.load();
					if(clazz != null && Beater.class.isAssignableFrom(clazz)) {
						log(Level.INFO, "Found beater class {0}, attempting to find a generating method and constructor", clazz.getName());
						ConfigurationSection beatConfig = beaterConfigs.getConfigurationSection(clazz.getSimpleName());
						Config beaterConfig = null;
						if(beatConfig != null) {
							try {
								ParameterizedType configType = (ParameterizedType)clazz.getGenericSuperclass();
								Class<?> configClass = (Class<?>) configType.getActualTypeArguments()[0];
								if(configClass.isAssignableFrom(Config.class)) {
									Constructor<?> genConfig = configClass.getConstructor(CheaterBeater.class, ConfigurationSection.class);
									beaterConfig = (Config) genConfig.newInstance(this, beatConfig);
								} else {
									log(Level.WARNING, "beater found extended something other than beaterConfig, skipping", clazz.getName());
								}
							} catch (IllegalAccessException failure) {
								log(Level.WARNING, "Creating configuration for beater {0} failed, illegal access failure", clazz.getName());
							} catch (IllegalArgumentException failure) {
								log(Level.WARNING, "Creating configuration for beater {0} failed, illegal argument failure", clazz.getName());
							} catch (InvocationTargetException failure) {
								log(Level.WARNING, "Creating configuration for hack {0} failed, invocation target failure", clazz.getName());
							}
						} else {
							log(Level.INFO, "beater for {0} found but no configuration, skipping", clazz.getSimpleName());
						}
						
						if(beaterConfig != null) {
							log(Level.INFO, "Configuration for beater {0} found, instance: {1}", clazz.getSimpleName(), beaterConfig.toString());
							Beater<?> beater = null;
							try {
								Constructor<?> constructBasic = clazz.getConstructor(CheaterBeater.class, beaterConfig.getClass());
								beater = (Beater<?>) constructBasic.newInstance(this, beaterConfig);
								log(Level.INFO, "Created a new beater of type {0}", clazz.getSimpleName());
							} catch (InvalidConfigException ice) {
								log(Level.WARNING, "Failed to activate {0} beater, configuration failed", clazz.getSimpleName());
							} catch (Exception e) {
								log(Level.WARNING, "Failed to activate {0} beater, configuration failed: {1}", clazz.getSimpleName(), e.getMessage());
							}
							
							if(beater == null) {
								log(Level.WARNING, "Failed to create beater of type {0}", clazz.getSimpleName());
							} else {
								register(beater);
								log(Level.INFO, "Registered new beater: {0}", clazz.getSimpleName());
							}
						} else {
							log(Level.INFO, "Configuration generation for beater {0} failed, skipping", info.getName());
						}
					}
				} catch (NoClassDefFoundError e) {
					log(Level.INFO, "Configuration generation for beater {0} failed, skipping", info.getName());
				} catch (Exception e) {
					log(Level.WARNING, "Failed to complete beater discovery {0}", info.getName());
				}
			}
		} catch (Exception e) {
			log(Level.WARNING, "Failed to complete beater registration");
		}
		
		if(beaters == null || beaters.size() == 0) {
			log(Level.WARNING, "No beaters enabled.");
			return;
		}
		
		for(Beater<?> beater: beaters) {
			beater.enable();
		}
		
		registerCommand("beater", new CommandListener(this));
	}
	
	public void onDisable() {
		if(beaters == null) return;
		for(Beater<?> beater : beaters) {
			beater.disable();
		}
		beaters.clear();
		beaters = null;
		config = null;
		instance = null;
	}
	
	public void register(Beater<?> beater) {
		if(beaters != null) {
			beaters.add(beater);
			getServer().getPluginManager().addPermission(
					new Permission("cheaterbeater." + beater.getName()).addParent("cheaterbeater.*", true));
		}
	}
	
	public void unregister(Beater<?> beater) {
		if(beaters != null) {
			beaters.remove(beater);
		}
	}
	
	public List<Beater<?>> getbeaters() {
		return Collections.unmodifiableList(beaters);
	}
	
	public static CheaterBeater instance() {
		return instance;
	}
	
	public PluginConfig config() {
		return config;
	}
	
	public AsyncPlayer getAsyncPlayerData(UUID id) {
		return playerData.getPlayerData(id);
	}
	
	// ===== debug / logging methods =====

	private static final String debugPrefix = "[DEBUG] ";

	public void debug(String message) {
		if (!config.isDebug()) return;
		log(Level.INFO, CheaterBeater.debugPrefix + message);
	}
	
	public void debug(String message, Object object) {
		if (!config.isDebug()) return;
		log(Level.INFO, CheaterBeater.debugPrefix + message, object);
	}
	
	public void debug(String message, Throwable thrown) {
		if (!config.isDebug()) return;
		log(Level.INFO, CheaterBeater.debugPrefix + message, thrown);
	}
	
	public void debug(String message, Object...objects) {
		if (!config.isDebug()) return;
		log(Level.INFO, CheaterBeater.debugPrefix + message, objects);
	}
		
	public void log(String message) {
		getLogger().log(Level.INFO, message);
	}

	public void log(Level level, String message) {
		getLogger().log(level, message);
	}
	
	public void log(Level level, String message, Throwable thrown) {
		getLogger().log(level, message, thrown);
	}

	public void log(Level level, String message, Object object) {
		getLogger().log(level, message, object);
	}

	public void log(Level level, String message, Object...objects) {
		getLogger().log(level, message, objects);
	}
	
	public int serverBroadcast(String message) {
		return serverBroadcast(message, config().getBroadcastPermission());
	}
	
	public int serverBroadcast(String message, String permission) {
		return getServer().broadcast(message, permission);
	}
	
	public void registerListener(Listener listener) {
		getServer().getPluginManager().registerEvents(listener, this);
	}
	
	public void unregisterListener(Listener listener) {
		HandlerList.unregisterAll(listener);
	}
	
	public void registerPacketAdapter(PacketAdapter adapter) {
		ProtocolLibrary.getProtocolManager().addPacketListener(adapter);
	}
	
	public void unregisterPacketAdapter(PacketAdapter adapter) {
		ProtocolLibrary.getProtocolManager().removePacketListener(adapter);
	}
	
	public void registerCommand(String command, CommandExecutor exec) {
		PluginCommand cmd = getCommand(command);
		if(cmd != null) {
			cmd.setExecutor(exec);
		} else {
			log(Level.WARNING, "Failed to register executor for {0}, please define that command in the plugin.yml", command);
		}
	}
}
