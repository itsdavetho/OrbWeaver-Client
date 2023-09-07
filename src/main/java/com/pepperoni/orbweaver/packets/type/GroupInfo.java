package com.pepperoni.orbweaver.packets.type;

import com.pepperoni.orbweaver.OrbWeaverPlugin;
import com.pepperoni.orbweaver.packets.Packet;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class GroupInfo extends Packet
{
	private String groupId;
	private String creatorUsername;

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
		this.creatorUsername = new String(creatorUsernameBuffer, StandardCharsets.UTF_8);

		byte[] groupIdBuffer = new byte[groupIdLength];
		data.readFully(groupIdBuffer);
		this.groupId = new String(groupIdBuffer, StandardCharsets.UTF_8);

		plugin.getUser().setGroupId(groupId);
		this.close();
	}

	public String getCreator() {
		return this.creatorUsername;
	}

	public String getGroupId() {
		return this.groupId;
	}
}
