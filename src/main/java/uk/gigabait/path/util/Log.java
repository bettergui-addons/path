package uk.gigabait.path.util;

import me.hsgamer.bettergui.api.addon.GetPlugin;

import java.util.logging.Level;

public class Log {

    private static final String PREFIX = "[Path] ";

    public static void info(GetPlugin addon, String message) {
        addon.getPlugin().getLogger().info(PREFIX + message);
    }

    public static void warn(GetPlugin addon, String message) {
        addon.getPlugin().getLogger().warning(PREFIX + message);
    }

    public static void error(GetPlugin addon, String message) {
        addon.getPlugin().getLogger().severe(PREFIX + message);
    }

    public static void error(GetPlugin addon, String message, Throwable throwable) {
        addon.getPlugin().getLogger().log(Level.SEVERE, PREFIX + message, throwable);
    }
}
