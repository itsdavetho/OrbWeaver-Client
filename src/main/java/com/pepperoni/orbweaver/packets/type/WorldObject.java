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
		int modelsReceived = data.readUnsignedShort();
		System.out.println("received " + modelsReceived + " models");

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
			System.out.println("new model loaded @ " + locationX + " " + locationY + " " + locationPlane);
			WorldPoint location = new WorldPoint(locationX, locationY, locationPlane);
			plugin.getModelManager().addModel(modelStorageId, modelId, location, orientation, animationId);
		}
	}
}
