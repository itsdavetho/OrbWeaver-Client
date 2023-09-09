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

public class Panel extends PluginPanel implements ActionListener {
	// TODO: UI strings should probably be defined by a language file but this doesn't matter until the plugin is done
	private final String joinGroup = "Join Group";
	private final String leaveGroup = "Leave Group";
	private final String createGroup = "Create Group";
	private final String login = "Login";
	private final String logout = "Logout";
	private final String notInGroup = "Not in a group";
	private final String couldNotJoinGroup = "Could not join group. Was that a valid ID?";
	private final String players = "Players";
	private final String groupId = "Group ID...";
	private final OrbWeaverPlugin plugin;
	private final JLabel currentPartyLabel = new JLabel(notInGroup, SwingConstants.CENTER);
	private final JLabel messageLabel = new JLabel();
	private final JLabel copySuccessLabel = new JLabel();
	private final JTextField groupIdTextField = new JTextField();

	private final JButton leaveJoinGroupButton = new JButton(joinGroup);
	private final JButton createGroupButton = new JButton(createGroup);
	private final JButton loginLogout = new JButton(login);
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

        createGroupButton.addActionListener(e -> {
			try
			{
				plugin.getUser().createGroup(plugin.getUser().getJWT());
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		});

        add(createGroupButton, gridBagConstraints);
        gridBagConstraints.gridy++;

        leaveJoinGroupButton.addActionListener(e -> {
            if (plugin.getUser().getGroupId() == null) {
                try {
					if(groupIdTextField.getText().trim().length() > 0)
					{
						plugin.getUser().joinGroup(groupIdTextField.getText());
					}
                } catch (IOException ex) {
                    plugin.sendPrivateMessage(couldNotJoinGroup);
                }
            } else {
				try
				{
					plugin.getUser().leaveGroup();
				}
				catch (IOException ex)
				{
					ex.printStackTrace();
				}
			}
        });

        add(groupIdTextField, gridBagConstraints);
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

        JLabel copyLabel = new JLabel(players, SwingConstants.CENTER);
        copyLabel.setFont(new Font(FontManager.getRunescapeFont().getName(), Font.PLAIN, 25));
        copyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel currentGroup = new JLabel(notInGroup, SwingConstants.CENTER);
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

        addPlaceholder(groupIdTextField, groupId);
        add(groupIdTextField, gridBagConstraints);
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
        groupIdTextField.setText(groupId);
    }

	// update the panel and overlay on state change (logged in/logged out, joined/left group, etc)
    public void update() {
		boolean isLoggedIn = plugin.getUser().isLoggedIn();
		boolean hasGroup = plugin.getUser().getGroupId() != null;

		this.loginLogout.setText(isLoggedIn ? logout : login);
		this.leaveJoinGroupButton.setVisible(isLoggedIn);
		this.groupIdTextField.setVisible(isLoggedIn);
		this.createGroupButton.setVisible(isLoggedIn && !hasGroup);

		if(!isLoggedIn) {
			this.groupIdTextField.setText("");
			addPlaceholder(groupIdTextField, groupId);
			this.plugin.getOverlay().setServerTitle("OrbWeaver");
			this.plugin.setPlayersOnline(0);
			this.plugin.setMaxPlayers(0);
		}

		this.leaveJoinGroupButton.setText(hasGroup ? leaveGroup : joinGroup);

		if (!hasGroup) {
			addPlaceholder(groupIdTextField, groupId);
		}
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
