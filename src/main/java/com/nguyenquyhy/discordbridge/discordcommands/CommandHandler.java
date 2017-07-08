package com.nguyenquyhy.discordbridge.discordcommands;

import java.util.Optional;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.nguyenquyhy.discordbridge.DiscordBridge;
import com.nguyenquyhy.discordbridge.models.ChannelConfig;
import com.nguyenquyhy.discordbridge.models.GlobalConfig;
import com.nguyenquyhy.discordbridge.utils.ErrorMessages;
import com.nguyenquyhy.discordbridge.utils.DiscordUtil;
import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.Channel;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.permissions.PermissionState;
import de.btobastian.javacord.entities.permissions.PermissionType;
import de.btobastian.javacord.entities.permissions.Permissions;
import de.btobastian.javacord.entities.permissions.Role;

public class CommandHandler {

    private static String id = "discordbridge";

    public static void handle(Optional<Player> player, String substring) {
	DiscordBridge mod = DiscordBridge.getInstance();
	mod.getLogger().info("Working");
	queueCommand(player, substring.toLowerCase().split(" "));

    }

    public static void handle(Message message) {
	DiscordBridge mod = DiscordBridge.getInstance();
	String content = message.getContent().substring(1);
	queueCommand(message.getAuthor(), message.getChannelReceiver(),mod, content.toLowerCase().split(" "));
    }

    private static void queueCommand(Optional<Player> player, String[] strings) {
	if (!(strings.length > 1)) {
	    sendMessageToSource(player, 0);
	    return;
	}

	if (strings[0].equals("ban") && permCheck(player, id + "ban")) {
	    
	}

    }
    private static void queueCommand(User user,Channel channel,DiscordBridge mod, String[] strings) {
	
	
	if(strings[0].equals("players")) PlayerCommand.run(mod, channel);
	else if(strings[0].equals("tps")) TpsCommand.run(mod, channel);
	else if(strings[0].equals("kick") && discordPermCheck(user, channel, PermissionType.KICK_MEMBERS)) KickCommand.run(mod, channel, strings[1]);
	else if(strings[0].equals("ban") && discordPermCheck(user, channel, PermissionType.BAN_MEMBERS)) BanCommand.run(mod,channel,strings[1]);
    }

    private static boolean permCheck(Optional<Player> player, String permission) {
	if (!player.isPresent())
	    return true;
	else if (player.get().hasPermission(permission))
	    return true;
	else
	    return false;
    }
    private static boolean discordPermCheck(User user, Channel channel, PermissionType perm){
	for(Role role: user.getRoles(channel.getServer())){
	    if(role.getPermissions().getState(perm).equals(PermissionState.ALLOWED)) return true;
	}
	return false;
    }

    private static void sendMessageToSource(Optional<Player> player, int code) {
	CommandSource src;
	if (player.isPresent())
	    src = Sponge.getServer().getPlayer(player.get().getUniqueId()).get().getCommandSource().get();
	else
	    src = Sponge.getServer().getConsole();

	if (code == 0) {
	    src.sendMessage(Text.of(TextColors.RED, "Not enough arguments"));
	}

    }

    private static void runCommand(String substring) {
	DiscordBridge mod = DiscordBridge.getInstance();
	GlobalConfig config = mod.getConfig();

	DiscordAPI client = mod.getBotClient();

	
	for (ChannelConfig channelConfig : config.channels) {
	    Channel channel = client.getChannelById(channelConfig.discordId);
	    if (channel == null) {
		ErrorMessages.CHANNEL_NOT_FOUND.log(channelConfig.discordId);
		return;
	    }
	    //ChannelUtil.banPlayer(channel, substring);
	}

    }
}
