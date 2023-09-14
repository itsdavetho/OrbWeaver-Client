package com.pepperoni.orbweaver.packets.incoming.player;

import com.pepperoni.orbweaver.OrbWeaverPlugin;
import com.pepperoni.orbweaver.packets.IncomingPacket;
import com.pepperoni.orbweaver.packets.IncomingPacketHandler;
import com.pepperoni.orbweaver.packets.outgoing.server.Info;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class UserGetJWT extends IncomingPacket
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

		System.out.println("logged in, requesting server information...");

		// once the JWT is received the user is confirmed to be logged in, we could request other information from the server, or send updates for example.
		IncomingPacketHandler incomingPacketHandler = plugin.getIncomingPacketHandler();
		new Info(plugin);

	}
}
