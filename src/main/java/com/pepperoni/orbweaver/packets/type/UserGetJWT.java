package com.pepperoni.orbweaver.packets.type;

import com.pepperoni.orbweaver.OrbWeaverPlugin;
import com.pepperoni.orbweaver.packets.Packet;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class UserGetJWT extends Packet
{
	public UserGetJWT(byte[] data) throws IOException
	{
		super(data);
	}

	@Override
	public void process(OrbWeaverPlugin plugin) throws IOException
	{
		DataInputStream data = this.getData();
		int size = data.readUnsignedByte();
		System.out.println("jwt size: " + size);
		byte[] jwtData = new byte[size];
		data.readFully(jwtData);

		String jwt = new String(jwtData, StandardCharsets.UTF_8);
		System.out.println("jwt: " + jwt);
		plugin.getUser().setJWT(jwt);
		plugin.getUser().setLoggedIn(true);

	}
}
