package uk.gigabait.path;

import me.hsgamer.bettergui.manager.MenuManager;
import uk.gigabait.path.util.Config;
import uk.gigabait.path.util.Log;
import uk.gigabait.path.util.YmlWalker;

import java.io.File;

public class MenuPath {

    public static void register(Main expansion) {
        MenuManager menuManager = expansion.getPlugin().get(MenuManager.class);

        Config.getMenuPaths().stream().filter(path -> !path.equalsIgnoreCase("none")).map(path -> path.startsWith("/") || path.startsWith("\\") ? new File(path) : new File(expansion.getPlugin().getDataFolder(), path)).forEach(path -> {
            if (!path.isDirectory()) {
                Log.warn(expansion, " ⚠️   Missed (not found): " + path.getAbsolutePath());
                return;
            }

            YmlWalker.walk(path).stream().filter(file -> file.getName().endsWith(".yml")).forEach(file -> {
                try {
                    menuManager.registerMenu(file);
                } catch (Exception e) {
                    Log.error(expansion, " ❌   Error registering menu: " + file.getName() + ": " + e.getMessage());
                }
            });
        });
    }
}
