package com.pepperoni.orbweaver.packets.outgoing.player;

import com.pepperoni.orbweaver.OrbWeaverPlugin;
import com.pepperoni.orbweaver.packets.OutgoingPacket;
import com.pepperoni.orbweaver.packets.PacketType;
import com.pepperoni.orbweaver.packets.PlayerUpdateType;
import java.io.DataOutputStream;
import java.io.IOException;
import net.runelite.api.coords.WorldPoint;

public class LocationUpdate extends OutgoingPacket {
	public LocationUpdate(OrbWeaverPlugin plugin, WorldPoint loc, int orientation) throws IOException {
		super(plugin, PacketType.PLAYER_UPDATE);
		DataOutputStream data = getDataOutputStream();

		data.writeByte(PlayerUpdateType.LOCATION.getIndex());
		data.writeShort(loc.getX());
		data.writeShort(loc.getY());
		data.writeByte(loc.getPlane());
		data.writeShort(orientation);

		this.send();
	}
}