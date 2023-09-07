package com.pepperoni.orbweaver.packets;

import com.pepperoni.orbweaver.OrbWeaverPlugin;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public abstract class Packet
{
	private byte opCode = -1;
	private byte[] data = null;
	private ByteArrayInputStream inputStream = null;
	private DataInputStream dataInputStream = null;

	public Packet(byte[] data) throws IOException
	{
		this.setData(data);
	}

	private void setData(byte[] data) throws IOException
	{
		this.data = data;
		this.inputStream = new ByteArrayInputStream(data);
		this.dataInputStream = new DataInputStream(inputStream);
		byte opCode = (byte) dataInputStream.readUnsignedByte();
		dataInputStream.skipBytes(1); // skip the op code in the data input stream
		this.setOpCode(opCode);
	}

	public DataInputStream getData()
	{
		return this.dataInputStream;
	}

	// this.dataInputStream is processed here
	public abstract void process(OrbWeaverPlugin plugin) throws IOException;

	public void close() throws IOException
	{
		this.inputStream.close();
		this.dataInputStream.close();
	}

	public boolean setOpCode(byte opCode)
	{
		if (opCode < 0 || opCode > 65535)
		{
			return false;
		}
		this.opCode = opCode;
		return true;
	}

	public byte getOpCode()
	{
		return this.opCode;
	}
}
