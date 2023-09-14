package com.pepperoni.orbweaver.packets.incoming.group;

import com.pepperoni.orbweaver.OrbWeaverPlugin;
import com.pepperoni.orbweaver.packets.IncomingPacket;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.Getter;

public class GroupInfo extends IncomingPacket
{
	@Getter
	private String groupId;
	@Getter
	private String creator;

	public GroupInfo(byte[] data) throws IOException
	{
		super(data);
	}

	// TODO: this packet can be changed later to use the user's ID instead of the username to reduce bandwidth
	@Override
	public void process(OrbWeaverPlugin plugin) throws IOException
	{
		DataInputStream data = this.getData();
		int creatorUsernameLength = data.readUnsignedByte();
		int groupIdLength = data.readUnsignedByte();

		byte[] creatorUsernameBuffer = new byte[creatorUsernameLength];
		data.readFully(creatorUsernameBuffer);
		this.creator = new String(creatorUsernameBuffer, StandardCharsets.UTF_8);

		byte[] groupIdBuffer = new byte[groupIdLength];
		data.readFully(groupIdBuffer);
		this.groupId = new String(groupIdBuffer, StandardCharsets.UTF_8);

		plugin.getUser().setGroupId(groupId);
		this.close();
	}
}
