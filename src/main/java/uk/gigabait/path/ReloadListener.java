package uk.gigabait.path;

import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Detects BetterGUI reload commands and schedules custom path processing afterwards.
 */
public final class ReloadListener implements Listener {
    private static final Set<String> SUPPORTED_COMMANDS = new HashSet<>(
            Arrays.asList("reloadmenu", "rlmenu", "reloadplugin", "rlplugin"));

    private final Main expansion;

    public ReloadListener(Main expansion) {
        this.expansion = expansion;
    }

    public void register() {
        expansion.getPlugin().getServer().getPluginManager().registerEvents(this, expansion.getPlugin());
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        handleCommand(event.getMessage());
    }

    @EventHandler(ignoreCancelled = true)
    public void onServerCommand(ServerCommandEvent event) {
        handleCommand(event.getCommand());
    }

    private void handleCommand(String raw) {
        if (raw == null || raw.isEmpty()) {
            return;
        }

        String stripped = raw.charAt(0) == '/' ? raw.substring(1) : raw;
        int space = stripped.indexOf(' ');
        String label = (space == -1 ? stripped : stripped.substring(0, space)).toLowerCase(Locale.ROOT);

        if (SUPPORTED_COMMANDS.contains(label)) {
            PathRegistry.schedule(expansion);
        }
    }
}
