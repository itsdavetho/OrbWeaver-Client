package com.pepperoni.orbweaver.ui;

import com.google.inject.Inject;
import com.pepperoni.orbweaver.Config;
import com.pepperoni.orbweaver.OrbWeaverPlugin;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

public class Overlay extends net.runelite.client.ui.overlay.Overlay
{
	private final PanelComponent panelComponent = new PanelComponent();
	private final Client client;
	private final OrbWeaverPlugin plugin;
	private final Config config;

	@Inject
	private Overlay(Client client, Config config, OrbWeaverPlugin plugin)
	{
		setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
		this.client = client;
		this.config = config;
		this.plugin = plugin;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		panelComponent.getChildren().clear();

		// Build overlay title
		panelComponent.getChildren().add(TitleComponent.builder()
			.text(plugin.getServerTitle())
			.color(Color.GREEN)
			.build());

		String serverAddress = "IP: " + plugin.getServerAddress() + ":" + plugin.getServerPort();

		// Set the size of the overlay (width)
		panelComponent.setPreferredSize(new Dimension(
			graphics.getFontMetrics().stringWidth(serverAddress) + 25,
			0));

		// Add a line on the overlay for world number
		panelComponent.getChildren().add(LineComponent.builder()
			.left("IP:")
			.right(plugin.getServerAddress() + ":" + plugin.getServerPort())
			.build());
		panelComponent.getChildren().add(LineComponent.builder()
			.left("Status:")
			.right(plugin.getUser().getGameStatus())
			.build());
		panelComponent.getChildren().add(LineComponent.builder()
			.left("Players:")
			.right(plugin.getPlayersOnline() + "/" + plugin.getMaxPlayers())
				.build()
			);
		// Show world type goes here ...

		return panelComponent.render(graphics);
	}

	public void setServerTitle(String serverTitle)
	{
		serverTitle = serverTitle.substring(0, 32); // no server title should be longer than 32 characters.
	}
}