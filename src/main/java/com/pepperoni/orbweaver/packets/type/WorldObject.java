package com.pepperoni.orbweaver.packets.type;

import com.pepperoni.orbweaver.OrbWeaverPlugin;
import com.pepperoni.orbweaver.packets.Packet;
import java.io.DataInputStream;
import java.io.IOException;
import net.runelite.api.coords.LocalPoint;
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
		int modelStorageId = data.readUnsignedByte();
		if(addObject) {
			int modelId = data.readUnsignedByte();
			int x = data.readUnsignedByte();
			int y = data.readUnsignedByte();
			LocalPoint location = new LocalPoint(x, y);
			int plane = data.readUnsignedByte();
			int orientation = data.readUnsignedByte();
			int animationId = data.readUnsignedByte();
			plugin.modelManager.addModel(modelStorageId, modelId, location, plane, orientation, animationId);
		} else {
			plugin.modelManager.removeObject(modelStorageId);
		}
	}
}
