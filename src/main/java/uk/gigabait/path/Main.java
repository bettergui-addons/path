package uk.gigabait.path;

import me.hsgamer.bettergui.api.addon.GetPlugin;
import me.hsgamer.bettergui.api.addon.Reloadable;
import me.hsgamer.hscore.expansion.common.Expansion;
import me.hsgamer.hscore.expansion.extra.expansion.DataFolder;
import uk.gigabait.path.util.Config;

public final class Main implements Expansion, Reloadable, DataFolder, GetPlugin {

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
}
