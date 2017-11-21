package com.nguyenquyhy.discordbridge.listeners;

import com.nguyenquyhy.discordbridge.DiscordBridge;
import com.nguyenquyhy.discordbridge.models.ChannelConfig;
import com.nguyenquyhy.discordbridge.models.GlobalConfig;
import com.nguyenquyhy.discordbridge.utils.ChannelUtil;
import com.nguyenquyhy.discordbridge.utils.ErrorMessages;
import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.Channel;
import io.github.nucleuspowered.nucleus.api.events.NucleusFirstJoinEvent;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.Getter;

public class FirstJoinListener {

    private DiscordBridge mod = DiscordBridge.getInstance();

    @Listener
    public void onPlayerFirstJoin(NucleusFirstJoinEvent event, @Getter("getTargetEntity") Player player) {

        if (event.isMessageCancelled()) {
            return;
        }

        DiscordAPI client = mod.getBotClient();
        GlobalConfig config = mod.getConfig();

        if (client != null) {
            for (ChannelConfig channelConfig : config.channels) {
                if (StringUtils.isNotBlank(channelConfig.discordId)
                    && channelConfig.discord != null
                    && StringUtils.isNotBlank(channelConfig.discord.firstJoinTemplate)) {
                    Channel channel = client.getChannelById(channelConfig.discordId);

                    if (channel == null) {
                        ErrorMessages.CHANNEL_NOT_FOUND.log(channelConfig.discordId);
                        return;
                    }

                    String message = channelConfig.discord.firstJoinTemplate
                        .replace("%a", player.getName());

                    ChannelUtil.sendMessage(channel, message);
                }
            }
        }
    }
}
