package uk.gigabait.path.util;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import uk.gigabait.path.Main;

import java.io.File;
import java.util.Collections;
import java.util.List;

public final class Config {

    private static BukkitConfig config;

    public static void init(Main expansion) {
        File folder = expansion.getDataFolder();
        if (!folder.exists() && !folder.mkdirs()) {
            throw new IllegalStateException("Failed to create addon folder");
        }

        config = new BukkitConfig(new File(folder, "config.yml"));
        config.setup();
        applyDefaults();
        config.save();
    }

    public static void reload() {
        if (config == null) return;
        config.setup();
        applyDefaults();
        config.save();
    }

    public static void clear() {
        config = null;
    }

    private static void applyDefaults() {
        if (config == null) return;

        if (!config.contains("menu-paths")) {
            config.set(Collections.singletonList("none"), "menu-paths");
        }

        if (!config.contains("template-paths")) {
            config.set(Collections.singletonList("none"), "template-paths");
        }
    }

    public static List<String> getMenuPaths() {
        if (config == null) return Collections.emptyList();
        return config.getOriginal().getStringList("menu-paths");
    }

    public static List<String> getTemplatePaths() {
        if (config == null) return Collections.emptyList();
        return config.getOriginal().getStringList("template-paths");
    }
}
