package uk.gigabait.path;

import me.hsgamer.bettergui.manager.MenuManager;
import uk.gigabait.path.util.Config;
import uk.gigabait.path.util.Log;
import uk.gigabait.path.util.YmlWalker;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class MenuPath {

    private MenuPath() {
        // utility class
    }

    public static int register(Main expansion) {
        MenuManager menuManager = expansion.getPlugin().get(MenuManager.class);
        if (menuManager == null) {
            Log.warn(expansion, " ⚠️   Menu manager is unavailable; skipping custom menu registration");
            return 0;
        }

        List<String> configuredPaths = Config.getMenuPaths();
        if (configuredPaths.isEmpty()) {
            return 0;
        }

        return configuredPaths.stream()
                .map(path -> normalizePath(expansion, path))
                .filter(path -> path != null)
                .distinct()
                .mapToInt(directory -> registerMenusInDirectory(expansion, menuManager, directory))
                .sum();
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

    private static int registerMenusInDirectory(Main expansion, MenuManager menuManager, File directory) {
        Set<String> processedFiles = new HashSet<>();
        int loaded = 0;

        Log.info(expansion, "📂   Scanning menus in: " + directory.getAbsolutePath());

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
                menuManager.registerMenu(file);
                loaded++;
            } catch (Exception exception) {
                Log.error(expansion, " ❌   Error registering menu: " + file.getName(), exception);
            }
        }

        if (loaded > 0) {
            Log.info(expansion, "✅   Loaded " + loaded + " menu(s) from " + directory.getAbsolutePath());
        } else if (!processedFiles.isEmpty()) {
            Log.warn(expansion, " ⚠️   No menus loaded from " + directory.getAbsolutePath());
        } else {
            Log.info(expansion, "ℹ️   No menu files found in " + directory.getAbsolutePath());
        }

        return loaded;
    }
}
