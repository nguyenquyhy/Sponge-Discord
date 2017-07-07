package com.nguyenquyhy.discordbridge.discordcommands;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;


import com.nguyenquyhy.discordbridge.DiscordBridge;
import com.nguyenquyhy.discordbridge.utils.ChannelUtil;

import de.btobastian.javacord.entities.Channel;

public class PlayerCommand {

public static void PlayerCommand(){
	
    }
    public static void run(DiscordBridge mod, Channel channel){
	ChannelUtil.sendMessage(channel, "Players Currently Online: ");
	for(Player player : Sponge.getServer().getOnlinePlayers()){
	    ChannelUtil.sendMessage(channel, player.getName());
	}
    }
}
