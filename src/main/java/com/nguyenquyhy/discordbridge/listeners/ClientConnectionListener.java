package com.nguyenquyhy.discordbridge.listeners;

import com.nguyenquyhy.discordbridge.DiscordBridge;
import com.nguyenquyhy.discordbridge.logics.LoginHandler;
import com.nguyenquyhy.discordbridge.models.ChannelConfig;
import com.nguyenquyhy.discordbridge.models.GlobalConfig;
import com.nguyenquyhy.discordbridge.utils.ChannelUtil;
import com.nguyenquyhy.discordbridge.utils.ErrorMessages;
import com.nguyenquyhy.discordbridge.utils.TextUtil;
import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.Channel;

import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by Hy on 10/13/2016.
 */
public class ClientConnectionListener {
    @Listener
    public void onJoin(ClientConnectionEvent.Join event, @Root Player player) {
	if (!player.hasPermission("nucleus.connectionmessages.disable")) {
	    DiscordBridge mod = DiscordBridge.getInstance();
	    GlobalConfig config = mod.getConfig();

	    UUID playerId = player.getUniqueId();

	    boolean loggingIn = false;
	    if (!mod.getHumanClients().containsKey(playerId)) {
		loggingIn = LoginHandler.loginHumanAccount(player);
	    }

	    if (!loggingIn && mod.getBotClient() != null) {
		// Use Bot client to send joined message
		for (ChannelConfig channelConfig : config.channels) {
		    if (StringUtils.isNotBlank(channelConfig.discordId) && channelConfig.discord != null
			    && StringUtils.isNotBlank(channelConfig.discord.joinedTemplate)) {
			Channel channel = mod.getBotClient().getChannelById(channelConfig.discordId);
			if (channel != null) {
			    String content = String.format(channelConfig.discord.joinedTemplate, TextUtil
				    .escapeForDiscord(player.getName(), channelConfig.discord.joinedTemplate, "%s"));
			    ChannelUtil.sendMessage(channel, content);
			} else {
			    ErrorMessages.CHANNEL_NOT_FOUND.log(channelConfig.discordId);
			}
			mod.setPlayerCount(1);
			ChannelUtil.setDescription(channel, "Online - Number of Players: "+mod.getPlayerCount());
		    }

		}
	    }

	}
    }

    @Listener
    public void onDisconnect(ClientConnectionEvent.Disconnect event, @Root Player player) {
	if (!player.hasPermission("nucleus.connectionmessages.disable")) {
	    DiscordBridge mod = DiscordBridge.getInstance();
	    GlobalConfig config = mod.getConfig();

	    UUID playerId = player.getUniqueId();

	    DiscordAPI client = mod.getHumanClients().get(playerId);
	    if (client == null)
		client = mod.getBotClient();
	    if (client != null) {
		for (ChannelConfig channelConfig : config.channels) {
		    if (StringUtils.isNotBlank(channelConfig.discordId) && channelConfig.discord != null
			    && StringUtils.isNotBlank(channelConfig.discord.leftTemplate)) {
			Channel channel = client.getChannelById(channelConfig.discordId);
			if (channel != null) {
			    String content = String.format(channelConfig.discord.leftTemplate, TextUtil
				    .escapeForDiscord(player.getName(), channelConfig.discord.leftTemplate, "%s"));
			    ChannelUtil.sendMessage(channel, content);
			} else {
			    ErrorMessages.CHANNEL_NOT_FOUND.log(channelConfig.discordId);
			}
			mod.setPlayerCount(-1);
			ChannelUtil.setDescription(channel, "Online - Number of Players: "+mod.getPlayerCount());
		    }
		    mod.removeAndLogoutClient(playerId);
		    // unauthenticatedPlayers.remove(playerId);
		    mod.getLogger().info(player.getName() + " has disconnected!");
		    
		}
	    }
	}
    }

}
