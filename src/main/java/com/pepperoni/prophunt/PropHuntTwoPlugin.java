package com.pepperoni.prophunt;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@PluginDescriptor(
	name = "Prop Hunt Two"
)
public class PropHuntTwoPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private PropHuntTwoConfig config;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Prop Hunt Two started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Prop Hunt Two stopped!");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			DatagramSocket clientSocket;
			try {
				clientSocket = new DatagramSocket();
				InetAddress serverAddress = InetAddress.getByName("127.0.0.1");
				int serverPort = 4200;

				int clientPort = serverPort + 1;

				List<byte[]> packet = createPacket(PacketType.USER_LOGIN, "unauthorized");
				byte[] username = "test".getBytes("UTF-8");
				byte[] password = "password".getBytes("UTF-8");
				byte[] worldBuffer = new byte[2];
				ByteBuffer.wrap(worldBuffer).putShort((short) 301);

				List<byte[]> bufferList = new ArrayList<>();
				bufferList.add(new byte[]{(byte) username.length, (byte) password.length});
				bufferList.add(username);
				bufferList.add(password);
				bufferList.add(worldBuffer);

				packet.addAll(bufferList);

				byte[] buffer = concatenateByteArrays(packet);
				DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, serverAddress, serverPort);

				clientSocket.send(datagramPacket);

				clientSocket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	private static List<byte[]> createPacket(PacketType packet, String token) {
		List<byte[]> packetList = new ArrayList<>();

		byte[] actionBuffer = new byte[1];
		actionBuffer[0] = (byte) packet.getIndex();

		byte[] jwtBuffer = token.getBytes();

		byte[] tokenSize = new byte[1];
		tokenSize[0] = (byte) jwtBuffer.length;

		packetList.add(actionBuffer);
		packetList.add(tokenSize);
		packetList.add(jwtBuffer);

		return packetList;
	}

	private static byte[] concatenateByteArrays(List<byte[]> arrays) {
		int totalLength = arrays.stream().mapToInt(array -> array.length).sum();
		byte[] result = new byte[totalLength];

		int currentIndex = 0;
		for (byte[] array : arrays) {
			System.arraycopy(array, 0, result, currentIndex, array.length);
			currentIndex += array.length;
		}

		return result;
	}
	@Provides
	PropHuntTwoConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PropHuntTwoConfig.class);
	}
}
