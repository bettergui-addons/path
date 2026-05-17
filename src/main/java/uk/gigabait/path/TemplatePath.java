package uk.gigabait.path;

import me.hsgamer.bettergui.config.TemplateConfig;
import uk.gigabait.path.util.Config;
import uk.gigabait.path.util.Log;
import uk.gigabait.path.util.PathFiles;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashSet;
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

        return PathFiles.directories(expansion.getPlugin().getDataFolder(), Config.getTemplatePaths(),
                        directory -> Log.warn(expansion, " ⚠️   Missed (not found): " + directory.getAbsolutePath()))
                .stream()
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

    private static int registerTemplatesInDirectory(Main expansion, TemplateConfig templateConfig, Method method, File directory) {
        Set<String> processedFiles = new HashSet<>();
        int loaded = 0;

        Log.info(expansion, "📂   Scanning templates in: " + directory.getAbsolutePath());

        for (File file : PathFiles.ymlFiles(directory)) {
            String uniqueKey = PathFiles.canonicalKey(file);
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
