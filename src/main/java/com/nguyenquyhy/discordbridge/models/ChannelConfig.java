package com.nguyenquyhy.discordbridge.models;

import java.util.ArrayList;
import java.util.List;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

/**
 * Created by Hy on 10/13/2016.
 */
@ConfigSerializable
public class ChannelConfig {
    /**
     * Configs initialized in constructor will be restored automatically if deleted.
     */
    public ChannelConfig() {
        discordId = "DISCORD_CHANNEL_ID";
        consoleCommandPrefix = "/";
        consoleCommandRole = new ArrayList<>();
    }

    /**
     * This is called only when the config file is first created.
     */
    public void initializeDefault() {
        discord = new ChannelDiscordConfig();
        discord.initializeDefault();
        minecraft = new ChannelMinecraftConfig();
        minecraft.initializeDefault();
    }

    @Setting
    public String discordId;
    @Setting
    public String consoleCommandPrefix;
    @Setting
    public List<String> consoleCommandRole;
    @Setting
    public ChannelDiscordConfig discord;
    @Setting
    public ChannelMinecraftConfig minecraft;

    public void migrate() {
        if (discord != null)
            discord.migrate();
    }
}