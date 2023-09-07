package com.pepperoni.orbweaver;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class PropHuntTwoPluginTest {
    public static void main(String[] args) throws Exception {
        ExternalPluginManager.loadBuiltin(OrbWeaverPlugin.class);
        RuneLite.main(args);
    }
}