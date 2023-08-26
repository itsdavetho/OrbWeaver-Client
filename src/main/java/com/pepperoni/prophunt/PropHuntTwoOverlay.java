package com.pepperoni.prophunt;

import com.google.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import java.awt.*;

public class PropHuntTwoOverlay extends Overlay {
    private final PanelComponent panelComponent = new PanelComponent();
    private final Client client;
    private final PropHuntTwoPlugin plugin;
    private final PropHuntTwoConfig config;

    @Inject
    private PropHuntTwoOverlay(Client client, PropHuntTwoConfig config, PropHuntTwoPlugin plugin) {
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        this.client = client;
        this.config = config;
        this.plugin = plugin;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.getChildren().clear();
        String overlayTitle = "Prop Hunt 2 (World " + client.getWorld() + ")";

        // Build overlay title
        panelComponent.getChildren().add(TitleComponent.builder()
                .text(overlayTitle)
                .color(Color.GREEN)
                .build());

        // Set the size of the overlay (width)
        panelComponent.setPreferredSize(new Dimension(
                graphics.getFontMetrics().stringWidth(overlayTitle) + 30,
                0));

        // Add a line on the overlay for world number
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Server:")
                .right(plugin.getServerAddress() + ":" + plugin.getServerPort())
                .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Status:")
                .right(plugin.getGameStatus())
                .build());
        // Show world type goes here ...

        return panelComponent.render(graphics);
    }
}