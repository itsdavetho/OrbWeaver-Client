package com.pepperoni.prophunt;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;


public class PacketHandler
{
	private final PropHuntTwoPlugin plugin;

	public PacketHandler(PropHuntTwoPlugin plugin)
	{
		this.plugin = plugin;
	}

	public static void debugPacket(DatagramPacket packet)
	{
		byte[] data = packet.getData();
		int length = packet.getLength();
		String remoteAddress = packet.getAddress().getHostAddress();
		int remotePort = packet.getPort();

		String packetContent = new String(data, 0, length, StandardCharsets.UTF_8);

		System.out.println("Received packet from " + remoteAddress + ":" + remotePort);
		System.out.println("Packet content: " + packetContent.toString());
	}

	public void handlePacket(DatagramPacket packet)
	{
		int offset = 0;
		byte[] data = packet.getData();
		byte packetType = data[0];

		if (packetType < 0 || packetType >= PacketType.values().length)
		{
			System.out.println("invalid prop hunt packet received");
			return;
		}
		offset++;

		if (packetType == PacketType.USER_GET_JWT.getIndex())
		{
			int size = 1;
			Utf8Serializer.Utf8SerializedData utf8Data = Utf8Serializer.serialize(data, size, offset);
			offset = utf8Data.offset;
			plugin.getUser().setJWT(utf8Data.data[0]);
			plugin.getUser().setLoggedIn(true);
		}
		else if (packetType == PacketType.ERROR_MESSAGE.getIndex())
		{
			ByteBuffer buffer = ByteBuffer.wrap(data, offset, packet.getLength());
			short dataValue = buffer.getShort();
			//  if (Errors.Errors[dataValue] != null) {
			System.out.println("ERROR RECV: " + dataValue);
			//}
		}
		else if (packetType == PacketType.PLAYER_LIST.getIndex())
		{
			try
			{
				HashMap<Short, PropHuntPlayer> players = new HashMap<>();
				int length = ((data[offset] & 0xFF) << 8) | (data[offset + 1] & 0xFF); // packet length 2 byte uint16
				offset += 2;

				while (offset < length) {
					int userId = ((data[offset] & 0xFF) << 8) | (data[offset + 1] & 0xFF); // userid 2 byte uint16
					offset += 2;
					int usernameLength = data[offset] & 0xFF; // username length 1byte uint8
					offset++;
					String username = new String(data, offset, usernameLength, StandardCharsets.UTF_8); // username utf8 * usernameLength
					offset += usernameLength;

					// Process the user data (userId and username)
					System.out.println("user: " + userId + ", usernameLength: " + usernameLength + ", username: " + username);
					PropHuntPlayer player = new PropHuntPlayer(username);
					players.put((short) userId, player);
				}

				//debugPacket(packet);
				plugin.updatePlayers(players);

				//	for (Map.Entry<Short, PropHuntPlayer> player : players.entrySet()) {
				//	System.out.println("Received user data: " + player);
				//}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else if (packetType == PacketType.PLAYER_UPDATE.getIndex())
		{
			/*short updateType = (short) ((short) data[offset] & 0xFF); // update type 1byte uint8
			offset++;
			short userIdToUpdate = (short) ((short) ((data[offset] & 0xFF) << 8) | (data[offset + 1] & 0xFF)); // 2 bytes uint16
			offset += 2;
			System.out.println("recv update " + updateType + " " + userIdToUpdate);
			*/plugin.updatePlayer(data);

		}
		else if (packetType == PacketType.GROUP_INFO.getIndex())
		{
			int creatorUsernameLength = packet.getData()[offset];
			offset++;
			int groupIdLength = packet.getData()[offset];
			offset++;

			byte[] creatorUsernameBuffer = new byte[creatorUsernameLength];
			System.arraycopy(packet.getData(), offset, creatorUsernameBuffer, 0, creatorUsernameLength);
			String creatorUsername = new String(creatorUsernameBuffer, StandardCharsets.UTF_8);
			offset += creatorUsernameLength;

			byte[] groupIdBuffer = new byte[groupIdLength];
			System.arraycopy(packet.getData(), offset, groupIdBuffer, 0, groupIdLength);
			String groupId = new String(groupIdBuffer, StandardCharsets.UTF_8);
			offset += groupIdLength;
			System.out.println("Received group info (creator: " + creatorUsername + ", GID: " + groupId + ")");
			plugin.getUser().setGroupId(groupId);
		}
		else if (packetType == PacketType.GROUP_LEAVE.getIndex())
		{
			plugin.sendPrivateMessage("You have left the Prop Hunt group");
			plugin.getUser().setGroupId(null);
		}
		else
		{
			System.out.println("Unknown MSG recv: " + ByteBuffer.wrap(data) + " action " + packetType);
		}
	}
}
