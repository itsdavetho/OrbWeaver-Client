package com.pepperoni.orbweaver.packets;

import com.google.inject.Inject;
import com.pepperoni.orbweaver.OrbWeaverPlugin;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;
import lombok.Getter;

public abstract class OutgoingPacket
{
	@Inject
	private final OrbWeaverPlugin plugin;
	private final ByteArrayOutputStream outputStream;
	@Getter
	private final DataOutputStream dataOutputStream;
	@Getter
	private final PacketType packetType;

	public OutgoingPacket(OrbWeaverPlugin plugin, PacketType packetType) throws IOException
	{
		this.outputStream = new ByteArrayOutputStream();
		this.dataOutputStream = new DataOutputStream(outputStream);
		this.packetType = packetType;
		this.plugin = plugin;

		byte opCode = (byte) packetType.getIndex();
		dataOutputStream.writeByte(opCode); // write the packet OP code as the first byte always

		String jwt = plugin.getUser().getJWT() != null ? plugin.getUser().getJWT() : "unauthorized";

		byte[] jwtBytes = jwt.getBytes(StandardCharsets.UTF_8);
		int jwtSize = jwt.length();

		dataOutputStream.writeByte(jwtSize);
		dataOutputStream.write(jwtBytes);
	}

	public byte[] getBytes()
	{
		return outputStream.toByteArray();
	}

	public void close() throws IOException
	{
		this.outputStream.close();
		this.dataOutputStream.close();
	}

	public void send() throws IOException
	{
		byte[] data = outputStream.toByteArray();
		DatagramPacket packet = new DatagramPacket(data, data.length, plugin.getServerAddress(), plugin.getServerPort());
		plugin.getSocket().send(packet);
		this.close();
	}
}
