package com.pepperoni.prophunt;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class PropHuntTwoPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(PropHuntTwoPlugin.class);
		RuneLite.main(args);
	}
}