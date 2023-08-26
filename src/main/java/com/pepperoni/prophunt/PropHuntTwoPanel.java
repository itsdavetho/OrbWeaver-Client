package com.pepperoni.prophunt;

import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PropHuntTwoPanel extends PluginPanel implements ActionListener {
    private final PropHuntTwoPlugin plugin;
    private final JLabel title;

    public PropHuntTwoPanel(PropHuntTwoPlugin plugin) {
        this.plugin = plugin;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 10, 0); // add padding
        title = new JLabel("Prop Hunt");
        title.setFont(FontManager.getRunescapeBoldFont());
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title, gbc);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
