package com.biggestnerd.cheaterbeater;

import org.bukkit.event.Listener;

import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

public abstract class Beater<T extends Config> {

	private CheaterBeater plugin;
	protected T config;
	private Object adapter;
	
	public Beater(CheaterBeater plugin, T config) {
		this.plugin = plugin;
		this.config = config;
	}
	
	public CheaterBeater plugin() {
		if(plugin == null) {
			plugin = CheaterBeater.instance();
		}
		return plugin;
	}
	
	public void enable() {
		dataBootstrap();
		if(this instanceof PacketHandler) {
			PacketHandler ph = (PacketHandler)this;
			adapter = new PacketAdapter(plugin(), ph.getPacketTypes()) {
				public void onPacketSending(PacketEvent event) { ph.handlePacketEvent(event); }
				public void onPacketReceiving(PacketEvent event) { ph.handlePacketEvent(event); }
			};
			plugin().registerPacketAdapter((PacketAdapter)adapter);
		}
		if(this instanceof Listener) {
			plugin().registerListener((Listener)this);
		}
	}
	
	public abstract void dataBootstrap();
	
	public void disable() {
		dataCleanup();
		if(this instanceof PacketHandler) {
			plugin().unregisterPacketAdapter((PacketAdapter)adapter);
		}
		if(this instanceof Listener) {
			plugin().unregisterListener((Listener)this);
		}
		config = null;
		plugin = null;
	}
	
	public void softDiable() {
		config.setEnabled(false);
	}
	
	public void softEnable() {
		config.setEnabled(true);
	}
	
	public boolean isEnabled() {
		return config == null ? false : config.isEnabled();
	}
	
	public String getName() {
		return config == null ? null : config.getName();
	}
	
	public Config config() {
		return config;
	}
	
	public abstract void dataCleanup();
	
	public abstract String status();
	
	@Override
	public boolean equals(Object o) {
		if(o != null && o instanceof Beater) {
			Beater<?> f = (Beater<?>) o;
			if(f.config != null && config != null && f.config.getName().equals(config.getName())) {
				return true;
			}
		}
		return false;
	}
}
