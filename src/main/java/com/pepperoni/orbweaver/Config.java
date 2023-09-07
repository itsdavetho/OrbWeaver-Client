package com.pepperoni.orbweaver;

import com.pepperoni.orbweaver.util.RandomStringGenerator;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("OrbWeaver")
public interface Config extends net.runelite.client.config.Config
{
    @ConfigItem(
            keyName = "server",
            name = "Server Address",
            description = "The game server (IP:Port)"
    )
    default String server() {
        return "127.0.0.1:4200";
    }

    @ConfigItem(
            keyName = "password",
            name = "Password",
            description = "A password (default randomly generated)"
    )
    default String password() {
        return RandomStringGenerator.generateRandomString();
    }

}
