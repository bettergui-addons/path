package uk.gigabait.path;

import me.hsgamer.bettergui.api.addon.GetPlugin;
import me.hsgamer.bettergui.api.addon.Reloadable;
import me.hsgamer.bettergui.api.addon.PostEnable;
import me.hsgamer.hscore.expansion.common.Expansion;
import me.hsgamer.hscore.expansion.extra.expansion.DataFolder;
import uk.gigabait.path.util.Config;

public final class Main implements Expansion, Reloadable, DataFolder, GetPlugin, PostEnable {

    @Override
    public boolean onLoad() {
        Config.init(this);
        return true;
    }

    @Override
    public void onEnable() {
        TemplatePath.register(this);
        MenuPath.register(this);
    }

    @Override
    public void onReload() {
        Config.reload();
        TemplatePath.register(this);
        MenuPath.register(this);
    }

    @Override
    public void onDisable() {
        Config.clear();
    }

    @Override
    public void onPostEnable() {
        // I do not know why but the right menu processing with a custom path is processed correctly after the plugin is reprinted
        getPlugin().getServer().dispatchCommand(getPlugin().getServer().getConsoleSender(), "reloadplugin");
    }
}
