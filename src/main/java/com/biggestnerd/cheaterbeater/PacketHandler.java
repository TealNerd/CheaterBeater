package com.biggestnerd.cheaterbeater;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

public interface PacketHandler {

	public PacketType[] getPacketTypes();
	
	public void handlePacketEvent(PacketEvent event);
}
