package com.nguyenquyhy.discordbridge.listeners;

import com.nguyenquyhy.discordbridge.DiscordBridge;
import com.nguyenquyhy.discordbridge.hooks.Boop;
import com.nguyenquyhy.discordbridge.hooks.Nucleus;
import com.nguyenquyhy.discordbridge.models.ChannelConfig;
import com.nguyenquyhy.discordbridge.models.GlobalConfig;
import com.nguyenquyhy.discordbridge.utils.ChannelUtil;
import com.nguyenquyhy.discordbridge.utils.ErrorMessages;
import com.nguyenquyhy.discordbridge.utils.TextUtil;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.text.channel.MessageChannel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Hy on 10/13/2016.
 */
public class ChatListener {

    DiscordBridge mod = DiscordBridge.getInstance();

    //0 - CustomNPC's
    List<String> fakePlayerUUIDs = new ArrayList<String>(Collections.singletonList("c9c843f8-4cb1-4c82-aa61-e264291b7bd6"));

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
            if (channel.getClass().getName().equals(Nucleus.getStaffMessageChannelClass()))
                isStaffChat = true;
            else if (channel.getClass().getName().equals(Boop.getMessageChannelClass()))
                isStaffChat = false;
            else if (!channel.getClass().getName().startsWith("org.spongepowered.api.text.channel.MessageChannel"))
                return; // Ignore all other types
        }

        String plainString = event.getRawMessage().toPlain().trim();
        if (StringUtils.isBlank(plainString) || plainString.startsWith("/")) return;

        // Replace with processed message body - Allows token processing to occur
        plainString = event.getFormatter().getBody().toText().toPlain().trim();

        plainString = TextUtil.formatMinecraftMessage(plainString);

        //Filters out fake player messages, such as CustomNPC messages.
        if (fakePlayerUUIDs.contains(player.getUniqueId().toString())) return;

        JDA client = mod.getBotClient();
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
                        TextChannel channel = client.getTextChannelById(channelConfig.discordId);

                        if (channel == null) {
                            ErrorMessages.CHANNEL_NOT_FOUND.log(channelConfig.discordId);
                            return;
                        }

                        // Format Mentions for Discord
                        plainString = TextUtil.formatMinecraftMention(plainString, channel.getGuild(), player, isBotAccount);

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
