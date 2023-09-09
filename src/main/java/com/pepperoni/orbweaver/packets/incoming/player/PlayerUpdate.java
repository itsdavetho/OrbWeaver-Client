package com.pepperoni.orbweaver.packets.incoming.player;

import com.pepperoni.orbweaver.OrbWeaverPlugin;
import com.pepperoni.orbweaver.packets.IncomingPacket;
import com.pepperoni.orbweaver.packets.PlayerUpdateType;
import com.pepperoni.orbweaver.player.OrbWeaverPlayer;
import java.io.DataInputStream;
import java.io.IOException;

public class PlayerUpdate extends IncomingPacket
{


	public PlayerUpdate(byte[] data) throws IOException
	{
		super(data);
	}

	@Override
	public void process(OrbWeaverPlugin plugin) throws IOException
	{
		DataInputStream data = this.getData();
		int updateType = data.readUnsignedByte();
		short userIdToUpdate = (short) data.readUnsignedShort();

		if (!plugin.getPlayers().containsKey(userIdToUpdate))
		{
			//System.out.println("Attempted to update a player, but they did not exist!");
			return;
		}
		if (updateType < 0 || updateType >= PlayerUpdateType.values().length)
		{
			System.out.println("invalid update type received: " + userIdToUpdate + " " + updateType + " " + PlayerUpdateType.values().length);
			return;
		}
		OrbWeaverPlayer orbWeaverPlayer = plugin.getPlayers().get(userIdToUpdate);

		if (updateType == PlayerUpdateType.LOCATION.getIndex())
		{
			int x = data.readUnsignedShort();
			int y = data.readUnsignedShort();
			int z = data.readUnsignedByte();
			short orientation = (short) data.readUnsignedShort();

			orbWeaverPlayer.setLocation(x, y, z);
			orbWeaverPlayer.setOrientation(orientation);
		}
	}
}
