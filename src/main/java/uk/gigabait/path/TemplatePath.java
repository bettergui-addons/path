package uk.gigabait.path;

import me.hsgamer.bettergui.config.TemplateConfig;
import uk.gigabait.path.util.Config;
import uk.gigabait.path.util.Log;
import uk.gigabait.path.util.YmlWalker;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class TemplatePath {

    private static volatile Method setupMethod;
    private static volatile boolean methodResolutionAttempted;

    private TemplatePath() {
        // utility class
    }

    public static int register(Main expansion) {
        TemplateConfig templateConfig = expansion.getPlugin().get(TemplateConfig.class);
        if (templateConfig == null) {
            Log.warn(expansion, " ⚠️   Template config is unavailable; skipping custom template registration");
            return 0;
        }

        Method method = resolveSetupMethod(expansion);
        if (method == null) {
            return 0;
        }

        List<String> configuredPaths = Config.getTemplatePaths();
        if (configuredPaths.isEmpty()) {
            return 0;
        }

        return configuredPaths.stream()
                .map(path -> normalizePath(expansion, path))
                .filter(path -> path != null)
                .distinct()
                .mapToInt(directory -> registerTemplatesInDirectory(expansion, templateConfig, method, directory))
                .sum();
    }

    private static Method resolveSetupMethod(Main expansion) {
        Method cached = setupMethod;
        if (cached != null) {
            return cached;
        }

        if (methodResolutionAttempted) {
            return null;
        }

        synchronized (TemplatePath.class) {
            if (setupMethod != null) {
                return setupMethod;
            }

            if (methodResolutionAttempted) {
                return null;
            }

            methodResolutionAttempted = true;
            try {
                Method method = TemplateConfig.class.getDeclaredMethod("setup", File.class);
                method.setAccessible(true);
                setupMethod = method;
                return method;
            } catch (ReflectiveOperationException exception) {
                Log.error(expansion, " ❌   Failed to access TemplateConfig#setup(File). Is the BetterGUI version supported?", exception);
                return null;
            }
        }
    }

    private static File resolvePath(Main expansion, String path) {
        if (path.startsWith("/") || path.startsWith("\\")) {
            return new File(path);
        }
        return new File(expansion.getPlugin().getDataFolder(), path);
    }

    private static File normalizePath(Main expansion, String raw) {
        if (raw == null || raw.equalsIgnoreCase("none")) {
            return null;
        }

        File directory = resolvePath(expansion, raw);
        if (!directory.isDirectory()) {
            Log.warn(expansion, " ⚠️   Missed (not found): " + directory.getAbsolutePath());
            return null;
        }

        try {
            return directory.getCanonicalFile();
        } catch (IOException ignored) {
            return directory;
        }
    }

    private static int registerTemplatesInDirectory(Main expansion, TemplateConfig templateConfig, Method method, File directory) {
        Set<String> processedFiles = new HashSet<>();
        int loaded = 0;

        Log.info(expansion, "📂   Scanning templates in: " + directory.getAbsolutePath());

        for (File file : YmlWalker.walk(directory)) {
            if (file == null) {
                continue;
            }

            String name = file.getName().toLowerCase(Locale.ROOT);
            if (!name.endsWith(".yml")) {
                continue;
            }

            String uniqueKey = file.getAbsolutePath();
            try {
                uniqueKey = file.getCanonicalPath();
            } catch (IOException ignored) {
                // fallback to absolute path
            }

            if (!processedFiles.add(uniqueKey)) {
                continue;
            }

            try {
                method.invoke(templateConfig, file);
                loaded++;
            } catch (Exception exception) {
                Log.error(expansion, " ❌   Failed to load template from: " + file.getAbsolutePath(), exception);
            }
        }

        if (loaded > 0) {
            Log.info(expansion, "✅   Loaded " + loaded + " template(s) from " + directory.getAbsolutePath());
        } else if (!processedFiles.isEmpty()) {
            Log.warn(expansion, " ⚠️   No templates loaded from " + directory.getAbsolutePath());
        } else {
            Log.info(expansion, "ℹ️   No template files found in " + directory.getAbsolutePath());
        }

        return loaded;
    }
}
