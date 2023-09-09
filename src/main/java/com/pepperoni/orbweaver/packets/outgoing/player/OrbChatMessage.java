package com.pepperoni.orbweaver.packets.outgoing.player;

import com.pepperoni.orbweaver.OrbWeaverPlugin;
import com.pepperoni.orbweaver.packets.OutgoingPacket;
import com.pepperoni.orbweaver.packets.PacketType;
import com.pepperoni.orbweaver.packets.PlayerUpdateType;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class OrbChatMessage extends OutgoingPacket
{
	public OrbChatMessage(OrbWeaverPlugin plugin, String chatMessage) throws IOException
	{
		super(plugin, PacketType.PLAYER_UPDATE);
		DataOutputStream data = this.getDataOutputStream();
		data.writeByte(PlayerUpdateType.CHAT_MESSAGE.getIndex());

		int chatLength = chatMessage.length();
		byte[] chatMessageBytes = chatMessage.getBytes(StandardCharsets.UTF_8);
		data.writeByte(chatLength);
		data.write(chatMessageBytes);

		this.send();
	}
}
