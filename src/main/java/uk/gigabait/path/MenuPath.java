package uk.gigabait.path;

import me.hsgamer.bettergui.manager.MenuManager;
import uk.gigabait.path.util.Config;
import uk.gigabait.path.util.Log;
import uk.gigabait.path.util.PathFiles;

import java.io.File;
import java.util.HashSet;
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

        return PathFiles.directories(expansion.getPlugin().getDataFolder(), Config.getMenuPaths(),
                        directory -> Log.warn(expansion, " ⚠️   Missed (not found): " + directory.getAbsolutePath()))
                .stream()
                .mapToInt(directory -> registerMenusInDirectory(expansion, menuManager, directory))
                .sum();
    }

    private static int registerMenusInDirectory(Main expansion, MenuManager menuManager, File directory) {
        Set<String> processedFiles = new HashSet<>();
        int loaded = 0;

        Log.info(expansion, "📂   Scanning menus in: " + directory.getAbsolutePath());

        for (File file : PathFiles.ymlFiles(directory)) {
            String uniqueKey = PathFiles.canonicalKey(file);
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
