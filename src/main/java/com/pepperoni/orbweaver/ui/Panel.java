package com.pepperoni.orbweaver.ui;

import com.pepperoni.orbweaver.OrbWeaverPlugin;
import net.runelite.api.Client;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class Panel extends PluginPanel implements ActionListener {
    private final OrbWeaverPlugin plugin;
    private final JLabel currentPartyLabel = new JLabel("Not in a group", SwingConstants.CENTER);
    private final JLabel messageLabel = new JLabel();
    private final JLabel copySuccessLabel = new JLabel();
    private final JTextField textFieldJoinParty = new JTextField();
    private final JButton leaveJoinGroupButton = new JButton("Join Group");
    private final JButton loginLogout = new JButton("Login");
    private final Client client;

    public Panel(OrbWeaverPlugin plugin, Client client) {
        this.plugin = plugin;
        this.client = client;
        setBackground(ColorScheme.DARK_GRAY_COLOR);
        setLayout(new GridBagLayout());

        GridBagConstraints gridBagConstraints = new GridBagConstraints();

        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;

        gridBagConstraints.weightx = 1;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;

        gridBagConstraints.insets = new Insets(0, 0, 8, 0);

        JButton buttonCreateParty = new JButton("Create Group");
        buttonCreateParty.addActionListener(e -> {
            plugin.getUser().createGroup(plugin.getUser().getJWT());
        });

        add(buttonCreateParty, gridBagConstraints);
        gridBagConstraints.gridy++;

        leaveJoinGroupButton.addActionListener(e -> {
            if (plugin.getUser().getGroupId() == null) {
                try {
					if(textFieldJoinParty.getText().trim().length() > 0)
					{
						plugin.getUser().joinGroup(textFieldJoinParty.getText());
					}
                } catch (UnsupportedEncodingException ex) {
                    plugin.sendPrivateMessage("Could not join group. Was that a valid ID?");
                }
            } else {
                plugin.getUser().leaveGroup();
            }
        });

        add(textFieldJoinParty, gridBagConstraints);
        gridBagConstraints.gridy++;

        add(leaveJoinGroupButton, gridBagConstraints);
        gridBagConstraints.gridy++;

        add(messageLabel, gridBagConstraints);
        gridBagConstraints.gridy++;

        JPanel partyPanel = new JPanel();
        partyPanel.setLayout(new BoxLayout(partyPanel, BoxLayout.Y_AXIS));
        partyPanel.setBorder(new LineBorder(ColorScheme.DARKER_GRAY_COLOR));

        partyPanel.addMouseListener(new MouseAdapter() {

        });

        Border border = partyPanel.getBorder();
        Border margin = new EmptyBorder(10, 10, 10, 10);

        partyPanel.setBorder(new CompoundBorder(border, margin));

        JLabel copyLabel = new JLabel("Players", SwingConstants.CENTER);
        copyLabel.setFont(new Font(FontManager.getRunescapeFont().getName(), Font.PLAIN, 25));
        copyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel currentGroup = new JLabel("Not in a group", SwingConstants.CENTER);
        currentGroup.setAlignmentX(Component.CENTER_ALIGNMENT);

        partyPanel.add(copyLabel);
        partyPanel.add(currentGroup);
        add(partyPanel, gridBagConstraints);
        gridBagConstraints.gridy++;


        loginLogout.addActionListener(e -> {
            try {
                if (!plugin.getUser().isLoggedIn()) {
                    plugin.getUser().login();
                } else {
                    plugin.getUser().logout();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        addPlaceholder(textFieldJoinParty, "Group ID...");
        add(textFieldJoinParty, gridBagConstraints);
        gridBagConstraints.gridy++;

        add(leaveJoinGroupButton, gridBagConstraints);
        gridBagConstraints.gridy++;

        add(loginLogout, gridBagConstraints);
        gridBagConstraints.gridy++;
    }

    private static void addPlaceholder(JTextField textField, String placeholder) {
        textField.setText(placeholder);
        textField.setForeground(Color.GRAY);

        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(Color.GRAY);
                }
            }
        });
    }

    public void setGroupTextField(String groupId) {
        if (groupId == null) {
            groupId = "";
        }
        textFieldJoinParty.setText(groupId);
    }

    public void updateLoginLogoutButton() {
        if (plugin.getUser().isLoggedIn()) {
			this.loginLogout.setText("Logout");
			this.textFieldJoinParty.setVisible(true);
			this.leaveJoinGroupButton.setVisible(true);
		} else {
			this.loginLogout.setText("Login");
			this.textFieldJoinParty.setVisible(false);
			this.leaveJoinGroupButton.setVisible(false);
			setGroupTextField(null);
		}
    }

    public void updateLeaveJoinGroupButton() {
		if (plugin.getUser().getGroupId() != null) {
            this.leaveJoinGroupButton.setText("Leave Group");
        } else {
            this.leaveJoinGroupButton.setText("Join Group");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
