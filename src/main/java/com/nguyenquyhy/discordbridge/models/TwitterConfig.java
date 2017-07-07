package com.nguyenquyhy.discordbridge.models;

import java.util.HashMap;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

/**
 * Created by Axle2005 on 7/6/2017.
 */
@ConfigSerializable
public class TwitterConfig {
    /**
     * Configs initialized in constructor will be restored automatically if deleted.
     */
    void initializeDefault() {
        twitterConsumerKey = "";
        twitterConsumerSecret="";
        twitterAccessToken="";
        twitterAccessTokenSecret="";
    }

    @Setting
    public String twitterConsumerKey;
    @Setting
    public String twitterConsumerSecret;
    @Setting
    public String twitterAccessToken;
    @Setting
    public String twitterAccessTokenSecret;
    
    
}
