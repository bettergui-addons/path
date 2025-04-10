package uk.gigabait.path;

import me.hsgamer.bettergui.config.TemplateConfig;
import uk.gigabait.path.util.Config;
import uk.gigabait.path.util.Log;
import uk.gigabait.path.util.YmlWalker;

import java.io.File;
import java.lang.reflect.Method;

public class TemplatePath {

    public static void register(Main expansion) {
        TemplateConfig templateConfig = expansion.getPlugin().get(TemplateConfig.class);

        try {
            Method setupMethod = TemplateConfig.class.getDeclaredMethod("setup", File.class);
            setupMethod.setAccessible(true);

            Config.getTemplatePaths().stream().filter(path -> !path.equalsIgnoreCase("none")).map(path -> path.startsWith("/") || path.startsWith("\\") ? new File(path) : new File(expansion.getPlugin().getDataFolder(), path)).forEach(path -> {
                if (!path.isDirectory()) {
                    Log.warn(expansion, " ⚠️   Missed (not found): " + path.getAbsolutePath());
                    return;
                }

                YmlWalker.walk(path).stream().filter(file -> file.getName().endsWith(".yml")).forEach(file -> {
                    try {
                        setupMethod.invoke(templateConfig, file);
                    } catch (Exception e) {
                        Log.error(expansion, " ❌ Failed to load template from: " + file.getAbsolutePath() + ": " + e.getMessage());
                    }
                });
            });
        } catch (Exception e) {
            Log.error(expansion, " ❌ Error when accessing template setup method: " + e.getMessage());
        }
    }
}
