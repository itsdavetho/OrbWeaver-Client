package com.pepperoni.orbweaver.packets.type;

import com.pepperoni.orbweaver.OrbWeaverPlugin;
import com.pepperoni.orbweaver.packets.Packet;
import com.pepperoni.orbweaver.players.Player;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class PlayerList extends Packet
{
	public PlayerList(byte[] data) throws IOException
	{
		super(data);
	}

	@Override
	public void process(OrbWeaverPlugin plugin) throws IOException
	{
		DataInputStream data = this.getData();
		HashMap<Short, Player> players = new HashMap<>();

		// Read the packet length (2-byte uint16)
		int length = data.readUnsignedShort();

		while (data.available() > 0)
		{
			// Read user ID (2-byte uint16)
			int userId = data.readUnsignedShort();

			// Read username length (1-byte uint8)
			int usernameLength = data.readUnsignedByte();

			// Read username (UTF-8)
			byte[] usernameBytes = new byte[usernameLength];
			data.readFully(usernameBytes);
			String username = new String(usernameBytes, StandardCharsets.UTF_8);

			// Process the user data (userId and username)
			Player player = new Player(username);
			players.put((short) userId, player);
		}

		plugin.updatePlayers(players);
	}

}
