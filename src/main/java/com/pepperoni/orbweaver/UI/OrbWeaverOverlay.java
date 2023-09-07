package com.pepperoni.orbweaver.UI;

import com.google.inject.Inject;
import com.pepperoni.orbweaver.Config;
import com.pepperoni.orbweaver.OrbWeaverPlugin;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import java.awt.*;

public class OrbWeaverOverlay extends net.runelite.client.ui.overlay.Overlay
{
    private final PanelComponent panelComponent = new PanelComponent();
    private final Client client;
    private final OrbWeaverPlugin plugin;
    private final Config config;
	private final String serverTitle = "OrbWeaver server";

    @Inject
    private OrbWeaverOverlay(Client client, Config config, OrbWeaverPlugin plugin) {
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        this.client = client;
        this.config = config;
        this.plugin = plugin;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.getChildren().clear();

        // Build overlay title
        panelComponent.getChildren().add(TitleComponent.builder()
                .text(serverTitle)
                .color(Color.GREEN)
                .build());

        String serverAddress = plugin.getServerAddress() + ":" + plugin.getServerPort();

        // Set the size of the overlay (width)
        panelComponent.setPreferredSize(new Dimension(
                graphics.getFontMetrics().stringWidth(serverAddress) + 50,
                0));

        // Add a line on the overlay for world number
        panelComponent.getChildren().add(LineComponent.builder()
                .left(plugin.getServerAddress() + ":" + plugin.getServerPort())
                .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Status:")
                .right(plugin.getUser().getGameStatus())
                .build());
        // Show world type goes here ...

        return panelComponent.render(graphics);
    }

	public void setServerTitle(String serverTitle) {
		serverTitle = serverTitle.substring(0, 32); // no server title should be longer than 32 characters.
	}
}