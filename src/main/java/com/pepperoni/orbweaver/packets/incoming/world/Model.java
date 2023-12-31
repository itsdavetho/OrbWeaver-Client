package com.pepperoni.orbweaver.packets.incoming.world;

import com.pepperoni.orbweaver.OrbWeaverPlugin;
import com.pepperoni.orbweaver.packets.IncomingPacket;
import java.io.DataInputStream;
import java.io.IOException;
import net.runelite.api.coords.WorldPoint;

public class Model extends IncomingPacket
{
	public Model(byte[] data) throws IOException
	{
		super(data);
	}

	@Override
	public void process(OrbWeaverPlugin plugin) throws IOException
	{
		DataInputStream data = this.getData();
		int modelsReceived = data.readUnsignedShort();
		//plugin.getModelManager().removeModels();
		for (int i = 0; i < modelsReceived; i++)
		{
			int modelStorageId = data.readUnsignedShort();
			int modelId = data.readUnsignedShort();
			int locationX = data.readUnsignedShort();
			int locationY = data.readUnsignedShort();
			int locationPlane = data.readUnsignedByte();
			int orientation = data.readUnsignedShort();
			int animationId = data.readShort(); // Changed to readShort

			// Create a LocalPoint for the object's location
			WorldPoint location = new WorldPoint(locationX, locationY, locationPlane);
			plugin.getModelManager().addModel(modelStorageId, modelId, location, orientation, animationId);
		}
	}
}
