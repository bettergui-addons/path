package uk.gigabait.path;

import uk.gigabait.path.util.Config;
import uk.gigabait.path.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Coordinates deferred registration of menus and templates from custom paths.
 */
public final class PathRegistry {
    private static final AtomicBoolean SCHEDULED = new AtomicBoolean(false);

    private PathRegistry() {
        // utility class
    }

    /**
     * Request that menus/templates be reloaded on the next server tick.
     * Multiple calls before the scheduled run are coalesced.
     */
    public static void schedule(Main expansion) {
        if (!SCHEDULED.compareAndSet(false, true)) {
            return;
        }

        expansion.getPlugin().getServer().getScheduler().runTask(expansion.getPlugin(), () -> {
            try {
                runNow(expansion);
            } catch (Exception exception) {
                Log.error(expansion, "❌   Failed to process custom paths", exception);
            } finally {
                SCHEDULED.set(false);
            }
        });
    }

    /**
     * Immediately reload configuration and register templates/menus from custom paths.
     */
    public static void runNow(Main expansion) {
        Config.reload();
        int templates = TemplatePath.register(expansion);
        int menus = MenuPath.register(expansion);
        Log.info(expansion, "✅   Custom paths processed (templates: " + templates + ", menus: " + menus + ")");
    }
}
