package com.pepperoni.orbweaver.Packets.Packet;

import com.pepperoni.orbweaver.Packets.OrbWeaverPacket;
import java.io.IOException;

public class PlayerUpdate extends OrbWeaverPacket
{


	public PlayerUpdate(byte[] data) throws IOException
	{
		super(data);
	}

	public void process() {

	}
}
