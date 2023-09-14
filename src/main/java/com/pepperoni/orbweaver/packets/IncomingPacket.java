package com.pepperoni.orbweaver.packets;

import com.pepperoni.orbweaver.OrbWeaverPlugin;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import lombok.Getter;

public abstract class IncomingPacket
{
	@Getter
	private byte opCode = -1;
	private byte[] bytes = null;
	private ByteArrayInputStream inputStream = null;
	@Getter
	private DataInputStream data = null;

	public IncomingPacket(byte[] bytes) throws IOException
	{
		this.setBytes(bytes);
	}

	private void setBytes(byte[] bytes) throws IOException
	{
		this.bytes = bytes;
		this.inputStream = new ByteArrayInputStream(bytes);
		this.data = new DataInputStream(inputStream);
		byte opCode = (byte) data.readUnsignedByte();
		this.setOpCode(opCode);
	}

	// this.dataInputStream is processed here
	public abstract void process(OrbWeaverPlugin plugin) throws IOException;

	public void close() throws IOException
	{
		this.inputStream.close();
		this.data.close();
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
}
