package com.pepperoni.orbweaver.packets;

import com.pepperoni.orbweaver.OrbWeaverPlugin;
import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class PacketHandler
{
	private final OrbWeaverPlugin plugin;

	public PacketHandler(OrbWeaverPlugin plugin)
	{
		this.plugin = plugin;
	}

	public static void debugPacket(DatagramPacket packet)
	{
		byte[] data = packet.getData();
		int length = packet.getLength();
		String remoteAddress = packet.getAddress().getHostAddress();
		int remotePort = packet.getPort();

		String packetContent = new String(data, 0, length, StandardCharsets.UTF_8);

		System.out.println("Received packet from " + remoteAddress + ":" + remotePort);
		System.out.println("Packet content: " + packetContent);
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
		Class<? extends Packet> packetRegistry = PacketRegistry.getHandler(packetType);

		if (packetRegistry != null)
		{
			try
			{
				System.out.println("trying to invoke packet " + packetType);
				Packet packetHandler = packetRegistry.getConstructor(byte[].class).newInstance(data);
				packetHandler.process(plugin);
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

	// creates a new buffer with: opCode tokenSize token. for use with PacketHandler.sendPacket
	public List<byte[]> createPacket(PacketType packet, String token)
	{
		List<byte[]> packetList = new ArrayList<>();

		byte[] actionBuffer = new byte[1];
		actionBuffer[0] = (byte) packet.getIndex();
		if (token == null)
		{
			token = "unauthorized";
		}
		byte[] jwtBuffer = token.getBytes();

		byte[] tokenSize = new byte[1];
		tokenSize[0] = (byte) jwtBuffer.length;

		packetList.add(actionBuffer);
		packetList.add(tokenSize);
		packetList.add(jwtBuffer);

		return packetList;
	}

	public void sendPacket(List<byte[]> packet)
	{
		try
		{
			byte[] buffer = concatenateByteArrays(packet);
			DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, plugin.getServerAddress(), plugin.getServerPort());
			plugin.getSocket().send(datagramPacket);
		}
		catch (IOException e)
		{
			System.err.println("Error sending packet: " + e.getMessage());
		}
	}

	public byte[] concatenateByteArrays(List<byte[]> arrays)
	{
		int totalLength = arrays.stream().mapToInt(array -> array.length).sum();
		byte[] result = new byte[totalLength];

		int currentIndex = 0;
		for (byte[] array : arrays)
		{
			System.arraycopy(array, 0, result, currentIndex, array.length);
			currentIndex += array.length;
		}

		return result;
	}
}
