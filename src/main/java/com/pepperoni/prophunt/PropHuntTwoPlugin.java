package com.pepperoni.prophunt;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@PluginDescriptor(
        name = "Prop Hunt Two"
)
public class PropHuntTwoPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private PropHuntTwoConfig config;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private PropHuntTwoOverlay overlay;

    @Inject
    private ClientToolbar clientToolbar;

    private PropHuntTwoPanel panel;
    private NavigationButton navButton;

    private DatagramSocket socket;
    private final PropHuntPackets packets;
    private final MessageHandler messageHandler;

    private InetAddress serverAddress;
    private int serverPort;
    private int clientPort;
    private String playerName = null;

    private WorldPoint lastLocation;

    public PropHuntTwoPlugin() {
        this.packets = new PropHuntPackets(this);
        this.messageHandler = new MessageHandler(this);
    }

    @Override
    protected void startUp() throws Exception {
        overlayManager.add(overlay);
        panel = new PropHuntTwoPanel(this);
        final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "panel_icon.png");
        navButton = NavigationButton.builder()
                .tooltip("Prop Hunt")
                .priority(5)
                .icon(icon)
                .panel(panel)
                .build();
        clientToolbar.addNavigation(navButton);
        log.info("Prop Hunt Two started!");
    }

    @Override
    protected void shutDown() throws Exception {
        socket.close();
        log.info("Prop Hunt Two stopped!");
    }

    @Subscribe
    public void onGameTick(GameTick tick) {
        if (client.getLocalPlayer() != null) {
            if (lastLocation != client.getLocalPlayer().getWorldLocation()) {
                // player moved, send location packet update
                lastLocation = client.getLocalPlayer().getWorldLocation();
            }
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOGGED_IN) {
            if (client.getLocalPlayer() != null && playerName == null) {
                playerName = client.getLocalPlayer().getName();
                configureServer();
                try {
                    login();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void startMessageHandlerThread() {
        Thread messageHandlerThread = new Thread(this::handleIncomingMessages);
        messageHandlerThread.start();
    }

    private void handleIncomingMessages() {
        byte[] buffer = new byte[512]; // Adjust buffer size as needed

        while (!Thread.interrupted()) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(packet); // This call blocks until a packet is received
                messageHandler.handleMessage(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void configureServer() {
        try {
            socket = new DatagramSocket();
            String[] server = config.server().split(":");
            if (server.length > 0) {
                String hostname = server[0];
                if (server.length < 2) {
                    serverPort = 4200;
                } else {
                    try {
                        serverPort = Integer.parseInt(server[1]);
                    } catch (NumberFormatException e) {
                        System.out.println("invalid port, trying default port (4200)");
                        serverPort = 4200;
                    }
                }

                try {
                    serverAddress = InetAddress.getByName(hostname);
                    if (serverAddress instanceof Inet4Address) {
                        String serverString = serverAddress.getHostName() + ":" + serverPort;
                        client.addChatMessage(ChatMessageType.PRIVATECHAT, "Prop Hunt", "Connecting (" + serverString + ")...", "Prop Hunt");
                        startMessageHandlerThread();
                    } else {
                        System.out.println("Invalid IPv6 Address.");
                    }
                } catch (UnknownHostException e) {
                    System.out.println("Invalid IP Address.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void login() throws IOException {
        int world = client.getWorld();
        System.out.println(playerName);

        List<byte[]> packet = packets.createPacket(PacketType.USER_LOGIN, "unauthorized");
        byte[] username = playerName.getBytes(StandardCharsets.UTF_8);
        byte[] password = config.password().getBytes(StandardCharsets.UTF_8);
        byte[] worldBuffer = new byte[2];
        ByteBuffer.wrap(worldBuffer).putShort((short) world);

        List<byte[]> bufferList = new ArrayList<>();
        bufferList.add(new byte[]{(byte) username.length, (byte) password.length});
        bufferList.add(username);
        bufferList.add(password);
        bufferList.add(worldBuffer);

        packet.addAll(bufferList);

        byte[] buffer = packets.concatenateByteArrays(packet);
        DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, serverAddress, serverPort);

        socket.send(datagramPacket);
    }

    public void createGroup(String jwt) {
        List<byte[]> packet = getPacketHandler().createPacket(PacketType.GROUP_NEW, jwt);
        byte[] buffer = getPacketHandler().concatenateByteArrays(packet);
        getPacketHandler().sendPacket(buffer);
        System.out.println("createGroup called");
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public PropHuntPackets getPacketHandler() {
        return this.packets;
    }

    public InetAddress getServerAddress() {
        return serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }

    @Provides
    PropHuntTwoConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(PropHuntTwoConfig.class);
    }
}
