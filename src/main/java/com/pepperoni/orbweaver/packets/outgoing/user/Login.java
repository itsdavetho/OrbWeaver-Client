package com.pepperoni.orbweaver.packets.outgoing.user;

import com.pepperoni.orbweaver.OrbWeaverPlugin;
import com.pepperoni.orbweaver.packets.OutgoingPacket;
import com.pepperoni.orbweaver.packets.PacketType;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Login extends OutgoingPacket
{
	public Login(OrbWeaverPlugin plugin, String username, String password, int world) throws IOException
	{
		super(plugin, PacketType.USER_LOGIN);
		byte[] usernameBytes = username.getBytes(StandardCharsets.UTF_8);
		byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);

		getDataOutputStream().writeByte(username.length());
		getDataOutputStream().writeByte(password.length());

		getDataOutputStream().write(usernameBytes);
		getDataOutputStream().write(passwordBytes);
		getDataOutputStream().writeShort(world);

		this.send();
	}
}
