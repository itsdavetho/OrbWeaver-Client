package com.pepperoni.orbweaver.players;


import com.pepperoni.orbweaver.OrbWeaverPlugin;
import com.pepperoni.orbweaver.packets.PacketType;
import com.pepperoni.orbweaver.packets.outgoing.group.LeaveGroup;
import com.pepperoni.orbweaver.packets.outgoing.group.NewGroup;
import com.pepperoni.orbweaver.packets.outgoing.group.JoinGroup;
import com.pepperoni.orbweaver.packets.outgoing.user.LocationUpdate;
import com.pepperoni.orbweaver.packets.outgoing.user.Login;
import com.pepperoni.orbweaver.packets.outgoing.user.Logout;
import java.io.IOException;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;

public class User
{
	private final OrbWeaverPlugin plugin;
	private final Client client;
	@Getter
	@Setter
	private String username = null;
	@Getter
	@Setter
	private WorldPoint lastLocation = null;
	@Getter
	@Setter
	private String JWT = null;
	@Getter
	private String groupId = null;
	@Getter
	private boolean loggedIn = false;
	@Getter
	@Setter
	private int world;

	public User(OrbWeaverPlugin plugin, Client client)
	{
		this.plugin = plugin;
		this.client = client;
	}

	public void login() throws IOException
	{
		Login login = new Login(plugin, username, plugin.getConfig().password(), this.world);
	}

	public void logout() throws IOException
	{
		if (isLoggedIn() && getJWT() != null && plugin.getSocket() != null)
		{
			Logout logout = new Logout(plugin);

			this.setLoggedIn(false);
			this.setJWT(null);
			this.groupId = "";
			plugin.getModelManager().removeModels();
		}
	}

	public void setLoggedIn(boolean loggedIn)
	{
		this.loggedIn = loggedIn;
		plugin.getPanel().updateLoginLogoutButton();
	}

	public void createGroup(String jwt) throws IOException
	{
		new NewGroup(plugin);

	}

	public void setGroupId(String groupId)
	{
		this.groupId = groupId;
		plugin.getPanel().setGroupTextField(groupId);
		plugin.getPanel().updateLeaveJoinGroupButton();
	}

	public void joinGroup(String groupId) throws IOException
	{
		if (isLoggedIn() && getJWT() != null && getGroupId() == null)
		{
			new JoinGroup(plugin, groupId);
		}
		else
		{
			plugin.sendPrivateMessage("Could not join group. Are you logged in or already in a group?");
		}
	}

	public void leaveGroup() throws IOException
	{
		new LeaveGroup(plugin);
	}

	public String getGameStatus()
	{
		return "inactive";
	}

	public void setLocation(WorldPoint loc, int orientation) throws IOException
	{
		this.lastLocation = loc;

		if(plugin.getSocket().isConnected())
		{
			new LocationUpdate(plugin, loc, orientation);
		}
	}

}
