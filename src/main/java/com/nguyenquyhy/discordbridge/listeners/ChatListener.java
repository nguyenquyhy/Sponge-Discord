package com.nguyenquyhy.discordbridge.listeners;

import com.nguyenquyhy.discordbridge.DiscordBridge;
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
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.text.channel.MessageChannel;

import java.util.UUID;

/**
 * Created by Hy on 10/13/2016.
 */
public class ChatListener {

    DiscordBridge mod = DiscordBridge.getInstance();

    /**
     * Send chat from Minecraft to Discord
     *
     * @param event
     */
    @Listener(order = Order.LATE)
    public void onChat(MessageChannelEvent.Chat event, @First Player player) {

        if (event.isMessageCancelled()) return;

        GlobalConfig config = mod.getConfig();

        boolean isStaffChat = false;
        if (event.getChannel().isPresent()) {
            MessageChannel channel = event.getChannel().get();
            if (channel.getClass().getName().equals("io.github.nucleuspowered.nucleus.modules.staffchat.StaffChatMessageChannel"))
                isStaffChat = true;
            else if (!channel.getClass().getName().startsWith("org.spongepowered.api.text.channel.MessageChannel"))
                return; // Ignore all other types
        }

        String plainString = event.getRawMessage().toPlain().trim();
        if (StringUtils.isBlank(plainString) || plainString.startsWith("/")) return;

        // Replace with processed message body - Allows token processing to occur
        plainString = event.getFormatter().getBody().toText().toPlain().trim();

        plainString = TextUtil.formatMinecraftMessage(plainString);

        DiscordAPI client = mod.getBotClient();
        boolean isBotAccount = true;
        if (mod.getHumanClients().containsKey(player.getUniqueId())) {
            client = mod.getHumanClients().get(player.getUniqueId());
            isBotAccount = false;
        }

        if (client != null) {
            for (ChannelConfig channelConfig : config.channels) {
                if (StringUtils.isNotBlank(channelConfig.discordId) && channelConfig.discord != null) {
                    String template = null;
                    if (!isStaffChat && channelConfig.discord.publicChat != null) {
                        template = isBotAccount ? channelConfig.discord.publicChat.anonymousChatTemplate : channelConfig.discord.publicChat.authenticatedChatTemplate;
                    } else if (isStaffChat && channelConfig.discord.staffChat != null) {
                        template = isBotAccount ? channelConfig.discord.staffChat.anonymousChatTemplate : channelConfig.discord.staffChat.authenticatedChatTemplate;
                    }

                    if (StringUtils.isNotBlank(template)) {
                        Channel channel = client.getChannelById(channelConfig.discordId);

                        if (channel == null) {
                            ErrorMessages.CHANNEL_NOT_FOUND.log(channelConfig.discordId);
                            return;
                        }

                        // Format Mentions for Discord
                        plainString = TextUtil.formatMinecraftMention(plainString, channel.getServer(), player, isBotAccount);

                        if (isBotAccount) {
//                                if (channel == null) {
//                                    LoginHandler.loginBotAccount();
//                                }
                            String content = String.format(
                                template.replace("%a",
                                    TextUtil.escapeForDiscord(player.getName(), template, "%a")),
                                plainString);
                            ChannelUtil.sendMessage(channel, content);
                        } else {
//                                if (channel == null) {
//                                    LoginHandler.loginHumanAccount(player.get());
//                                }
                            ChannelUtil.sendMessage(channel, String.format(template, plainString));
                        }
                    }
                }
            }
        }
    }
}
