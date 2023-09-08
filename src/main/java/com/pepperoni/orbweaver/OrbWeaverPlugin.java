package com.pepperoni.orbweaver;

import com.google.inject.Inject;
import com.google.inject.Provides;
import com.pepperoni.orbweaver.packets.PacketHandler;
import com.pepperoni.orbweaver.players.OrbWeaverPlayer;
import com.pepperoni.orbweaver.players.User;
import com.pepperoni.orbweaver.ui.Overlay;
import com.pepperoni.orbweaver.ui.Panel;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.SwingUtilities;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Animation;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.DecorativeObject;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.GroundObject;
import net.runelite.api.Model;
import net.runelite.api.Player;
import net.runelite.api.Renderable;
import net.runelite.api.RuneLiteObject;
import net.runelite.api.Tile;
import net.runelite.api.TileItem;
import net.runelite.api.WallObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuEntryAdded;
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

@Slf4j
@PluginDescriptor(
	name = "OrbWeaver",
	description = "Connect your creative minds",
	tags = {"multiplayer", "creative"}

)
public class OrbWeaverPlugin extends Plugin
{
	@Getter
	@Inject
	private Client client;

	@Getter
	private final User user;

	@Getter
	@Inject
	private ClientThread clientThread;

	@Getter
	@Inject
	private Config config;

	@Inject
	private OverlayManager overlayManager;

	@Getter
	@Inject
	private Overlay overlay;

	@Inject
	private ClientToolbar clientToolbar;

	@Getter
	private Panel panel;
	private NavigationButton navButton;

	@Inject
	private ChatCommandManager commandManager;

	@Getter
	private DatagramSocket socket;
	@Getter
	@Setter
	private InetAddress serverAddress;
	@Getter
	@Setter
	private int serverPort;
	private final PacketHandler packetHandler;

	@Getter
	@Setter
	private Map<Short, OrbWeaverPlayer> players = new HashMap<>();

	@Getter
	@Setter
	private String serverTitle = "OrbWeaver";

	@Getter
	@Setter
	private int playersOnline = 0;

	@Getter
	@Setter
	private int maxPlayers = 0;


	@Getter
	@Inject
	public OrbModelManager modelManager;

	public OrbWeaverPlugin()
	{
		user = new User(this, client);
		packetHandler = new PacketHandler(this);
	}

	@Override
	protected void startUp()
	{
		commandManager.registerCommandAsync("!panel", this::reloadPanel);
		configureServer();
		overlayManager.add(overlay);
		loadPanel();
		log.info("OrbWeaver started!");
	}

	@Override
	protected void shutDown()
	{
		commandManager.unregisterCommand("!panel");
		user.logout();
		overlayManager.remove(overlay);
		panel = null;
		clientToolbar.removeNavigation(navButton);
		getUser().setLocation(null, -1);
		getUser().setUsername(null);
		user.setJWT(null);
		user.setGroupId(null);
		user.setLoggedIn(false);
		log.info("OrbWeaver stopped!");
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		if (client.getLocalPlayer() == null)
		{
			return;
		}

		if (client.getLocalPlayer().getWorldLocation() != null)
		{
			if (getUser().getLastLocation() == null || !getUser().getLastLocation().equals(client.getLocalPlayer().getWorldLocation()))
			{
				getUser().setLocation(client.getLocalPlayer().getWorldLocation(), client.getLocalPlayer().getOrientation());
			}
		}

		if (getUser().getUsername() == null)
		{
			String playerName = client.getLocalPlayer().getName();
			getUser().setUsername(playerName);
			getUser().setWorld(client.getWorld());
		}
	}

	@Subscribe
	public void onClientTick(ClientTick event)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}
		for (Map.Entry<Integer, OrbWeaverModel> model : this.getModelManager().getModels().entrySet())
		{
			int modelStorageId = model.getKey();
			OrbWeaverModel orbWeaverModel = model.getValue();
			if (!orbWeaverModel.getRuneLiteObject().isActive() && orbWeaverModel.isActive() == false)
			{
				clientThread.invoke(() -> {
					orbWeaverModel.setActive(false);
					orbWeaverModel.getRuneLiteObject().setActive(true);
					orbWeaverModel.setActive(true);
				});
			}
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.HOPPING)
		{
			if (getUser().getWorld() == client.getWorld())
			{
				getUser().setWorld(client.getWorld());
			}
		}
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		String target = event.getTarget();
		String option = event.getOption();
		Tile tile = client.getSelectedSceneTile();
		if (tile != null)
		{
			if (option.equals("Walk here"))
			{
				// get all game objects on this tile
				GameObject[] gameObjects = tile.getGameObjects();
				for (GameObject gameObject : gameObjects)
				{
					if (gameObject == null)
					{
						continue;
					}
					Renderable renderable = gameObject.getRenderable();
					if (renderable == null)
					{
						continue;
					}
					if (renderable instanceof Model)
					{
						Model model = (Model) renderable;
					}
				}

				// get any ground object on this tile
				GroundObject groundObject = tile.getGroundObject();
				if (groundObject != null)
				{
					Renderable renderable = groundObject.getRenderable();
					if (renderable instanceof Model)
					{
						Model model = (Model) groundObject.getRenderable();
					}
				}

				// get any decorativeobject on this tile
				DecorativeObject decorativeObject = tile.getDecorativeObject();
				if (decorativeObject != null)
				{
					Renderable renderable = decorativeObject.getRenderable();
					if (renderable instanceof Model)
					{
						Model model = (Model) decorativeObject.getRenderable();
					}
				}

				// get any wallobject on this tile
				WallObject wallObject = tile.getWallObject();
				if (wallObject != null)
				{
					Renderable renderable = wallObject.getRenderable1();
					if (renderable instanceof Model)
					{
						Model model = (Model) renderable;
					}
				}

				List<TileItem> tileItems = tile.getGroundItems();
			}
		}

		// get any player on this tile
		Player player = event.getMenuEntry().getPlayer();
		if (player != null && option.equals("Trade with"))
		{

		}

		// get the local player (You) on this tile
		Player localPlayer = client.getLocalPlayer();
		if (localPlayer != null && tile != null && option.equals("Walk here"))
		{
			if (tile.getLocalLocation().equals(localPlayer.getLocalLocation()))
			{

			}
		}
	}

	private void reloadPanel(ChatMessage chatMessage, String s)
	{
		panel = null;
		clientToolbar.removeNavigation(navButton);
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				loadPanel();
			}
		});
	}

	private void loadPanel()
	{
		panel = new Panel(this, client);
		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "panel_icon.png");
		navButton = NavigationButton.builder()
			.tooltip("OrbWeaver")
			.priority(5)
			.icon(icon)
			.panel(panel)
			.build();
		clientToolbar.addNavigation(navButton);
	}

	private void startMessageHandlerThread()
	{
		Thread messageHandlerThread = new Thread(this::handleIncomingMessages);
		messageHandlerThread.start();
	}

	private void handleIncomingMessages()
	{
		byte[] buffer = new byte[512]; // Adjust buffer size as needed

		while (!Thread.interrupted())
		{
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			try
			{
				socket.receive(packet); // This call blocks until a packet is received
				packetHandler.handlePacket(packet);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public PacketHandler getPacketHandler()
	{
		return packetHandler;
	}

	public void configureServer()
	{
		try
		{
			socket = new DatagramSocket();
			String[] server = config.server().split(":");
			if (server.length > 0)
			{
				String hostname = server[0];
				if (server.length < 2)
				{
					serverPort = 4200;
				}
				else
				{
					try
					{
						serverPort = Integer.parseInt(server[1]);
					}
					catch (NumberFormatException e)
					{
						System.out.println("invalid port, trying default port (4200)");
						serverPort = 4200;
					}
				}

				try
				{
					serverAddress = InetAddress.getByName(hostname);
					if (serverAddress instanceof Inet4Address)
					{
						String serverString = serverAddress.getHostName() + ":" + serverPort;
						// sendPrivateMessage("Connecting (" + serverString + ")...");
						startMessageHandlerThread();
					}
					else
					{
						System.out.println("Invalid IPv6 Address.");
					}
				}
				catch (UnknownHostException e)
				{
					System.out.println("Invalid IP Address.");
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Provides
	Config provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(Config.class);
	}

	public void sendPrivateMessage(String message)
	{
		clientThread.invokeLater(() -> client.addChatMessage(ChatMessageType.PRIVATECHAT, "OrbWeaver", message, "OrbWeaver"));
	}

}
