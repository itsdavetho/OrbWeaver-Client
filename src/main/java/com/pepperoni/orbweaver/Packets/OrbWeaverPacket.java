package com.pepperoni.orbweaver.Packets;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public abstract class OrbWeaverPacket
{
	private byte opCode = -1;
	private byte[] data = null;
	private ByteArrayInputStream inputStream = null;
	private DataInputStream dataInputStream = null;

	public OrbWeaverPacket(byte[] data) throws IOException
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

	public DataInputStream getData() {
		return this.dataInputStream;
	}

	//public byte[] getData() {
	//	return this.data;
	//}

	public boolean setOpCode(byte opCode) {
		if(opCode < 0 || opCode > 65535) {
			return false;
		}
		this.opCode = opCode;
		return true;
	}

	public byte getOpCode() {
		return this.opCode;
	}
}
