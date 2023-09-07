package com.pepperoni.orbweaver.Players;

import net.runelite.api.coords.WorldPoint;

public class OrbWeaverPlayer
{
	private short propId;
	private byte propType;
	private short orientation;
	private WorldPoint location;
	private byte team;
	private byte status;
	private final String username;

	public OrbWeaverPlayer(String username)
	{
		this.username = username;
	}

	public String getUsername()
	{
		return this.username;

	}

	public short getPropId()
	{
		return propId;
	}

	public void setProp(short propId, byte propType)
	{
		this.propId = propId;
		this.propType = propType;
	}

	public byte getPropType()
	{
		return propType;
	}

	public WorldPoint getLocation()
	{
		return this.location;
	}

	public void setLocation(int x, int y, int z)
	{
		this.location = new WorldPoint(x, y, z);
	}

	public short getOrientation()
	{
		return orientation;
	}

	public void setOrientation(short orientation)
	{
		this.orientation = orientation;
	}

	public byte getTeam()
	{
		return team;
	}

	public void setTeam(byte team)
	{
		this.team = team;
	}

	public byte getStatus()
	{
		return status;
	}

	public void setStatus(byte status)
	{
		this.status = status;
	}

	@Override
	public String toString()
	{
		return "UserData{" +
			"propId=" + propId +
			", propType=" + propType +
			", orientation=" + orientation +
			", team=" + team +
			", status=" + status +
			", username='" + username + '\'' +
			'}';
	}
}
