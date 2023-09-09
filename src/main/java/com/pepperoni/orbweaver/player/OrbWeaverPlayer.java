package com.pepperoni.orbweaver.player;

import net.runelite.api.coords.WorldPoint;

public class OrbWeaverPlayer
{
	private short modelId;
	private byte modelType;
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

	public short getModelId()
	{
		return modelId;
	}

	public void setModel(short modelId, byte modelType)
	{
		this.modelId = modelId;
		this.modelType = modelType;
	}

	public byte getModelType()
	{
		return modelType;
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
			"modelId=" + modelId +
			", modelType=" + modelType +
			", orientation=" + orientation +
			", team=" + team +
			", status=" + status +
			", username='" + username + '\'' +
			'}';
	}
}
