package uk.gigabait.path;

import me.hsgamer.bettergui.api.addon.GetPlugin;
import me.hsgamer.bettergui.api.addon.PostEnable;
import me.hsgamer.bettergui.api.addon.Reloadable;
import me.hsgamer.hscore.expansion.common.Expansion;
import me.hsgamer.hscore.expansion.extra.expansion.DataFolder;
import uk.gigabait.path.util.Config;

public final class Main implements Expansion, Reloadable, DataFolder, GetPlugin, PostEnable {
    private ReloadListener reloadListener;

    @Override
    public boolean onLoad() {
        Config.init(this);
        return true;
    }

    @Override
    public void onEnable() {
        reloadListener = new ReloadListener(this);
        reloadListener.register();
    }

    @Override
    public void onReload() {
        scheduleRegistration();
    }

    @Override
    public void onDisable() {
        if (reloadListener != null) {
            reloadListener.unregister();
            reloadListener = null;
        }
        Config.clear();
    }

    @Override
    public void onPostEnable() {
        scheduleRegistration();
    }

    public void scheduleRegistration() {
        PathRegistry.schedule(this);
    }
}
