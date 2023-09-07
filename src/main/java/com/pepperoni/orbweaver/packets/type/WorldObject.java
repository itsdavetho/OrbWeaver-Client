package com.pepperoni.orbweaver.packets.type;

import com.pepperoni.orbweaver.OrbWeaverPlugin;
import com.pepperoni.orbweaver.packets.Packet;
import java.io.DataInputStream;
import java.io.IOException;
import net.runelite.api.coords.WorldPoint;

public class WorldObject extends Packet
{
	public WorldObject(byte[] data) throws IOException
	{
		super(data);
	}

	@Override
	public void process(OrbWeaverPlugin plugin) throws IOException
	{
		DataInputStream data = this.getData();
		boolean addObject = data.readUnsignedByte() == 0;
		int objectStorageId = data.readUnsignedByte();
		if(addObject) {
			int x = data.readUnsignedByte();
			int y = data.readUnsignedByte();
			int z = data.readUnsignedByte();
			WorldPoint location = new WorldPoint(x, y, z);
			int modelId = data.readUnsignedByte();
			int orientation = data.readUnsignedByte();
			plugin.objects.addObject(objectStorageId, modelId, location, orientation);
		} else {
			plugin.objects.removeObject(objectStorageId);
		}
	}
}
