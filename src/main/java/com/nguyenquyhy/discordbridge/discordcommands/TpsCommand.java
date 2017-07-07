package com.nguyenquyhy.discordbridge.discordcommands;

import org.spongepowered.api.Sponge;
import com.nguyenquyhy.discordbridge.DiscordBridge;
import com.nguyenquyhy.discordbridge.utils.ChannelUtil;

import de.btobastian.javacord.entities.Channel;

public class TpsCommand {

    
    public static void run(DiscordBridge mod, Channel channel){
	String content = (double)Sponge.getServer().getTicksPerSecond()+"/20";
	ChannelUtil.sendMessage(channel, content);
    }
    
}
