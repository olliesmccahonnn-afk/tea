package dev.mythclient.module.render;

import dev.mythclient.core.Category;
import dev.mythclient.core.ClientModule;
import dev.mythclient.mixin.SimpleOptionAccessor;
import dev.mythclient.setting.NumberSetting;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

/** Raises brightness past vanilla's 100%. Restores your old value on disable and on game exit. */
public class GammaModule extends ClientModule {
    private final NumberSetting level = (NumberSetting) setting(new NumberSetting("Gamma", 12, 1, 15, 1));
    private Double saved;

    public GammaModule() {
        super("Gamma", "Brightens dark areas beyond the vanilla limit.", Category.RENDER);
        // options.txt would otherwise save the raised value
        ClientLifecycleEvents.CLIENT_STOPPING.register(c -> restore());
    }

    @SuppressWarnings("unchecked")
    private void apply(double v) {
        ((SimpleOptionAccessor<Double>) (Object) mc.options.getGamma()).mythclient$setRaw(v);
    }

    private void restore() {
        if (saved != null) { apply(saved); saved = null; }
    }

    @Override
    protected void onEnable() {
        saved = mc.options.getGamma().getValue();
        apply(level.doubleValue());
    }

    @Override
    protected void onDisable() { restore(); }

    @Override
    public void onTick() {
        if (isEnabled() && saved != null) apply(level.doubleValue());
    }
}
