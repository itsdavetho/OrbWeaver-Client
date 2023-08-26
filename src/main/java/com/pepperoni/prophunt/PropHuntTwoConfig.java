package com.pepperoni.prophunt;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("prophunttwo")
public interface PropHuntTwoConfig extends Config
{
	@ConfigItem(
		keyName = "server",
		name = "Server Address",
		description = "The game server (IP:Port)"
	)
	default String server()
	{
		return "127.0.0.1:4200";
	}
}
