package com.pepperoni.prophunt;

import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
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

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.*;

@Slf4j
@PluginDescriptor(
        name = "Prop Hunt Two"
)
public class PropHuntTwoPlugin extends Plugin {

    private final PropHuntPackets packets;

    private final PropHuntUser user;

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
    //private int clientPort;

    public PropHuntTwoPlugin() {
        packets = new PropHuntPackets(this);
        user = new PropHuntUser(this, client);
        messageHandler = new MessageHandler(this);
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
        user.logout();
        overlayManager.remove(overlay);
        panel = null;
        clientToolbar.removeNavigation(navButton);
        getUser().setLocation(null);
        getUser().setUsername(null);
        user.setJWT(null);
        user.setGroupId(null);
        user.setLoggedIn(false);
        // socket.close();
        log.info("Prop Hunt Two stopped!");
    }

    @Subscribe
    public void onGameTick(GameTick tick) {
        if (client.getLocalPlayer() != null) {
            if (getUser().getLastLocation() != client.getLocalPlayer().getWorldLocation()) {
                // player moved, send location packet update
                getUser().setLocation(client.getLocalPlayer().getWorldLocation());
            }

            if(getUser().getUsername() == null && client.getLocalPlayer().getName() != null) {
                String playerName = client.getLocalPlayer().getName();
                getUser().setUsername(playerName);
                getUser().setWorld(client.getWorld());
            }
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOGGED_IN) {
            if (getUser().getUsername() == null && client.getLocalPlayer() != null && client.getLocalPlayer().getName() != null ) {
                getUser().setUsername(client.getLocalPlayer().getName());
                getUser().setWorld(client.getWorld());
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

    public PropHuntTwoPanel getPanel() {
        return panel;
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

    public void configureServer() {
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
                        sendPrivateMessage("Connecting (" + serverString + ")...");
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

    public PropHuntUser getUser() {
        return this.user;
    }

    @Provides
    PropHuntTwoConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(PropHuntTwoConfig.class);
    }

    public void sendPrivateMessage(String message) {
        clientThread.invokeLater(() -> client.addChatMessage(ChatMessageType.PRIVATECHAT, "Prop Hunt", message, "Prop Hunt"));
    }

    public PropHuntTwoConfig getConfig() {
        return this.config;
    }

    public ClientThread getClientThread() {
        return clientThread;
    }
}
