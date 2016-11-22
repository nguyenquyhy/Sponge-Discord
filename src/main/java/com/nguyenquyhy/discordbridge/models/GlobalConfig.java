package com.nguyenquyhy.discordbridge.models;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hy on 10/13/2016.
 */
@ConfigSerializable
public class GlobalConfig {
    public GlobalConfig() {
        channels = new ArrayList<>();
        tokenStore = TokenStore.JSON;
        prefixBlacklist = "";
        minecraftBroadcastTemplate = "&2<BROADCAST> %s";
        botToken = "";
    }

    @Setting
    public String botToken;
    @Setting
    public TokenStore tokenStore;
    @Setting
    public String prefixBlacklist;
    @Setting
    public String minecraftBroadcastTemplate;
    @Setting
    public List<ChannelConfig> channels;

    public void migrate() {
        if (channels != null) {
            channels.forEach(ChannelConfig::migrate);
        }
    }
}
