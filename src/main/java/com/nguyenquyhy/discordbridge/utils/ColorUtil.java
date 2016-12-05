package com.nguyenquyhy.discordbridge.utils;

import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ColorUtil {
    private static Map<Color, TextColor> colorTextColor = new HashMap<>();

    public static void init() {
        colorTextColor.put(Color.BLACK, TextColors.BLACK);
        colorTextColor.put(Color.getColor("00AA00"), TextColors.DARK_BLUE);
        colorTextColor.put(Color.BLACK, TextColors.DARK_GREEN);
        colorTextColor.put(Color.BLACK, TextColors.DARK_AQUA);
        colorTextColor.put(Color.BLACK, TextColors.DARK_RED);
        colorTextColor.put(Color.BLACK, TextColors.DARK_PURPLE);
        colorTextColor.put(Color.BLACK, TextColors.GOLD);
        colorTextColor.put(Color.BLACK, TextColors.GRAY);
        colorTextColor.put(Color.BLACK, TextColors.DARK_GRAY);
        colorTextColor.put(Color.BLACK, TextColors.BLUE);
        colorTextColor.put(Color.BLACK, TextColors.GREEN);
        colorTextColor.put(Color.BLACK, TextColors.AQUA);
        colorTextColor.put(Color.BLACK, TextColors.RED);
        colorTextColor.put(Color.BLACK, TextColors.LIGHT_PURPLE);
        colorTextColor.put(Color.BLACK, TextColors.YELLOW);
        colorTextColor.put(Color.BLACK, TextColors.WHITE);
/*        case "0000AA":
        return TextColors.DARK_BLUE;
        case "00AA00":
        return TextColors.DARK_GREEN;
        case "00AAAA":
        return TextColors.DARK_AQUA;
        case "AA0000":
        return TextColors.DARK_RED;
        case "AA00AA":
        return TextColors.DARK_PURPLE;
        case "FFAA00":
        return TextColors.GOLD;
        case "AAAAAA":
        return TextColors.GRAY;
        case "555555":
        return TextColors.DARK_GRAY;
        case "5555FF":
        return TextColors.BLUE;
        case "55FF55":
        return TextColors.GREEN;
        case "55FFFF":
        return TextColors.AQUA;
        case "FF5555":
        return TextColors.RED;
        case "FF55FF":
        return TextColors.LIGHT_PURPLE;
        case "FFFF55":
        return TextColors.YELLOW;
        case "FFFFFF":
        return TextColors.WHITE;
        default: return TextColors.RESET;*/
    }

    public static TextColor getTextColor(Color color) {
            return colorTextColor.containsKey(color) ? colorTextColor.get(color) : TextColors.RESET;
    }
}
