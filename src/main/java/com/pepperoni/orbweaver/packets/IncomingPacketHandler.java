package com.pepperoni.orbweaver.packets;

import com.pepperoni.orbweaver.OrbWeaverPlugin;
import java.io.IOException;
import java.net.DatagramPacket;


public class IncomingPacketHandler
{
	private final OrbWeaverPlugin plugin;

	public IncomingPacketHandler(OrbWeaverPlugin plugin)
	{
		this.plugin = plugin;
	}
	// receives an op code from the data stream, and tries to find a packet in the registry it is associated with, and invokes it if it exists.
	public void handlePacket(DatagramPacket packet) throws IOException
	{
		byte[] data = packet.getData();
		byte opCode = data[0];

		if (opCode < 0 || opCode >= PacketType.values().length)
		{
			System.out.println("invalid orbweaver packet received");
			return;
		}
		PacketType packetType = PacketType.fromIndex(opCode);
		Class<? extends IncomingPacket> packetRegistry = PacketRegistry.getHandler(packetType);

		if (packetRegistry != null)
		{
			try
			{
				System.out.println("trying to invoke packet " + packetType);
				IncomingPacket incomingPacketHandler = packetRegistry.getConstructor(byte[].class).newInstance(data);
				incomingPacketHandler.process(plugin);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println("Invalid op code received: " + opCode);
		}
	}
}
