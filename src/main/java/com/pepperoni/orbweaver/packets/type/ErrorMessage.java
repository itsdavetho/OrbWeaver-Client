package com.pepperoni.orbweaver.packets.type;

import com.pepperoni.orbweaver.OrbWeaverPlugin;
import com.pepperoni.orbweaver.packets.Packet;
import java.io.DataInputStream;
import java.io.IOException;

public class ErrorMessage extends Packet
{

	public ErrorMessage(byte[] data) throws IOException
	{
		super(data);
	}

	@Override
	public void process(OrbWeaverPlugin plugin) throws IOException
	{
		DataInputStream data = this.getData();

		int errorCode = data.readUnsignedByte();

		System.out.println("ERROR RECV: " + errorCode);
	}
}
