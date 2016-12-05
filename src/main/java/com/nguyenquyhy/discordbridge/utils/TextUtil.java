package com.nguyenquyhy.discordbridge.utils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.nguyenquyhy.discordbridge.DiscordBridge;
import com.nguyenquyhy.discordbridge.models.ChannelConfig;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.permissions.Role;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyle;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Hy on 8/29/2016.
 */
public class TextUtil {
    private static final Pattern urlPattern =
            Pattern.compile("(?<first>(^|\\s))(?<colour>(&[0-9a-flmnork])+)?(?<url>(http(s)?://)?([A-Za-z0-9]+\\.)+[A-Za-z0-9]{2,}\\S*)", Pattern.CASE_INSENSITIVE);
    private static final  Pattern MENTION_PATTERN = Pattern.compile("<@!?[0-9]{18}>");

    private static final StyleTuple EMPTY = new StyleTuple(TextColors.NONE, TextStyles.NONE);

    public static String formatDiscordMessage(String message) {
        for (Emoji emoji : Emoji.values()) {
            message = message.replace(emoji.unicode, emoji.minecraftFormat);
        }
        return message;
    }

    public static String formatMinecraftMessage(String message) {
        for (Emoji emoji : Emoji.values()) {
            message = message.replace(emoji.minecraftFormat, emoji.discordFormat);
        }
        return message;
    }

    private static Map<String, Map<String, Boolean>> needReplacementMap = new HashMap<>();

    public static String escapeForDiscord(String text, String template, String token) {
        if (!needReplacementMap.containsKey(token)) {
            needReplacementMap.put(token, new HashMap<>());
        }
        Map<String, Boolean> needReplacement = needReplacementMap.get(token);
        if (!needReplacement.containsKey(template)) {
            boolean need = !Pattern.matches(".*`.*" + token + ".*`.*", template)
                    && Pattern.matches(".*_.*" + token + ".*_.*", template);
            needReplacement.put(template, need);
        }
        if (needReplacement.get(template)) text = text.replace("_", "\\_");
        return text;
    }

    public static Text formatForMinecraft(String message) {
        Preconditions.checkNotNull(message, "message");
        if (message.isEmpty()) {
            return Text.EMPTY;
        }

        Matcher m = urlPattern.matcher(message);
        if (!m.find()) {
            return TextSerializers.FORMATTING_CODE.deserialize(message);
        }

        List<Text> texts = Lists.newArrayList();
        String remaining = message;
        StyleTuple st = EMPTY;
        do {

            // We found a URL. We split on the URL that we have.
            String[] textArray = remaining.split(urlPattern.pattern(), 2);
            Text first = Text.builder().color(st.colour).style(st.style)
                    .append(TextSerializers.FORMATTING_CODE.deserialize(textArray[0])).build();

            // Add this text to the list regardless.
            texts.add(first);

            // If we have more to do, shove it into the "remaining" variable.
            if (textArray.length == 2) {
                remaining = textArray[1];
            } else {
                remaining = null;
            }

            // Get the last colour & styles
            String colourMatch = m.group("colour");
            if (colourMatch != null && !colourMatch.isEmpty()) {
                first = TextSerializers.FORMATTING_CODE.deserialize(m.group("colour") + " ");
            }

            st = getLastColourAndStyle(first, st);

            // Build the URL
            String url = m.group("url");
            String toParse = TextSerializers.FORMATTING_CODE.stripCodes(url);
            String whiteSpace = m.group("first");
            texts.add(Text.of(whiteSpace));

            try {
                URL urlObj;
                if (!toParse.startsWith("http://") && !toParse.startsWith("https://")) {
                    urlObj = new URL("http://" + toParse);
                } else {
                    urlObj = new URL(toParse);
                }

                texts.add(Text.builder(url).color(TextColors.DARK_AQUA).style(TextStyles.UNDERLINE)
                        .onHover(TextActions.showText(Text.of("Click to open " + url)))
                        .onClick(TextActions.openUrl(urlObj))
                        .build());
            } catch (MalformedURLException e) {
                // URL parsing failed, just put the original text in here.
                DiscordBridge.getInstance().getLogger().warn("Malform: " + url);
                texts.add(Text.builder(url).color(st.colour).style(st.style).build());
            }
        } while (remaining != null && m.find());

        // Add the last bit.
        if (remaining != null) {
            texts.add(Text.builder().color(st.colour).style(st.style)
                    .append(TextSerializers.FORMATTING_CODE.deserialize(remaining)).build());
        }

        // Join it all together.
        return Text.join(texts);
    }

    public static String formatMentions(Message message) {
        String s = message.getContent();

        for (User mention: message.getMentions()) {
            DiscordBridge.getInstance().getLogger().info("Found Mention: @" + mention.getName() + ":" + mention.getId());
            s = s.replace("<@"+mention.getId()+">","@" + mention.getName());
            s = s.replace("<@!"+mention.getId()+">","@" + mention.getName()); // Change to getNickname() When supported
        }
        return s;
    }

    public static TextColor resolveTextColor(String s) {
        switch (s.toUpperCase()){
            case "§0":
            case "&0":
            case "000000":
                return TextColors.BLACK;
            case "§1":
            case "&1":
            case "0000AA":
                return TextColors.DARK_BLUE;
            case "§2":
            case "&2":
            case "00AA00":
                return TextColors.DARK_GREEN;
            case "§3":
            case "&3":
            case "00AAAA":
                return TextColors.DARK_AQUA;
            case "§4":
            case "&4":
            case "AA0000":
                return TextColors.DARK_RED;
            case "§5":
            case "&5":
            case "AA00AA":
                return TextColors.DARK_PURPLE;
            case "§6":
            case "&6":
            case "FFAA00":
                return TextColors.GOLD;
            case "§7":
            case "&7":
            case "AAAAAA":
                return TextColors.GRAY;
            case "§8":
            case "&8":
            case "555555":
                return TextColors.DARK_GRAY;
            case "§9":
            case "&9":
            case "5555FF":
                return TextColors.BLUE;
            case "§a":
            case "&a":
            case "55FF55":
                return TextColors.GREEN;
            case "§b":
            case "&b":
            case "55FFFF":
                return TextColors.AQUA;
            case "§c":
            case "&c":
            case "FF5555":
                return TextColors.RED;
            case "§d":
            case "&d":
            case "FF55FF":
                return TextColors.LIGHT_PURPLE;
            case "§e":
            case "&e":
            case "FFFF55":
                return TextColors.YELLOW;
            case "§f":
            case "&f":
            case "FFFFFF":
                return TextColors.WHITE;
            default: return TextColors.RESET;
        }
    }

    public static String replacePlaceholders(ChannelConfig config, Message message){
        String s = config.minecraft.chatTemplate;
        s = s.replace("%a", message.getAuthor().getName());
        // Javacord doesn't support nicknames yet!
        //s = s.replace("%n", message.getAuthor().getNickname());
        int position = 0;
        String roleName = config.minecraft.defaultRole;
        Color roleColor;
        for (Role role: message.getAuthor().getRoles(message.getChannelReceiver().getServer())){
            if (role.getPosition() > position) {
                position = role.getPosition();
                roleName = role.getName();
                roleColor = role.getColor();
            }
        }
        s = true ? s.replace("%r", roleName): s.replace("%r", roleColor + roleName);

        return String.format(s, TextUtil.formatDiscordMessage(message.getContent()));
    }

    private static StyleTuple getLastColourAndStyle(Text text, StyleTuple current) {
        List<Text> texts = flatten(text);
        TextColor tc = TextColors.NONE;
        TextStyle ts = TextStyles.NONE;
        for (int i = texts.size() - 1; i > -1; i--) {
            // If we have both a Text Colour and a Text Style, then break out.
            if (tc != TextColors.NONE && ts != TextStyles.NONE) {
                break;
            }

            if (tc == TextColors.NONE) {
                tc = texts.get(i).getColor();
            }

            if (ts == TextStyles.NONE) {
                ts = texts.get(i).getStyle();
            }
        }

        if (current == null) {
            return new StyleTuple(tc, ts);
        }

        return new StyleTuple(tc != TextColors.NONE ? tc : current.colour, ts != TextStyles.NONE ? ts : current.style);
    }

    private static List<Text> flatten(Text text) {
        List<Text> texts = Lists.newArrayList(text);
        if (!text.getChildren().isEmpty()) {
            text.getChildren().forEach(x -> texts.addAll(flatten(x)));
        }

        return texts;
    }

    private static final class StyleTuple {
        final TextColor colour;
        final TextStyle style;

        StyleTuple(TextColor colour, TextStyle style) {
            this.colour = colour;
            this.style = style;
        }
    }
}
