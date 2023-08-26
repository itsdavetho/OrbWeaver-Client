package com.pepperoni.prophunt;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatCommandManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
    private final PropHuntPackets packets;
    private final MessageHandler messageHandler;
    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;
    @Inject
    private PropHuntTwoConfig config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private PropHuntTwoOverlay overlay;

    @Inject
    private ChatCommandManager commandManager;

    @Inject
    private ClientToolbar clientToolbar;
    private PropHuntTwoPanel panel;
    private NavigationButton navButton;
    private DatagramSocket socket;
    private InetAddress serverAddress;
    private int serverPort;
    private int clientPort;
    private String playerName = null;

    private WorldPoint lastLocation = null;
    private String token = null;
    private String groupId = null;
    private boolean loggedIn = false;

    public PropHuntTwoPlugin() {
        this.packets = new PropHuntPackets(this);
        this.messageHandler = new MessageHandler(this);
    }

    @Override
    protected void startUp() {
        commandManager.registerCommandAsync("!panel", this::reloadPanel);
        configureServer();
        overlayManager.add(overlay);
        loadPanel();
        log.info("Prop Hunt Two started!");
    }

    @Override
    protected void shutDown() {
        commandManager.unregisterCommand("!panel");
        logout();
        overlayManager.remove(overlay);
        panel = null;
        clientToolbar.removeNavigation(navButton);
        lastLocation = null;
        playerName = null;
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
            if (socket == null && client.getLocalPlayer() != null && client.getLocalPlayer().getName() != null && playerName == null) {
                playerName = client.getLocalPlayer().getName();
            }
        }
    }

    private void reloadPanel(ChatMessage chatMessage, String s) {
        panel = null;
        clientToolbar.removeNavigation(navButton);
        System.out.println("reloading panel");

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                loadPanel();
            }
        });
    }

    private void loadPanel() {
        panel = new PropHuntTwoPanel(this, client);
        final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "panel_icon.png");
        navButton = NavigationButton.builder()
                .tooltip("Prop Hunt")
                .priority(5)
                .icon(icon)
                .panel(panel)
                .build();
        clientToolbar.addNavigation(navButton);
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
                        clientThread.invokeLater(() -> client.addChatMessage(ChatMessageType.PRIVATECHAT, "Prop Hunt", "Connecting (" + serverString + ")...", "Prop Hunt"));
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

    public void login() throws IOException {
        if (socket == null) {
            configureServer();
        }

        if (playerName == null) {
            if (client.getLocalPlayer() != null && client.getLocalPlayer().getName() != null) {
                playerName = client.getLocalPlayer().getName();
            }
        }

        if (playerName != null) {
            int world = client.getWorld();

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

            packets.sendPacket(packet);
        } else {
            System.out.println("Failed to login: local player not found");
        }
    }

    public void logout() {
        if(getLoggedIn() && token != null && socket != null) {
            List<byte[]> packet = packets.createPacket(PacketType.USER_LOGOUT, token);
            packets.sendPacket(packet);
        }
    }

    public boolean getLoggedIn() {
        return this.loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
        panel.updateLoginLogoutButton();
    }

    public void createGroup(String jwt) {
        List<byte[]> packet = getPacketHandler().createPacket(PacketType.GROUP_NEW, jwt);
        getPacketHandler().sendPacket(packet);
    }
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
        panel.setGroupTextField(groupId);
        panel.updateLeaveJoinGroupButton();
    }

    public void joinGroup(String groupId) throws UnsupportedEncodingException {
        if(getLoggedIn() && getJWT() != null && getGroupId() == null) {
            List<byte[]> packet = getPacketHandler().createPacket(PacketType.GROUP_JOIN, getJWT());
            byte[] groupBuffer = groupId.getBytes(StandardCharsets.UTF_8);
            List<byte[]> bufferList = new ArrayList<>();
            bufferList.add(new byte[]{(byte) groupBuffer.length});
            bufferList.add(groupBuffer);
            packet.addAll(bufferList);
            packets.sendPacket(packet);
        } else {
            sendPrivateMessage("Could not join group. Are you logged in or already in a group?");
        }
    }

    public void leaveGroup() {

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

    public String getJWT() {
        return token;
    }

    public void setJWT(String token) {
        this.token = token;
    }

    public String getGameStatus() {
        return "inactive";
    }

    @Provides
    PropHuntTwoConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(PropHuntTwoConfig.class);
    }

    public void sendPrivateMessage(String message) {
        clientThread.invokeLater(() -> client.addChatMessage(ChatMessageType.PRIVATECHAT, "Prop Hunt", message, "Prop Hunt"));
    }
}
